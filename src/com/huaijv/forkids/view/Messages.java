package com.huaijv.forkids.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.huaijv.forkids.R;
import com.huaijv.forkids.db.MessagesDBHelper;
import com.huaijv.forkids.model.GlobalVariables;
import com.huaijv.forkids.utils.ChildThread;
import com.huaijv.forkids.utils.ChildThread.WorkForMain;
import com.huaijv.forkids.utils.CreateGlobalMenu;
import com.huaijv.forkids.utils.JsonUtils;
import com.huaijv.forkids.utils.NetUtils;
import com.huaijv.forkids.viewElems.MessageListViewAdapter;
import com.huaijv.forkids.viewElems.MessageListViewAdapter.FlagListener;
import com.slidingmenu.lib.SlidingMenu;

/**
 * Messages[activity]: 消息通知
 * 
 * @author chaos
 * 
 */
public class Messages extends Activity {

	private SlidingMenu menu = null;
	private ListView messageListView = null;
	private MessageListViewAdapter messageListViewAdapter = null;
	private List<Map<String, Object>> listItems = null;
	private List<Map<String, Object>> listItemsOri = null;
	private Map<String, Object> map = null;
	private GlobalApplication app = null;
	private Handler mainHandler = null, childHandler = null;
	private ChildThread childThread = null;
	private MessagesDBHelper dbHelper = null;
	private Cursor cursor = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messages);

		app = (GlobalApplication) this.getApplication();
		// app.cancelProgressBar();
		if (!app.activities.contains(this))
			app.activities.add(this);

		mainHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (app.MSG_ERROR == msg.what) {
					Toast.makeText(Messages.this, "err", Toast.LENGTH_SHORT)
							.show();
					// app.exitAll();
				} else if (app.MSG_GET == msg.what) {
					listItems = (List<Map<String, Object>>) msg.obj;
					messageListViewAdapter = new MessageListViewAdapter(
							Messages.this, listItems, dbHelper);
					messageListViewAdapter.setFlagListener(new mFlagListener());
					messageListView.setAdapter(messageListViewAdapter);
				}
				app.cancelProgressBar();
			}
		};

		childThread = new ChildThread(new WorkForMain() {

			@Override
			public void doJob(Message msg) {
				if (app.MSG_GET == msg.what) {
					String rev = NetUtils
							.getDataByUrl(
									"http://huaijv-sap.eicp.net:8088/forkids/kidannouncements?from=Parent",
									app.getLoginString());
					if ("err".equalsIgnoreCase(rev)) {
						mainHandler.sendEmptyMessage(app.MSG_ERROR);
					} else {
						Message toMain = mainHandler.obtainMessage(app.MSG_GET);
						try {
							listItems = JsonUtils.jsonArray2List(rev);
							/*
							 * 从本地数据库sqlite中取得已经缓存的消息通知
							 */
							dbHelper = new MessagesDBHelper(Messages.this);
							cursor = dbHelper.select();
							listItemsOri = new ArrayList<Map<String, Object>>();
							String text = "\n";
							for (int i = 0; i < cursor.getCount(); i++) {
								map = new HashMap<String, Object>();
								cursor.moveToPosition(i);
								map.put("annoId", cursor.getString(1));
								map.put("type", cursor.getString(2));
								listItemsOri.add(map);
								text += i + "[" + cursor.getInt(1) + " -- "
										+ cursor.getString(2) + "]\n";
							}
							/*
							 * 将缓存的消息通知与从服务器取得的消息通知进行合并（其目的是取得每一条消息通知的“已读/未读/重要”标识
							 */
							for (int i = 0; i < listItems.size(); i++) {
								int flag = 0;
								for (int j = 0; j < listItemsOri.size(); j++) {
									if (Integer.parseInt(listItemsOri.get(j)
											.get("annoId").toString()) == Integer
											.parseInt(listItems.get(i)
													.get("annoId").toString())) {
										listItems.get(i)
												.put("type",
														listItemsOri.get(j)
																.get("type"));
										flag = 1;
									}
								}
								if (flag == 0) {
									listItems.get(i).put("type", "-1");
									dbHelper.insertWithRealIdAndType(
											(Integer) listItems.get(i).get(
													"annoId"), 0);
								}
							}
							sortImportantMessage(listItems);
							toMain.obj = listItems;
						} catch (JSONException e) {
							e.printStackTrace();
						}
						mainHandler.sendMessage(toMain);
					}
				}
			}
		});

		childThread.start();
		childHandler = childThread.getHandler();
		app.showProgressBar();
		/*
		 * 让childThread从服务器获取消息通知
		 */
		childHandler.sendEmptyMessage(app.MSG_GET);

		messageListView = (ListView) this.findViewById(R.id.message_listview);

		menu = CreateGlobalMenu.returnGlobalMenu(Messages.this);
		Button navigationButton = (Button) findViewById(R.id.navigation_btn);
		navigationButton.setHeight(GlobalVariables.screenWidth / 9);
		navigationButton.setWidth(GlobalVariables.screenWidth / 9);
		navigationButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				menu.toggle();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(Messages.this, Homepage.class);
			startActivity(intent);
			app.activities.remove(this);
			app.cancelProgressBar();
			finish();
		}
		return true;
	}

	/**
	 * mFlagListener: 定义当每一条消息的“小红旗”被点击后执行的操作
	 * 
	 * @author chaos
	 * 
	 */
	public class mFlagListener implements FlagListener {
		@Override
		public void onFlagClick(int position) {
			messageListViewAdapter.changeFlag(position);
		}
	}

	/**
	 * sortImportantMessage: 将重要消息重新排序至list的首部
	 * 
	 * @param list
	 */
	private void sortImportantMessage(List<Map<String, Object>> list) {
		// inverseList(list);
		int cursor = 0;
		for (int i = 0; i < list.size(); i++) {
			if (Integer.parseInt(list.get(i).get("type").toString()) != 1) {
				cursor = i;
				break;
			}
		}
		for (int i = 1; i < list.size(); i++) {
			if ((Integer.parseInt(list.get(i - 1).get("type").toString()) != 1)
					&& (Integer.parseInt(list.get(i).get("type").toString()) == 1)) {
				list.add(cursor, list.remove(i));
				cursor++;
			}
		}
		// inverseList(list);
	}

	/**
	 * inverseList: 颠倒list的顺序
	 * 
	 * @param list
	 */
	private void inverseList(List<Map<String, Object>> list) {
		int size = list.size();
		int middle = (size % 2 == 0) ? size / 2 : (size - 1) / 2;
		for (int i = 0; i < middle; i++) {
			int j = size - 1 - i;
			map = list.remove(i);
			list.add(i, list.remove(j - 1));
			list.add(j, map);
		}
	}

}

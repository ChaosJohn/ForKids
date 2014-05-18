package com.huaijv.forkids.view;

import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.huaijv.forkids.R;
import com.huaijv.forkids.model.GlobalVariables;
import com.huaijv.forkids.utils.ChildThread;
import com.huaijv.forkids.utils.ChildThread.WorkForMain;
import com.huaijv.forkids.utils.CreateGlobalMenu;
import com.huaijv.forkids.utils.JsonUtils;
import com.huaijv.forkids.utils.NetUtils;
import com.huaijv.forkids.viewElems.TipsAdapter;
import com.slidingmenu.lib.SlidingMenu;

/**
 * Tips[activity]: 育英知识列表界面
 * 
 * @author chaos
 * 
 */
public class Tips extends Activity {

	private SlidingMenu menu = null;

	private ListView mListView;
	private TipsAdapter listViewAdapter;
	public List<Map<String, Object>> listItems;
	private GlobalApplication app = null;
	private ChildThread childThread = null;
	private Handler mainHandler = null, childHandler = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tips_home);

		app = (GlobalApplication) this.getApplication();
		if (!app.activities.contains(this))
			app.activities.add(this);

		mainHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (app.MSG_ERROR == msg.what) {
					Toast.makeText(getApplicationContext(), "err",
							Toast.LENGTH_LONG).show();
				} else if (app.MSG_GET == msg.what) {
					setView();
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
									"http://huaijv-sap.eicp.net:8088/forkids/kidnursingknowledges?from=Parent&page=1&size=10",
									app.getLoginString());
					if ("err".equalsIgnoreCase(rev)) {
						mainHandler.sendEmptyMessage(app.MSG_ERROR);
					} else {
						try {
							listItems = JsonUtils.jsonArray2List(rev);
							for (int i = 0; i < listItems.size(); i++) {
								/*
								 * 从时间字符串中截取日期
								 */
								String timeChangedString = listItems.get(i)
										.get("changeAt").toString();
								String timeCreatedString = listItems.get(i)
										.get("createAt").toString();
								if (!(timeChangedString
										.equalsIgnoreCase("null") && timeCreatedString
										.equalsIgnoreCase("null"))) {
									String timeString = (timeChangedString
											.equalsIgnoreCase("null")) ? timeCreatedString
											: timeChangedString;
									String[] timeStrings = timeString
											.split(" ");
									listItems.get(i)
											.put("time", timeStrings[0]);
								}
							}
							mainHandler.sendEmptyMessage(app.MSG_GET);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});

		childThread.start();
		childHandler = childThread.getHandler();
		/*
		 * 让childThread获取育英知识列表
		 */
		app.showProgressBar();
		childHandler.sendEmptyMessage(app.MSG_GET);

		mListView = (ListView) findViewById(R.id.listview01);

		menu = CreateGlobalMenu.returnGlobalMenu(Tips.this);
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

	private void setView() {
		listViewAdapter = new TipsAdapter(this, listItems);
		mListView.setAdapter(listViewAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(getApplicationContext(),
						TipsDetail.class);
				intent.putExtra("knId", listItems.get(arg2).get("knId")
						.toString());
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			startActivity(new Intent(Tips.this, Homepage.class));
			app.cancelProgressBar();
			app.activities.remove(this);
			finish();
		}
		return true;
	}

}
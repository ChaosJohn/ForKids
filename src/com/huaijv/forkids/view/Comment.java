package com.huaijv.forkids.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.Toast;

import com.huaijv.forkids.R;
import com.huaijv.forkids.model.GlobalVariables;
import com.huaijv.forkids.utils.ChildThread;
import com.huaijv.forkids.utils.ChildThread.WorkForMain;
import com.huaijv.forkids.utils.JsonUtils;
import com.huaijv.forkids.utils.NetUtils;
import com.huaijv.forkids.viewElems.CommentAdapter;

/**
 * Comment[activity]: 评论界面
 * 
 * @Usage 跳转到Comment的intent必须以"from"为key，指定从哪个activity跳转而来
 * 
 * @author chaos
 * 
 */
public class Comment extends Activity {

	private GlobalApplication app = null;
	private List<Map<String, Object>> listItems = null;
	private Map<String, Object> map = null;
	private ListView commentListView = null;
	private CommentAdapter commentAdapter = null;
	private String actId = null;
	private String knId = null;
	private String momId = null;
	private Handler mainHandler, childHandler;
	private ChildThread childThread;
	private final int MSG_GET = 0;
	private final int MSG_UPLOAD = 1;
	private final int MSG_ERROR = -1;
	private EditText editText = null;
	private String from = null;
	private String getUrl = null;
	private String postUrl = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);

		/*
		 * 根据从不同的activity，设置评论的获取url和提交url，以及相应的模块id
		 */
		from = Comment.this.getIntent().getExtras().getString("from");
		if (from.equalsIgnoreCase("Journal")) {
			actId = Comment.this.getIntent().getExtras().getString("actId");
			getUrl = "http://huaijv-sap.eicp.net:8088/forkids/kidactcomms?find=ByActId&actId="
					+ actId;
			postUrl = "http://huaijv-sap.eicp.net:8088/forkids/kidactcomms/";
		} else if (from.equalsIgnoreCase("Tips")) {
			knId = Comment.this.getIntent().getExtras().getString("knId");
			getUrl = "http://huaijv-sap.eicp.net:8088/forkids/kidactcomms?find=ByKnId&knId="
					+ knId;
			postUrl = "http://huaijv-sap.eicp.net:8088/forkids/kidnkcomms/";
		} else if (from.equalsIgnoreCase("Moments")) {
			momId = Comment.this.getIntent().getExtras().getString("momId");
			getUrl = "http://huaijv-sap.eicp.net:8088/forkids/kidmomecomms?find=ByMomId&momId="
					+ momId;
			postUrl = "http://huaijv-sap.eicp.net:8088/forkids/kidmomecomms";
		}

		app = (GlobalApplication) this.getApplication();
		// app.cancelProgressBar();
		if (!app.activities.contains(this))
			app.activities.add(this);

		mainHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (MSG_GET == msg.what) {
					try {
						listItems = JsonUtils
								.jsonArray2List(msg.obj.toString());
						setView();
						setListener();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else if (MSG_UPLOAD == msg.what) {
					commentAdapter.addItem((Map<String, Object>) msg.obj);
					editText.setText("");
				} else if (MSG_ERROR == msg.what) {

				}
				app.cancelProgressBar();
			}
		};

		childThread = new ChildThread(new WorkForMain() {
			@Override
			public void doJob(Message msg) {
				Message toMain = mainHandler.obtainMessage();
				String dataString = null;
				if (MSG_GET == msg.what) {
					dataString = NetUtils.getDataByUrl(getUrl,
							app.getLoginString());
					toMain.what = MSG_GET;
					toMain.obj = dataString;
					mainHandler.sendMessage(toMain);
				} else if (MSG_UPLOAD == msg.what) {
					toMain.what = MSG_UPLOAD;
					dataString = msg.obj.toString();
					map = new HashMap<String, Object>();
					/*
					 * 以下代码为假数据，应该填写用户的一些信息，只是这些数据后台还没有提供
					 */
					map.put("commAuthorPortrait",
							"http://t2.baidu.com/it/u=2923210268,2460374937&fm=23&gp=0.jpg");
					map.put("commAuthor", "胡汉三");
					map.put("createAt", "刚刚");
					map.put("actId", actId);
					map.put("commContent", dataString);
					if (!("err".equalsIgnoreCase(NetUtils.postDataByUrl(
							postUrl, JsonUtils.map2JsonObjectString(map),
							app.getLoginString())))) {
						Toast.makeText(Comment.this, dataString,
								Toast.LENGTH_SHORT).show();
						toMain.obj = map;
						mainHandler.sendMessage(toMain);
					} else {
						mainHandler.sendEmptyMessage(MSG_ERROR);
					}
				}
			}
		});

		childThread.start();
		childHandler = childThread.getHandler();
		app.showProgressBar();
		childHandler.sendEmptyMessage(MSG_GET); // 让childThread获取已有的评论列表
		setListener();
	}

	private void setView() {
		commentListView = (ListView) findViewById(R.id.comment_listview);
		commentAdapter = new CommentAdapter(Comment.this, listItems);
		commentListView.setAdapter(commentAdapter);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.height = GlobalVariables.screenHeight / 10;
		findViewById(R.id.header).setLayoutParams(params);

		editText = (EditText) findViewById(R.id.comment_edittext);
	}

	private void setListener() {
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				app.activities.remove(this);
				app.cancelProgressBar();
				finish();
			}
		});
		/*
		 * 点击发送按钮，将短信框内的评论发送出去
		 */
		findViewById(R.id.comment_send).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						String commentContent = editText.getText().toString();
						/*
						 * 如果用户所填文本不为空，才执行发送操作
						 */
						if (!("".equalsIgnoreCase(commentContent))) {
							app.showProgressBar();
							Message toChild = childHandler
									.obtainMessage(MSG_UPLOAD);
							Toast.makeText(getApplicationContext(),
									commentContent, Toast.LENGTH_LONG).show();
							toChild.obj = commentContent;
							childHandler.sendMessage(toChild);
						}
					}
				});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			app.activities.remove(this);
			app.cancelProgressBar();
			finish();
		}
		return true;
	}

}

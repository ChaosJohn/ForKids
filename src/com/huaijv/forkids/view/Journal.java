package com.huaijv.forkids.view;

import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huaijv.forkids.R;
import com.huaijv.forkids.utils.ChildThread;
import com.huaijv.forkids.utils.ChildThread.WorkForMain;
import com.huaijv.forkids.utils.JsonUtils;
import com.huaijv.forkids.utils.JsoupUtils;
import com.huaijv.forkids.utils.NetUtils;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;

/**
 * Journal[activity]: 活动日志详情界面
 * 
 * @author chaos
 * 
 */
public class Journal extends Activity {

	final UMSocialService mController = UMServiceFactory.getUMSocialService(
			"com.umeng.share", RequestType.SOCIAL);

	private TextView titleTextView = null;
	private LinearLayout layout = null;
	private Handler mainHandler, childHandler;
	private ChildThread childThread;

	private GlobalApplication app = null;
	private List<Map<String, Object>> listItems = null;
	/*
	 * actId: 活动日志ID
	 */
	private String actId = null;
	private Map<String, Object> contentMap = null;

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_journal);

		Bundle bundle = this.getIntent().getExtras();
		actId = bundle.getString("actId");
		Toast.makeText(Journal.this, actId, Toast.LENGTH_SHORT).show();
		app = (GlobalApplication) this.getApplication();
		if (!app.activities.contains(this))
			app.activities.add(this);

		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				app.activities.remove(this);
				finish();
			}
		});
		titleTextView = (TextView) findViewById(R.id.journal_detail_title);
		titleTextView.setText("");
		layout = (LinearLayout) findViewById(R.id.content_layout);
		/*
		 * 点击评论按钮进入评论activity
		 */
		findViewById(R.id.comment_btn).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(Journal.this, Comment.class);
						intent.putExtra("actId", actId);
						intent.putExtra("from", "Journal");
						startActivity(intent);
					}
				});

		/*
		 * 点击分享按钮弹出分享界面
		 */
		findViewById(R.id.journal_share_btn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mController.setShareContent("--来自[家园宝]");
						mController.getConfig().removePlatform(
								SHARE_MEDIA.DOUBAN);
						mController.openShare(Journal.this, false);
					}
				});

		mainHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (app.MSG_GET == msg.what) {
					contentMap = (Map<String, Object>) msg.obj;
					/*
					 * 将获取到的元数据转换成图文混排的list
					 */
					List<Map<String, Object>> textList = JsoupUtils.html2List(
							contentMap.get("content").toString(), Journal.this);
					/*
					 * 遍历textList，动态向内容区域内添加文本和图片
					 */
					for (int i = 0; i < textList.size(); i++) {
						if (textList.get(i).containsKey("img")) {
							layout.addView((ImageView) textList.get(i).get(
									"imageview"));
						} else if (textList.get(i).containsKey("p")) {
							TextView textView = new TextView(Journal.this);
							textView.setText(textList.get(i).get("p")
									.toString());
							textView.setTextColor(Color.parseColor("#000000"));
							textView.setTextSize(18);
							textView.setLineSpacing(0, (float) 1.1);
							layout.addView(textView);
						}
					}
					titleTextView.setText(contentMap.get("title").toString());
					Toast.makeText(Journal.this, "ok", Toast.LENGTH_SHORT)
							.show();
				} else if (app.MSG_ERROR == msg.what) {
					Toast.makeText(Journal.this, "err", Toast.LENGTH_SHORT)
							.show();
				}
				app.cancelProgressBar();
			}
		};

		childThread = new ChildThread(new WorkForMain() {
			@Override
			public void doJob(Message msg) {
				if (app.MSG_GET == msg.what) {
					String rev = NetUtils.getDataByUrl(
							"http://huaijv-sap.eicp.net:8088/forkids/kidclassactivitys/"
									+ actId, app.getLoginString());
					if ("err".equalsIgnoreCase(rev)) {
						mainHandler.sendEmptyMessage(app.MSG_ERROR);
					} else {
						Message toMain = mainHandler.obtainMessage(app.MSG_GET);
						try {
							toMain.obj = JsonUtils.jsonObjectString2Map(rev);
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
		 * 让childThread获取活动日志的详情
		 */
		childHandler.sendEmptyMessage(app.MSG_GET);
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

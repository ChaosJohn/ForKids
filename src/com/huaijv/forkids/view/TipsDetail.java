package com.huaijv.forkids.view;

import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.app.Activity;
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
 * TipsDetail[activity]: 育英知识详情界面
 * 
 * @author chaos
 * 
 */
public class TipsDetail extends Activity {

	final UMSocialService mController = UMServiceFactory.getUMSocialService(
			"com.umeng.share", RequestType.SOCIAL);
	private GlobalApplication app = null;
	private String knId = null;
	private ChildThread childThread = null;
	private Handler mainHandler = null, childHandler = null;
	private Map<String, Object> contentMap = null;
	private LinearLayout layout = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tips_detail);

		knId = this.getIntent().getExtras().getString("knId");
		app = (GlobalApplication) this.getApplication();
		if (!app.activities.contains(this))
			app.activities.add(this);

		mainHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (app.MSG_ERROR == msg.what) {
					Toast.makeText(getApplicationContext(), "err",
							Toast.LENGTH_SHORT).show();
				} else {
					setContent();
				}
				app.cancelProgressBar();
			}
		};

		childThread = new ChildThread(new WorkForMain() {

			@Override
			public void doJob(Message msg) {
				if (app.MSG_GET == msg.what) {
					String rev = NetUtils.getDataByUrl(
							"http://huaijv-sap.eicp.net:8088/forkids/kidnursingknowledges/"
									+ knId, app.getLoginString());
					if ("err".equalsIgnoreCase(rev)) {
						mainHandler.sendEmptyMessage(app.MSG_ERROR);
					} else {
						try {
							contentMap = JsonUtils.jsonObjectString2Map(rev);
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
		 * 让childThread从服务器根据id获取育英知识详情
		 */
		app.showProgressBar();
		childHandler.sendEmptyMessage(app.MSG_GET);

		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				app.activities.remove(this);
				finish();
			}
		});

		findViewById(R.id.share_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mController.setShareContent("--来自[家园宝]");
				mController.getConfig().removePlatform(SHARE_MEDIA.DOUBAN);
				mController.openShare(TipsDetail.this, false);
			}
		});

	}

	/**
	 * setContent: 将从服务器获取的数据组织程图文混排的格式显示出来
	 */
	private void setContent() {
		layout = (LinearLayout) findViewById(R.id.content_layout);
		List<Map<String, Object>> textList = JsoupUtils.html2List(contentMap
				.get("content").toString(), TipsDetail.this);
		for (int i = 0; i < textList.size(); i++) {
			if (textList.get(i).containsKey("img")) {
				layout.addView((ImageView) textList.get(i).get("imageview"));
			} else if (textList.get(i).containsKey("p")) {
				TextView textView = new TextView(TipsDetail.this);
				textView.setText(textList.get(i).get("p").toString());
				textView.setTextColor(Color.parseColor("#000000"));
				textView.setTextSize(18);
				textView.setLineSpacing(0, (float) 1.1);
				layout.addView(textView);
			}
		}
		((TextView) TipsDetail.this.findViewById(R.id.tips_detail_title))
				.setText(contentMap.get("title").toString());
		/*
		 * 设置点赞次数
		 */
		String applaudCount = contentMap.get("applaudCount").toString();
		applaudCount = "null".equalsIgnoreCase(applaudCount) ? "0"
				: applaudCount.toString();
		((TextView) TipsDetail.this.findViewById(R.id.like_count))
				.setText(applaudCount);
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

package com.huaijv.forkids.utils;

import java.util.Map;

import org.json.JSONException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.huaijv.forkids.view.Messages;

/**
 * MyReceiver: 定义消息推送收到后,用户从notification bar中点击消息的跳转事件
 * 
 * @author chaos
 * 
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "MyReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		Log.d(TAG, "onReceive-" + intent.getAction() + ",extras:");
		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			String regId = bundle
					.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			Log.d(TAG, "接收Registration Id:" + regId);

		} else if (JPushInterface.ACTION_UNREGISTER.equals(intent.getAction())) {
			String regId = bundle
					.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			Log.d(TAG, "接收Registration Id:" + regId);

		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
				.getAction())) {
			Log.d(TAG,
					"接收到推送下来的自定义消息:"
							+ bundle.getString(JPushInterface.EXTRA_MESSAGE));
		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
				.getAction())) {
			Log.d(TAG, "接收到推送下来的通知:");
			int notificationId = bundle
					.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			Log.d(TAG, "接收到推送下来的通知的ID:" + notificationId);
		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
				.getAction())) {
			Log.d(TAG, "用户点击打开了通知");
			// 打开自定义的Activity
			Toast.makeText(context,
					bundle.getString(JPushInterface.EXTRA_EXTRA),
					Toast.LENGTH_LONG).show();
			try {
				Map<String, Object> map = JsonUtils.jsonObjectString2Map(bundle
						.getString(JPushInterface.EXTRA_EXTRA));
				String type = map.get("type").toString();
				/*
				 * 根据不同的type的值进入不同的activity
				 */
				if (type.equalsIgnoreCase("announcement")) {
					Intent i = new Intent(context, Messages.class);
					i.putExtras(bundle);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(i);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			Log.d(TAG, "Unhandled intent-" + intent.getAction());
		}
	}

}

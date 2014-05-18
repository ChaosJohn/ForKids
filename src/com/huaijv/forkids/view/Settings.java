package com.huaijv.forkids.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.huaijv.forkids.R;
import com.huaijv.forkids.model.GlobalVariables;
import com.huaijv.forkids.utils.CreateGlobalMenu;
import com.huaijv.forkids.utils.OtherUtils;
import com.slidingmenu.lib.SlidingMenu;

/**
 * Settings[activity]: 设置界面
 * 
 * @author chaos
 * 
 */
public class Settings extends Activity {
	private SlidingMenu menu = null;
	private CheckBox checkBox;
	private Button AboutUs_Btn;
	private Button Exit_Btn;
	private GlobalApplication app = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		app = (GlobalApplication) this.getApplication();
		if (!app.activities.contains(this))
			app.activities.add(this);
		checkBox = (CheckBox) findViewById(R.id.setting_checkbox);
		AboutUs_Btn = (Button) findViewById(R.id.aboutus_btn);
		Exit_Btn = (Button) findViewById(R.id.exit_btn);

		/*
		 * 设置app是否开启消息推送
		 */
		checkBox.setOnCheckedChangeListener(new CheckBoxListener());
		if (null == OtherUtils.loadFromFile("/sdcard/forkids/PushOrNot")) {
			OtherUtils.saveToFile("/sdcard/forkids/PushOrNot", "1");
			checkBox.setChecked(true);
		} else {
			String checkStatus = (String) OtherUtils
					.loadFromFile("/sdcard/forkids/PushOrNot");
			checkBox.setChecked("1".equalsIgnoreCase(checkStatus) ? true
					: false);
		}

		/*
		 * 点击进入“关于我们”界面
		 */
		AboutUs_Btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(Settings.this, AboutUs.class);
				startActivity(intent);

			}
		});
		Exit_Btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				app.exitAll();
			}
		});
		menu = CreateGlobalMenu.returnGlobalMenu(Settings.this);
		Button navigationButton = (Button) findViewById(R.id.navigation_btn);
		navigationButton.setHeight(GlobalVariables.screenWidth / 9);
		navigationButton.setWidth(GlobalVariables.screenWidth / 9);
		navigationButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				menu.toggle();
			}
		});
	}

	// private void exit2() {
	//
	// ActivityManager actMgr = (ActivityManager) this
	// .getSystemService(ACTIVITY_SERVICE);
	//
	// actMgr.restartPackage(getPackageName());
	// }

	/**
	 * CheckBoxListener: “是否接受推送消息”选中框的监听事件
	 * 
	 * @author chaos
	 * 
	 */
	class CheckBoxListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (isChecked) {
				Toast.makeText(Settings.this, "您将能收到推送消息", Toast.LENGTH_SHORT)
						.show();
				OtherUtils.saveToFile("/sdcard/forkids/PushOrNot", "1");
				JPushInterface.resumePush(getApplicationContext());

			} else {
				Toast.makeText(Settings.this, "您将收不到推送消息", Toast.LENGTH_SHORT)
						.show();
				OtherUtils.saveToFile("/sdcard/forkids/PushOrNot", "0");
				JPushInterface.stopPush(getApplicationContext());
			}

		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(Settings.this, Homepage.class);
			startActivity(intent);
			app.activities.remove(this);
			finish();
		}
		return true;
	}

}

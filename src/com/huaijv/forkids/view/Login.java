package com.huaijv.forkids.view;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.huaijv.forkids.R;
import com.huaijv.forkids.utils.OtherUtils;

/**
 * Login[activity]: 登录界面
 * 
 * @author chaos
 * 
 */
public class Login extends Activity {

	private GlobalApplication app = null;
	private EditText nameEditText = null;
	private EditText passwordEditText = null;
	private Button loginBtn = null;
	private String name = null;
	private String password = null;
	private String loginInfoString = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		app = (GlobalApplication) this.getApplication();
		if (!app.activities.contains(this))
			app.activities.add(this);
		nameEditText = (EditText) findViewById(R.id.login_name);
		passwordEditText = (EditText) findViewById(R.id.login_password);
		loginBtn = (Button) findViewById(R.id.login_btn);

		/*
		 * 点击登录按钮，实现登录操作，并且将登录信息（权限信息）存入本地文件
		 */
		loginBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				name = nameEditText.getText().toString();
				password = passwordEditText.getText().toString();
				loginInfoString = name + ":" + password;
				String dirSrc = "/mnt/sdcard/forkids/";
				File fileDir = new File(dirSrc);
				if (!fileDir.exists())
					fileDir.mkdir();
				OtherUtils.saveToFile("/sdcard/forkids/account",
						loginInfoString);
				if (loginInfoString.equalsIgnoreCase((String) OtherUtils
						.loadFromFile("/sdcard/forkids/account"))) {
					app.setLoginString(loginInfoString);
					Intent intent = new Intent(Login.this, Welcome.class);
					startActivity(intent);
					app.activities.remove(this);
					app.cancelProgressBar();
					finish();
				}
			}
		});

	}

}

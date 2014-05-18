package com.huaijv.forkids.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.huaijv.forkids.R;

/**
 * AboutUs[activity]: 关于我们
 * 
 * @author chaos
 * 
 */
public class AboutUs extends Activity {
	private Button back_btn;
	private GlobalApplication app = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);

		app = (GlobalApplication) this.getApplication();
		if (!app.activities.contains(this))
			app.activities.add(this);

		back_btn = (Button) findViewById(R.id.back_btn);
		back_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				app.activities.remove(this);
				// app.cancelProgressBar();
				finish();
			}
		});
	}
}

package com.huaijv.forkids.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;

import com.huaijv.forkids.R;
import com.huaijv.forkids.model.GlobalVariables;
import com.huaijv.forkids.model.TrainingHomeItem;
import com.huaijv.forkids.utils.CreateGlobalMenu;
import com.huaijv.forkids.viewElems.TrainingHomeAdapter;
import com.slidingmenu.lib.SlidingMenu;

/**
 * Training[activity]: 培训机构首页，分组显示(暂时是假数据)
 * 
 * @author chaos
 * 
 */
public class Training extends Activity {

	private SlidingMenu menu = null;
	private GridView gridListView;
	private TrainingHomeAdapter trainingMainAdapter;
	private List<Map<String, Object>> mainListItems;

	private String urlString = "http://huaijv-sap.eicp.net:8088/forkids/";

	private TrainingHomeItem[] mainDatas = {
			new TrainingHomeItem(urlString + "images1.jpg", "美术"),
			new TrainingHomeItem(urlString + "images3.jpg", "书法"),
			new TrainingHomeItem(urlString + "images5.jpg", "相声"),
			new TrainingHomeItem(urlString + "images7.jpg", "舞蹈"),
			new TrainingHomeItem(urlString + "images9.jpg", "唱歌"),
			new TrainingHomeItem(urlString + "images11.jpg", "画画"),
			new TrainingHomeItem(urlString + "images13.jpg", "表演"),
			new TrainingHomeItem(urlString + "images6.jpg", "话剧") };

	private GlobalApplication app = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_training);

		app = (GlobalApplication) this.getApplication();
		if (!app.activities.contains(this))
			app.activities.add(this);

		System.out.print("start");
		mainListItems = getMainListItems();
		gridListView = (GridView) findViewById(R.id.list_gridView);
		trainingMainAdapter = new TrainingHomeAdapter(this, mainListItems);

		gridListView.setAdapter(trainingMainAdapter);

		gridListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(Training.this, TrainingGroup.class);
				startActivity(intent);
			}
		});

		menu = CreateGlobalMenu.returnGlobalMenu(Training.this);
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

	private List<Map<String, Object>> getMainListItems() {
		List<Map<String, Object>> mainListItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < mainDatas.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("image", mainDatas[i].getImage());
			map.put("title", mainDatas[i].getTitle());
			mainListItems.add(map);
		}
		return mainListItems;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			startActivity(new Intent(Training.this, Homepage.class));
			app.activities.remove(this);
			app.cancelProgressBar();
			finish();
		}
		return true;
	}

}

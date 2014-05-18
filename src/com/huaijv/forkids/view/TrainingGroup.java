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
import android.widget.ListView;

import com.huaijv.forkids.R;
import com.huaijv.forkids.model.TrainingGroupItem;
import com.huaijv.forkids.viewElems.TrainingGroupAdapter;

/**
 * TrainingGroup[activity]: 培训机构某个分组下的列表显示(暂时是假数据)
 * 
 * @author chaos
 * 
 */
public class TrainingGroup extends Activity {

	private ListView listView;
	private TrainingGroupAdapter trainingAdapter;
	private List<Map<String, Object>> listItems;

	private String urlStrings = "http://huaijv-sap.eicp.net:8088/forkids/";

	private TrainingGroupItem[] datas = {
			new TrainingGroupItem(urlStrings + "images1.jpg", "教育机构1", "美术"),
			new TrainingGroupItem(urlStrings + "images3.jpg", "教育机构2", "画画"),
			new TrainingGroupItem(urlStrings + "images5.jpg", "教育机构3", "书法"),
			new TrainingGroupItem(urlStrings + "images7.jpg", "教育机构4", "跳舞"),
			new TrainingGroupItem(urlStrings + "images9.jpg", "教育机构5", "唱歌"),
			new TrainingGroupItem(urlStrings + "images11.jpg", "教育机构6", "话剧"),
			new TrainingGroupItem(urlStrings + "images13.jpg", "教育机构7", "相声"),
			new TrainingGroupItem(urlStrings + "images6.jpg", "教育机构8", "表演"), };

	private GlobalApplication app = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_training_group);

		app = (GlobalApplication) this.getApplication();
		// app.cancelProgressBar();
		if (!app.activities.contains(this))
			app.activities.add(this);

		listItems = getListItems();
		listView = (ListView) findViewById(R.id.training_listview);
		trainingAdapter = new TrainingGroupAdapter(this, listItems);
		listView.setAdapter(trainingAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),
						TrainingContent.class);
				startActivity(intent);
			}
		});

		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	}

	private List<Map<String, Object>> getListItems() {
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < datas.length; i++) {

			Map<String, Object> map = new HashMap<String, Object>();

			// ImageView iconImageView = new ImageView(TrainingGroup.this);
			// new
			// DownLoadImageWithCache(iconImageView).execute(datas[i].getIcon());
			map.put("icon", /* iconImageView */datas[i].getIcon());
			map.put("name", datas[i].getName());

			map.put("species", datas[i].getSpecies());

			listItems.add(map);

		}
		return listItems;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// Intent intent = new Intent(Intent.ACTION_MAIN);
			// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// intent.addCategory(Intent.CATEGORY_HOME);
			// startActivity(intent);
			app.activities.remove(this);
			app.cancelProgressBar();
			finish();
		}
		return true;
	}

}

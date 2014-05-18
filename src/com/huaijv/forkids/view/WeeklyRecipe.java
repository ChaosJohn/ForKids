package com.huaijv.forkids.view;

import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.huaijv.forkids.R;
import com.huaijv.forkids.model.GlobalVariables;
import com.huaijv.forkids.utils.ChildThread;
import com.huaijv.forkids.utils.ChildThread.WorkForMain;
import com.huaijv.forkids.utils.CreateGlobalMenu;
import com.huaijv.forkids.utils.JsonUtils;
import com.huaijv.forkids.utils.NetUtils;
import com.huaijv.forkids.utils.OtherUtils;
import com.slidingmenu.lib.SlidingMenu;

/**
 * WeeklyRecipe[activity]: 每周食谱界面
 * 
 * @author chaos
 * 
 */
public class WeeklyRecipe extends Activity {

	private SlidingMenu menu = null;

	private String[] spinnerStr = { "第一周", "第二周", "第三周", "第四周", "第五周", "第六周",
			"第七周", "第八周", "第九周", "第十周", "第十一周", "第十二周", "第十三周", "第十四周", "第十五周",
			"第十六周", "第十七周", "第十八周", "第十九周", "第二十周", };

	private ListView recipeListView = null;
	private RecipeListViewAdapter recipeListViewAdapter = null;
	private List<Map<String, Object>> listItems = null;

	private GlobalApplication app = null;
	private Handler mainHandler = null, childHandler = null;
	private ChildThread childThread = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weekly_recipe);

		app = (GlobalApplication) this.getApplication();
		if (!app.activities.contains(this))
			app.activities.add(this);

		recipeListView = (ListView) findViewById(R.id.listview_recipes);

		mainHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (app.MSG_ERROR == msg.what) {
					Toast.makeText(WeeklyRecipe.this, "err", Toast.LENGTH_LONG)
							.show();
				} else if (app.MSG_GET == msg.what) {
					recipeListViewAdapter = new RecipeListViewAdapter(
							WeeklyRecipe.this, listItems);
					recipeListView.setAdapter(recipeListViewAdapter);
					recipeListView.setSelection(3);
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
									"http://huaijv-sap.eicp.net:8088/forkids/kidfoods?from=Parent",
									app.getLoginString());
					if ("err".equalsIgnoreCase(rev)) {
						mainHandler.sendEmptyMessage(app.MSG_GET);
					} else {
						try {
							listItems = JsonUtils.jsonArray2List(rev);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						int size = listItems.size();
						for (int i = size - 1; i >= 0; i--) {
							if (i >= 5) {
								listItems.remove(i);
							}
						}
						mainHandler.sendEmptyMessage(app.MSG_GET);
					}
				}
			}
		});
		childThread.start();
		childHandler = childThread.getHandler();
		/*
		 * 让childThread从服务器获取每周食谱的数据
		 */
		app.showProgressBar();
		childHandler.sendEmptyMessage(app.MSG_GET);

		/*
		 * 周选择下拉框
		 */
		Spinner spinner = (Spinner) findViewById(R.id.week_spinner);
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
				this, android.R.layout.simple_spinner_item, spinnerStr);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());

		menu = CreateGlobalMenu.returnGlobalMenu(WeeklyRecipe.this);
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

	public class MyOnItemSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			app.showProgressBar();
			childHandler.sendEmptyMessage(app.MSG_GET);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}

	}

	public class RecipeListViewAdapter extends BaseAdapter {

		// 上下文
		private Context context = null;
		// 每周食谱集合
		private List<Map<String, Object>> listItems = null;
		// 视图容器
		private LayoutInflater listContainer = null;

		// 自定义控件集合
		private final class RecipeViewItem {
			public TextView weekdayName;
			public TextView breakfast;
			public TextView lunch;
			public TextView supper;
			public LinearLayout weekdayLayout;
		}

		private final String[] weekdayNames = { "周一", "周二", "周三", "周四", "周五", };

		// 构造函数
		public RecipeListViewAdapter(Context context,
				List<Map<String, Object>> listItems) {
			this.context = context;
			listContainer = LayoutInflater.from(this.context);
			this.listItems = listItems;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listItems.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			// arg0 => position
			// arg1 => convertView
			// arg2 => parent

			Log.e("method", "getView()");

			RecipeViewItem recipeViewItem = null;
			if (arg1 == null) {
				recipeViewItem = new RecipeViewItem();
				arg1 = listContainer.inflate(
						R.layout.weekly_recipe_listview_item, null);
				recipeViewItem.weekdayName = (TextView) arg1
						.findViewById(R.id.textview_weekday);
				recipeViewItem.breakfast = (TextView) arg1
						.findViewById(R.id.textview_breakfast);
				recipeViewItem.lunch = (TextView) arg1
						.findViewById(R.id.textview_lunch);
				recipeViewItem.supper = (TextView) arg1
						.findViewById(R.id.textview_supper);
				recipeViewItem.weekdayLayout = (LinearLayout) arg1
						.findViewById(R.id.weekday_layout);
				arg1.setTag(recipeViewItem);
			} else {
				recipeViewItem = (RecipeViewItem) arg1.getTag();
			}
			recipeViewItem.weekdayName.setText(weekdayNames[arg0]);
			recipeViewItem.breakfast.setText((String) listItems.get(arg0).get(
					"breakfast"));
			recipeViewItem.lunch.setText((String) listItems.get(arg0).get(
					"lunch"));
			recipeViewItem.supper.setText((String) listItems.get(arg0).get(
					"supper"));
			String colorString = null;
			colorString = OtherUtils.getColorByPosition(arg0);
			recipeViewItem.weekdayLayout.setBackgroundColor(Color
					.parseColor(colorString));
			return arg1;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			startActivity(new Intent(WeeklyRecipe.this, Homepage.class));
			app.cancelProgressBar();
			app.activities.remove(this);
			finish();
		}
		return true;
	}

}

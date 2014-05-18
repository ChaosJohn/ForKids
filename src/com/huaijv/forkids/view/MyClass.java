package com.huaijv.forkids.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.huaijv.forkids.R;
import com.huaijv.forkids.model.GlobalVariables;
import com.huaijv.forkids.utils.ChildThread;
import com.huaijv.forkids.utils.ChildThread.WorkForMain;
import com.huaijv.forkids.utils.CreateGlobalMenu;
import com.huaijv.forkids.utils.JsonUtils;
import com.huaijv.forkids.utils.NetUtils;
import com.huaijv.forkids.utils.OtherUtils;
import com.slidingmenu.lib.SlidingMenu;

public class MyClass extends Activity {

	private GlobalApplication app = null;
	private SlidingMenu menu = null;
	private ViewPager viewPager;
	private ImageView slidingBlock;
	private List<View> views;
	private int offset = 0;// 偏移量
	private int currentIndex = 0;// 默认页面卡
	private int slidingBlockWidth/* , bmpW2 */;// 小横线的宽度
	private String colorString = null;
	private List<Map<String, Object>> listItems = null;

	private Handler mainHandler, childHandler;
	private ChildThread childThread;
	private int MSG_PLAN = 1;
	private int MSG_LIST = 0;
	private List<String> learnsList = null;

	private String[] spinnerStr = { "第一周", "第二周", "第三周", "第四周", "第五周", "第六周",
			"第七周", "第八周", "第九周", "第十周", "第十一周", "第十二周", "第十三周", "第十四周", "第十五周",
			"第十六周", "第十七周", "第十八周", "第十九周", "第二十周", };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myclass_home);
		InitImageView();
		InitTextView();
		app = (GlobalApplication) this.getApplication();
		if (!app.activities.contains(this))
			app.activities.add(this);

		mainHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (MSG_LIST == msg.what) {
					try {
						listItems = JsonUtils
								.jsonArray2List(msg.obj.toString()/* , keys */);
						InitViewPager();
						app.cancelProgressBar();

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else if (MSG_PLAN == msg.what) {
					((TextView) MyClass.this.findViewById(R.id.plan_content))
							.setText(msg.obj.toString());
					/*
					 * 继续让childThread获取活动日志
					 */
					childHandler.sendEmptyMessage(MSG_LIST);
				}
			}
		};

		childThread = new ChildThread(new WorkForMain() {

			@Override
			public void doJob(Message msg) {
				Message toMain = mainHandler.obtainMessage();
				if (MSG_LIST == msg.what) {
					/*
					 * 获取活动日志
					 */
					String dataString = NetUtils
							.getDataByUrl(
									"http://huaijv-sap.eicp.net:8088/forkids/kidclassactivitys?from=Parent",
									app.getLoginString());
					if ("err".equalsIgnoreCase(dataString)) {
						mainHandler.sendEmptyMessage(app.MSG_ERROR);
					} else {
						toMain.obj = dataString;
						toMain.what = MSG_LIST;
						mainHandler.sendMessage(toMain);
					}
				} else if (MSG_PLAN == msg.what) {
					/*
					 * 获取教学计划[包括了教学重点和学习活动列表]
					 */
					String rev = NetUtils
							.getDataByUrl(
									"http://huaijv-sap.eicp.net:8088/forkids/kidclasscourses?from=Parent",
									app.getLoginString());
					if ("err".equalsIgnoreCase(rev)) {
						mainHandler.sendEmptyMessage(app.MSG_ERROR);
					} else {
						try {
							List<Map<String, Object>> revList = JsonUtils
									.jsonArray2List(rev);
							toMain.obj = revList.get(0).get("mainGoal")
									.toString();

							// //增加的learnlist

							learnsList = new ArrayList<String>();
							for (int i = 1; i <= 12; i++) {
								String learn = revList.get(0).get("learn" + i)
										.toString();
								if (!"null".equalsIgnoreCase(learn)) {
									learnsList.add(learn);
								}
							}
							toMain.what = MSG_PLAN;
							mainHandler.sendMessage(toMain);
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
		 * 让childThread从服务器获取教学计划[包括了教学重点和学习活动列表]
		 */
		app.showProgressBar();
		childHandler.sendEmptyMessage(MSG_PLAN);

		/*
		 * “周选择下拉框”
		 */
		Spinner spinner = (Spinner) findViewById(R.id.myclass_week_spinner);
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
				this, android.R.layout.simple_spinner_item, spinnerStr);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());

		menu = CreateGlobalMenu.returnGlobalMenu(MyClass.this);

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

	/**
	 * InitViewPager: 将需要的数据添加到viewPager中，支持左右滑动
	 */
	private void InitViewPager() {
		viewPager = (ViewPager) findViewById(R.id.vPager);
		views = new ArrayList<View>();
		LayoutInflater inflater = getLayoutInflater();
		/*
		 * 添加活动日志
		 */
		for (int i = 0; i < 5; i++) {
			View view = inflater.inflate(R.layout.myclass_plan_empty, null);
			LinearLayout layout = (LinearLayout) view
					.findViewById(R.id.myclass_plan_layout);
			View typeView = inflater.inflate(R.layout.myclass_plan_type, null);
			((TextView) typeView.findViewById(R.id.myclass_plan_item_type))
					.setText("学习活动");
			typeView.findViewById(R.id.myclass_plan_item_type)
					.setBackgroundColor(Color.parseColor("#EBEAE6"));
			layout.addView(typeView);

			for (int j = 0; j < learnsList.size(); j++) {
				String rawString = learnsList.get(j);
				String[] rawStrings = rawString.split("\\$");
				View v = inflater.inflate(R.layout.myclass_plan_journal, null);
				((TextView) v.findViewById(R.id.myclass_plan_item_content))
						.setText(rawStrings[0] + ": " + rawStrings[1]);
				v.findViewById(R.id.myclass_plan_item_content).setPadding(0,
						13, 0, 13);
				((TextView) v.findViewById(R.id.myclass_plan_item_time))
						.setVisibility(View.GONE);
				colorString = OtherUtils.getColorByPosition(j);
				v.findViewById(R.id.myclass_plan_color_layout)
						.setBackgroundColor(Color.parseColor(colorString));
				v.setTag(rawStrings[1]);
				v.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						String tag = (String) v.getTag();
						Uri url = Uri.parse("http://www.baidu.com/baidu?word="
								+ tag);
						startActivity(new Intent(Intent.ACTION_VIEW, url));
					}
				});
				layout.addView(v);
			}

			typeView = inflater.inflate(R.layout.myclass_plan_type, null);
			((TextView) typeView.findViewById(R.id.myclass_plan_item_type))
					.setText("活动日志");
			typeView.findViewById(R.id.myclass_plan_item_type)
					.setBackgroundColor(Color.parseColor("#EBEAE6"));
			layout.addView(typeView);
			/*
			 * 添加活动日志
			 */
			for (int j = 0; j < listItems.size(); j++) {
				View v = inflater.inflate(R.layout.myclass_plan_journal, null);
				((TextView) v.findViewById(R.id.myclass_plan_item_content))
						.setText(listItems.get(j).get("title").toString());
				String timeChangedString = listItems.get(j).get("changeAt")
						.toString();
				String timeCreatedString = listItems.get(j).get("createAt")
						.toString();
				if (!(timeChangedString.equalsIgnoreCase("null") && timeCreatedString
						.equalsIgnoreCase("null"))) {
					String timeString = (timeChangedString
							.equalsIgnoreCase("null")) ? timeCreatedString
							: timeChangedString;
					String[] timeStrings = timeString.split(" ");
					((TextView) v.findViewById(R.id.myclass_plan_item_time))
							.setText(timeStrings[1]);
				}
				colorString = OtherUtils.getColorByPosition(j);
				v.findViewById(R.id.myclass_plan_color_layout)
						.setBackgroundColor(Color.parseColor(colorString));
				v.setTag(listItems.get(j).get("actId"));
				v.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(MyClass.this, Journal.class);
						intent.putExtra("actId", v.getTag().toString());
						startActivity(intent);
					}
				});
				layout.addView(v);

			}

			views.add(view);
		}

		viewPager.setAdapter(new MyViewPagerAdapter(views));
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());

	}

	/**
	 * InitTextView: 设置点击viewPager上方的“周x“，切换viewPager到不同的工作日
	 */
	private void InitTextView() {
		findViewById(R.id.plan_mon).setOnClickListener(new MyOnClick(0));
		findViewById(R.id.plan_tue).setOnClickListener(new MyOnClick(1));
		findViewById(R.id.plan_wen).setOnClickListener(new MyOnClick(2));
		findViewById(R.id.plan_the).setOnClickListener(new MyOnClick(3));
		findViewById(R.id.plan_fri).setOnClickListener(new MyOnClick(4));
	}

	/**
	 * InitImageView: 设置指向不同工作日的滑块
	 */
	private void InitImageView() {
		slidingBlock = (ImageView) findViewById(R.id.iv_bottom_line);
		slidingBlockWidth = slidingBlock.getLayoutParams().width;
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels - 20;
		offset = (screenW / 5 - slidingBlockWidth) / 2;
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		slidingBlock.setImageMatrix(matrix);//
	}

	class MyOnClick implements OnClickListener {
		int index = 0;

		MyOnClick(int i) {
			this.index = i;
		}

		@Override
		public void onClick(View v) {
			viewPager.setCurrentItem(index);
		}

	}

	/**
	 * MyViewPagerAdapter: viewPager的适配器
	 * 
	 * @author chaos
	 * 
	 */
	class MyViewPagerAdapter extends PagerAdapter {
		private List<View> listViews;

		MyViewPagerAdapter(List<View> listViews) {
			this.listViews = listViews;

		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(listViews.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(listViews.get(position), 0);
			return listViews.get(position);
		}

		@Override
		public int getCount() {
			return listViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}

	/**
	 * MyOnPageChangeListener: 设置当viewPager切换时的监听时间
	 * 
	 * @author chaos
	 * 
	 */
	class MyOnPageChangeListener implements OnPageChangeListener {
		int one = offset * 2 + slidingBlockWidth;
		int two = offset * 2 + slidingBlockWidth;
		int three = offset * 2 + slidingBlockWidth;
		int fore = offset * 2 + slidingBlockWidth;
		int five = offset * 2;

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = new TranslateAnimation(one * currentIndex,
					one * arg0, 0, 0);
			currentIndex = arg0;
			animation.setFillAfter(true);
			animation.setDuration(300);
			slidingBlock.startAnimation(animation);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			startActivity(new Intent(MyClass.this, Homepage.class));
			app.activities.remove(this);
			app.cancelProgressBar();
			finish();
		}
		return true;
	}

	/**
	 * MyOnItemSelectedListener: 设置“周次选择”spinner的选中监听事件
	 * 
	 * @author chaos
	 * 
	 */
	public class MyOnItemSelectedListener implements OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			app.showProgressBar();
			childHandler.sendEmptyMessage(MSG_PLAN);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}

	}
}

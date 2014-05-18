package com.huaijv.forkids.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.huaijv.forkids.R;
import com.huaijv.forkids.model.FeedItem;
import com.huaijv.forkids.model.GlobalVariables;
import com.huaijv.forkids.utils.CreateGlobalMenu;
import com.huaijv.forkids.utils.DownLoadBackgroundImageWithCache;
import com.huaijv.forkids.utils.DownLoadImageWithCache;
import com.huaijv.forkids.utils.DownLoadSquareImageWithCache;
import com.huaijv.forkids.utils.OtherUtils;
import com.huaijv.forkids.viewElems.MyScrollView;
import com.huaijv.forkids.viewElems.MyScrollView.OnScrollListener;
import com.slidingmenu.lib.SlidingMenu;

/**
 * Homepage[activity]: 首页
 * 
 * @author chaos
 * 
 */
public class Homepage extends Activity implements OnScrollListener {

	private SlidingMenu menu = null;

	/**
	 * feeds: 假数据
	 */
	private FeedItem[] feeds = {
			new FeedItem("您的宝宝已入园", "8:30", "正在接触新的世界", 0),
			new FeedItem("今天的早点是", "9:40", "牛奶和手指饼干", 3),
			new FeedItem("开饭了～", "11:30", "今天的午饭是：米饭 糖醋排骨 蛋汤", 3),
			new FeedItem("精彩瞬间", "13:40", "宝宝们的精彩活动～", 4),
			new FeedItem("放学了～", "16:00", "您的宝宝已出园\n路上注意安全呀～", 0),
			new FeedItem("今日学习内容", "16:20", "学习讲三只小猪的故事；与小伙伴们玩了老鹰捉小鸡的游戏", 2), };

	private List<Map<String, Object>> listItems = null;
	private LayoutInflater listContainer = null;
	private String colorString = null;
	/*
	 * state: 判断程序是否崩溃过
	 */
	private int state = 0;

	private final class FeedViewItem {
		public TextView feedTime;
		public TextView feedContent;
		public ImageView imageView1;
		public ImageView imageView2;
		public ImageView imageView3;
		public LinearLayout linearLayout;
		public LinearLayout colorLayout;
	}

	private LinearLayout listLayout = null;

	private MyScrollView scrollView = null;
	private LinearLayout topInfoBar = null;
	private LinearLayout floatInfoBar = null;
	private LinearLayout schoolLogo = null;
	private GlobalApplication app = null;
	private long exitTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_homepage);

		app = (GlobalApplication) this.getApplication();
		if (!app.activities.contains(this))
			app.activities.add(this);

		scrollView = (MyScrollView) findViewById(R.id.homepage_scrollview);
		topInfoBar = (LinearLayout) findViewById(R.id.homepage_top_info_bar);
		floatInfoBar = (LinearLayout) findViewById(R.id.homepage_float_info_bar);
		schoolLogo = (LinearLayout) findViewById(R.id.school_logo);
		scrollView.setOnScrollListener(this);
		findViewById(R.id.parent_layout).getViewTreeObserver()
				.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						onScroll(scrollView.getScrollY());
						System.out.println(scrollView.getScrollY());
					}
				});

		try {
			/*
			 * 验证程序是否出现过崩溃，若崩溃过，则Application的数据也会消失，所以要重启app
			 */
			System.out.println(app.basicInfoMap.get("portrait").toString());
			state = 1;
		} catch (Exception e) {
			/*
			 * 重启ap
			 */
			int activityLength = app.activities.size();
			for (int i = activityLength - 1; i >= 0; i--) {
				if (app.activities.get(i) != Homepage.this) {
					Activity toExitActivity = app.activities.remove(i);
					toExitActivity.finish();
				}
			}
			startActivity(new Intent(this, Welcome.class));
			finish();
		}
		if (state == 1) {
			/*
			 * 设置小孩子头像/学校logo/小孩子姓名
			 */
			new DownLoadSquareImageWithCache(
					(ImageView) topInfoBar.findViewById(R.id.avatar))
					.execute(app.basicInfoMap.get("portrait").toString());

			((TextView) topInfoBar.findViewById(R.id.kid_name))
					.setText(app.basicInfoMap.get("stuName").toString());
			((TextView) topInfoBar.findViewById(R.id.weather_content))
					.setText(app.weatherInfo);
			/*
			 * 点击天气则调用系统内置浏览器进入中国天气网
			 */
			topInfoBar.findViewById(R.id.weather_content).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri
									.parse(app.weatherUrl)));
						}
					});
			new DownLoadBackgroundImageWithCache(schoolLogo)
					.execute(app.basicInfoMap.get("logoUrl").toString());

			listItems = getFeedList();
			setListContent();

			menu = CreateGlobalMenu.returnGlobalMenu(Homepage.this);
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
	}

	private List<Map<String, Object>> getFeedList() {
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < feeds.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("feedTitle", feeds[i].getTitleString());
			map.put("feedTime", feeds[i].getTimeString());
			map.put("feedContent", feeds[i].getContentString());
			map.put("image1", feeds[i].getImage1());
			map.put("image2", feeds[i].getImage2());
			map.put("image3", feeds[i].getImage3());
			map.put("typeId", feeds[i].getClassId());
			listItems.add(map);
		}
		return listItems;
	}

	/**
	 * onKeyDown: 在首页按两下返回键退出
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			/*
			 * System.currentTimeMillis()无论何时调用，肯定大于2000
			 */
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				app.cancelProgressBar();
				app.exitAll();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onScroll(int scrollY) {
		// TODO Auto-generated method stub
		int floatInfoBar2ParentTop = Math.max(scrollY, floatInfoBar.getTop());
		topInfoBar.layout(0, floatInfoBar2ParentTop, topInfoBar.getWidth(),
				floatInfoBar2ParentTop + topInfoBar.getHeight());
	}

	/**
	 * setListContent: 将加载的数据显示到首页
	 */
	private void setListContent() {
		listLayout = (LinearLayout) Homepage.this
				.findViewById(R.id.homepage_list_layout);
		listContainer = LayoutInflater.from(Homepage.this);
		for (int i = 0; i < listItems.size(); i++) {
			listLayout.addView(getView(i), 0);
		}
	}

	/**
	 * getView: 数据的下标创建每一条数据的显示view
	 * 
	 * @param position
	 * @return
	 */
	public View getView(int position) {
		FeedViewItem feedViewItem = null;
		feedViewItem = new FeedViewItem();
		View v = new View(Homepage.this);
		v = listContainer.inflate(R.layout.homepage_listview_item, null);
		feedViewItem.feedTime = (TextView) v
				.findViewById(R.id.homepage_listview_item_time);
		feedViewItem.feedContent = (TextView) v
				.findViewById(R.id.homepage_listview_item_content);
		feedViewItem.imageView1 = (ImageView) v.findViewById(R.id.feed_image1);
		feedViewItem.imageView2 = (ImageView) v.findViewById(R.id.feed_image2);
		feedViewItem.imageView3 = (ImageView) v.findViewById(R.id.feed_image3);
		feedViewItem.linearLayout = (LinearLayout) v
				.findViewById(R.id.image_layout);
		feedViewItem.colorLayout = (LinearLayout) v
				.findViewById(R.id.homepage_listview_item_color);
		feedViewItem.feedTime.setText((String) listItems.get(position).get(
				"feedTime"));
		feedViewItem.feedContent.setText((String) listItems.get(position).get(
				"feedContent"));

		LinearLayout.LayoutParams imageViewLayoutParams = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		if (null != listItems.get(position).get("image1")) {
			new DownLoadImageWithCache(feedViewItem.imageView1)
					.execute((String) listItems.get(position).get("image1"));
			LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			linearLayoutParams.height = (GlobalVariables.screenWidth - 69) / 3;
			imageViewLayoutParams.height = (GlobalVariables.screenWidth - 69) / 3;
			imageViewLayoutParams.width = (GlobalVariables.screenWidth - 69) / 3;
			feedViewItem.linearLayout.setLayoutParams(linearLayoutParams);
			feedViewItem.imageView1.setLayoutParams(imageViewLayoutParams);
			feedViewItem.imageView2.setLayoutParams(imageViewLayoutParams);
			feedViewItem.imageView3.setLayoutParams(imageViewLayoutParams);
		}

		colorString = OtherUtils.getColorByPosition(position);
		feedViewItem.colorLayout.setBackgroundColor(Color
				.parseColor(colorString));

		if (null != listItems.get(position).get("image2"))
			new DownLoadImageWithCache(feedViewItem.imageView2)
					.execute((String) listItems.get(position).get("image2"));
		if (null != listItems.get(position).get("image3"))
			new DownLoadImageWithCache(feedViewItem.imageView3)
					.execute((String) listItems.get(position).get("image3"));

		int typeId = (Integer) listItems.get(position).get("typeId");
		if (typeId >= 1 && typeId <= 4) {
			v.setClickable(true);
			v.setTag(typeId);
			v.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int typeId = (Integer) v.getTag();
					switch (typeId) {
					case 1:
						startActivity(new Intent(Homepage.this, Messages.class));
						break;
					case 2:
						// startActivity(new Intent(Homepage.this,
						// MyClass.class));
						Uri url = Uri
								.parse("http://www.baidu.com/baidu?word=三只小猪");
						startActivity(new Intent(Intent.ACTION_VIEW, url));
						break;
					case 3:
						startActivity(new Intent(Homepage.this,
								WeeklyRecipe.class));
						break;
					case 4:
						startActivity(new Intent(Homepage.this, Moments.class));
						break;
					default:
						break;
					}
				}
			});
		}

		return v;
	}

}

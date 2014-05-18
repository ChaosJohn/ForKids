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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.huaijv.forkids.R;
import com.huaijv.forkids.model.GlobalVariables;
import com.huaijv.forkids.utils.ChildThread;
import com.huaijv.forkids.utils.ChildThread.WorkForMain;
import com.huaijv.forkids.utils.CreateGlobalMenu;
import com.huaijv.forkids.utils.DownLoadSquareImageWithCache;
import com.huaijv.forkids.utils.JsonUtils;
import com.huaijv.forkids.utils.NetUtils;
import com.huaijv.forkids.utils.OtherUtils;
import com.slidingmenu.lib.SlidingMenu;

/**
 * Moments[activity]: 精彩瞬间首页
 * 
 * @author chaos
 * 
 */
public class Moments extends Activity {
	private SlidingMenu menu = null;

	private ImageView gridImageView = null;
	private TextView gridTextView = null;
	private GridView photoGridView = null;
	// private GridView videoGridView = null;
	private HorizontalScrollView photoHorizontalScrollView = null;
	// private HorizontalScrollView videoHorizontalScrollView = null;
	// private View gridLayout = null;
	private int cWidth;
	private int hSpacing = 13;

	private Handler mainHandler, childHandler;
	private ChildThread childThread;
	private GlobalApplication app = null;
	private final int MSG_GET = 0;
	private final int MSG_UPLOAD = 1;
	private final int MSG_ERROR = -1;
	private List<Map<String, Object>> listItems = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_moments);

		app = (GlobalApplication) this.getApplication();
		if (!app.activities.contains(this))
			app.activities.add(this);

		mainHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (MSG_GET == msg.what) {
					setPhotoVideo();
				}
				app.cancelProgressBar();
			}
		};
		childThread = new ChildThread(new WorkForMain() {

			@Override
			public void doJob(Message msg) {
				if (MSG_GET == msg.what) {
					try {
						String rev = NetUtils
								.getDataByUrl(
										"http://huaijv-sap.eicp.net:8088/forkids/kidalbums?from=Parent",
										app.getLoginString());
						if ("err".equalsIgnoreCase(rev)) {
							mainHandler.sendEmptyMessage(MSG_ERROR);
						} else {
							listItems = JsonUtils.jsonArray2List(rev);
							mainHandler.sendEmptyMessage(MSG_GET);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});

		childThread.start();
		childHandler = childThread.getHandler();
		app.showProgressBar();

		/*
		 * 让childThread从服务器获取精彩瞬间列表
		 */
		childHandler.sendEmptyMessage(MSG_GET);

		menu = CreateGlobalMenu.returnGlobalMenu(Moments.this);
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

	public void setPhotoVideo() {
		photoGridView = (GridView) findViewById(R.id.moments_gridview_photo);
		photoGridView.setAdapter(new MAdapter(Moments.this, listItems));
		int numOfColumns = photoGridView.getAdapter().getCount();
		cWidth = GlobalVariables.screenWidth * 10 / 27;
		LayoutParams params = new LayoutParams(numOfColumns
				* (cWidth + hSpacing), LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.CENTER_VERTICAL;
		photoGridView.setLayoutParams(params);
		photoGridView.setColumnWidth(cWidth);
		photoGridView.setHorizontalSpacing(hSpacing);
		photoGridView.setNumColumns(numOfColumns);
		photoGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(Moments.this, MomentsGallery.class);
				intent.putExtra("albumId", listItems.get(arg2).get("albumId")
						.toString());
				startActivity(intent);
			}
		});
	}

	/**
	 * MAdapter: 水平单行gridview的适配器
	 * 
	 * @author chaos
	 * 
	 */
	class MAdapter extends BaseAdapter {
		Context mContext;
		LayoutInflater mInflater;
		String colorString;
		List<Map<String, Object>> listItems;

		public MAdapter(Context c, List<Map<String, Object>> listItems) {
			mContext = c;
			mInflater = LayoutInflater.from(mContext);
			this.listItems = listItems;
		}

		@Override
		public int getCount() {
			return listItems.size();
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View contentView, ViewGroup parent) {
			View retval = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.moments_gridview_item, null);
			gridImageView = (ImageView) retval
					.findViewById(R.id.moments_gridview_item_image);
			gridTextView = (TextView) retval
					.findViewById(R.id.moments_gridview_item_text);
			new DownLoadSquareImageWithCache(gridImageView).execute(listItems
					.get(position).get("coverUrl").toString());
			gridImageView.setBackgroundColor(Color.parseColor("#0A779E"));
			gridTextView.setText(listItems.get(position).get("abName")
					.toString());
			gridImageView.getLayoutParams().height = cWidth;
			gridImageView.getLayoutParams().width = cWidth;
			gridTextView.getLayoutParams().height = 49;
			gridTextView.getLayoutParams().width = cWidth;
			colorString = OtherUtils.getColorByPosition(position);
			gridTextView.setBackgroundColor(Color.parseColor(colorString));

			return retval;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			startActivity(new Intent(Moments.this, Homepage.class));
			app.activities.remove(this);
			app.cancelProgressBar();
			finish();
		}
		return true;
	}

}
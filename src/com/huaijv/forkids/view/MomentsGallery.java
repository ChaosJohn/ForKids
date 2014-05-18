package com.huaijv.forkids.view;

import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.huaijv.forkids.R;
import com.huaijv.forkids.model.GlobalVariables;
import com.huaijv.forkids.utils.ChildThread;
import com.huaijv.forkids.utils.ChildThread.WorkForMain;
import com.huaijv.forkids.utils.DownLoadImageWithCache;
import com.huaijv.forkids.utils.JsonUtils;
import com.huaijv.forkids.utils.NetUtils;

/**
 * MomentsGallery[activity]: 精彩瞬间相册详情
 * 
 * @author chaos
 * 
 */
public class MomentsGallery extends Activity {

	private int gridItemSize = -1;
	private GridView gridView = null;
	private List<Map<String, Object>> listItems = null;
	private Handler mainHandler, childHandler;
	private ChildThread childThread;
	private GlobalApplication app = null;
	private String albumId = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_moments_gallery);

		app = (GlobalApplication) this.getApplication();
		if (!app.activities.contains(this))
			app.activities.add(this);

		albumId = MomentsGallery.this.getIntent().getExtras()
				.getString("albumId");
		Toast.makeText(this, albumId, Toast.LENGTH_SHORT).show();

		mainHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (app.MSG_GET == msg.what) {
					loadView();
				} else {
					Toast.makeText(MomentsGallery.this, "err",
							Toast.LENGTH_SHORT).show();
				}
				app.cancelProgressBar();
			}
		};

		childThread = new ChildThread(new WorkForMain() {
			@Override
			public void doJob(Message msg) {
				if (app.MSG_GET == msg.what) {
					String rev = NetUtils.getDataByUrl(
							"http://huaijv-sap.eicp.net:8088/forkids/kidmoments?find=ByAlbumId&albumId="
									+ albumId, app.getLoginString());
					if ("err".equalsIgnoreCase(rev)) {
						mainHandler.sendEmptyMessage(app.MSG_ERROR);
					} else {
						try {
							listItems = JsonUtils.jsonArray2List(rev);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						mainHandler.sendEmptyMessage(app.MSG_GET);
					}
				}
			}
		});
		childThread.start();
		childHandler = childThread.getHandler();
		/*
		 * 让childThread从服务器获取相册详情
		 */
		app.showProgressBar();
		childHandler.sendEmptyMessage(app.MSG_GET);

	}

	private void loadView() {
		initView();
		setListeners();
	}

	private void initView() {
		gridView = (GridView) findViewById(R.id.moments_gallery_activity_gridview);
		gridView.setAdapter(new ImageAdapter(MomentsGallery.this, listItems));
		gridItemSize = (GlobalVariables.screenWidth - 40) / 3;
	}

	/**
	 * setListeners: 设置监听事件
	 */
	private void setListeners() {
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(MomentsGallery.this,
						MomentsDetail.class);
				intent.putExtra("index", Integer.toString(arg2));
				app.imageList = listItems;
				startActivity(intent);
			}
		});

		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	/**
	 * ImageAdapter: 图片gridview适配器
	 * 
	 * @author chaos
	 * 
	 */
	public class ImageAdapter extends BaseAdapter {
		private Context context;
		private List<Map<String, Object>> listItems = null;

		public ImageAdapter(Context context, List<Map<String, Object>> listItems) {
			this.context = context;
			this.listItems = listItems;
		}

		public int getCount() {
			return listItems.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			if (convertView == null) {
				imageView = new ImageView(context);
				imageView.setLayoutParams(new GridView.LayoutParams(
						gridItemSize, gridItemSize));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			} else {
				imageView = (ImageView) convertView;
			}
			new DownLoadImageWithCache(imageView).execute(listItems
					.get(position).get("contentUrl").toString());

			return imageView;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			app.activities.remove(this);
			app.cancelProgressBar();
			finish();
		}
		return true;
	}

}

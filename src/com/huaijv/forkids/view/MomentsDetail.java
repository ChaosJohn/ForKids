package com.huaijv.forkids.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.huaijv.forkids.R;
import com.huaijv.forkids.model.GlobalVariables;
import com.huaijv.forkids.utils.DownLoadImageWithCache;
import com.huaijv.forkids.utils.SaveImageByUrl;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;

/**
 * MomentsDetail[activity]: 精彩瞬间详细界面[包括大图展示，图片描述等]
 * 
 * @author chaos
 * 
 */
public class MomentsDetail extends Activity implements OnGestureListener {
	private int imageIndex = 0;
	final UMSocialService mController = UMServiceFactory.getUMSocialService(
			"com.umeng.share", RequestType.SOCIAL);

	// 设置WindowManager
	private WindowManager wm = null;
	private WindowManager.LayoutParams wmParams = null;
	// 声明两个按钮,分别代表向左和向右滑动
	private ImageView btnLeft = null;
	private ImageView btnRight = null;
	// ImageView的alpha值
	private int mAlpha = 0;
	private boolean isHide;
	/**
	 * viewFlipper: 水平滑动图片展示
	 */
	private ViewFlipper viewFlipper = null;
	private GestureDetector detector;
	private GlobalApplication app = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_moments_detail);

		app = (GlobalApplication) this.getApplication();
		if (!app.activities.contains(this))
			app.activities.add(this);

		/*
		 * 获取点击的图片index
		 */
		Bundle bundle = getIntent().getExtras();
		imageIndex = Integer.parseInt(bundle.getString("index"));

		viewFlipper = (ViewFlipper) this
				.findViewById(R.id.moments_detail_imageview);
		detector = new GestureDetector(this);
		initImageButtonView();
		ImageView firstImageView = new ImageView(this);

		/*
		 * 分别下载所有图片
		 */
		new DownLoadImageWithCache(firstImageView).execute(app.imageList
				.get(imageIndex).get("contentUrl").toString());
		for (int i = 0; i < app.imageList.size(); i++) {
			if (imageIndex == i) {
				viewFlipper.addView(firstImageView);
				continue;
			}
			ImageView imageView = new ImageView(this);
			new DownLoadImageWithCache(imageView).execute(app.imageList.get(i)
					.get("contentUrl").toString());
			viewFlipper.addView(imageView);
		}

		/*
		 * 设置当前显示的图片
		 */
		viewFlipper.setDisplayedChild(imageIndex);

		setDescWithTime();

		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				app.activities.remove(this);
				finish();
			}
		});

		/*
		 * 点击分享按钮
		 */
		findViewById(R.id.share_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				mController.setShareContent("--来自[爱宝宝]");
				mController.setShareMedia(new UMImage(MomentsDetail.this,
						MomentsDetail.this.app.imageList.get(imageIndex)
								.get("contentUrl").toString()));
				mController.getConfig().removePlatform(SHARE_MEDIA.DOUBAN);
				mController.openShare(MomentsDetail.this, false);
			}
		});

		/*
		 * 点击评论按钮
		 */
		findViewById(R.id.comment_btn).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(MomentsDetail.this,
								Comment.class);
						intent.putExtra("from", "Moments");
						intent.putExtra("momId",
								MomentsDetail.this.app.imageList
										.get(imageIndex).get("mmId").toString());
						Toast.makeText(
								getApplicationContext(),
								MomentsDetail.this.app.imageList
										.get(imageIndex).get("mmId").toString(),
								Toast.LENGTH_LONG).show();
						startActivity(intent);
					}
				});

		/*
		 * 点击下载按钮
		 */
		findViewById(R.id.save_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new SaveImageByUrl(MomentsDetail.this)
						.execute(MomentsDetail.this.app.imageList
								.get(imageIndex).get("contentUrl").toString());
			}
		});

	}

	/**
	 * changeCurrentImageForShare: 改变当前的图片下标
	 */
	private void changeCurrentImageForShare(int offset) {
		imageIndex += offset;
		if (imageIndex < 0)
			imageIndex += app.imageList.size();
		else if (imageIndex >= app.imageList.size())
			imageIndex -= app.imageList.size();
	}

	/**
	 * initImageButtonView: 初始化悬浮按钮
	 */
	private void initImageButtonView() {
		// 获取WindowManager
		wm = (WindowManager) getApplicationContext().getSystemService("window");
		// 设置LayoutParams相关参数
		wmParams = new WindowManager.LayoutParams();
		// 设置window type
		wmParams.type = LayoutParams.TYPE_PHONE;
		// 设置图片格式,效果为背景透明
		wmParams.format = PixelFormat.RGBA_8888;
		// 设置Window flag参数
		wmParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
				| LayoutParams.FLAG_NOT_FOCUSABLE;
		// 设置x、y初始值
		wmParams.x = 0;
		wmParams.y = 0;
		// 设置窗口长宽数据
		wmParams.width = 121;
		wmParams.height = 121;
		// 创建左右按钮
		createLeftButtonView();
		createRightButtonView();
	}

	/**
	 * createLeftButtonView: 设置左边按钮
	 */
	private void createLeftButtonView() {
		btnLeft = new ImageView(this);
		btnLeft.setImageResource(R.drawable.left_btn);
		btnLeft.setAlpha(0);
		btnLeft.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				// 上一个图像
				viewFlipper.setInAnimation(MomentsDetail.this,
						R.anim.push_right_in);
				viewFlipper.setOutAnimation(MomentsDetail.this,
						R.anim.push_right_out);
				viewFlipper.showPrevious();
				changeCurrentImageForShare(-1);
				setDescWithTime();
			}
		});
		// 调整窗口
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		wmParams.y = GlobalVariables.screenHeight * 7 / 16 - 60;
		// 显示图像
		wm.addView(btnLeft, wmParams);
	}

	/**
	 * createRightButtonView: 设置右边按钮
	 */
	private void createRightButtonView() {
		btnRight = new ImageView(this);
		btnRight.setImageResource(R.drawable.right_btn);
		btnRight.setAlpha(0);
		btnRight.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				// 下一个图像
				viewFlipper.setInAnimation(MomentsDetail.this,
						R.anim.push_left_in);
				viewFlipper.setOutAnimation(MomentsDetail.this,
						R.anim.push_left_out);
				viewFlipper.showNext();
				changeCurrentImageForShare(1);
				setDescWithTime();
			}
		});
		// 调整窗口
		wmParams.gravity = Gravity.RIGHT | Gravity.TOP/* | Gravity.CENTER_VERTICAL */;
		wmParams.y = GlobalVariables.screenHeight * 7 / 16 - 60;
		// 显示图像
		wm.addView(btnRight, wmParams);
	}

	/**
	 * 设置按钮渐显效果
	 */
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1 && mAlpha < 255) {
				// 通过设置不透明度设置按钮的渐显效果
				mAlpha += 50;
				if (mAlpha > 255)
					mAlpha = 255;
				btnLeft.setAlpha(mAlpha);
				btnLeft.invalidate();
				btnRight.setAlpha(mAlpha);
				btnRight.invalidate();
				if (!isHide && mAlpha < 255)
					mHandler.sendEmptyMessageDelayed(1, 100);

			} else if (msg.what == 0 && mAlpha > 0) {
				mAlpha -= 10;
				if (mAlpha < 0)
					mAlpha = 0;
				btnLeft.setAlpha(mAlpha);
				btnLeft.invalidate();
				btnRight.setAlpha(mAlpha);
				btnRight.invalidate();
				if (isHide && mAlpha > 0)
					mHandler.sendEmptyMessageDelayed(0, 800);
			}
		}
	};

	/**
	 * setDescWithTime: 设置每一张图片的描述和时间
	 */
	private void setDescWithTime() {
		((TextView) findViewById(R.id.moments_detail_desc))
				.setText(app.imageList.get(imageIndex).get("mmName").toString());
		((TextView) findViewById(R.id.moments_detail_time))
				.setText(app.imageList.get(imageIndex).get("createAt")
						.toString());
	}

	private void showImageButtonView() {
		isHide = false;
		mHandler.sendEmptyMessage(1);
	}

	private void hideImageButtonView() {
		new Thread() {
			public void run() {
				try {
					Thread.sleep(1500);
					isHide = true;
					mHandler.sendEmptyMessage(0);
				} catch (Exception e) {
					;
				}
			}
		}.start();
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

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		this.detector.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
		case MotionEvent.ACTION_DOWN:
			showImageButtonView();
			break;
		case MotionEvent.ACTION_UP:
			hideImageButtonView();
			break;
		}
		return true;
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {

		if (e1.getX() - e2.getX() > 120) {
			this.viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_left_in));
			this.viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_left_out));
			this.viewFlipper.showNext();
			changeCurrentImageForShare(1);
			return true;
		} else if (e1.getX() - e2.getX() < -120) {
			this.viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_right_in));
			this.viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_right_out));
			this.viewFlipper.showPrevious();
			changeCurrentImageForShare(-1);
			return true;
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 在程序退出(Activity销毁)时销毁窗口
		wm.removeView(btnLeft);
		wm.removeView(btnRight);
	}
}

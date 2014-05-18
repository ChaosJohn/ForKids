package com.huaijv.forkids.view;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.huaijv.forkids.R;
import com.huaijv.forkids.model.GlobalVariables;
import com.huaijv.forkids.utils.OtherUtils;

/**
 * GlobalApplication[Application]: 全局Application
 * 
 * @author chaos
 * 
 */
public class GlobalApplication extends Application {

	private static GlobalApplication mInstance = null;
	public boolean m_bKeyRight = true;
	BMapManager mBMapManager = null;
	/*
	 * progressbar所需要的一些组件
	 */
	WindowManager windowManager = null;
	WindowManager.LayoutParams windowManagerLayoutParams = null;
	LayoutInflater inflater = null;
	View progressBarView = null;

	/*
	 * MSG_xxx: 线程操作的消息
	 */
	public final int MSG_GET = 0;
	public final int MSG_UPLOAD = 1;
	public final int MSG_ERROR = -1;

	List<Map<String, Object>> imageList = null;

	/*
	 * basicInfoMap: app登录时从服务器获取的用户基本信息
	 */
	public Map<String, Object> basicInfoMap = null;

	/*
	 * activities: 用于存储打开的所有activity，供app退出时使用
	 */
	public List<Activity> activities = null;

	/*
	 * 与服务器数据交互时提供的权限信息"username:password"
	 */
	private static String loginInfoString = null;

	private final static String city = "无锡";
	public String weatherInfo = null;
	public String weatherUrl = null;

	/*
	 * 百度地图的appkey，别忘了替换
	 */
	public static final String strKey = "fcveDrgP3eyRSAUui6X7flVG";

	/*
	 * 标志progressbar是否被移除
	 */
	int progressBarRemoved = 1;

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		activities = new ArrayList<Activity>();
		initPush();
		initEngineManager(this);
		initProgressBar(this);
		setGlobalVariables();
	}

	/**
	 * initPush: 初始化jpush推送，设置tag
	 */
	private void initPush() {
		JPushInterface.setDebugMode(true);
		JPushInterface.init(this);
		// JPushSetTag("abc");
	}

	/**
	 * JPushSetTag: 给jpush设置一组tag
	 * 
	 * @param tag
	 */
	public void JPushSetTag(String... tags) {
		Set<String> tagSet = new LinkedHashSet<String>();
		for (String tag : tags) {
			tagSet.add(tag);
		}
		JPushInterface.setAliasAndTags(this, null, tagSet);
	}

	/**
	 * JPushClearTag: 清除jpush的tag
	 */
	public void JPushClearTag() {
		JPushInterface.setAliasAndTags(this, null, null);
	}

	/**
	 * getLoginString: 从acount文件中获取登录权限"username:password"
	 * 
	 * @return
	 */
	public String getLoginString() {
		if (null == loginInfoString) {
			loginInfoString = (String) OtherUtils
					.loadFromFile("/sdcard/forkids/account");
		}
		return loginInfoString;
	}

	public void setLoginString(String loginString) {
		this.loginInfoString = loginString;
	}

	/**
	 * exitAll: 杀死所有的activity，退出app
	 */
	public void exitAll() {
		int activityLength = activities.size();
		for (int i = activityLength - 1; i >= 0; i--) {
			Activity toExitActivity = activities.remove(i);
			toExitActivity.finish();
		}
	}

	/**
	 * setGlobalVariables: 设置全局变量，比如屏幕尺寸
	 */
	private void setGlobalVariables() {
		DisplayMetrics dm = this.getApplicationContext().getResources()
				.getDisplayMetrics();

		int screenWidthDip = dm.widthPixels; // 屏幕宽（dip，如：320dip）
		int screenHeightDip = dm.heightPixels; // 屏幕宽（dip，如：533dip）

		GlobalVariables.screenHeight = screenHeightDip;
		GlobalVariables.screenWidth = screenWidthDip;

	}

	public void initEngineManager(Context context) {
		if (mBMapManager == null) {
			mBMapManager = new BMapManager(context);
		}

		if (!mBMapManager.init(strKey, new MyGeneralListener())) {
			// Toast.makeText(
			// DemoApplication.getInstance().getApplicationContext(),
			// "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * initProgressBar: 初始化可全局调用的progressbar
	 * 
	 * @param context
	 */
	public void initProgressBar(Context context) {
		windowManager = (WindowManager) getApplicationContext()
				.getSystemService(WINDOW_SERVICE);
		windowManagerLayoutParams = new WindowManager.LayoutParams();
		windowManagerLayoutParams.type = LayoutParams.TYPE_PHONE;
		windowManagerLayoutParams.format = PixelFormat.RGBA_8888;
		windowManagerLayoutParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
				| LayoutParams.FLAG_NOT_FOCUSABLE;
		windowManagerLayoutParams.x = 0;
		windowManagerLayoutParams.y = 0;
		windowManagerLayoutParams.height = 121;
		windowManagerLayoutParams.width = 121;
		windowManagerLayoutParams.gravity = Gravity.CENTER;
		inflater = LayoutInflater.from(context);
		progressBarView = inflater.inflate(R.layout.progressbar_layout, null);
	}

	/**
	 * showProgressBar: 显示progressbar
	 */
	public void showProgressBar() {
		if (progressBarRemoved == 1) {
			windowManager.addView(progressBarView, windowManagerLayoutParams);
			progressBarRemoved = 0;
		}
	}

	/**
	 * cancelProgressBar: 移除progressbar
	 */
	public void cancelProgressBar() {
		if (progressBarRemoved == 0) {
			windowManager.removeView(progressBarView);
			progressBarRemoved = 1;
		}
	}

	/**
	 * getInstance: 获取Application实例
	 * 
	 * @return
	 */
	public static GlobalApplication getInstance() {
		return mInstance;
	}

	/**
	 * MyGeneralListener: 常用事件监听，用来处理通常的网络错误，授权验证错误等
	 * 
	 * @author chaos
	 * 
	 */
	static class MyGeneralListener implements MKGeneralListener {
		@Override
		public void onGetNetworkState(int iError) {
			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
				Toast.makeText(
						GlobalApplication.getInstance().getApplicationContext(),
						"您的网络出错啦！", Toast.LENGTH_LONG).show();
			} else if (iError == MKEvent.ERROR_NETWORK_DATA) {
				Toast.makeText(
						GlobalApplication.getInstance().getApplicationContext(),
						"网络出错", Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void onGetPermissionState(int iError) {
			// 非零值表示百度地图key验证未通过
			if (iError != 0) {
				// 授权Key错误：
				Toast.makeText(
						GlobalApplication.getInstance().getApplicationContext(),
						"请检查您的网络连接是否正常！", Toast.LENGTH_LONG).show();
				GlobalApplication.getInstance().m_bKeyRight = false;
			} else {
				GlobalApplication.getInstance().m_bKeyRight = true;
			}
		}
	}

	public class MyTagAliasCallBack implements TagAliasCallback {
		@Override
		public void gotResult(int arg0, String arg1, Set<String> arg2) {
		}
	}
}
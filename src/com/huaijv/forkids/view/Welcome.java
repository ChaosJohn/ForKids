package com.huaijv.forkids.view;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.huaijv.forkids.R;
import com.huaijv.forkids.db.WeatherDBHelper;
import com.huaijv.forkids.db.WeatherDBManager;
import com.huaijv.forkids.utils.ChildThread;
import com.huaijv.forkids.utils.ChildThread.WorkForMain;
import com.huaijv.forkids.utils.JsonUtils;
import com.huaijv.forkids.utils.NetUtils;
import com.huaijv.forkids.utils.OtherUtils;

/**
 * Welcome[activity]: 欢迎界面
 * 
 * @author chaos
 * 
 */
public class Welcome extends Activity {

	private Handler mainHandler = null, childHandler = null;
	private ChildThread childThread = null;
	private GlobalApplication app = null;
	private String loginInfoString = null;
	private final int MSG_BASEINFO = 0;
	private final int MSG_WEATHER = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);

		app = (GlobalApplication) this.getApplication();
		if (!app.activities.contains(this))
			app.activities.add(this);

		/*
		 * 从sdcard中获取登录信息
		 */
		loginInfoString = (String) OtherUtils
				.loadFromFile("/sdcard/forkids/account");

		if (null == loginInfoString) {
			/*
			 * 获取不到，则跳转到登录界面
			 */
			Intent intent = new Intent(Welcome.this, Login.class);
			app.activities.remove(this);
			startActivity(intent);
			finish();
		} else {
			app.setLoginString(loginInfoString);
			mainHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					if (app.MSG_ERROR == msg.what) {
						Intent intent = new Intent(Welcome.this, Login.class);
						app.activities.remove(this);
						startActivity(intent);
						finish();
					} else if (MSG_BASEINFO == msg.what) {
						app.basicInfoMap = (Map<String, Object>) msg.obj;
						app.JPushSetTag("s" + app.basicInfoMap.get("schoolId"),
								"c" + app.basicInfoMap.get("classId"), "k"
										+ app.basicInfoMap.get("userId"));
						Intent intent = new Intent(Welcome.this, Homepage.class);
						app.activities.remove(this);
						startActivity(intent);
						finish();
					} else if (MSG_WEATHER == msg.what) {
						childHandler.sendEmptyMessage(MSG_BASEINFO);
					}
				}
			};

			childThread = new ChildThread(new WorkForMain() {
				@Override
				public void doJob(Message msg) {
					if (MSG_BASEINFO == msg.what) {
						/*
						 * 获取用户基本信息
						 */
						String rev = NetUtils
								.getDataByUrl(
										"http://huaijv-sap.eicp.net:8088/forkids/kidstudents?from=Parent",
										app.getLoginString());
						if ("err".equalsIgnoreCase(rev)) {
							mainHandler.sendEmptyMessage(app.MSG_ERROR);
						} else {
							Message toMain = mainHandler
									.obtainMessage(MSG_BASEINFO);
							try {
								toMain.obj = JsonUtils
										.jsonObjectString2Map(rev);
							} catch (JSONException e) {
								e.printStackTrace();
							}
							mainHandler.sendMessage(toMain);
						}
					} else if (MSG_WEATHER == msg.what) {
						/*
						 * 获取天气信息
						 */
						WeatherDBHelper helper = new WeatherDBHelper(
								getApplicationContext());
						WeatherDBManager manager = new WeatherDBManager(
								getApplicationContext());
						manager.copyDatabase();
						String cityName = "无锡";
						String cityCode = null;
						String sql = "select * from city_table where CITY ="
								+ "'" + cityName + "'" + ";";
						Cursor cursor = helper.getReadableDatabase().rawQuery(
								sql, null);
						if (cursor != null) {
							cursor.moveToFirst();
							cityCode = cursor.getString(cursor
									.getColumnIndex("WEATHER_ID"));
						}
						cursor.close();
						helper.close();
						String weatherUrl = "http://www.weather.com.cn/data/cityinfo/"
								+ cityCode + ".html";
						app.weatherUrl = "http://mobile.weather.com.cn/city/"
								+ cityCode + ".html";
						String weatherJson = queryStringForGet(weatherUrl);
						JSONObject jsonObject;
						try {
							jsonObject = new JSONObject(weatherJson);
							JSONObject weatherObject = jsonObject
									.getJSONObject("weatherinfo");
							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyy-MM-dd");
							String date = sdf.format(new java.util.Date());
							app.weatherInfo = date + " "
									+ weatherObject.getString("city") + ": "
									+ weatherObject.getString("temp2") + "/"
									+ weatherObject.getString("temp1") + " "
									+ weatherObject.getString("weather");
							mainHandler.sendEmptyMessage(MSG_WEATHER);
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				}
			});

			childThread.start();
			childHandler = childThread.getHandler();
			childHandler.sendEmptyMessage(MSG_WEATHER);
		}
	}

	private String queryStringForGet(String url) {
		HttpGet request = new HttpGet(url);

		String result = null;

		try {
			HttpResponse response = new DefaultHttpClient().execute(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
				return result;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}
}

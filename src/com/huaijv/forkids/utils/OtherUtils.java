package com.huaijv.forkids.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * OtherUtils: 无法分类的工具函数都放在此工具包中
 * 
 * @author chaos
 * 
 */
public class OtherUtils {
	/**
	 * getFileNameNoEx: 去除文件名的后缀名
	 * 
	 * @param filename
	 * @return
	 */
	public static String getFileNameNoEx(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename;
	}

	/**
	 * setListViewHeightBasedOnChildren:
	 * 设置listview的高度，从而把listview给撑高(慎重使用，貌似不太好用)
	 * 
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	/**
	 * getBitmapStrBase64: 把Bitmap转换成Base64
	 * 
	 * @param bitmap
	 * @return
	 */
	public static String getBitmapStrBase64(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 100, baos);
		byte[] bytes = baos.toByteArray();
		return Base64.encodeToString(bytes, 0);
	}

	/**
	 * getBitmapFromStrBase64: 把Base64转换成Bitmap
	 * 
	 * @param iconBase64
	 * @return
	 */
	public static Bitmap getBitmapFromStrBase64(String iconBase64) {
		byte[] bitmapArray;
		bitmapArray = Base64.decode(iconBase64, 0);
		return BitmapFactory
				.decodeByteArray(bitmapArray, 0, bitmapArray.length);
	}

	/**
	 * saveToFile: 将可序列化的对象存入制定路径的文件
	 * 
	 * @param path
	 * @param object
	 */
	public static void saveToFile(String path, Object object) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		File f = new File(path);
		try {
			fos = new FileOutputStream(f);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(object); // 括号内参数为要保存java对象
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				oos.close();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * loadFromFile: 从制定路径的文件中取出可序列化的对象
	 * 
	 * @param path
	 * @return
	 */
	public static Object loadFromFile(String path) {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		File f = new File(path);
		if (!f.exists()) {
			return null;
		}
		try {
			fis = new FileInputStream(f);
			ois = new ObjectInputStream(fis);
			Object object = (Object) ois.readObject();// 强制类型转换
			return object;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				ois.close();
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * getColorByPosition: 根据位置下标返回颜色值，3个一循环
	 * 
	 * @param position
	 * @return
	 */
	public static String getColorByPosition(int position) {
		String colorString = null;
		switch (position % 3) {
		case 0:
			colorString = "#F5AC4E";
			break;
		case 1:
			colorString = "#F68475";
			break;
		case 2:
			colorString = "#2FBAE8";
			break;
		default:
			break;
		}
		return colorString;
	}

}

package com.huaijv.forkids.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.widget.ImageView;

/**
 * JsoupUtils: html元素提取工具包
 * 
 * @author chaos
 * 
 */
public class JsoupUtils {

	private static List<Map<String, Object>> listItems = null;
	private static Map<String, Object> map = null;
	private static String tagName = null;
	private static String imgSrc = null;
	private static ImageView imageView = null;

	/**
	 * html2List: 将html字符串转换成图文List
	 * 
	 * @param html
	 * @param context
	 * @return
	 */
	public static List<Map<String, Object>> html2List(String html,
			Context context) {

		listItems = new ArrayList<Map<String, Object>>();
		if (!html.startsWith("<p>")) {
			map = new HashMap<String, Object>();
			map.put("p", html);
			listItems.add(map);
			return listItems;
		}
		Document document = Jsoup.parse(html);
		Elements allElements = document.select("*");
		for (Element element : allElements) {
			tagName = element.tagName();
			if (tagName.equalsIgnoreCase("p")) {
				map = new HashMap<String, Object>();
				map.put("p", element.text());
				listItems.add(map);
			} else if (tagName.equalsIgnoreCase("img")) {
				map = new HashMap<String, Object>();
				imgSrc = element.attr("abs:src");
				imageView = new ImageView(context);
				new DownLoadImageWithCache(imageView).execute(imgSrc);
				map.put("img", imgSrc);
				map.put("imageview", imageView);
				listItems.add(map);
			}
		}
		return listItems;
	}

	/**
	 * html2MapWithContentAndFirstImage: 将从html中提取出正文字符串和第一张图片
	 * 
	 * @param html
	 * @return
	 */
	public static Map<String, Object> html2MapWithContentAndFirstImage(
			String html) {
		map = new HashMap<String, Object>();
		if (!html.startsWith("<")) {
			map.put("textContent", html);
			return map;
		}
		Document document = Jsoup.parse(html);
		Elements allElements = document.select("*");
		int elementNum = 0;
		for (Element element : allElements) {
			tagName = element.tagName();
			if (tagName.equalsIgnoreCase("html")) {
				map.put("textContent", element.text());
				elementNum++;
			} else if (tagName.equalsIgnoreCase("img")) {
				map.put("img", element.attr("abs:src"));
				elementNum++;
			}
			if (2 == elementNum)
				break;
		}
		return map;
	}

	public static String list2String(List<Map<String, Object>> listItems) {
		String listString = "";
		for (int i = 0; i < listItems.size(); i++) {
			map = listItems.get(i);
			listString += "["
					+ (map.containsKey("p") ? (map.get("p").toString()) : (map
							.get("img").toString())) + "]  \n";
		}

		return listString;
	}

}

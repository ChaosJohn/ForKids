package com.huaijv.forkids.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * JsonUtils: json数据处理工具包
 * 
 * @author chaos
 * 
 */
public class JsonUtils {

	private static List<Map<String, Object>> listItems = null;
	private static Map<String, Object> map = null;
	private static JSONArray jsonArray = null;
	private static JSONObject jsonObject = null;
	private static JSONObject tempJsonObject = null;

	/**
	 * list2JsonArray: 将list包装程json数组
	 * 
	 * @param list
	 * @return
	 */
	public static JSONArray list2JsonArray(List<Map<String, Object>> list) {
		jsonArray = new JSONArray();
		for (int i = 0; i < list.size(); i++) {
			try {
				jsonArray.put(i, map2JsonObject(list.get(i)));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return jsonArray;
	}

	/**
	 * jsonArray2List: 将json数组转换成List
	 * 
	 * @param jsonArray
	 * @return
	 */
	public static List<Map<String, Object>> jsonArray2List(JSONArray jsonArray) {
		listItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				tempJsonObject = jsonArray.getJSONObject(i);
				listItems.add(jsonObjectString2Map(tempJsonObject.toString()));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return listItems;
	}

	/**
	 * jsonArray2List: 将json数组的字符串表示转换成List
	 * 
	 * @param jsonArray
	 * @return
	 */
	public static List<Map<String, Object>> jsonArray2List(String jsonString)
			throws JSONException {
		jsonArray = new JSONArray(jsonString);
		listItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < jsonArray.length(); i++) {
			tempJsonObject = jsonArray.getJSONObject(i);
			listItems.add(jsonObjectString2Map(tempJsonObject.toString()));
		}
		return listItems;
	}

	/**
	 * map2JsonObject: 将map转换成json对象
	 * 
	 * @param map
	 * @return
	 */
	public static JSONObject map2JsonObject(Map<String, Object> map) {
		return new JSONObject(map);
	}

	/**
	 * map2JsonObjectString: 将map装换成json对象的字符串表示
	 * 
	 * @param map
	 * @return
	 */
	public static String map2JsonObjectString(Map<String, Object> map) {
		return new JSONObject(map).toString();
	}

	/**
	 * jsonObjectString2Map: 将json对象的字符串表示转换为Map对象
	 * 
	 * @param jsonString
	 * @return
	 * @throws JSONException
	 */
	public static Map<String, Object> jsonObjectString2Map(String jsonString)
			throws JSONException

	{

		JSONObject jsonObject;
		jsonObject = new JSONObject(jsonString);
		@SuppressWarnings("unchecked")
		Iterator<String> keyIter = jsonObject.keys();
		String key;
		Object value;
		Map<String, Object> valueMap = new HashMap<String, Object>();
		while (keyIter.hasNext()) {
			key = (String) keyIter.next();
			value = jsonObject.get(key);
			valueMap.put(key, value);
		}
		return valueMap;

	}

}

package com.huaijv.forkids.viewElems;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huaijv.forkids.R;
import com.huaijv.forkids.db.MessagesDBHelper;

/**
 * MessageListViewAdapter: 通知消息的适配器
 * 
 * @author chaos
 * 
 */
public class MessageListViewAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, Object>> listItems;
	private LayoutInflater listContainer;
	private FlagListener flagListener;
	private Map<String, Object> map;
	private MessagesDBHelper dbHelper = null;

	public class ListItemView {
		public TextView type_text;
		public TextView content;
		public TextView time;
		public ImageView flag;
		public LinearLayout typeLayout;
		public LinearLayout contentLayout;
	}

	/**
	 * FlagListener: 回调接口
	 * 
	 * @author chaos
	 * 
	 */
	public interface FlagListener {
		public void onFlagClick(int position);
	}

	public MessageListViewAdapter(Context context,
			List<Map<String, Object>> listItems, MessagesDBHelper dbHelper) {
		this.context = context;
		listContainer = LayoutInflater.from(this.context);
		this.listItems = listItems;
		this.dbHelper = dbHelper;
	}

	public void setFlagListener(FlagListener flagListener) {
		this.flagListener = flagListener;
	}

	public int getCount() {
		return listItems.size();
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int arg0) {
		return 0;
	}

	public View getView(int position, View converView, ViewGroup parent) {
		ListItemView listItemView = null;
		if (converView == null) {
			listItemView = new ListItemView();
			converView = listContainer.inflate(R.layout.message_listview_item,
					null);
			listItemView.flag = (ImageView) converView
					.findViewById(R.id.message_listview_item_flag);
			listItemView.time = (TextView) converView
					.findViewById(R.id.message_listview_item_time);
			listItemView.type_text = (TextView) converView
					.findViewById(R.id.message_listview_item_type_text);
			listItemView.content = (TextView) converView
					.findViewById(R.id.message_listview_item_content_text);
			listItemView.contentLayout = (LinearLayout) converView
					.findViewById(R.id.message_listview_item_content_layout);
			listItemView.typeLayout = (LinearLayout) converView
					.findViewById(R.id.message_listview_item_type_layout);
			converView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) converView.getTag();
		}

		String typeString = null;
		String typeColor = null;
		int type = Integer.parseInt(listItems.get(position).get("type")
				.toString());
		switch (type) {
		case -1:
			typeString = "未读消息";
			typeColor = "#F5AC4E";
			break;
		case 0:
			typeString = "已读消息";
			typeColor = "#2FBAE8";
			break;
		case 1:
			typeString = "重要消息";
			typeColor = "#F68475";
			break;
		default:
			break;
		}
		listItemView.content.setText((String) listItems.get(position).get(
				"annoContent"));
		listItemView.type_text.setText(typeString);
		listItemView.time.setText((String) listItems.get(position).get(
				"createAt"));
		listItemView.flag.setImageBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.flag_normal));
		listItemView.flag
				.setImageResource((1 == type) ? R.drawable.flag_important
						: R.drawable.flag_normal);
		listItemView.typeLayout.setBackgroundColor(Color.parseColor(typeColor));
		listItemView.flag.setTag(position);
		listItemView.flag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int position = (Integer) v.getTag();
				flagListener.onFlagClick(position);
			}
		});

		return converView;
	}

	public void changeFlag(int position) {
		map = listItems.get(position);
		int type = Integer.parseInt(map.remove("type").toString());
		if (1 == type) {
			map.put("type", "0");
			type = 0;
		} else {
			map.put("type", "1");
			type = 1;
		}
		notifyDataSetChanged();
		dbHelper.updateByRealId(
				Integer.parseInt(listItems.get(position).get("annoId")
						.toString()), type);
	}

	public List<Map<String, Object>> getList() {
		return listItems;
	}

}

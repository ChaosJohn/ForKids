package com.huaijv.forkids.viewElems;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaijv.forkids.R;

/**
 * TipsAdapter: 育英知识listview的适配器
 * 
 * @author chaos
 * 
 */
public class TipsAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, Object>> listItems;
	private LayoutInflater listContainer;

	public final class ListItemView {
		public ImageView image;
		public TextView title;
		public TextView time;
	}

	public TipsAdapter(Context context, List<Map<String, Object>> listItems) {
		this.context = context;
		listContainer = LayoutInflater.from(this.context);
		this.listItems = listItems;
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

	public View getView(int position, View convertView, ViewGroup parent) {
		ListItemView listItemView = null;
		if (convertView == null) {
			listItemView = new ListItemView();
			convertView = listContainer.inflate(R.layout.tips_listview_item,
					null);
			listItemView.image = (ImageView) convertView
					.findViewById(R.id.tips_listview_item_image);
			listItemView.title = (TextView) convertView
					.findViewById(R.id.tips_listview_item_title);

			listItemView.time = (TextView) convertView
					.findViewById(R.id.tips_listview_item_time);
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}

		listItemView.title.setText((String) listItems.get(position)
				.get("title"));
		listItemView.time.setText((String) listItems.get(position).get("time"));

		return convertView;
	}
}

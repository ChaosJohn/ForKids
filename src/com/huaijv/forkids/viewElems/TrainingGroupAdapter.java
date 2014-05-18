package com.huaijv.forkids.viewElems;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.huaijv.forkids.R;
import com.huaijv.forkids.model.GlobalVariables;
import com.huaijv.forkids.utils.DownLoadImageWithCache;

/**
 * TrainingGroupAdapter: 培训机构某个分组listview适配器
 * 
 * @author chaos
 * 
 */
public class TrainingGroupAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, Object>> listItems;
	private LayoutInflater listContainer;

	public final class ListItemView {
		public ImageView icon;
		public TextView name;
		public TextView species;
		public LinearLayout layout;
	}

	public TrainingGroupAdapter(Context context,
			List<Map<String, Object>> listItems) {
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
			convertView = listContainer.inflate(
					R.layout.training_group_listview_item, null);
			listItemView.icon = (ImageView) convertView
					.findViewById(R.id.training_group_item_image);
			listItemView.name = (TextView) convertView
					.findViewById(R.id.training_group_item_title);
			listItemView.species = (TextView) convertView
					.findViewById(R.id.training_group_item_tag);
			listItemView.layout = (LinearLayout) convertView
					.findViewById(R.id.training_group_item_layout);
			AbsListView.LayoutParams linearLayoutParams = new AbsListView.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			linearLayoutParams.height = GlobalVariables.screenHeight / 8;
			((LinearLayout) listItemView.layout)
					.setLayoutParams(linearLayoutParams);

			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}

		new DownLoadImageWithCache(listItemView.icon)
				.execute((String) listItems.get(position).get("icon"));

		listItemView.name.setText((String) listItems.get(position).get("name"));
		listItemView.species.setText((String) listItems.get(position).get(
				"species"));

		return convertView;
	}

}

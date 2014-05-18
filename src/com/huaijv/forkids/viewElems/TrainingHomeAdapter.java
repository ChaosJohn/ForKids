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
 * TrainingHomeAdapter: 培训机构首页分组gridView适配器
 * 
 * @author chaos
 * 
 */
public class TrainingHomeAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, Object>> mainListItems;
	private LayoutInflater listContainer;

	public final class ListItemView {
		public ImageView image;
		public TextView title;
		public LinearLayout layout;
	}

	public TrainingHomeAdapter(Context context,
			List<Map<String, Object>> listItems) {
		this.context = context;
		listContainer = LayoutInflater.from(this.context);
		this.mainListItems = listItems;
	}

	public int getCount() {
		return mainListItems.size();
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
					R.layout.training_home_gridview_item, null);
			listItemView.image = (ImageView) convertView
					.findViewById(R.id.training_home_item_image);
			listItemView.title = (TextView) convertView
					.findViewById(R.id.training_home_item_text);
			listItemView.layout = (LinearLayout) convertView
					.findViewById(R.id.training_home_item_layout);
			AbsListView.LayoutParams linearLayoutParams = new AbsListView.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			linearLayoutParams.height = GlobalVariables.screenHeight / 8;
			((LinearLayout) listItemView.layout)
					.setLayoutParams(linearLayoutParams);

			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}

		new DownLoadImageWithCache(listItemView.image)
				.execute((String) mainListItems.get(position).get("image"));
		listItemView.title.setText((String) mainListItems.get(position).get(
				"title"));

		return convertView;
	}

}

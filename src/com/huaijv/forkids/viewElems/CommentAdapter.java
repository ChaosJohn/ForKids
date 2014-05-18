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
import com.huaijv.forkids.utils.DownLoadImageWithCache;

/**
 * CommentAdapter: 评论listview适配器
 * 
 * @author chaos
 * 
 */
public class CommentAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, Object>> listItems;
	private LayoutInflater listContainer;

	public final class ListItemView {
		public ImageView avatar;
		public TextView name;
		public TextView time;
		public TextView content;
	}

	public CommentAdapter(Context context, List<Map<String, Object>> listItems) {
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
			convertView = listContainer.inflate(R.layout.comment_listview_item,
					null);
			listItemView.avatar = (ImageView) convertView
					.findViewById(R.id.comment_listview_item_avatar);
			listItemView.name = (TextView) convertView
					.findViewById(R.id.comment_listview_item_name);
			listItemView.content = (TextView) convertView
					.findViewById(R.id.comment_listview_item_content);
			listItemView.time = (TextView) convertView
					.findViewById(R.id.comment_listview_item_time);
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}

		new DownLoadImageWithCache(listItemView.avatar)
				.execute((String) listItems.get(position).get(
						"commAuthorPortrait"));

		listItemView.name.setText((String) listItems.get(position).get(
				"commAuthor"));
		listItemView.time.setText((String) listItems.get(position).get(
				"createAt"));
		listItemView.content.setText((String) listItems.get(position).get(
				"commContent"));

		return convertView;
	}

	/**
	 * addItem: 向listview内的首部再新增一条消息
	 * 
	 * @param map
	 */
	public void addItem(Map<String, Object> map) {
		listItems.add(0, map);
		notifyDataSetChanged();
	}

}

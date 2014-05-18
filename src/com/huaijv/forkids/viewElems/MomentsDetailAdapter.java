package com.huaijv.forkids.viewElems;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.huaijv.forkids.utils.DownLoadImageWithCache;

/**
 * MomentsDetailAdapter: 精彩瞬间详细界面滑动适配器
 * 
 * @author chaos
 * 
 */
public class MomentsDetailAdapter extends PagerAdapter {

	private String[] imageUrls;

	private Context context;

	public MomentsDetailAdapter(Context context, String[] imageUrls) {
		this.context = context;
		this.imageUrls = imageUrls;
	}

	@Override
	public int getCount() {
		return imageUrls.length;
	}

	@Override
	public boolean isViewFromObject(View view, Object obj) {
		return view == (View) obj;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ImageView imageView = new ImageView(this.context);
		new DownLoadImageWithCache(imageView).execute(this.imageUrls[position]);
		((ViewPager) container).addView(imageView, position);
		return imageView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

}

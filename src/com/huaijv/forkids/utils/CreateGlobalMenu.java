package com.huaijv.forkids.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.huaijv.forkids.R;
import com.huaijv.forkids.model.GlobalVariables;
import com.huaijv.forkids.view.Homepage;
import com.huaijv.forkids.view.Maps;
import com.huaijv.forkids.view.Messages;
import com.huaijv.forkids.view.Moments;
import com.huaijv.forkids.view.MyClass;
import com.huaijv.forkids.view.Pay;
import com.huaijv.forkids.view.Settings;
import com.huaijv.forkids.view.Tips;
import com.huaijv.forkids.view.WeeklyRecipe;
import com.slidingmenu.lib.SlidingMenu;

/**
 * CreateGlobalMenu: 创建一个全局的侧滑菜单
 * 
 * @Usage 在需要调用全局菜单的activity中调用SlidingMenu menu =
 *        CreateGlobalMenu.returnGlobalMenu(Context
 *        context)，并且setSlideMenuOnClickListener()函数内添加对该activity的跳转，就能实现全局侧滑。
 *        如果需要点击按钮侧滑或取消侧滑，则需在按钮的监听事件内调用menu.toggle()即可
 * 
 * @author chaos
 * 
 */
public class CreateGlobalMenu {
	private static Context context = null;
	private static SlidingMenu menu = null;

	public static SlidingMenu returnGlobalMenu(Context context) {
		CreateGlobalMenu.context = context;
		/* SlidingMenu */menu = new SlidingMenu(context);
		setSlideMenu(menu, context);
		setSlideMenuOnClickListener(menu, context);
		menu.attachToActivity((Activity) context, SlidingMenu.SLIDING_CONTENT);
		return menu;
	}

	/**
	 * setSlideMenu: 设置侧滑菜单的布局和侧滑样式
	 * 
	 * @param menu
	 * @param context
	 */
	private static void setSlideMenu(SlidingMenu menu, Context context) {
		menu.setMode(SlidingMenu.LEFT);
		// menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setBehindWidth(GlobalVariables.screenWidth * 3 / 4);
		menu.setFadeDegree(0.35f);
		menu.setMenu(R.layout.global_menu);
	}

	private static void setSlideMenuOnClickListener(SlidingMenu menu,
			Context context) {
		menu.findViewById(R.id.menu_homepage_layout).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (Homepage.class == CreateGlobalMenu.context
								.getClass()) {
							CreateGlobalMenu.menu.toggle();
						} else
							CreateGlobalMenu.context.startActivity(new Intent(
									CreateGlobalMenu.context, Homepage.class));
					}
				});
		menu.findViewById(R.id.menu_message_layout).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (Messages.class == CreateGlobalMenu.context
								.getClass()) {
							CreateGlobalMenu.menu.toggle();
						} else {
							CreateGlobalMenu.context.startActivity(new Intent(
									CreateGlobalMenu.context, Messages.class));
							// CreateGlobalMenu.context
						}
					}
				});
		menu.findViewById(R.id.menu_myclass_layout).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (MyClass.class == CreateGlobalMenu.context
								.getClass()) {
							CreateGlobalMenu.menu.toggle();
						} else
							CreateGlobalMenu.context.startActivity(new Intent(
									CreateGlobalMenu.context, MyClass.class));

					}
				});
		menu.findViewById(R.id.menu_weeklyrecipe_layout).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (WeeklyRecipe.class == CreateGlobalMenu.context
								.getClass()) {
							CreateGlobalMenu.menu.toggle();
						} else
							CreateGlobalMenu.context.startActivity(new Intent(
									CreateGlobalMenu.context,
									WeeklyRecipe.class));
						// CreateGlobalMenu.context.finish();

					}
				});
		menu.findViewById(R.id.menu_moments_layout).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (Moments.class == CreateGlobalMenu.context
								.getClass()) {
							CreateGlobalMenu.menu.toggle();
						} else
							CreateGlobalMenu.context.startActivity(new Intent(
									CreateGlobalMenu.context, Moments.class));
					}
				});
		menu.findViewById(R.id.menu_tips_layout).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (Tips.class == CreateGlobalMenu.context.getClass()) {
							CreateGlobalMenu.menu.toggle();
						} else
							CreateGlobalMenu.context.startActivity(new Intent(
									CreateGlobalMenu.context, Tips.class));
					}
				});
		menu.findViewById(R.id.menu_maps_layout).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (Maps.class == CreateGlobalMenu.context.getClass()) {
							CreateGlobalMenu.menu.toggle();
						} else
							CreateGlobalMenu.context.startActivity(new Intent(
									CreateGlobalMenu.context, Maps.class));
					}
				});
		menu.findViewById(R.id.menu_pay_layout).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (Pay.class == CreateGlobalMenu.context.getClass()) {
							CreateGlobalMenu.menu.toggle();
						} else
							CreateGlobalMenu.context.startActivity(new Intent(
									CreateGlobalMenu.context, Pay.class));
					}
				});
		// menu.findViewById(R.id.menu_training_layout).setOnClickListener(
		// new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// if (Training.class == CreateGlobalMenu.context
		// .getClass()) {
		// CreateGlobalMenu.menu.toggle();
		// } else
		// CreateGlobalMenu.context.startActivity(new Intent(
		// CreateGlobalMenu.context, Training.class));
		// }
		// });
		menu.findViewById(R.id.menu_setting_layout).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (Settings.class == CreateGlobalMenu.context
								.getClass()) {
							CreateGlobalMenu.menu.toggle();
						} else
							CreateGlobalMenu.context.startActivity(new Intent(
									CreateGlobalMenu.context, Settings.class));
					}
				});
	}
}

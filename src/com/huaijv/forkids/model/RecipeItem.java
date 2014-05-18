package com.huaijv.forkids.model;

import java.io.Serializable;


public class RecipeItem implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String weekdayNameString = null; 
	private String breakfastString = null; 
	private String lunchString = null;
	private String supperString = null;
	
	public RecipeItem() {}
	
	public RecipeItem(int weekday, String breakfastString, String lunchString, String supperString) {
		setWeekdayNameByInt(weekday); 
		this.breakfastString = breakfastString; 
		this.lunchString = lunchString; 
		this.supperString = supperString; 
	}
	
	private void setWeekdayNameByInt(int weekday) {
		switch(weekday) {
		case 1: 
			weekdayNameString = "周一"; 
			break; 
		case 2: 
			weekdayNameString = "周二"; 
			break; 
		case 3: 
			weekdayNameString = "周三"; 
			break; 
		case 4: 
			weekdayNameString = "周四"; 
			break;
		case 5: 
			weekdayNameString = "周五"; 
			break;
		}
	}
	
	public String getWeekdayName() {
		return this.weekdayNameString; 
	}
	
	public String getBreakfast() {
		return this.breakfastString;
	}
	
	public String getLunch() {
		return this.lunchString; 
	}
	
	public String getSupper() {
		return this.supperString; 
	}
	
	public String toString() {
		return (this.breakfastString + "\n" + this.lunchString + "\n" + this.supperString); 
	}

}
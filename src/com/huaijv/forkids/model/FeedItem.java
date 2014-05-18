package com.huaijv.forkids.model;

import java.io.Serializable;

public class FeedItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String titleString = null;
	private String timeString = null;
	private String contentString = null;
	private String image1 = null;
	private String image2 = null;
	private String image3 = null;
	private int classId = 0; 

	public FeedItem() {
	}

	public FeedItem(String titleString, String timeString, String contentString, int classId) {
		this.titleString = titleString;
		this.timeString = timeString;
		this.contentString = contentString;
		this.classId = classId;
	}

	public FeedItem(String titleString, String timeString,
			String contentString, int classId, String image1) {
		this.titleString = titleString;
		this.timeString = timeString;
		this.contentString = contentString;
		this.classId = classId;
		this.image1 = image1;
	}

	public FeedItem(String titleString, String timeString,
			String contentString, int classId, String image1, String image2) {
		this.titleString = titleString;
		this.timeString = timeString;
		this.contentString = contentString;
		this.classId = classId;
		this.image1 = image1;
		this.image2 = image2;
	}

	public FeedItem(String titleString, String timeString,
			String contentString, int classId, String image1, String image2, String image3) {
		this.titleString = titleString;
		this.timeString = timeString;
		this.contentString = contentString;
		this.classId = classId;
		this.image1 = image1;
		this.image2 = image2;
		this.image3 = image3;
	}

	public String getTitleString() {
		return titleString;
	}

	public void setTitleString(String titleString) {
		this.titleString = titleString;
	}

	public String getTimeString() {
		return timeString;
	}

	public void setTimeString(String timeString) {
		this.timeString = timeString;
	}

	public String getContentString() {
		return contentString;
	}

	public void setContentString(String contentString) {
		this.contentString = contentString;
	}

	public String toString() {
		return ("Title: " + this.titleString + "\nTime: " + this.timeString
				+ "\nContent: " + this.contentString);
	}

	public String getImage1() {
		return image1;
	}

	public void setImage1(String image1) {
		this.image1 = image1;
	}
	
	public String getImage2() {
		return image2;
	}

	public void setImage2(String image2) {
		this.image2 = image2;
	}
	
	public String getImage3() {
		return image3;
	}

	public void setImage3(String image3) {
		this.image3 = image3;
	}

	public int getClassId() {
		return classId;
	}

	public void setClassId(int classId) {
		this.classId = classId;
	}

}

package com.huaijv.forkids.model;

public class CommentItem {

	private String avatar = null;
	private String name = null;
	private String time = null;
	private String content = null;
	
	public CommentItem(String avatar, String name, String time, String content) {
		this.avatar = avatar;
		this.name = name; 
		this.time = time; 
		this.content = content;		
	}
	
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

}

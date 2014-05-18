package com.huaijv.forkids.model;

import java.io.Serializable;

public class MessageItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String content = null;
	private int type = 0;
	private String time = null;

	public MessageItem() {
	}

	public MessageItem(String content, String time, int type) {
		this.content = content; 
		this.time = time; 
		this.type = type; 
	}

	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}

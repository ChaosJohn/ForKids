package com.huaijv.forkids.model;

public class TrainingHomeItem {
	
	private String image;
	private String title;
	
	
	public TrainingHomeItem() {}
	
	public TrainingHomeItem ( String image,String title){
		this.image=image;
		this.title=title;
	
	}
	public String getImage(){
		return this.image;
	}
	
	public String getTitle(){
		return this.title;
	}
	


}

package com.huaijv.forkids.model;

public class TrainingGroupItem {
	
	private String icon;
	private String name;
	private String species;
	
	public TrainingGroupItem() {}
	
	public TrainingGroupItem ( String icon,String name,String species){
		this.icon=icon;
		this.name=name;
		this.species=species;
	}
	public String getIcon(){
		return this.icon;
	}
	
	public String getName(){
		return this.name;
	}
	public String getSpecies(){
		return this.species;
	}

}

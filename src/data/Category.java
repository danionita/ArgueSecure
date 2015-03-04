package data;

import argTool.View;

public class Category extends Element{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int wrapSize = 1;

	public Category(String desc){
		super(desc);
	}
	
	public Category(){
		super();
	}
	
	public int getNr() {
		return getParent().getIndex(this) + 1;
	}
		
	public void setDesc(String desc){
		super.setUserObject(desc);
	}
	
	public void setWrapSize(int hSize){
		wrapSize = hSize;
	}
	
	public int getWrapSize(){
		return wrapSize;
	}
	
	
	public Object getDesc(){
		return super.getUserObject();
	}
	
	public String toString(){
		String result = "<html>" + View.FORMAT + "Category: " + super.getUserObject().toString();		
		return result;
	}		
}

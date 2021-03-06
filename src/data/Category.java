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
	
        @Override
	public String toString(){
		String result = "<html>" + View.CATEGORY_FORMAT + "CATEGORY: " + super.getUserObject().toString().toUpperCase();		
		return result;
	}		
        
        public String toOutputString(){
		String result =  View.OUTPUT_CATEGORY_FORMAT + "CATEGORY: " + super.getUserObject().toString().toUpperCase()+"</div>";		
		return result;
	}		
}

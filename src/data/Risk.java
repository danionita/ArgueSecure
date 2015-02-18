package data;

import argTool.View;

public class Risk extends Element{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Risk(String desc){
		super(desc);
	}
	
	public Risk(){
		super();
	}
	
	@Override
	public int getNr() {
		return getParent().getIndex(this) + 1;
	}
	
	public void setDesc(String desc){
		super.setUserObject(desc);
	}
	
	public Object getDesc(){
		return super.getUserObject();
	}
	
	public String toString(){
		String desc = "R" + getNr() + ": ";
		if (isActive()) desc = "<html>" + View.FORMAT +"<b>"+desc+"</b>" + super.getUserObject() + "</html>";
		else desc = desc + super.getUserObject();
		return desc;
	}
	
}

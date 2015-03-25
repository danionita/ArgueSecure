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
                int nr=getParent().getIndex(this) + 1;
                int categoriesBefore =getParent().getParent().getIndex(getParent());
                for(int i=0;i<categoriesBefore;i++ ){
                    nr+=getRoot().getChildAt(i).getChildCount();
                }
		return nr;
	}
	
	public void setDesc(String desc){
		super.setUserObject(desc);
	}
	
	public Object getDesc(){
		return super.getUserObject();
	}
	
        @Override
	public String toString(){
		String desc = "R" + getNr() + ": ";
		if (isActive()) desc = "<html>" + View.RISK_FORMAT +"<b>"+desc + super.getUserObject() + "</b></html>";
		else desc = desc + super.getUserObject();
		return desc;
	}
        
        public String toOutputString(){
		String desc = "R" + getNr() + ": ";
		if (isActive()) desc =  View.OUTPUT_RISK_FORMAT +"<b>"+desc + super.getUserObject() + "</b></div>";
		else desc = desc + super.getUserObject();
		return desc;
	}
	
}

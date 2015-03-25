package data;

import argTool.View;

public class Assumption extends Element{
	
	private static final long serialVersionUID = 1L;

	public Assumption(String desc){
		super(desc);
		setAllowsChildren(false);
	}
	
	public Assumption(){
		super();
		setAllowsChildren(false);
	}
		
        @Override
	public String toString(){
		boolean valid = ((Claim)getParent()).getValid();
		String desc = "<html>" + View.FORMAT + ((valid) ? Claim.VALID : Claim.INVALID);
		
		//desc += "A"+(((Claim)getParent()).getNr()+1)+"."+(getNr())+": "+super.getUserObject()+"</font></body></html>";
		desc += super.getUserObject()+"</font></body></html>";
                
		return desc;
	}
        
        public String toOutputString(){
            	boolean valid = ((Claim)getParent()).getValid();
		String desc = View.OUTPUT_ASSUMPTION_FORMAT + ((valid) ? Claim.VALID : Claim.INVALID);
		desc += "A"+(((Claim)getParent()).getNr()+1)+"."+(getNr())+": "+super.getUserObject()+"</font></div>";
		return desc;
        }
	
	public int getNr(){
		return getParent().getIndex(this)+1;
	}

}

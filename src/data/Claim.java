package data;

import argTool.View;

public class Claim extends Element{

	private boolean transferClaim;
	private boolean implementedClaim;
	
	public boolean isImplementedClaim() {
		return implementedClaim;
	}

	public void setImplementedClaim(boolean implementedClaim) {
		this.implementedClaim = implementedClaim;
	}

	private static final long serialVersionUID = 1L;
	protected static final String VALID = "<font color=\"green\">";
	protected static final String INVALID = "<font color = \"red\">";

	public Claim(String desc){
		super(desc);
		super.setValid(true);
		transferClaim = false;
		implementedClaim = false;
	}
	
	public Claim(){
		super();
		super.setValid(true);
		transferClaim=false;
		implementedClaim=false;
	}
		
	/**
	 * returns the last result in the list of assumptions associated with this claim
	 * @return
	 */
	public Assumption lastAssumption(){	
		return (Assumption)getLastChild();
	}
		
	/**
	 * adds a new assumption associated with this claim at the bottom of the list.
	 * @param desc the assumption's description
	 */
	
	/**
	 * This claim's number in the risk.
	 * @return
	 */
	public int getNr(){
		return getParent().getIndex(this);
	}
	
        @Override
	public String toString(){
		String result = "<html>" + View.RISK_FORMAT ;
		result += ((super.getValid()) ? VALID : INVALID);
                result += super.getUserObject();
		if (implementedClaim) result +="&#x2713;";
                result += "</font></html>";		
		return result ;
	}
        
       public String toOutputString(){
           	String result = View.OUTPUT_CLAIM_FORMAT ;

			result += ((super.getValid()) ? VALID : INVALID);

		if (transferClaim){
			result += "T" + + (getNr()+1) +": " + super.getUserObject();
                   
		}else{
			result += "C" + (getNr()+1) +": "+super.getUserObject();
                    
		}                
		if (implementedClaim) result +=" &#x2713;";
		result += "</font></div>";
		
		return result ;
       }
	
	public boolean isDefender(){
		return (getNr() % 2 == 1);
	}
	
	public void setTransferClaim(boolean b){
		transferClaim = b;
	}
	
	public boolean getTransferClaim(){
		return transferClaim;
	}
        
        public boolean isTransferClaim(){
              return transferClaim;
	}
}

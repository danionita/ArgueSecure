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
		implementedClaim = true;
	}
	
	public Claim(){
		super();
		super.setValid(true);
		transferClaim=false;
		implementedClaim=true;
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
	
	public String toString(){
		String result = "<html>" + View.FORMAT + "<b>";
		if (!implementedClaim){
			result += "<font color=\"blue\">";
		}
		else{
			result += ((super.getValid()) ? VALID : INVALID);
		}
		if (transferClaim){
			//result += "<i>Transfer claim:</i>";
			result += "T" + + (getNr()+1) +": " + super.getUserObject();
		}else{
			//result += isDefender() ? "Defender C" : "Attacker C";
			result += "C" + (getNr()+1) +": "+super.getUserObject();
		}
		if (!implementedClaim) result =
		result += "</b></font></html>";
		
		return result ;
	}
	
	public boolean isDefender(){
		return (getNr() % 2 == 1);
	}
	
	public void setTransferClaim(boolean b){
		transferClaim = b;
		this.setAllowsChildren(b);
	}
	
	public boolean getTransferClaim(){
		return transferClaim;
	}
}

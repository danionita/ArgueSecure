package data;

import java.util.NoSuchElementException;

import javax.swing.tree.DefaultMutableTreeNode;

public class Model {
	private Risk currentRisk;
	private Element activeElement;
	public Element getActiveElement() {
		return activeElement;
	}

	public void setActiveElement(Element activeElement) {
		try{activeElement.setActive(false);}
		catch(NullPointerException e){/*Nothing needs to be done; if it was Null, it is already "false".*/}
		
		this.activeElement = activeElement;
		
		try{activeElement.setActive(true);}
		catch(NullPointerException e){/*Nothing needs to be done; if it was Null, no element needs to be set.*/}
	}

	private DefaultMutableTreeNode assessmentRoot;


	public Model(){
		assessmentRoot = new DefaultMutableTreeNode("Argumentation-based Risk Assessment");
	}

	/**
	 * returns the assessment risk tree. Root node is the assessment itself, with its children constituting rounds.
	 * @return
	 */
	public DefaultMutableTreeNode getAssessment(){
		return assessmentRoot;
	}
	
	public void setAssessment(DefaultMutableTreeNode root){
		assessmentRoot = root;
		try{
			Risk lastRisk = (Risk)root.getLastChild().getChildAt(root.getLastChild().getChildCount()-1);
			currentRisk = lastRisk;
		}catch(NoSuchElementException e){
			System.out.println("This assessment contained no risks...");
		}
	}

	public Risk getRisk(){
		return currentRisk;
	}
	
	/**
	 * returns a specific risk for perusal
	 * @require assessmentRoot.getChildCount=>i>0
	 * @param i
	 * @return
	 */
	public Risk getRisk(int i){
		Risk result = null;
		try{result = (Risk)assessmentRoot.getChildAt(i);}
		catch(NullPointerException e){
			System.err.println("getRound was called with an invalid index: " + i);
			System.err.println("Current amount of rounds: "+ assessmentRoot.getChildCount());
		}
		return result;
	}

	/**
	 * sets the currently active Risk to be discussed at i
	 * @param i = the round to be displayed in the argumentation pane
	 */
	public void setRisk(int i){
		currentRisk = getRisk(i);
	}
	
	public void setRisk(Risk n){
		currentRisk = n;
	}

}

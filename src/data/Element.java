package data;

import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;

@SuppressWarnings("serial")
public abstract class Element extends DefaultMutableTreeNode implements Serializable{
	private boolean isValid;
	private boolean active;
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Element(String desc){
		super(desc);
		setValid(true);
	}
	
	public Element(){
		super();
		setValid(true);
	}
	
	public boolean getValid(){
		return isValid;
	}
	
	public void setValid(boolean b){
		isValid = b;
	}
	
	public void setDesc(String desc){
		super.setUserObject(desc);
	}
	
	public Object getDesc(){
		return super.getUserObject();
	}
	
	public String toString(){
		return super.getUserObject().toString();
	}
	
	public abstract int getNr();
	
}

package container;

import pad.IPad;


public interface IContainer {	
			
	public void setId(String id);
	
	public String getId();
	
	
	/* Pad occupation */
	
	public void setPad(IPad pad);
	
	public void freePad();
	
	public boolean isOccupied();
}

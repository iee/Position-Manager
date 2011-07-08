package container;

import org.eclipse.jface.text.Position;
import org.eclipse.swt.widgets.Composite;

public class Container {

	private String	  fContainerID;
	private Position  fPosition;	
	private Composite fComposite;
	private boolean   fIsDisposed;
	
	private static StyledTextManager fStyledTextManager;

	public static void setStyledTextManager(StyledTextManager styledTextManager) {
		fStyledTextManager = styledTextManager;
	}
	
	Container(Position position, String containerID) {		
		fPosition = position;
		fContainerID = containerID;
		fComposite = fStyledTextManager.AllocateComposite(fContainerID);
		fIsDisposed = false;
	}

	public String getContainerID() {
		return fContainerID;
	}
	
	public Position getPosition() {
		return fPosition;
	}
	
	public Composite getComposite() {
		return fComposite;
	}
	
	public boolean isDisposed() {
		return fIsDisposed;
	}
		
	void updatePosition(int offset, int length) {
		fPosition.setOffset(offset);
		fPosition.setLength(length);
		onMove();
	}
	
	void dispose() {
		fStyledTextManager.ReleaseComposite(fContainerID);
		fIsDisposed = true;
	}
	
	protected void onMove() {
		/* Update StyledText stuff */
		fStyledTextManager.setBounds(fContainerID, fPosition.offset);
	}
		
	@Override
	public String toString() {
		return "[" + fContainerID + ", " + fPosition + "]";
	}
		
	private Container(Position position) {
		fPosition = position;
	}
	
	static Container atOffset(int offset) {
		return new Container(new Position(offset, 0));
	}
}

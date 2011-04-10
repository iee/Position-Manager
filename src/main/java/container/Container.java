package container;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;

import pad.IPad;



class Container implements IContainer, Comparable<Container> {
	
	private String fId;
	private Position fPosition;
	
	private IPad fPad;
	private IContainerFormat fFormat;
	
	
	Container(Position position, IDocument document) {
		fPosition = position;
		fFormat = new ContainerFormat(position, document);
	}
	
	Container(String id, Position position, IDocument document) {
		fPosition = position;
		fFormat = new ContainerFormat(position, document);
	}
		
	/* Creating temporary Container for comparison */
	
	static Container atOffset(int offset) {
		return new Container(offset);
	}
	
	private Container(int offset) {
		fPosition = new Position(offset, 0);
	}
	

	/* Public interface */
		
	public void setId(String id) {
		fId = id;
		fFormat.setContainerId(id);
	}
	
	public String getId() {
		if (fId == null) {
			fId = fFormat.getContainerId();
		}
		return fId;
	}
	
	public void updatePosition(int offset, int length) {
		fPosition.setOffset(offset);
		fPosition.setLength(length);
	}
		
	public void movePosition(int delta) {
		fPosition.setOffset(fPosition.getOffset() + delta);
	}
	
	public Position getPosition() {
		return fPosition;
	}	
		
	
	@Override
	public void setPad(IPad pad) {
		fPad = pad;
		
		/* do something */
		
	}

	@Override
	public void freePad() {
		if (fPad != null) {
			
			/* free */
			
			fPad = null;
		}
	}

	@Override
	public boolean isOccupied() {
		return fPad != null;
	}
	
	

	@Override
	public int compareTo(Container container) {
		Integer offset = fPosition.getOffset();
		return offset.compareTo(container.getPosition().getOffset());
	}
	
	@Override
	public String toString() {
		return "Pad [fPosition=" + fPosition + "]";
	}
}

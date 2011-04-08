package container;

import org.eclipse.jface.text.Position;

public class Container {
	private Position fPosition;
	
	public static Container atOffset(int offset) {
		return new Container(new Position(offset, 0));
	}
			
	public Container(Position position) {
		fPosition = position;
	}
			
	Position getPosition() {
		return fPosition;
	}
	
	void setPosition(Position position) {
		fPosition = position;
	}
	
	void updatePosition(int offset, int length) {
		fPosition.setOffset(offset);
		fPosition.setLength(length);
	}
	
	void move(int delta) {
		fPosition.setOffset(fPosition.getOffset() + delta);
	}
	
	@Override
	public String toString() {
		return "Pad [fPosition=" + fPosition + "]";
	}
}

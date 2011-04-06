package position;

import org.eclipse.jface.text.Position;

public class Pad {
	private Position fPosition;
	
	public static Pad atOffset(int offset) {
		return new Pad(new Position(offset, 0));
	}
			
	public Pad(Position position) {
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

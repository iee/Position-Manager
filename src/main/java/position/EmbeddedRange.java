package position;

import org.eclipse.jface.text.Position;

public class EmbeddedRange {
	String fId;
	String fType;
	Position fPosition;
	
	public EmbeddedRange(String id, String type, Position position) {
		fId = id;
		fType = type;
		fPosition = position;
	}
	
	public String getId() {
		return fId;
	}
	
	String getType() {
		return fType;
	}
	
	Position getPosition() {
		return fPosition;
	}
	
	void setPosition(Position position) {
		fPosition = position;
	}

	@Override
	public String toString() {
		return "EmbeddedRange [fId=" + fId + ", fPosition=" + fPosition
				+ ", fType=" + fType + "]";
	}
}

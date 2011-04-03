package position;

import java.util.Scanner;
import java.util.regex.MatchResult;

import org.eclipse.jface.text.Position;

public class EmbeddedRange {
	private String fId;
	private String fType;
	private Position fPosition;
	private boolean fIsVisiable;
	
	public static EmbeddedRange atOffset(int offset) {
		return new EmbeddedRange("", "", new Position(offset, 0));
	}
		
	public EmbeddedRange(String id, String type, Position position) {
		fId = id;
		fType = type;
		fPosition = position;
		fIsVisiable = false;
	}
	
	public EmbeddedRange(String initString, Position position) {
		Scanner s = new Scanner(initString);
		s.findInLine("/\\* (\\S+) (\\S+) \\*/");
		MatchResult result = s.match();
		s.close();
		
		if (result.groupCount() != 2) {
			throw new RuntimeException();
		}

		fId = result.group(0);
		fType = result.group(1);
		fPosition = position;
		fIsVisiable = false;
	}
	
	String getId() {
		return fId;
	}
	
	void setId(String id) {
		fId = id;
	}
	
	String getType() {
		return fType;
	}
	
	void setType(String type) {
		fType = type;
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
	
	boolean isVisiable() {
		return fIsVisiable;
	}
	
	void setIsVisiable(boolean isVisiable) {
		fIsVisiable = isVisiable;
	}

	@Override
	public String toString() {
		return "EmbeddedRange [fId=" + fId + ", fPosition=" + fPosition
				+ ", fType=" + fType + "]";
	}
}

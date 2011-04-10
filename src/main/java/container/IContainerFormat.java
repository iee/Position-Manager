package container;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;

public interface IContainerFormat {
	
	void connect(IDocument document, Position position);
	
	void parse();
	void generate();
		
	void setContainerId(String id);
	
	String getContainerId();
	
	boolean hasEditorText();
	
	void setEditorText(String text);
	
	String getEditorText();
}

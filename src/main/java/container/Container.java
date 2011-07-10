package container;

import org.eclipse.jface.text.Position;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

public class Container {

	private String	  fContainerID;
	private Position  fPosition;	
	private Composite fComposite;
	private boolean   fIsDisposed;
	
/*
   private static StyledTextManager fStyledTextManager;
	public static void setStyledTextManager(StyledTextManager styledTextManager) {
		fStyledTextManager = styledTextManager;
	}
*/
	private static StyledText fStyledText;
	
	public static void setStyledTextManager(StyledText styledText) {
		fStyledText = styledText;
	}
	
	Container(Position position, String containerID) {		
		fPosition = position;
		fContainerID = containerID;
		//fComposite = fStyledTextManager.allocateComposite(fContainerID);
		//fStyledTextManager.updateCompositePresentaion(fContainerID, fPosition.offset);
		
		fComposite = new Composite(fStyledText, SWT.NONE);
		
		fStyledText.addLineStyleListener(new LineStyleListener() {
			@Override
			public void lineGetStyle(LineStyleEvent e) {
				if (e.lineText.startsWith("<")) {
					StyleRange style = new StyleRange();
					style.start = e.lineOffset;
					style.length = 2;
					style.metrics = new GlyphMetrics(0, 0, 10);
					e.styles = new StyleRange[] { style };	
				}
			}
		});
		
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
	}
	
	void dispose() {
		//fStyledTextManager.releaseComposite(fContainerID);
		fComposite.dispose();
		fIsDisposed = true;
	}
	
	public void updatePresentation() {
		//fStyledTextManager.updateCompositePresentaion(fContainerID, fPosition.offset);
		Point point = fStyledText.getLocationAtOffset(fPosition.getOffset());
		Point gabarit = fComposite.getSize();
		fComposite.setBounds(point.x, point.y, gabarit.x, gabarit.y);
	}

	@Override
	public String toString() {
		return "[" + fContainerID + ", " + fPosition + "]";
	}


	/* Functions for comparator */
	
	private Container(Position position) {
		fPosition = position;
	}
	
	static Container atOffset(int offset) {
		return new Container(new Position(offset, 0));
	}
}

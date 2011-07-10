package container;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;

public class StyledTextManager {
	
	private StyledText fStyledText;
	private Map<String, Composite> fID2CompositeMap = new TreeMap<String, Composite>();
	
	public StyledTextManager(StyledText styledText) {
		fStyledText = styledText;

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

				
				//e.styles = new StyleRange[1];
				//e.lineOffset 
				
				//StyleRange style = new StyleRange();
				
				//Rectangle rect = 
				//style.metrics = new GlyphMetrics(0, 0, ;
			}
		});
	}

	public Point getLocationAtOffset(int offset) {
		return fStyledText.getLocationAtOffset(offset);
	}
	
	Composite allocateComposite(String containerID) {
		System.out.println("allocateComposite");
		
		Composite composite = new Composite(fStyledText, SWT.NONE);
		        
		fID2CompositeMap.put(containerID, composite);		
		return composite;
	}
	
	void releaseComposite(String fContainerID) {
		System.out.println("releaseComposite");
		
		Composite composite = fID2CompositeMap.get(fContainerID);
		Assert.isNotNull(composite);
		
		composite.dispose();
		fID2CompositeMap.remove(fContainerID);
	}
	
	void updateCompositePresentaion(String id, int offset)
	{
		System.out.println("updateCompositePresentaion");
		
		/* Set position */
		
		Composite composite = fID2CompositeMap.get(id);
		Assert.isNotNull(composite);
			
		Point point = fStyledText.getLocationAtOffset(offset);
		Point gabarit = composite.getSize();
		composite.setBounds(point.x, point.y, gabarit.x, gabarit.y);
				
		/* Set style */
		
		//StyleRange style = fStyledText.getStyleRangeAtOffset(offset);
		//Rectangle bounds = composite.getBounds();

		//int ascent = 2 * bounds.height / 3;
		//int descent = bounds.height - ascent;
		
		//if (style == null) {
		StyleRange style = new StyleRange();
		style.start = offset;
		style.length = 2;
		style.metrics = new GlyphMetrics(0, 0, 40);
		//}

		fStyledText.setStyleRange(style);
	}
}

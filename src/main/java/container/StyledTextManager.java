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

//		fStyledText.addLineStyleListener(new LineStyleListener() {
//			
//			@Override
//			public void lineGetStyle(LineStyleEvent e) {
//				e.styles = new StyleRange[1];
//				
//				StyleRange style = new StyleRange();
//				
//				Rectangle rect = 
//				style.metrics = new GlyphMetrics(0, 0, ;
//			}
//		});
	}
	
	void setBounds(String containerID, int offset)
	{
		Composite composite = fID2CompositeMap.get(containerID);
			
		Assert.isNotNull(composite);
			
		Point point = fStyledText.getLocationAtOffset(offset);
		
		Point size = composite.getSize();
		
		composite.setBounds(point.x, point.y, size.x, size.y);
	}
	
	Composite AllocateComposite(String containerID) {
		Composite composite = new Composite(fStyledText, SWT.NULL);
		
		composite.addControlListener(new ControlListener() {
			@Override
			public void controlMoved(ControlEvent e) {
			}

			@Override
			public void controlResized(ControlEvent e) {
				Composite c = (Composite) e.getSource();
				int offset = fStyledText.getOffsetAtLocation(c.getLocation());
				StyleRange style = fStyledText.getStyleRangeAtOffset(offset);
				Rectangle rect = c.getBounds();

				int ascent = 2*rect.height/3;
				int descent = rect.height - ascent;
				
				if (style == null) {
					style = new StyleRange();
					style.start = offset;
					style.length = 1;
				}

				style.metrics = new GlyphMetrics(ascent + 5, descent + 5, rect.width + 2*5);

				fStyledText.setStyleRange(style);
			}
		});

		// REMOVE IT
       /* FileDialog fd = new FileDialog(fStyledText.getShell(), SWT.OPEN);
        fd.setText("Open");
        fd.setFilterPath("");
        String[] filterExt = { "*.jpg", "*.png", ".dif" };
        fd.setFilterExtensions(filterExt);
        String selected = fd.open();
        
        System.out.println(selected);
        
        Image image;
        
        if (selected != null) {
        	image = new Image(fStyledText.getDisplay(), selected);
    		composite.setBackgroundImage(image);
    		Rectangle rect = image.getBounds();
    		composite.setSize(new Point(rect.height, rect.width));
        }
		// REMOVE IT
		 */
        
		fID2CompositeMap.put(containerID, composite);		
		return composite;
	}
	
	void ReleaseComposite(String fContainerID) {
		Composite composite = fID2CompositeMap.get(fContainerID);
		
		Assert.isNotNull(composite);
		
		composite.dispose();
		fID2CompositeMap.remove(fContainerID);
	}
}

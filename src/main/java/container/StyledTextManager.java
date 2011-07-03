package container;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;

class StyledTextManager {
	
	private StyledText fStyledText;
	private Map<String, Composite> fID2CompositeMap = new TreeMap<String, Composite>();
	
	StyledTextManager(StyledText styledText) {
		fStyledText = styledText;
	}
	
	Composite AllocateComposite(String fContainerID) {
		Composite composite = new Composite(fStyledText, SWT.NONE);
		fID2CompositeMap.put(fContainerID, composite);		
		return composite;
	}
	
	void ReleaseComposite(String fContainerID) {
		Composite composite = fID2CompositeMap.get(fContainerID);
		composite.dispose();
		fID2CompositeMap.remove(fContainerID);
	}
}

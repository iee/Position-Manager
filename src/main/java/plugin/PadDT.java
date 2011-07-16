package plugin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import pad.Pad;

public class PadDT extends Pad {
	
	@Override
	public void createPartControl(Composite parent)
	{
		parent.setLayout(new RowLayout());
		DateTime calendar = new DateTime(parent, SWT.CALENDAR);
	}
}

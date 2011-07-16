package plugin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import pad.Pad;

public class PadDT extends Pad {
	
	public PadDT() {
		super();
	}
	
	@Override
	public void createPartControl(Composite parent)
	{
		parent.setLayout(new RowLayout());
		DateTime calendar = new DateTime(parent, SWT.CALENDAR);
	}

	
	protected PadDT(String containerID) {
		super(containerID);
	}
	
	
	@Override
	public Pad copy(String containerID) {
		// TODO Auto-generated method stub
		return new PadDT(containerID);
	}
}

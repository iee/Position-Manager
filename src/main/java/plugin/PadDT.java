package plugin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;

import pad.Pad;
import view.TestView;

public class PadDT extends Pad {

	public PadDT(String id) {
		super(id);
		addAction();
	}
	
	private void addAction()
	{
		Composite mainComposite = TestView.getMainComposite();
		
		Button button = new Button(mainComposite, SWT.PUSH);
		
		button.addMouseListener(new MouseListener() {
			@Override
			public void mouseDown(MouseEvent e) {
				TestView.getPadManager().addPad(new PadDT("PadID_DT"), 0);
			}

			@Override
			public void mouseUp(MouseEvent e) {
			}			
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}
		});
		
		button.setText("Add datetime PAD");
	}
	
	@Override
	public void createPartControl(Composite parent)
	{
		parent.setLayout(new RowLayout());
		
		DateTime calendar = new DateTime (parent, SWT.CALENDAR);
		calendar.addSelectionListener (new SelectionAdapter () {
			public void widgetSelected (SelectionEvent e) {
				// Insert date in document?
			}
		});
		
		parent.pack();
	}
}

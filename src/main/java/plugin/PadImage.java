package plugin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import pad.Pad;
import view.TestView;

public class PadImage extends Pad {

	public PadImage(String id) {
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
				TestView.getPadManager().addPad(new PadImage("PadID_Image"), 0);
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
	public void createPartControl(final Composite parent)
	{
		parent.setLayout(new RowLayout());
		
		final Label label = new Label(parent, SWT.BORDER);
		Button button = new Button(parent, SWT.PUSH);
		
		button.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				FileDialog dialog = new FileDialog((Shell) parent.getParent());
				String filename = dialog.open();
				
				if (filename != null) {
					Image image = new Image(label.getDisplay(), filename);
					if (image != null) 
						label.setImage(image);	
				}
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		parent.pack();
	}
}

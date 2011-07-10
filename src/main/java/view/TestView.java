package view;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import pad.Pad;
import pad.PadManager;
import plugin.PadDT;

import container.Container;
import container.ContainerManager;
import container.ContainerManagerEvent;
import container.IContainerManagerListener;
import container.StyledTextManager;

public class TestView extends ViewPart {
	public static final String ID = "Test.view";

	private TextViewer fTextViewer;
	private TreeViewer fContainerTreeViewer;
	private TreeViewer fPadTreeViewer;
	//private TreeViewer fCheckTreeViewer;
	
	private IDocument fDocument;
	private ContainerManager fContainerManager;
	private static PadManager fPadManager;
	
	public TestView() {
		fDocument = new Document();
		fContainerManager = new ContainerManager(fDocument);
		fPadManager = new PadManager(fContainerManager);
	}

	/* XXX: Временное решения для обеспечения возможности добавление 
	 * своих действия на главный контрол */
	private static Composite fMainComposite;
	public static Composite getMainComposite()
	{
		return fMainComposite;
	}
	
	public static PadManager getPadManager()
	{
		return fPadManager;
	}
	
	@Override
	public void createPartControl(Composite parent) {
		fMainComposite = parent;
		fTextViewer = new TextViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		fTextViewer.setDocument(fDocument);
		//Container.setStyledTextManager(new StyledTextManager(fTextViewer.getTextWidget()));
		Container.setStyledTextManager(fTextViewer.getTextWidget());
		
		Button button = new Button(parent, SWT.PUSH);
		
		button.addMouseListener(new MouseListener() {
			@Override
			public void mouseDown(MouseEvent e) {				
				fPadManager.addPad(new Pad("PadID"), 0);
			}
			
			@Override
			public void mouseUp(MouseEvent e) {
			}			
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}
		});
		
		button.setText("Add pad");
		
		// Register plugin
		new PadDT("PadDT");
		
		// button.Add
		
		//fTextViewer.setInput(getViewSite());

		fContainerTreeViewer = new TreeViewer(parent);		
		fContainerTreeViewer.setLabelProvider(new LabelProvider());
		fContainerTreeViewer.setContentProvider(new ContainerTreeViewerContentProvider());
		fContainerTreeViewer.setInput(fContainerManager);
		
		fPadTreeViewer = new TreeViewer(parent);
		fPadTreeViewer.setLabelProvider(new LabelProvider());
		fPadTreeViewer.setContentProvider(new PadTreeViewerContentProvider());
		fPadTreeViewer.setInput(fPadManager);
		
		//fCheckTreeViewer = new TreeViewer(parent);		
		//fCheckTreeViewer.setLabelProvider(new LabelProvider());
		//fCheckTreeViewer.setContentProvider(new TreeViewerContentProviderCheck());
		//fCheckTreeViewer.setInput(fContainerManager);
		
		fContainerManager.addContainerManagerListener(new IContainerManagerListener() {
			@Override
			public void debugNotification(ContainerManagerEvent event) {
				fContainerTreeViewer.refresh();
				fPadTreeViewer.refresh();
			}
			
			@Override
			public void containerCreated(ContainerManagerEvent event) {
			}

			@Override
			public void containerDuplicated(ContainerManagerEvent event) {	
			}

			@Override
			public void containerRemoved(ContainerManagerEvent event) {				
			}
		});
	}

	@Override
	public void setFocus() {
		fTextViewer.getControl().setFocus();
	}
}

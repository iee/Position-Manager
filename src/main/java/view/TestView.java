package view;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import container.Container;
import container.ContainerManager;
import container.IStateChangedListener;
import container.StateChangedEvent;
import container.StyledTextManager;

public class TestView extends ViewPart {
	public static final String ID = "Test.view";

	private TextViewer fTextViewer;
	private TreeViewer fContainerTreeViewer;
	private TreeViewer fCheckTreeViewer;
	private IDocument fDocument;
	private ContainerManager fContainerManager;
	
	public TestView() {
		fDocument = new Document();
		fContainerManager = new ContainerManager(fDocument);
	}

	@Override
	public void createPartControl(Composite parent) {
		fTextViewer = new TextViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		fTextViewer.setDocument(fDocument);

		
		Button button = new Button(parent, SWT.PUSH);
		
		button.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseDown(MouseEvent e) {				
					fContainerManager.RequestContainerAllocation(new Position(0));
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		button.setText("Add container");
		
		// button.Add
		
		//fTextViewer.setInput(getViewSite());
		
		Container.setStyledTextManager(new StyledTextManager(fTextViewer.getTextWidget()));

		fContainerTreeViewer = new TreeViewer(parent);		
		fContainerTreeViewer.setLabelProvider(new LabelProvider());
		fContainerTreeViewer.setContentProvider(new TreeViewerContentProvider());
		fContainerTreeViewer.setInput(fContainerManager);
		
		//fCheckTreeViewer = new TreeViewer(parent);		
		//fCheckTreeViewer.setLabelProvider(new LabelProvider());
		//fCheckTreeViewer.setContentProvider(new TreeViewerContentProviderCheck());
		//fCheckTreeViewer.setInput(fContainerManager);
		
		fContainerManager.addStateChangedListener(new IStateChangedListener() {
			@Override
			public void stateChanged(StateChangedEvent event) {
				fContainerTreeViewer.refresh();	
				//fCheckTreeViewer.refresh();
			}
		});
	}

	@Override
	public void setFocus() {
		fTextViewer.getControl().setFocus();
	}
}
package view;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import position.IStateChangedListener;
import position.PositionManager;
import position.StateChangedEvent;

public class TestView extends ViewPart {
	public static final String ID = "Test.view";

	private TextViewer fTextViewer;
	private TreeViewer fTreeViewer; 
	private IDocument fDocument;
	private PositionManager fPositionManager;

	public TestView() {
		fDocument = new Document();
		fPositionManager = new PositionManager(fDocument);
	}

	@Override
	public void createPartControl(Composite parent) {
		fTextViewer = new TextViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		fTextViewer.setDocument(fDocument);
		//fTextViewer.setInput(getViewSite());
		
		fTreeViewer = new TreeViewer(parent);		
		fTreeViewer.setLabelProvider(new LabelProvider());
		fTreeViewer.setContentProvider(new TreeViewerContentProvider());
		fTreeViewer.setInput(fPositionManager);
		
		fPositionManager.addStateChangedListener(new IStateChangedListener() {
			@Override
			public void stateChanged(StateChangedEvent event) {
				fTreeViewer.refresh();				
			}
		});
	}

	@Override
	public void setFocus() {
		fTextViewer.getControl().setFocus();
	}
}
package view;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import container.ContainerManager;
import container.IStateChangedListener;
import container.StateChangedEvent;


public class TestView extends ViewPart {
	public static final String ID = "Test.view";

	private TextViewer fTextViewer;
	private TreeViewer fPadsTreeViewer;
	private TreeViewer fCheckTreeViewer;
	private IDocument fDocument;
	private ContainerManager fPositionManager;

	public TestView() {
		fDocument = new Document();
		fPositionManager = new ContainerManager(fDocument);
	}

	@Override
	public void createPartControl(Composite parent) {
		fTextViewer = new TextViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		fTextViewer.setDocument(fDocument);
		//fTextViewer.setInput(getViewSite());
		
		fPadsTreeViewer = new TreeViewer(parent);		
		fPadsTreeViewer.setLabelProvider(new LabelProvider());
		fPadsTreeViewer.setContentProvider(new TreeViewerContentProvider());
		fPadsTreeViewer.setInput(fPositionManager);
		
		fCheckTreeViewer = new TreeViewer(parent);		
		fCheckTreeViewer.setLabelProvider(new LabelProvider());
		fCheckTreeViewer.setContentProvider(new TreeViewerContentProviderCheck());
		fCheckTreeViewer.setInput(fPositionManager);
		
		fPositionManager.addStateChangedListener(new IStateChangedListener() {
			@Override
			public void stateChanged(StateChangedEvent event) {
				fPadsTreeViewer.refresh();	
				fCheckTreeViewer.refresh();
			}
		});
	}

	@Override
	public void setFocus() {
		fTextViewer.getControl().setFocus();
	}
}
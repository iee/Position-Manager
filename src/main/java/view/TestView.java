package view;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import container.Container;
import container.ContainerManager;
import container.ContainerManagerEvent;
import container.IContainerManagerListener;
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
		
		Container.setStyledTextManager(new StyledTextManager(fTextViewer.getTextWidget()));
		
		//fTextViewer.setInput(getViewSite());
		fContainerTreeViewer = new TreeViewer(parent);		
		fContainerTreeViewer.setLabelProvider(new LabelProvider());
		fContainerTreeViewer.setContentProvider(new TreeViewerContentProvider());
		fContainerTreeViewer.setInput(fContainerManager);
		
		//fCheckTreeViewer = new TreeViewer(parent);		
		//fCheckTreeViewer.setLabelProvider(new LabelProvider());
		//fCheckTreeViewer.setContentProvider(new TreeViewerContentProviderCheck());
		//fCheckTreeViewer.setInput(fContainerManager);
		
		fContainerManager.addContainerManagerListener(new IContainerManagerListener() {
			@Override
			public void containerCreated(ContainerManagerEvent event) {
				fContainerTreeViewer.refresh();	
				//fCheckTreeViewer.refresh();
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
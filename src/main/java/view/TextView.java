package view;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import position.PositionManager;

public class TextView extends ViewPart {
	public static final String ID = "Test.view";

	private TextViewer viewer;
	private final IDocument fDocument;


	public TextView() {
		fDocument = new Document();
		new PositionManager(fDocument);
	}

	/**
	 * This is a call back that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		viewer = new TextViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setDocument(fDocument);
		viewer.setInput(getViewSite());
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
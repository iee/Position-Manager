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
import plugin.ImagePad;
import plugin.PadDT;

import container.Container;
import container.ContainerManager;
import container.ContainerManagerEvent;
import container.IContainerManagerListener;

public class TestView extends ViewPart {
	public static final String ID = "Test.view";

	private TextViewer fTextViewer;
	private TreeViewer fContainerTreeViewer;
//	private TreeViewer fPadTreeViewer;
	
	private IDocument fDocument;
	private ContainerManager fContainerManager;
	private PadManager fPadManager;
		
	@Override
	public void createPartControl(Composite parent) {
		initPadPlatform(parent);
		
		initColendarButton(parent);
		initSimplePadButton(parent);
		initImageButton(parent);

		fContainerTreeViewer = new TreeViewer(parent);		
		fContainerTreeViewer.setLabelProvider(new LabelProvider());
		fContainerTreeViewer.setContentProvider(new ContainerTreeViewerContentProvider());
		fContainerTreeViewer.setInput(fContainerManager);
		
/*		fPadTreeViewer = new TreeViewer(parent);
		fPadTreeViewer.setLabelProvider(new LabelProvider());
		fPadTreeViewer.setContentProvider(new PadTreeViewerContentProvider());
		fPadTreeViewer.setInput(fPadManager); */
				
		fContainerManager.addContainerManagerListener(new IContainerManagerListener() {
			@Override
			public void debugNotification(ContainerManagerEvent event) {
				fContainerTreeViewer.refresh();
//				fPadTreeViewer.refresh();
			}
			
			@Override public void containerCreated(ContainerManagerEvent event) {}
			@Override public void containerDuplicated(ContainerManagerEvent event) {}
			@Override public void containerRemoved(ContainerManagerEvent event) {}
		});
	}
	
	private void initPadPlatform(Composite parent) {
		fDocument = new Document();		
		fTextViewer = new TextViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		fTextViewer.setDocument(fDocument);		
		Container.setStyledText(fTextViewer.getTextWidget());		
		fContainerManager = new ContainerManager(fDocument, fTextViewer.getTextWidget());
		fPadManager = new PadManager(fContainerManager);
	}
	
	
	private void initImageButton(Composite parent) {
		Button button = new Button(parent, SWT.PUSH);
		button.setText("Add image");
		
		button.addMouseListener(new MouseListener() {
			@Override
			public void mouseDown(MouseEvent e) {				
				fPadManager.addPad(new ImagePad(), fTextViewer.getTextWidget().getCaretOffset());
			}
			
			@Override public void mouseUp(MouseEvent e) {}
			@Override public void mouseDoubleClick(MouseEvent e) {}
		});
	}
	
	
	private void initColendarButton(Composite parent) {
		Button button = new Button(parent, SWT.PUSH);
		button.setText("Add calendar");
		
		button.addMouseListener(new MouseListener() {
			@Override
			public void mouseDown(MouseEvent e) {
				fPadManager.addPad(new PadDT(), fTextViewer.getTextWidget().getCaretOffset());
			}
			
			@Override public void mouseUp(MouseEvent e) {}
			@Override public void mouseDoubleClick(MouseEvent e) {}
		});
	}
	
	
	private void initSimplePadButton(Composite parent) {
		Button button = new Button(parent, SWT.PUSH);
		button.setText("Add simple pad");
		
		button.addMouseListener(new MouseListener() {
			@Override
			public void mouseDown(MouseEvent e) {				
				fPadManager.addPad(new Pad(), fTextViewer.getTextWidget().getCaretOffset());
			}
			
			@Override public void mouseUp(MouseEvent e) {}
			@Override public void mouseDoubleClick(MouseEvent e) {}
		});
	}

	
	@Override
	public void setFocus() {
		fTextViewer.getControl().setFocus();
	}
}

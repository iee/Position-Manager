package view;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.rules.DefaultPartitioner;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.DefaultAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.editors.text.TextEditor;

import container.ContainerManager;
import container.IContainerManagerListener;
import container.IEEDocumentProvider;

public class TestView extends TextEditor {

	public static final String ID = "Test.view";

	private TextEditor fEditor;
	private SourceViewer fViewer;
	private ProjectionViewer fProjectionViewer;
	private IDocument fDocument;
	private ContainerManager fContainerManager;
	private ProjectionAnnotationModel fProjectionAnnotationModel;
    private ProjectionSupport fProjectionSupport;
    

	
    public TestView() {
    	super();
    	// fDocument = new Document();
    	// fContainerManager = new ContainerManager(fProjectionViewer);
        // setSourceViewerConfiguration(new SourceViewerConfiguration());
    	// setDocumentProvider(new IEEDocumentProvider());
    }
    
    @Override
	public void createPartControl(Composite parent) {
    	super.createPartControl(parent);
    	
    	ProjectionViewer viewer = (ProjectionViewer) getSourceViewer();
        
    	fProjectionSupport = new ProjectionSupport(viewer, getAnnotationAccess(), getSharedColors());
    	fProjectionSupport.install();
	
		viewer.doOperation(ProjectionViewer.TOGGLE);
		
		fProjectionAnnotationModel = viewer.getProjectionAnnotationModel();
		    	
    	fContainerManager = new ContainerManager(viewer.getDocument(), fProjectionAnnotationModel);
    	
    	System.out.println(viewer.getDocument());
    	// System.out.println(fViewer.getDocument());
		//fTextViewer.setInput(getViewSite());
		
//		fPadsTreeViewer = new TreeViewer(parent);		
//		fPadsTreeViewer.setLabelProvider(new LabelProvider());
//		fPadsTreeViewer.setContentProvider(new TreeViewerContentProvider());
//		fPadsTreeViewer.setInput(fContainerManager);
		
//		fCheckTreeViewer = new TreeViewer(parent);		
//		fCheckTreeViewer.setLabelProvider(new LabelProvider());
//		fCheckTreeViewer.setContentProvider(new TreeViewerContentProviderCheck());
//		fCheckTreeViewer.setInput(fContainerManager);
		
//		fContainerManager.addStateChangedListener(new IContainerManagerListener() {
//			@Override
//			public void embeddedRangeSetChanged(EmbeddedRangeSetChangedEvent event) {
//				fPadsTreeViewer.refresh();	
//				fCheckTreeViewer.refresh();
//			}
//		});
	}
	@Override
	public void setFocus() {
		fProjectionViewer.getControl().setFocus();
	}
	
    protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
    	fProjectionViewer = new ProjectionViewer(parent, ruler, getOverviewRuler(), isOverviewRulerVisible(), styles);
        
    	// ensure decoration support has been created and configured.
    	getSourceViewerDecorationSupport(fProjectionViewer);
    	return fProjectionViewer;
    }
}
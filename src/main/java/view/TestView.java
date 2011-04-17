package view;

import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.editors.text.TextEditor;

import container.ContainerManager;

public class TestView extends TextEditor {

	public static final String ID = "Test.view";

	private ProjectionViewer fProjectionViewer;
	private ProjectionAnnotationModel fProjectionAnnotationModel;
    private ProjectionSupport fProjectionSupport;

    public TestView() {
    	super();
    }

    @Override
	public void createPartControl(Composite parent) {
    	super.createPartControl(parent);

    	ProjectionViewer viewer = (ProjectionViewer) getSourceViewer();

    	fProjectionSupport = new ProjectionSupport(viewer, getAnnotationAccess(), getSharedColors());
    	fProjectionSupport.install();

		viewer.doOperation(ProjectionViewer.TOGGLE);

		fProjectionAnnotationModel = viewer.getProjectionAnnotationModel();

    	new ContainerManager(viewer.getDocument(), fProjectionAnnotationModel);

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

    @Override
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
    	fProjectionViewer = new ProjectionViewer(parent, ruler, getOverviewRuler(), isOverviewRulerVisible(), styles);
    	getSourceViewerDecorationSupport(fProjectionViewer);

    	return fProjectionViewer;
    }
}
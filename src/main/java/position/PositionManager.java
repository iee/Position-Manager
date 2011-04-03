package position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.core.commands.common.EventManager;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.DocumentPartitioningChangedEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IDocumentPartitionerExtension2;
import org.eclipse.jface.text.IDocumentPartitioningListener;
import org.eclipse.jface.text.IDocumentPartitioningListenerExtension2;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.rules.FastPartitioner;

public class PositionManager extends EventManager {
	
	private IDocument fDocument;
	private IDocumentPartitioner fDocumentPartitioner;
	private String fPartitioningCategory;
	
	private ArrayList<EmbeddedRange> fEmbeddedRanges;
	
	private TreeMap<Position, EmbeddedRange> fPositionToEmbeddedRange;
	

	public EmbeddedRange getEmbeddedRange(String id) {
		Iterator<EmbeddedRange> it = fEmbeddedRanges.iterator();
		while (it.hasNext()) {
			EmbeddedRange element = it.next();
			if (element.getId().equals(id)) {
				return element;
			}
		}
		return null;
	}
	
	public Object[] getEmbeddedRanges() {
		return fEmbeddedRanges.toArray();
	}
	
	public void addStateChangedListener(IStateChangedListener listener) {
		Assert.isNotNull(listener);
		addListenerObject(listener);
	}
	
	public void removeStateChangedListener(IStateChangedListener listener) {
		Assert.isNotNull(listener);
		removeListenerObject(listener);
	}
	
	protected void doFireStateChangedEvent(StateChangedEvent event) {		
		Object[] listeners = getListeners();
		for (int i = 0; i < listeners.length; i++) {
			((IStateChangedListener) listeners[i]).stateChanged(event);
		}
	}
	
	public PositionManager(IDocument document) {
		fPositionToEmbeddedRange = new TreeMap<Position, EmbeddedRange>();
		fEmbeddedRanges = new ArrayList<EmbeddedRange>();
		fDocument = document;
		
		fDocumentPartitioner = new FastPartitioner(
			new PartitioningScanner(),
			new String[] { IConfiguration.CONTENT_TYPE_EMBEDDED });
			
		String[] categories = ((IDocumentPartitionerExtension2) fDocumentPartitioner).getManagingPositionCategories();
		fPartitioningCategory = categories[0];
		
		((IDocumentExtension3) fDocument).setDocumentPartitioner(
				IConfiguration.PARTITIONING_ID, fDocumentPartitioner);
		
		initDocumentChangeListener();
		initPartitioningChangeListener();
		
		fDocumentPartitioner.connect(fDocument);
	}
			
	protected void initDocumentChangeListener() {		
		fDocument.addDocumentListener(new IDocumentListener() {        	
        	@Override
        	public void documentChanged(DocumentEvent event) {
				try {				
					ITypedRegion region = ((IDocumentExtension3) fDocument)
						.getPartition(IConfiguration.PARTITIONING_ID, event.getOffset(), false);
					
					if (region.getType().equals(IConfiguration.CONTENT_TYPE_EMBEDDED)) {						
						Position offsetPosition = new Position(event.getOffset(), 0);
						
						Position oldElementPosition = fPositionToEmbeddedRange.floorKey(offsetPosition);
						Position newElementPosition = new Position(region.getOffset(), region.getLength());
					
						EmbeddedRange element = fPositionToEmbeddedRange.remove(oldElementPosition);
						element.setPosition(newElementPosition);
								
						fPositionToEmbeddedRange.put(newElementPosition, element);
					}

					Position moveFrom = fPositionToEmbeddedRange.higherKey(
						new Position(event.getOffset() + event.getLength(), 0));
															
					SortedMap<Position, EmbeddedRange> moving = fPositionToEmbeddedRange.tailMap(moveFrom, true);
					
	
					//Position[] positions = fDocument.getPositions(fPartitioningCategory);
					
					//trace("My positions: " + Arrays.toString(positions));
					
/*					fEmbeddedRanges.clear();
					int id = 0;
					for (Position position : positions) {	
						fEmbeddedRanges.add(new EmbeddedRange(Integer.toString(id++), "simple", position));
					}*/
					
					doFireStateChangedEvent(new StateChangedEvent());
					
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BadPartitioningException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
        	
        	@Override
        	public void documentAboutToBeChanged(DocumentEvent event) {
    			//System.out.println(event.toString());
        	}        	
		});
//		boolean containsPositionCategory(String category);
//		Position[] getPositions(String category) throws BadPositionCategoryException;
//		boolean containsPosition(String category, int offset, int length);
//		int computeIndexInCategory(String category, int offset) throws BadLocationException, BadPositionCategoryException;
//		String getContentType(int offset) throws BadLocationException;
	}
	
	protected void initPartitioningChangeListener() {		
		class DocumentPartitioningListener implements
		IDocumentPartitioningListener,
		IDocumentPartitioningListenerExtension2 {
			@Override
			public void documentPartitioningChanged(DocumentPartitioningChangedEvent event) {
				if (event.isEmpty()) {
					trace("no changes");
				}

				IRegion changedRegion = event.getChangedRegion(IConfiguration.CONTENT_TYPE_EMBEDDED);
				trace("Region: " + changedRegion.toString());
	
				for (String s : event.getChangedPartitionings()) {
					trace("Changed partitionings: " + s);
				}
								
				AbstractDocument abstractDocument = (AbstractDocument) fDocument;
				try {
					Position[] positions = abstractDocument.getPositions(fPartitioningCategory);
					trace("Class:" + positions[0].getClass().getName());
					trace(Arrays.toString(positions));
				} catch (BadPositionCategoryException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void documentPartitioningChanged(IDocument document) {
				trace("");
			}
		}		
		fDocument.addDocumentPartitioningListener(new DocumentPartitioningListener());
//		ITypedRegion getPartition(String partitioning, int offset, boolean preferOpenPartitions) throws BadLocationException, BadPartitioningException;
//		ITypedRegion[] computePartitioning(String partitioning, int offset, int length, boolean includeZeroLengthPartitions) throws BadLocationException, BadPartitioningException;
	}
	
    private static void trace(String message) {
    	System.out.println(Thread.currentThread().getStackTrace()[3].getMethodName() + "() " + message);
  	}
}

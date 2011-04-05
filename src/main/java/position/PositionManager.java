package position;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;

import org.eclipse.core.commands.common.EventManager;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.DocumentPartitioningChangedEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IDocumentPartitioningListener;
import org.eclipse.jface.text.IDocumentPartitioningListenerExtension2;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.rules.FastPartitioner;

public class PositionManager extends EventManager {
	
	private IDocument fDocument;
	private IDocumentPartitioner fDocumentPartitioner;
	
	private NavigableSet<EmbeddedRange> fEmbeddedRanges;
	private Map<String, EmbeddedRange> fIdToEmbeddedRangeMap;
	
	
	/* Public interface */

	public EmbeddedRange getEmbeddedRange(String id) {
		return fIdToEmbeddedRangeMap.get(id);		
	}
	
	public Object[] getEmbeddedRanges() {
		return fEmbeddedRanges.toArray();
	}
	
	
	/* Functions for observers */
	
	public void addStateChangedListener(IStateChangedListener listener) {
		Assert.isNotNull(listener);
		addListenerObject(listener);
	}
	
	public void removeStateChangedListener(IStateChangedListener listener) {
		Assert.isNotNull(listener);
		removeListenerObject(listener);
	}
	
	protected void fireStateChangedEvent(StateChangedEvent event) {		
		Object[] listeners = getListeners();
		for (int i = 0; i < listeners.length; i++) {
			((IStateChangedListener) listeners[i]).stateChanged(event);
		}
	}
	
	public PositionManager(IDocument document) {
		fEmbeddedRanges = new TreeSet<EmbeddedRange>(new EmbeddedRangeComparator());
		fIdToEmbeddedRangeMap = new HashMap<String, EmbeddedRange>();
		fDocument = document;
		
		fDocumentPartitioner = new FastPartitioner(
			new PartitioningScanner(),
			new String[] { IConfiguration.CONTENT_TYPE_EMBEDDED });
					
		((IDocumentExtension3) fDocument).setDocumentPartitioner(
				IConfiguration.PARTITIONING_ID, fDocumentPartitioner);
			
		initDocumentListener();		
		fDocumentPartitioner.connect(fDocument);
		
		System.out.println("dddd");
	}
				
	protected void initDocumentListener() {		
		class DocumentListener implements
		IDocumentListener, IDocumentPartitioningListener, IDocumentPartitioningListenerExtension2
		{        	
        	private boolean fIsPartitionChanged;
			
        	DocumentListener() {
        		fIsPartitionChanged = false;
        	}
        	
        	@Override
			public void documentPartitioningChanged(DocumentPartitioningChangedEvent event) {
        		if (event.getChangedRegion(IConfiguration.PARTITIONING_ID) != null) {
					fIsPartitionChanged = true;
				}
			}
        	
			@Override
        	public void documentChanged(DocumentEvent event) {
		   		trace("");
				try {
					if (fIsPartitionChanged) {
						
						/* Case 1:
						 * Changed text area contains some embedded regions
						 * (occurred when document partitioning changes)
						 */					
						onPartitioningChanged(event);
					} else {					
						ITypedRegion region = ((IDocumentExtension3) fDocument)
							.getPartition(IConfiguration.PARTITIONING_ID, event.getOffset(), false);
						
						if (region.getType().equals(IConfiguration.CONTENT_TYPE_EMBEDDED)) {
							
							/* Case 2:
							 * Changed text area is inside the embedded region
							 */						
							onChangeInsideEmbeddedRange(event, region);			
						} else {
							
							/* Case 3:
							 * Changed text is outside of any embedded regions
							 */	
							onChangeOutsideEmbeddedRanges(event);
						}
					}
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BadPartitioningException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				fIsPartitionChanged = false;
				fireStateChangedEvent(new StateChangedEvent());
			}

			private void onPartitioningChanged(DocumentEvent event) throws BadLocationException, BadPartitioningException {
				
				/* If changed area contains some embedded regions (that means that partitioning change event has occurred),
				 * we have to update embedded ranges set and id mapping. All elements between 'first' and 'last'
				 * were removed, new elements can possibly be created.
				 */
				
				EmbeddedRange first = fEmbeddedRanges.higher(
					EmbeddedRange.atOffset(event.getOffset()));
				
				EmbeddedRange last = fEmbeddedRanges.floor(
					EmbeddedRange.atOffset(event.getOffset() + event.getLength()));
								
				if (first != null && last != null) {
					NavigableSet<EmbeddedRange> forRemoveSet =
						fEmbeddedRanges.subSet(first, true, last, true);
						
					/* Remove elements from set */
						
					Iterator<EmbeddedRange> it = forRemoveSet.iterator();
					while (it.hasNext()) {
						EmbeddedRange e = it.next();
						e.setIsVisiable(false);
						fEmbeddedRanges.remove(e);
					}
				}
				
				/* Scan text area for new elements */

				int offset = event.getOffset();
				while (offset < event.getOffset() + event.getText().length()) {
					ITypedRegion region = ((IDocumentExtension3) fDocument)
						.getPartition(IConfiguration.PARTITIONING_ID, offset, false);
					
					if (region.getType().equals(IConfiguration.CONTENT_TYPE_EMBEDDED)) {
						
						/* Embedded range is found */
						
						EmbeddedRange e = new EmbeddedRange(
							fDocument.get(region.getOffset(), region.getLength()),
							new Position(region.getOffset(), region.getLength()));
						
						trace(e.getId());
						
						if (fIdToEmbeddedRangeMap.containsKey(e.getId())) {
							if (fIdToEmbeddedRangeMap.get(e.getId()).isVisiable()) {
								
								/* Embedded region is duplicated by coping, set new id */
								
								e.setId(e.getId() + "!");
								fIdToEmbeddedRangeMap.put(e.getId(), e);						
							} else {
								
								/* Embedded region was cut before, so get it from map by it's id */
								
								e = fIdToEmbeddedRangeMap.get(e.getId());
							}
						} else {
							
							/* Create embedded region with new unique id */
							
							fIdToEmbeddedRangeMap.put(e.getId(), e);
						}
						
						fEmbeddedRanges.add(e);
						e.setIsVisiable(true);
					}
					offset += region.getLength();
				}	
			}
			
			private void onChangeOutsideEmbeddedRanges(DocumentEvent event) {
				
				int offset = event.getOffset() + event.getLength();
				int delta = event.getText().length() - event.getLength();
				moveEmbeddedRangesAfterOffset(offset, delta);			
			}
			
			private void onChangeInsideEmbeddedRange(DocumentEvent event, ITypedRegion region) {
				EmbeddedRange current = fEmbeddedRanges.floor(EmbeddedRange.atOffset(event.getOffset()));								
				
				Assert.isNotNull(current);
				Assert.isTrue(current.getPosition().getOffset() == region.getOffset());
				
				current.updatePosition(region.getOffset(), region.getLength());
											
				int offset = event.getOffset() + event.getLength();
				int delta = event.getText().length() - event.getLength();
				moveEmbeddedRangesAfterOffset(offset, delta);
			}
			
			private void moveEmbeddedRangesAfterOffset(int offset, int delta) {
				EmbeddedRange from = fEmbeddedRanges.higher(EmbeddedRange.atOffset(offset));
				
				if (from == null)
					return;
				
				NavigableSet<EmbeddedRange> tail = fEmbeddedRanges.tailSet(from, true);
				Iterator<EmbeddedRange> it = tail.iterator();
				while (it.hasNext()) {
					EmbeddedRange e = it.next();
					e.updatePosition(
						e.getPosition().getOffset() + delta,
						e.getPosition().getLength());
				}
			}
			
			@Override public void documentPartitioningChanged(IDocument document) {}
			@Override public void documentAboutToBeChanged(DocumentEvent event) {}        
		}
		
		DocumentListener listener = new DocumentListener();
		fDocument.addDocumentPartitioningListener(listener);
		fDocument.addDocumentListener(listener);
	}
		
    private static void trace(String message) {
    	System.out.println(Thread.currentThread().getStackTrace()[3].getMethodName() + "() " + message);
  	}
}

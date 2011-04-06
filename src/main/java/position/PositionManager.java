package position;

import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeSet;

import org.eclipse.core.commands.common.EventManager;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.DocumentPartitioningChangedEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IDocumentPartitioningListener;
import org.eclipse.jface.text.IDocumentPartitioningListenerExtension2;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.rules.FastPartitioner;

public class PositionManager extends EventManager {
    
    private IDocument fDocument;
    private IDocumentPartitioner fDocumentPartitioner;
    
    private NavigableSet<Pad> fPads;
    private PadComparator fPadComparator;
    
    /* Public interface */
    
    public Object[] getPads() {
        return fPads.toArray();
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
        fPadComparator = new PadComparator();
    	fPads = new TreeSet<Pad>(fPadComparator);
        fDocument = document;
        
        fDocumentPartitioner = new FastPartitioner(
            new PartitioningScanner(),
            new String[] { IConfiguration.CONTENT_TYPE_EMBEDDED });
                    
        ((IDocumentExtension3) fDocument).setDocumentPartitioner(
            IConfiguration.PARTITIONING_ID, fDocumentPartitioner);
            
        initDocumentListener();        
        fDocumentPartitioner.connect(fDocument);
    }
    
    protected Pad getPadContainingOffset(int offset) {
    	Pad p = fPads.lower(Pad.atOffset(offset));
    	if (p != null && p.getPosition().includes(offset)) {
    		return p;
    	}
    	return null;
    }
                
    protected void initDocumentListener() {        
        class DocumentListener implements
        IDocumentListener, IDocumentPartitioningListener, IDocumentPartitioningListenerExtension2
        {            
            private boolean fIsPartitionChanged;
            IRegion changedRegion;
            
            DocumentListener() {
                fIsPartitionChanged = false;
            }
            
            @Override
            public void documentPartitioningChanged(DocumentPartitioningChangedEvent event) {
                trace(event.toString());
                IRegion changedRegion = event.getChangedRegion(IConfiguration.PARTITIONING_ID);
            	if (changedRegion != null) {
            		trace(changedRegion.toString());
                    fIsPartitionChanged = true;
                }
            }
            
            @Override
            public void documentChanged(DocumentEvent event) {
                
            	final int unmodifiedOffset = event.getOffset() + event.getLength();
                final int moveDelta = event.getText().length() - event.getLength();
                
                /* Positive delta means moving unmodified pads forward.
                 * We have to perform this action before any other modifications.
                 */
           
                if (moveDelta > 0) {
                	moveUnmodifiedPads(unmodifiedOffset, moveDelta);
                }
                            	
            	try {
                    if (fIsPartitionChanged) {
                        
                        /* Case 1:
                         * Changed text area contains some embedded regions
                         * (occurred when document partitioning changes)
                         */                    
                        onPartitioningChanged(event);
                        
                    } else {      
                    	Pad current = getPadContainingOffset(event.getOffset());
                        if (current != null) {
                            
                        	/* Case 2:
                             * Changed text area is inside pad area
                             */ 
                        	onChangesInsidePad(current, event);
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
                
                /* If delta is negative, we move unmodified pads backward,
                 * but do it after any other modifications were done.
                 */
                
                if (moveDelta < 0) {
                	moveUnmodifiedPads(unmodifiedOffset, moveDelta);
                }
                
                fIsPartitionChanged = false;
                fireStateChangedEvent(new StateChangedEvent());
            }
            
            private void onPartitioningChanged(DocumentEvent event) throws BadLocationException, BadPartitioningException {
                trace(event.toString());
                /* If changed area contains some embedded regions (that means that partitioning change event has occurred),
                 * we have to update embedded ranges set and id mapping. All elements between 'first' and 'last'
                 * were removed, new elements can possibly be created.
                 */
                
                Pad from = getPadContainingOffset(event.getOffset());
                if (from == null) {
                    from = fPads.ceiling(Pad.atOffset(event.getOffset()));
                }

                Pad	to = fPads.lower(Pad.atOffset(event.getOffset() + event.getLength()));
                                              
                if (from != null && to != null && fPadComparator.isAscending(from, to)) {
                    
                	/* Clear set from removed pads */
                	
                	NavigableSet<Pad> forRemoveSet = fPads.subSet(from, true, to, true);                    
                	if (forRemoveSet != null) {
                		while (forRemoveSet.pollFirst() != null);
                	}
                }
                
                /* Scan text area for new elements */
                
                int offset = event.getOffset();
                while (offset < event.getOffset() + event.getText().length()) {
                    ITypedRegion region = ((IDocumentExtension3) fDocument)
                        .getPartition(IConfiguration.PARTITIONING_ID, offset, false);
                    
                    if (region.getType().equals(IConfiguration.CONTENT_TYPE_EMBEDDED)) {
                        
                        /* Embedded range is found */
                        
                        Pad e = new Pad(new Position(region.getOffset(), region.getLength()));
                                                                        
                        fPads.add(e);
                    }
                    offset += region.getLength();
                }
            }
            
            private void onChangesInsidePad(Pad pad, DocumentEvent event) throws BadLocationException, BadPartitioningException {
                trace(event.toString() + " / " + pad.toString());
            	ITypedRegion region = ((IDocumentExtension3) fDocument)
                	.getPartition(IConfiguration.PARTITIONING_ID, event.getOffset(), false);
            	
                Assert.isTrue(pad.getPosition().getOffset() == region.getOffset());
                
                pad.updatePosition(region.getOffset(), region.getLength());
            }
                        
            private void moveUnmodifiedPads(int offset, int delta) {
            	Pad from = fPads.ceiling(Pad.atOffset(offset));          
            	if (from == null)
                    return;
                
                NavigableSet<Pad> tail = fPads.tailSet(from, true);
                Iterator<Pad> it = tail.iterator();
                while (it.hasNext()) {
                    Pad e = it.next();
                    e.move(delta);
                }
            }
            
            @Override
            public void documentAboutToBeChanged(DocumentEvent event) {}
            
            @Override public void documentPartitioningChanged(IDocument document) {}
        }
        
        DocumentListener listener = new DocumentListener();
        fDocument.addDocumentPartitioningListener(listener);
        fDocument.addDocumentListener(listener);
    }
    
    private static void trace(String message) {
        System.out.println(Thread.currentThread().getStackTrace()[3].getMethodName() + "() " + message);
    }
    
    public Position[] getElementsCheck() {
    	try {
			Position[] positions = fDocument.getPositions(((FastPartitioner)fDocumentPartitioner).getManagingPositionCategories()[0]);
			return positions;	
		} catch (BadPositionCategoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
}

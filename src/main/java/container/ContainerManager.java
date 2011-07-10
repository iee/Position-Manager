package container;

import java.util.Iterator;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;

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
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.rules.FastPartitioner;

public class ContainerManager extends EventManager {

    private final IDocument fDocument;
    private final IDocumentPartitioner fDocumentPartitioner;

    private final Map<String, Container> fID2ContainerMap;
    private final NavigableSet<Container> fContainers;
    private final ContainerComparator fContainerComparator;
    
    private static long fNextContainerID = 0;
    
    private Queue<DocumentEvent> fDocumentModificationRequests;
    private boolean fIsDocumentListenerBusy;

    
    /* Public interface */

    public static String allocateContainerID() {
    	return String.format("%d", fNextContainerID++);
    }
    
    public Object[] getElements() {
        return fContainers.toArray();
    }
        
    public void RequestContainerAllocation(String containerID, int offset) {
    	String containerEmbeddedRegion = 
    		IConfiguration.EMBEDDED_REGION_BEGINS +
			containerID +
			IConfiguration.EMBEDDED_REGION_ENDS + "\n";

   		requestDocumentModification(fDocument, offset, 0, containerEmbeddedRegion);
    }
    
    public void RequestContainerRelease(String containerID) {
    	Container c = fID2ContainerMap.get(containerID);
    	Assert.isNotNull(c);    	
    	Position at = c.getPosition();

   		requestDocumentModification(fDocument, at.getOffset(), at.getLength(), "");
    }
    
    
    /* Functions for observers */

    public void addContainerManagerListener(IContainerManagerListener listener) {
        Assert.isNotNull(listener);
        addListenerObject(listener);
    }

    public void removeContainerManagerListener(IContainerManagerListener listener) {
        Assert.isNotNull(listener);
        removeListenerObject(listener);
    }

    protected void fireContainerCreated(ContainerManagerEvent event) {
        Object[] listeners = getListeners();
        for (int i = 0; i < listeners.length; i++) {
        	((IContainerManagerListener) listeners[i]).containerCreated(event);	
        }
    }

    protected void fireContainerRemoved(ContainerManagerEvent event) {
        Object[] listeners = getListeners();
        for (int i = 0; i < listeners.length; i++) {
        	((IContainerManagerListener) listeners[i]).containerRemoved(event);	
        }
    }
    
    protected void fireContainerDuplicated(ContainerManagerEvent event) {
        Object[] listeners = getListeners();
        for (int i = 0; i < listeners.length; i++) {
            ((IContainerManagerListener) listeners[i]).containerDuplicated(event);
        }
    }
    
    protected void fireDebugNotification(ContainerManagerEvent event) {
        Object[] listeners = getListeners();
        for (int i = 0; i < listeners.length; i++) {
            ((IContainerManagerListener) listeners[i]).debugNotification(event);
        }
    }

    
    /* Constructor */
    
    public ContainerManager(IDocument document) {
        fContainerComparator = new ContainerComparator();
    	fContainers = new TreeSet<Container>(fContainerComparator);
    	fID2ContainerMap = new TreeMap<String, Container>();
        fDocument = document;
        
        fDocumentModificationRequests = new ConcurrentLinkedQueue<DocumentEvent>();
    	fIsDocumentListenerBusy = false;

        fDocumentPartitioner = new FastPartitioner(
            new PartitioningScanner(),
            new String[] { IConfiguration.CONTENT_TYPE_EMBEDDED });

        ((IDocumentExtension3) fDocument).setDocumentPartitioner(
            IConfiguration.PARTITIONING_ID, fDocumentPartitioner);

        initDocumentListener();
        fDocumentPartitioner.connect(fDocument);
    }
    
    /* Presentation update */
    
    private void updateContainerPresentaions() {
    	Iterator<Container> it = fContainers.iterator();
    	while (it.hasNext()) {
    	    Container container = it.next();
    	    container.updatePresentation();
    	}
    }
    
    /* Document modification access */
    
    private void requestDocumentModification(IDocument document, int offset, int length, String text) {
    	DocumentEvent modification = new DocumentEvent(document, offset, length, text);

    	if (fIsDocumentListenerBusy) {
    		fDocumentModificationRequests.add(new DocumentEvent(document, offset, length, text));
    	} else {
    		applyDocumentModification(modification);	
    	}
    }
    
    private void processNextModification() {
    	if (fIsDocumentListenerBusy) {
    		return;
    	}	    
    	
    	DocumentEvent modification = fDocumentModificationRequests.poll();
	    if (modification != null) {
	    	applyDocumentModification(modification);
	    }
    }
    
    private void applyDocumentModification(DocumentEvent modification) {
    	try {
			modification.getDocument().replace(modification.getOffset(), modification.getLength(), modification.getText());
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
  

    /* Document modification event processing */
    
    protected void initDocumentListener() {
        class DocumentListener implements
        IDocumentListener, IDocumentPartitioningListener, IDocumentPartitioningListenerExtension2
        {
            private IRegion fChangedPartitioningRegion;

            public DocumentListener() {
            	fChangedPartitioningRegion = null;
            }
            
            @Override
            public void documentPartitioningChanged(DocumentPartitioningChangedEvent event) {
            	fChangedPartitioningRegion = event.getChangedRegion(IConfiguration.PARTITIONING_ID);
            }
   
            @Override
            public void documentChanged(DocumentEvent event) {                
            	fIsDocumentListenerBusy = true;
            	
            	/* All pads which placed after 'unmodifiedOffset'
            	 * are considered to be just moved without any other modifications.
            	 *
            	 * It's calculated according following equation
            	 * 'unmodified offset' = max('end of partitioning changed area', 'end of document changed area') - 'moving_delta'
            	 */

            	int unmodifiedOffset;
            	final int movingDelta = event.getText().length() - event.getLength();

            	if (fChangedPartitioningRegion != null) {
            		unmodifiedOffset = Math.max(
            			event.getOffset() + event.getText().length(),
            			fChangedPartitioningRegion.getOffset() + fChangedPartitioningRegion.getLength());
            		unmodifiedOffset -= movingDelta;
            	} else {
            		unmodifiedOffset = event.getOffset() + event.getLength();
            	}

                /* Positive delta means that unmodified pads move forward.
                 * We have to perform this action before any other modifications to avoid collisions.
                 */

                if (movingDelta > 0) {
                	moveUnmodifiedPads(unmodifiedOffset, movingDelta);
                }

            	try {
                    if (fChangedPartitioningRegion != null) {

                        /* Case 1:
                         * Document partitioning is changed, so updating the set of the pads
                         */
                        onPartitioningChanged(event, unmodifiedOffset);

                    } else {
                    	Container current = getContainerHavingOffset(event.getOffset());
                        if (current != null) {

                        	/* Case 2:
                             * Changed text area is inside current pad's area, updating it
                             */
                        	onChangesInsidePad(current, event);
                        }

                        /* Case 3:
                         * No pad modified, do nothing.
                         */
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
                 * but after any other modifications are done.
                 */

                if (movingDelta < 0) {
                	moveUnmodifiedPads(unmodifiedOffset, movingDelta);
                }
                
                fChangedPartitioningRegion = null;
            	fIsDocumentListenerBusy = false;
                processNextModification();
                
                updateContainerPresentaions();
                /* For debug */
                
                fireDebugNotification(new ContainerManagerEvent(null));
            }

            private void onPartitioningChanged(DocumentEvent event, int unmodifiedOffset) throws BadLocationException, BadPartitioningException {

            	/* Remove all elements within changed area */

                int beginRegionOffset = Math.min(event.getOffset(), fChangedPartitioningRegion.getOffset());

                Container from = fContainers.ceiling(Container.atOffset(beginRegionOffset));
                Container	to = fContainers.lower(Container.atOffset(unmodifiedOffset));

                if (from != null && to != null && fContainerComparator.isNotDescending(from, to)) {
                	NavigableSet<Container> removeSet = fContainers.subSet(from, true, to, true);
                	if (removeSet != null) {
                		Container container;
                		while ((container = removeSet.pollFirst()) != null) {
                			
                			// XXX remove container
                			
                			fID2ContainerMap.remove(container.getContainerID());
                			container.dispose();
                			fireContainerRemoved(new ContainerManagerEvent(container));
                		}
                	}
                }

                /* Scanning for new containers */

                int offset = beginRegionOffset;
                while (offset < fChangedPartitioningRegion.getOffset() + fChangedPartitioningRegion.getLength()) {
                    ITypedRegion region = ((IDocumentExtension3) fDocument)
                        .getPartition(IConfiguration.PARTITIONING_ID, offset, false);

                    if (region.getType().equals(IConfiguration.CONTENT_TYPE_EMBEDDED)) {
                    	String containerTextRegion = fDocument.get(region.getOffset(), region.getLength());                   	
                        
                    	// XXX add container                    	                    	                    	
                        
                    	String containerID = loadContainerIDFromTextRegion(containerTextRegion);                    	
                    	
                    	Container original = fID2ContainerMap.get(containerID); 
                    	if (original != null) {
                    		containerID = allocateContainerID();
                    	}
                    	
                    	Container container = new Container(
                        	new Position(region.getOffset(), region.getLength()),
                        	containerID);
                        
                        fID2ContainerMap.put(containerID, container);
                        fContainers.add(container);
                        
                        if (original == null) {
                        	fireContainerCreated(new ContainerManagerEvent(container));
                        } else {
                            saveContainerID2TextRegion(container, containerID);
                        	fireContainerDuplicated(new ContainerManagerEvent(container, original));
                        }
                    }
                    offset += region.getLength();
                }
            }

            private void onChangesInsidePad(Container container, DocumentEvent event) throws BadLocationException, BadPartitioningException {
            	ITypedRegion region = ((IDocumentExtension3) fDocument)
                	.getPartition(IConfiguration.PARTITIONING_ID, event.getOffset(), false);

                Assert.isTrue(container.getPosition().getOffset() == region.getOffset());
                
                // XXX update container
                container.updatePosition(region.getOffset(), region.getLength());
            }

            private void moveUnmodifiedPads(int offset, int delta) {
            	Container from = fContainers.ceiling(Container.atOffset(offset));
            	if (from == null)
                    return;

                NavigableSet<Container> tail = fContainers.tailSet(from, true);
                Iterator<Container> it = tail.iterator();
                while (it.hasNext()) {
                    Container container = it.next();                                        
                    Position position = container.getPosition();
                    
                    // XXX update container
                    container.updatePosition(			 
                    	position.getOffset() + delta,
                    	position.getLength());
                }
            }
             
            protected Container getContainerHavingOffset(int offset) {
            	Container c = fContainers.lower(Container.atOffset(offset));
            	if (c != null && c.getPosition().includes(offset)) {
            		return c;
            	}
            	return null;
            }
            
            @Override public void documentPartitioningChanged(IDocument document) {}
            @Override public void documentAboutToBeChanged(DocumentEvent event) {}
        }

        DocumentListener listener = new DocumentListener();
        fDocument.addDocumentPartitioningListener(listener);
        fDocument.addDocumentListener(listener);
    }

    
    /* Additional functions */
    
	protected String loadContainerIDFromTextRegion(String text) {	
		int from = IConfiguration.EMBEDDED_REGION_BEGINS.length();
		int to = text.indexOf(IConfiguration.EMBEDDED_REGION_ENDS);
		
		return text.substring(from, to);		
	}
	
	protected void saveContainerID2TextRegion(Container container, String newContainerID) {
		int from = container.getPosition().getOffset() + IConfiguration.EMBEDDED_REGION_BEGINS.length();
		int length = container.getPosition().getLength()
				- IConfiguration.EMBEDDED_REGION_BEGINS.length()
				- IConfiguration.EMBEDDED_REGION_ENDS.length();
		
		requestDocumentModification(fDocument, from, length, newContainerID);
	}
}

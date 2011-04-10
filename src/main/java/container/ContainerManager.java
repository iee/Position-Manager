package container;

import java.util.Iterator;
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
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.rules.FastPartitioner;



public class ContainerManager extends EventManager {

    private final IDocument fDocument;
    private final IDocumentPartitioner fDocumentPartitioner;
    
    private final NavigableSet<Container> fContainers;

    
    /* Public interface */



    public ContainerManager(IDocument document) {
    	fContainers = new TreeSet<Container>();
        fDocument = document;

        fDocumentPartitioner = new FastPartitioner(
            new PartitioningScanner(),
            new String[] { IConfiguration.CONTENT_TYPE_EMBEDDED });

        ((IDocumentExtension3) fDocument).setDocumentPartitioner(
            IConfiguration.PARTITIONING_ID, fDocumentPartitioner);

        initDocumentListener();
        fDocumentPartitioner.connect(fDocument);
    }
    
    public Object[] getContainers() {
        return fContainers.toArray();
    }
    
    public void createContainer(Position position, String id) {
		IContainer container = new Container(
			id, new Position(position.getOffset(), position.getLength()), fDocument);
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
    
    
    /* Internal functions */

    protected Container getContainerHavingOffset(int offset) {
    	Container c = fContainers.lower(Container.atOffset(offset));
    	if (c != null && c.getPosition().includes(offset)) {
    		return c;
    	}
    	return null;
    }

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

            	/* All DocumentRanges which placed after 'unmodifiedOffset'
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

                /* Positive delta means that unmodified DocumentRanges move forward.
                 * We have to perform this action before any other modifications to avoid collisions.
                 */

                if (movingDelta > 0) {
                	moveUnmodifiedContainers(unmodifiedOffset, movingDelta);
                }

            	try {
                    if (fChangedPartitioningRegion != null) {

                        /* Case 1:
                         * Document partitioning is changed, so updating the set of the DocumentRanges
                         */
                        onPartitioningChanged(event, unmodifiedOffset);

                    } else {
                    	Container current = getContainerHavingOffset(event.getOffset());
                        if (current != null) {

                        	/* Case 2:
                             * Changed text area is inside current DocumentRange's area, updating it
                             */
                        	onChangesInsideConteiner(current, event);
                        }

                        /* Case 3:
                         * No DocumentRange modified, do nothing.
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

                /* If delta is negative, we move unmodified DocumentRanges backward,
                 * but after any other modifications are done.
                 */

                if (movingDelta < 0) {
                	moveUnmodifiedContainers(unmodifiedOffset, movingDelta);
                }

                fChangedPartitioningRegion = null;
            }

            private void onPartitioningChanged(DocumentEvent event, int unmodifiedOffset) throws BadLocationException, BadPartitioningException {

            	/* Remove all elements within changed area */

                int beginRegionOffset = Math.min(event.getOffset(), fChangedPartitioningRegion.getOffset());

                Container from = fContainers.ceiling(Container.atOffset(beginRegionOffset));
                Container to = fContainers.lower(Container.atOffset(unmodifiedOffset));

                if (from != null && to != null && from.compareTo(to) <= 0) {
                	NavigableSet<Container> removeSet = fContainers.subSet(from, true, to, true);
                	if (removeSet != null) {
                		Container c;
                		while ((c = removeSet.pollFirst()) != null) {
                			fireContainerRemoved(new ContainerManagerEvent(c));
                		}
                	}
                }

                /* Scanning for new DocumentRanges */

                int offset = beginRegionOffset;
                while (offset < fChangedPartitioningRegion.getOffset() + fChangedPartitioningRegion.getLength()) {
                    ITypedRegion region = ((IDocumentExtension3) fDocument)
                        .getPartition(IConfiguration.PARTITIONING_ID, offset, false);

                    if (region.getType().equals(IConfiguration.CONTENT_TYPE_EMBEDDED)) {
                        
                    	Container c = new Container(
                        	new Position(region.getOffset(), region.getLength()),
                        	fDocument);
                        
                        fContainers.add(c);
                        fireContainerCreated(new ContainerManagerEvent(c));
                    }
                    offset += region.getLength();
                }
            }

            private void onChangesInsideConteiner(Container container, DocumentEvent event) throws BadLocationException, BadPartitioningException {
            	ITypedRegion region = ((IDocumentExtension3) fDocument)
                	.getPartition(IConfiguration.PARTITIONING_ID, event.getOffset(), false);

                Assert.isTrue(container.getPosition().getOffset() == region.getOffset());

                container.updatePosition(region.getOffset(), region.getLength());
            }

            private void moveUnmodifiedContainers(int offset, int delta) {
            	Container from = fContainers.ceiling(Container.atOffset(offset));
            	if (from == null)
                    return;

                NavigableSet<Container> tail = fContainers.tailSet(from, true);
                Iterator<Container> it = tail.iterator();
                while (it.hasNext()) {
                    Container c = it.next();
                    c.movePosition(delta);
                }
            }

            @Override public void documentAboutToBeChanged(DocumentEvent event) {}
            @Override public void documentPartitioningChanged(IDocument document) {}
        }

        DocumentListener listener = new DocumentListener();
        fDocument.addDocumentPartitioningListener(listener);
        fDocument.addDocumentListener(listener);
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
}

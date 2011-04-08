package container;

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

public class ContainerManager extends EventManager {

    private final IDocument fDocument;
    private final IDocumentPartitioner fDocumentPartitioner;

    private final NavigableSet<Container> fContainers;
    private final ContainerComparator fContainerComparator;

    /* Public interface */

    public Object[] getContainers() {
        return fContainers.toArray();
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

    public ContainerManager(IDocument document) {
        fContainerComparator = new ContainerComparator();
    	fContainers = new TreeSet<Container>(fContainerComparator);
        fDocument = document;

        fDocumentPartitioner = new FastPartitioner(
            new PartitioningScanner(),
            new String[] { IConfiguration.CONTENT_TYPE_EMBEDDED });

        ((IDocumentExtension3) fDocument).setDocumentPartitioner(
            IConfiguration.PARTITIONING_ID, fDocumentPartitioner);

        initDocumentListener();
        fDocumentPartitioner.connect(fDocument);
    }

    protected Container getPadContainingOffset(int offset) {
    	Container p = fContainers.lower(Container.atOffset(offset));
    	if (p != null && p.getPosition().includes(offset)) {
    		return p;
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
            	if (fChangedPartitioningRegion != null) {
            		trace(fChangedPartitioningRegion.toString());
                }
            }

            @Override
            public void documentChanged(DocumentEvent event) {

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
                    	Container current = getPadContainingOffset(event.getOffset());
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
                fireStateChangedEvent(new StateChangedEvent());
            }

            private void onPartitioningChanged(DocumentEvent event, int unmodifiedOffset) throws BadLocationException, BadPartitioningException {

            	/* Remove all elements within changed area */

                int beginRegionOffset = Math.min(event.getOffset(), fChangedPartitioningRegion.getOffset());

                Container from = fContainers.ceiling(Container.atOffset(beginRegionOffset));
                Container	to = fContainers.lower(Container.atOffset(unmodifiedOffset));

                if (from != null && to != null && fContainerComparator.isNotDescending(from, to)) {
                	NavigableSet<Container> removeSet = fContainers.subSet(from, true, to, true);
                	if (removeSet != null) {
                		while (removeSet.pollFirst() != null);
                	}
                }

                /* Scanning for new pads */

                int offset = beginRegionOffset;
                while (offset < fChangedPartitioningRegion.getOffset() + fChangedPartitioningRegion.getLength()) {
                    ITypedRegion region = ((IDocumentExtension3) fDocument)
                        .getPartition(IConfiguration.PARTITIONING_ID, offset, false);

                    if (region.getType().equals(IConfiguration.CONTENT_TYPE_EMBEDDED)) {
                        Container e = new Container(new Position(region.getOffset(), region.getLength()));
                        fContainers.add(e);
                        trace("added");
                    }
                    offset += region.getLength();
                }
            }

            private void onChangesInsidePad(Container pad, DocumentEvent event) throws BadLocationException, BadPartitioningException {
            	ITypedRegion region = ((IDocumentExtension3) fDocument)
                	.getPartition(IConfiguration.PARTITIONING_ID, event.getOffset(), false);

                Assert.isTrue(pad.getPosition().getOffset() == region.getOffset());

                pad.updatePosition(region.getOffset(), region.getLength());
            }

            private void moveUnmodifiedPads(int offset, int delta) {
            	Container from = fContainers.ceiling(Container.atOffset(offset));
            	if (from == null)
                    return;

                NavigableSet<Container> tail = fContainers.tailSet(from, true);
                Iterator<Container> it = tail.iterator();
                while (it.hasNext()) {
                    Container e = it.next();
                    e.move(delta);
                }
            }

            @Override public void documentAboutToBeChanged(DocumentEvent event) {}
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

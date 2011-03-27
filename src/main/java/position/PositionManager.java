package position;


import java.util.Arrays;

import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DocumentPartitioningChangedEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioningListener;
import org.eclipse.jface.text.IDocumentPartitioningListenerExtension2;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.rules.FastPartitioner;

public class PositionManager {

	private final IDocument fDocument;

	public PositionManager(IDocument document) {
		fDocument = document;
		initPartitioning();
	}

	/**
	 * Used by JUNIT for avoid initPartitioning()
	 */
	public PositionManager()
	{
		fDocument = null;
	}

	/**
	 *
	 * @param current
	 * @param content
	 */
	public void createPosition(Position offset, String content)
	{
		// TODO
	}

	/**
	 *
	 * @param position
	 */
	public void deletePosition(Position position)
	{
		// TODO
	}

	/**
	 *
	 * @param position
	 * @param content
	 */
	public void setPosition(Position position, String content)
	{
		// TODO
	}

	/**
	 *
	 * @param position
	 * @return
	 */
	public String getPosition(Position position)
	{
		// TODO
		return null;
	}

	protected void initPartitioning() {
		final IDocumentExtension3 documentExtension3 = (IDocumentExtension3) fDocument;
		final FastPartitioner partitioner = new FastPartitioner(
			new PartitioningScanner(),
			new String[] { PartitioningScanner.CONTENT_TYPE_EMBEDDED });

		documentExtension3.setDocumentPartitioner(
			PartitioningScanner.CONTENT_TYPE_EMBEDDED, partitioner);

		class DocumentPartitioningListener implements
			IDocumentPartitioningListener,
			IDocumentPartitioningListenerExtension2 {

			@Override
			public void documentPartitioningChanged(DocumentPartitioningChangedEvent event) {
/*				if (event.isEmpty()) {
					trace("no changes");
				}

				IRegion region = event.getChangedRegion(PartitioningScanner.CONTENT_TYPE_EMBEDDED);
				trace("Region: " + region.toString());

				for (String s : event.getChangedPartitionings()) {
					trace("Changed partitionings: " + s);
				}*/


				String[] categories = partitioner.getManagingPositionCategories();
				AbstractDocument abstractDocument = (AbstractDocument) fDocument;
				try {
					Position[] positions = abstractDocument.getPositions(categories[0]);
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
		partitioner.connect(fDocument);
	}

    private static void trace(String message) {
//    	System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName() + ": " + message);
    	System.out.println(Thread.currentThread().getStackTrace()[3].getMethodName() + "() " + message);
  	}
}

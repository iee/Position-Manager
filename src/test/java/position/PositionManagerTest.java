package position;

import junit.framework.TestCase;

import org.eclipse.jface.text.Position;
import org.junit.Test;

import container.ContainerManager;

public class PositionManagerTest extends TestCase {

	ContainerManager fPositionManager;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		/* TODO (pavele): Use mockito */

		//IDocument document = null;

		//fPositionManager = new PositionManager();
	}

	@Test
	public void createPosition() {
		Position positionA = new Position(1, 10);
		String contentA = "/* <--- */\n" + "JSON_OBJECT\n" + "/* ---> */\n";

		//fPositionManager.createPosition(positionA, contentA);

		// TODO(pavel): check created position

		assertEquals(0, 1);
	}

	@Test
	public void deletePostion() {
		// TODO(pavel): too same as above
	}

	@Test
	public void changePartition()
	{
		// TODO
	}
}

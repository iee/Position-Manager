package pad;

import java.util.UUID;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import container.Container;


public abstract class Pad
{
	protected String	fContainerID;
	protected Container	fContainer;
	protected Composite fComposite;

	
	public Pad() {
		fContainerID = UUID.randomUUID().toString();
	}
	
	
	public Pad(String id)
	{
		fContainerID = id;
	}
	
	
	public String get—ontainerID()
	{
		return fContainerID;
	}
	
	
	public boolean isContainerAttached()
	{
		return fContainer != null;
	}
	
	
	public Container getContainer()
	{
		return fContainer;
	}
	
	
	public void attachContainer(Container container)
	{
		Assert.isNotNull(container);
		Assert.isLegal(!isContainerAttached(), "Another container is already attached");
		Assert.isLegal(fContainerID.equals(container.getContainerID()));
		
		fContainer = container;
		createPartControl(fContainer.getComposite());
		fContainer.getComposite().pack();
	}
	
	
	public void detachContainer()
	{
		Assert.isLegal(isContainerAttached(), "No container attached");
		Assert.isLegal(fContainerID.equals(fContainer.getContainerID()));
		
		fContainer = null;
	}
	
	
	/* Abstract methods */
	
	/**
	 * Method is called when Container is attached to Pad
	 */
	public abstract void createPartControl(Composite parent);
	
	
	/**
	 * Copy pad with @param containerID
	 * @return
	 */
	public abstract Pad copy(String containerID);
}

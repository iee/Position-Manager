package pad;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import container.Container;


public abstract class AbstractPad
{
	protected String	fPadID;
	protected Container	fContainer;
	protected Composite fComposite;

	public AbstractPad(String id)
	{
		fPadID = id;
	}
	
	public String getID()
	{
		return fPadID;
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
		fContainer = container;
	}
	
	public void detachContainer()
	{
		Assert.isLegal(isContainerAttached(), "No container attached");
		fContainer = null;
	}
	
	public void resize(int height, int length)
	{		
	}
	/*
	public void addPadListener(IStateChangedListener listener) {
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
    */
}

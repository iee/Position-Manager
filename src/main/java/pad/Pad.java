package pad;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import container.Container;


public class Pad
{
	protected String	fPadID;
	protected Container	fContainer;
	protected Composite fComposite;

	public Pad(String id)
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
		createPartControl(fContainer.getComposite());
	}
	
	public void detachContainer()
	{
		Assert.isLegal(isContainerAttached(), "No container attached");
		fContainer = null;
	}
		
	public void createPartControl(Composite parent)
	{
//		parent.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
//		parent.setSize(new Point(5, 5));

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Cat");
		button.setBounds(0, 0, 100, 100);
		parent.setSize(button.getSize());

	}
}

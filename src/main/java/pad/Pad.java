package pad;

import java.util.UUID;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import container.Container;


public class Pad
{
	protected String	fContainerID;
	protected Container	fContainer;
	protected Composite fComposite;

	public Pad() {
		fContainerID = UUID.randomUUID().toString();
	}
	
	Pad(String id)
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

package container;


public interface IContainerManagerListener {
	public void containerCreated(ContainerManagerEvent event);
	public void containerRemoved(ContainerManagerEvent event);	
}

package container;


public class ContainerManagerEvent {

	private Container fContainer;
	private Container fOriginal;

	ContainerManagerEvent(Container container) {
		fContainer = container;
		fOriginal = null;
	}
	
	ContainerManagerEvent(Container container, Container original) {
		fContainer = container;
		fOriginal = original;
	}	

	public Container getContainer() {
		return fContainer;
	}
	
	public Container getOriginal() {
		return fOriginal;
	}
	
	public boolean isContainerDuplicated() {
		return fOriginal != null;
	}
}
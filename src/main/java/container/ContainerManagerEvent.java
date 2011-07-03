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

	Container getContainer() {
		return fContainer;
	}
	
	Container getOriginal() {
		return fOriginal;
	}
	
	boolean isContainerDuplicated() {
		return fOriginal != null;
	}
}
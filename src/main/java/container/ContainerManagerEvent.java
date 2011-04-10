package container;



public class ContainerManagerEvent {
	
	private IContainer fContainer;
	
	ContainerManagerEvent(IContainer container) {
		fContainer = container;
	}
	
	IContainer getDocumentRange() {
		return fContainer;
	}
}

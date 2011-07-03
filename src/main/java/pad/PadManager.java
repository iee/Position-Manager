package pad;

import org.eclipse.core.runtime.Assert;

import container.Container;
import container.ContainerManager;
import container.ContainerManagerEvent;
import container.IContainerManagerListener;

public class PadManager {
	
	private ContainerManager fContainerManager;
		
	public PadManager(ContainerManager containerManager) {
		fContainerManager = containerManager;
		
		InitListeners();
	}
	
	protected void InitListeners() {
		fContainerManager.addContainerManagerListener(new IContainerManagerListener() {

			@Override
			public void containerCreated(ContainerManagerEvent event) {
				event.getContainer();
				
			}

			@Override
			public void containerDuplicated(ContainerManagerEvent event) {
				Assert.isLegal(event.isContainerDuplicated());
								
			}

			@Override
			public void containerRemoved(ContainerManagerEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	protected void bind(Pad pad, Container container) {
		
	}
	
	protected void unbind(Pad pad) {
		
	}
}

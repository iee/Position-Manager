package pad;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.Assert;

import container.Container;
import container.ContainerManager;
import container.ContainerManagerEvent;
import container.IContainerManagerListener;

public class PadManager {
	
	private ContainerManager fContainerManager;
	
	private Map<String, Pad> fPads = new TreeMap<String, Pad>();
	private Map<String, Pad> fSuspendedPads = new TreeMap<String, Pad>();
		
	public PadManager(ContainerManager containerManager) {
		fContainerManager = containerManager;
		
		InitListeners();
	}
	
	/* Public interface */
	
	public void addPad(Pad pad, int location) {
		String containerID = ContainerManager.allocateContainerID();		
		fSuspendedPads.put(containerID, pad);		
		fContainerManager.RequestContainerAllocation(containerID, location);
	}
	
	public void removePad(Pad pad) {
		if (pad.isContainerAttached()) {
			String containerID = pad.getContainer().getContainerID();
			fContainerManager.RequestContainerRelease(containerID);
		}
	}
	
	/* Internal functions */
		
	protected void InitListeners() {
		fContainerManager.addContainerManagerListener(new IContainerManagerListener() {

			@Override
			public void containerCreated(ContainerManagerEvent event) {
				Container container = event.getContainer();
				Pad pad = fSuspendedPads.get(container.getContainerID());
				Assert.isNotNull(pad);				
				
				pad.attachContainer(container);				
			}

			@Override
			public void containerDuplicated(ContainerManagerEvent event) {
				Assert.isLegal(event.isContainerDuplicated());
				Container container = event.getContainer();
				String containerID = container.getContainerID();
				
				Pad original = fPads.get(containerID);
				Assert.isNotNull(original);
				
				Pad pad = new Pad(containerID);
				fPads.put(containerID, pad);
				pad.attachContainer(container);
			}

			@Override
			public void containerRemoved(ContainerManagerEvent event) {			
				String containerID = event.getContainer().getContainerID();
				Pad pad = fPads.get(containerID);
				Assert.isNotNull(pad);
				
				pad.detachContainer();
				fPads.remove(pad);
				fSuspendedPads.put(containerID, pad);
			}
		});
	}
}

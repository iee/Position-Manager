package container;

import java.util.Comparator;

public class ContainerComparator implements Comparator<Container> {
	
	@Override
	public int compare(Container arg0, Container arg1) {
		Integer offset0 = arg0.getPosition().getOffset();
		Integer offset1 = arg1.getPosition().getOffset();
		return offset0.compareTo(offset1);
	}
	
	public boolean isNotDescending(Container arg0, Container arg1) {
		return compare(arg0, arg1) <= 0;
	}
}

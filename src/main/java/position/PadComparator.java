package position;

import java.util.Comparator;

public class PadComparator implements Comparator<Pad> {
	
	@Override
	public int compare(Pad arg0, Pad arg1) {
		Integer offset0 = arg0.getPosition().getOffset();
		Integer offset1 = arg1.getPosition().getOffset();
		return offset0.compareTo(offset1);
	}
	
	public boolean isNotDescending(Pad arg0, Pad arg1) {
		return compare(arg0, arg1) <= 0;
	}
}

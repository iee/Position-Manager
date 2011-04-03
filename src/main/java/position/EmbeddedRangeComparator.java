package position;

import java.util.Comparator;

public class EmbeddedRangeComparator implements Comparator<EmbeddedRange> {

	@Override
	public int compare(EmbeddedRange arg0, EmbeddedRange arg1) {
		Integer offset0 = arg0.getPosition().getOffset();
		Integer offset1 = arg1.getPosition().getOffset();
		return offset0.compareTo(offset1);
	}	
}

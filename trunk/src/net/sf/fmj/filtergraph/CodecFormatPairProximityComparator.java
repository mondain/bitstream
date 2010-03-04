package net.sf.fmj.filtergraph;

import java.util.Comparator;

import javax.media.Format;

/**
 * 
 * @author Ken Larson
 * 
 */
public class CodecFormatPairProximityComparator implements Comparator {
	private final FormatProximityComparator comparator;

	public CodecFormatPairProximityComparator(final Format target) {
		super();
		this.comparator = new FormatProximityComparator(target);
	}

	public int compare(Object a, Object b) {
		if (a == null && b == null)
			return 0;
		if (a == null) // then a < b, return -1
			return -1;
		if (b == null)
			return 1; // a > b

		final CodecFormatPair aCast = (CodecFormatPair) a;
		final CodecFormatPair bCast = (CodecFormatPair) b;

		return comparator.compare(aCast.getFormat(), bCast.getFormat());
	}

}

package net.sf.fmj.filtergraph;

import java.util.Comparator;

import javax.media.Format;
import javax.media.format.AudioFormat;

import net.sf.fmj.utility.FormatUtils;

import com.lti.utils.ObjUtils;

/**
 * 
 * Compares formats to a target format. Used to improve search order in building
 * filter graph, when a target format is known.
 * 
 * @author Ken Larson
 * 
 */
public class FormatProximityComparator implements Comparator {
	private final Format target;

	public FormatProximityComparator(final Format target) {
		super();
		this.target = target;
	}

	public int compare(Object a, Object b) {
		if (a == null && b == null)
			return 0;
		if (a == null) // then a < b, return -1
			return -1;
		if (b == null)
			return 1; // a > b

		final Format aCast = (Format) a;
		final Format bCast = (Format) b;

		// similarity is higher for better matches, but we need lower values to
		// sort right, so we multiply by -1.
		return -1 * (similarity(aCast, target) - similarity(bCast, target));
	}

	private static final int EQUALS = 0x80000000;
	private static final int MATCHES = 0x40000000;
	private static final int SAME_CLASS = 0x20000000;
	private static final int ASSIGNABLE = 0x10000000;
	private static final int ENCODINGS_EQUAL = 0x08000000;
	private static final int DATA_TYPES_EQUAL = 0x04000000;

	private static int similarity(Format a, Format b) {
		if (a.equals(b))
			return EQUALS;

		int n = 0;

		if (a.matches(b))
			n += MATCHES;

		if (a.getClass() == b.getClass())
			n += SAME_CLASS;

		if (a.getClass().isAssignableFrom(b.getClass())
				|| b.getClass().isAssignableFrom(a.getClass()))
			n += ASSIGNABLE;

		if (ObjUtils.equal(a.getEncoding(), b.getEncoding()))
			n += ENCODINGS_EQUAL;

		if (ObjUtils.equal(a.getDataType(), b.getDataType()))
			n += DATA_TYPES_EQUAL;

		if (a instanceof AudioFormat && b instanceof AudioFormat)
			n += audioFormatSimilarity((AudioFormat) a, (AudioFormat) b);

		// TODO: video format similarity

		return n;

	}

	private static int similarity(int a, int b, int equalsValue,
			int matchesValue) {
		int n = 0;
		if (a == b)
			n += equalsValue;
		if (FormatUtils.matches(a, b))
			n += matchesValue;
		return n;
	}

	private static int similarity(float a, float b, int equalsValue,
			int matchesValue) {
		int n = 0;
		if (a == b)
			n += equalsValue;
		if (FormatUtils.matches(a, b))
			n += matchesValue;
		return n;
	}

	private static int similarity(double a, double b, int equalsValue,
			int matchesValue) {
		int n = 0;
		if (a == b)
			n += equalsValue;
		if (FormatUtils.matches(a, b))
			n += matchesValue;
		return n;
	}

	// all attributes have equal ranking
	private static int SAMPLE_RATE_EQUAL = 0x00800000;
	private static int SAMPLE_RATE_MATCHES = 0x00400000;
	private static int SAMPLE_SIZE_IN_BITS_EQUAL = 0x00800000;
	private static int SAMPLE_SIZE_IN_BITS_MATCHES = 0x00400000;
	private static int CHANNELS_EQUAL = 0x00800000;
	private static int CHANNELS_MATCHES = 0x00400000;
	private static int ENDIAN_EQUAL = 0x00800000;
	private static int ENDIAN_MATCHES = 0x00400000;
	private static int SIGNED_EQUAL = 0x00800000;
	private static int SIGNED_MATCHES = 0x00400000;
	private static int FRAME_SIZE_IN_BITS_EQUAL = 0x00800000;
	private static int FRAME_SIZE_IN_BITS_MATCHES = 0x00400000;
	private static int FRAME_RATE_EQUAL = 0x00800000;
	private static int FRAME_RATE_MATCHES = 0x00400000;

	private static int audioFormatSimilarity(AudioFormat a, AudioFormat b) {
		int n = 0;
		n += similarity(a.getSampleRate(), b.getSampleRate(),
				SAMPLE_RATE_EQUAL, SAMPLE_RATE_MATCHES);
		n += similarity(a.getSampleSizeInBits(), b.getSampleSizeInBits(),
				SAMPLE_SIZE_IN_BITS_EQUAL, SAMPLE_SIZE_IN_BITS_MATCHES);
		n += similarity(a.getChannels(), b.getChannels(), CHANNELS_EQUAL,
				CHANNELS_MATCHES);
		n += similarity(a.getEndian(), b.getEndian(), ENDIAN_EQUAL,
				ENDIAN_MATCHES);
		n += similarity(a.getSigned(), b.getSigned(), SIGNED_EQUAL,
				SIGNED_MATCHES);
		n += similarity(a.getFrameSizeInBits(), b.getFrameSizeInBits(),
				FRAME_SIZE_IN_BITS_EQUAL, FRAME_SIZE_IN_BITS_MATCHES);
		n += similarity(a.getFrameRate(), b.getFrameRate(), FRAME_RATE_EQUAL,
				FRAME_RATE_MATCHES);
		return n;

	}

}

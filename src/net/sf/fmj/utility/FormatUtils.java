package net.sf.fmj.utility;

import java.awt.Dimension;

import javax.media.Format;
import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;

import net.sf.fmj.codegen.MediaCGUtils;

/**
 * Not part of standard JMF. Cannot be part of Format class because then
 * serialization becomes incompatible with reference impl.
 * 
 * @author Ken Larson
 * 
 */
public class FormatUtils {
	public static final Class byteArray = byte[].class;
	public static final Class shortArray = short[].class;
	public static final Class intArray = int[].class;
	public static final Class formatArray = Format[].class; // TODO: is this
															// used or allowed?

	// here to avoid messing up the serialization signature in the format
	// classes. The Eclipse compiler
	// will insert anonymous fields for these:
	public static final Class videoFormatClass = VideoFormat.class;
	public static final Class audioFormatClass = AudioFormat.class;

	public static Dimension clone(Dimension d) {
		if (d == null)
			return null;
		return new Dimension(d);
	}

	/**
	 * Is a a subclass of b? Strict.
	 */
	public static boolean isSubclass(Class a, Class b) {
		if (a == b)
			return false;
		if (!(b.isAssignableFrom(a)))
			return false;
		return true;
	}

	public static boolean isOneAssignableFromTheOther(Class a, Class b) {
		return a == b || b.isAssignableFrom(a) || a.isAssignableFrom(b);
	}

	public static long stringEncodingCodeVal(String s) {
		long result = 0;
		for (int i = 0; i < s.length(); ++i) {
			final char c = s.charAt(i);
			result *= 64;
			result += charEncodingCodeVal(c);

		}
		return result;
	}

	private static int charEncodingCodeVal(char c) {

		if (c <= (char) 95)
			return c - 32;
		if (c == 96)
			return -1;
		if (c <= 122)
			return c - 64;
		if (c <= 127)
			return -1;
		if (c <= 191)
			return -94;
		if (c <= 255)
			return -93;

		return -1;

	}

	public static boolean specified(Object o) {
		return o != null;
	}

	public static boolean specified(int v) {
		return v != Format.NOT_SPECIFIED;
	}

	public static boolean specified(float v) {
		return v != (float) Format.NOT_SPECIFIED;
	}

	public static boolean specified(double v) {
		return v != (double) Format.NOT_SPECIFIED;
	}

	public static boolean byteArraysEqual(byte[] ba1, byte[] ba2) {
		if (ba1 == null && ba2 == null)
			return true;
		if (ba1 == null || ba2 == null)
			return false;

		if (ba1.length != ba2.length)
			return false;
		for (int i = 0; i < ba1.length; ++i) {
			if (ba1[i] != ba2[i])
				return false;
		}
		return true;
	}

	public static boolean nullSafeEquals(Object o1, Object o2) {
		if (o1 == null && o2 == null)
			return true;
		if (o1 == null || o2 == null)
			return false;
		return o1.equals(o2);
	}

	public static boolean nullSafeEqualsIgnoreCase(String o1, String o2) {
		if (o1 == null && o2 == null)
			return true;
		if (o1 == null || o2 == null)
			return false;
		return o1.equalsIgnoreCase(o2);
	}

	public static boolean matches(Object o1, Object o2) {
		if (o1 == null || o2 == null)
			return true;
		return o1.equals(o2);
	}

	// public static boolean matchesIgnoreCase(String o1, String o2)
	// { if (o1 == null || o2 == null)
	// return true;
	// return o1.equalsIgnoreCase(o2);
	// }

	public static boolean matches(int v1, int v2) {
		if (v1 == Format.NOT_SPECIFIED || v2 == Format.NOT_SPECIFIED)
			return true;
		return v1 == v2;
	}

	public static boolean matches(float v1, float v2) {
		if (v1 == (float) Format.NOT_SPECIFIED
				|| v2 == (float) Format.NOT_SPECIFIED)
			return true;
		return v1 == v2;
	}

	public static boolean matches(double v1, double v2) {
		if (v1 == (double) Format.NOT_SPECIFIED
				|| v2 == (double) Format.NOT_SPECIFIED)
			return true;
		return v1 == v2;
	}

	// public static void trace(String msg, Format o)
	// {
	// System.out.println(msg + MediaCGUtils.formatToStr(o));
	// }
	//	
	// public static void trace(String msg, Object o)
	// {
	// if (o instanceof Format)
	// trace(msg, (Format) o);
	// else
	// System.out.println(msg + o);
	// }
	//	
	// public static void trace(String msg, boolean o)
	// {
	// System.out.println(msg + o);
	// }

	private static final boolean TRACE = false;

	public static void traceRelax(Format f1, Format result) {
		if (!TRACE)
			return;

		System.out.println("assertEquals(" + MediaCGUtils.formatToStr(f1)
				+ ".relax(), " + MediaCGUtils.formatToStr(result) + ");");
	}

	// private static void checkSizeNotCloned(Format f1, Format result)
	// {
	// if (f1 != null && result != null)
	// {
	// if (f1 instanceof VideoFormat && result instanceof VideoFormat)
	// {
	// VideoFormat fCast1 = (VideoFormat) f1;
	// VideoFormat fCastResult = (VideoFormat) result;
	//				
	// if (fCast1.getSize() != null &&
	// fCast1.getSize().equals(fCastResult.getSize()) && fCast1.getSize() !=
	// fCastResult.getSize())
	// throw new RuntimeException("Size CLONED!");
	//				
	//				
	// }
	// }
	// }
	//	
	// private static void checkSizeCloned(Format f1, Format result)
	// {
	// if (f1 != null && result != null)
	// {
	// if (f1 instanceof VideoFormat && result instanceof VideoFormat)
	// {
	// VideoFormat fCast1 = (VideoFormat) f1;
	// VideoFormat fCastResult = (VideoFormat) result;
	//				
	// if (fCast1.getSize() != null && fCast1.getSize() ==
	// fCastResult.getSize())
	// throw new RuntimeException("Size NOT CLONED!");
	//				
	//				
	// }
	// }
	// }

	public static void traceIntersects(Format f1, Format f2, Format result) {
		if (!TRACE)
			return;
		System.out.println("assertEquals(" + MediaCGUtils.formatToStr(f1)
				+ ".intersects(" + MediaCGUtils.formatToStr(f2) + "), "
				+ MediaCGUtils.formatToStr(result) + ");");
		// checkSizeNotCloned(f1, result);
		// checkSizeNotCloned(f2, result);
	}

	public static void traceMatches(Format f1, Format f2, boolean result) {
		if (!TRACE)
			return;
		System.out.println("assertEquals(" + MediaCGUtils.formatToStr(f1)
				+ ".matches(" + MediaCGUtils.formatToStr(f2) + "), " + result
				+ ");");
	}

	public static void traceEquals(Format f1, Format f2, boolean result) {
		if (!TRACE)
			return;
		// System.out.println("assertEquals(" + MediaCGUtils.formatToStr(f1) +
		// ".equals(" + MediaCGUtils.formatToStr(f2) + "), " + result + ");");
	}

	public static void traceClone(Format f1, Format f2) {
		if (!TRACE)
			return;
		System.out.println("assertEquals(" + MediaCGUtils.formatToStr(f1)
				+ ".clone(), " + MediaCGUtils.formatToStr(f2) + ");");
		// checkSizeCloned(f1, f2);
	}

	public static String frameRateToString(float frameRate) {
		// hack to get frame rates to print out same as JMF: 1 decimal place,
		// but NO rounding.
		frameRate = ((float) ((long) (frameRate * 10))) / 10.f;
		String s = "" + frameRate;

		return s;
	}

}

package net.sf.fmj.utility;

/**
 * Because java does not support unsigned types, a collection of useful
 * functions for treating types as unsigned.
 * 
 * @author Ken Larson
 */
public final class UnsignedUtils {
	private UnsignedUtils() {
		super();
	}

	public static final int MAX_UBYTE = ((int) Byte.MAX_VALUE) * 2 + 1; // 255
	public static final int MAX_USHORT = ((int) Short.MAX_VALUE) * 2 + 1; // 65535
	public static final long MAX_UINT = ((long) Integer.MAX_VALUE) * 2 + 1;

	public static int uByteToInt(byte value) {
		if (value >= 0)
			return value;
		else
			return MAX_UBYTE + 1 + value;
	}

	public static int uShortToInt(short value) {
		if (value >= 0)
			return value;
		else
			return MAX_USHORT + 1 + value;
	}

	public static long uIntToLong(int value) {
		if (value >= 0)
			return value;
		else
			return MAX_UINT + 1L + value;
	}
}

package net.sf.fmj.utility;

/**
 * 
 * @author Ken Larson
 * 
 */
public final class ByteUtils {
	private ByteUtils() {
		super();
	}

	// because java does not support unsigned bytes, we can use this function to
	// treat a byte as unsigned.
	// TODO: duplicated in UnsignedUtils
	public static int uByteToInt(byte b) {
		return b & 0xff;
	}

}

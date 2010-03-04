package net.sf.fmj.utility;

/**
 * 
 * @author Ken Larson
 * 
 */
public final class StringUtils {

	public static String byteToHexString_ZeroPad(byte b) {
		String s = Integer.toHexString(ByteUtils.uByteToInt(b));
		if (s.length() == 1)
			s = "0" + s;
		return s;
	}

	private static final int MAX_STANDARD_ASCII = 255;

	public static String replaceSpecialUrlChars(final String raw) {
		return replaceSpecialUrlChars(raw, false);
	}

	// see http://en.wikipedia.org/wiki/Query_string
	public static String replaceSpecialUrlChars(final String raw,
			final boolean isPath) {

		if (raw == null) {
			return null;
		}

		final StringBuffer buf = new StringBuffer();

		for (int i = 0; i < raw.length(); ++i) {
			char c = raw.charAt(i);

			if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
					|| (c >= '0' && c <= '9')
					|| (c == '.' || c == '-' || c == '_' || c == '~')
					|| (isPath && (c == '/' || c == ':' || c == '\\'))) {
				buf.append(c);
			} else {
				if (i > MAX_STANDARD_ASCII)
					throw new IllegalArgumentException(); // TODO: unicode?
				buf.append('%');
				buf.append(byteToHexString_ZeroPad((byte) c));

			}
		}

		return buf.toString();
	}

	public static String restoreSpecialURLChars(String cooked) {

		if (cooked == null) {
			return null;
		}

		StringBuffer buf = new StringBuffer();
		StringBuffer hexValueBuf = new StringBuffer();
		int state = 0;

		for (int i = 0; i < cooked.length(); ++i) {
			char c = cooked.charAt(i);

			if (state == 0) {
				if (c == '%') {
					state = 1;
					hexValueBuf = new StringBuffer();
				} else {
					buf.append(c);
				}
			} else {
				hexValueBuf.append(c);

				if (hexValueBuf.length() == 2) {
					String token = hexValueBuf.toString();
					int value = Integer.parseInt(token, 16); // TODO: what to do
																// with num
																// format
																// except?
					buf.append((char) value);

					state = 0;
				} else if (hexValueBuf.length() == 1
						&& hexValueBuf.charAt(0) == '%') {
					buf.append('%');
					state = 0;
				}

			}
		}
		if (state == 1) {
			buf.append("&" + hexValueBuf.toString()); // incomplete
		}
		return buf.toString();
	}

	/**
	 * Dump to string using a debugger-like format - both hex and ascii. Bytes
	 * dumped will be byteslen - offset.
	 */
	public static String dump(byte[] bytes, int offset, int byteslen) {
		final StringBuffer b = new StringBuffer();

		final int width = 32;
		int len = width;
		while (offset < byteslen) {
			int remainder = 0;

			if (offset + len > byteslen) {
				len = byteslen - offset;
				remainder = width - len;
			}
			b.append(StringUtils.byteArrayToHexString(bytes, len, offset));
			for (int i = 0; i < remainder; ++i) {
				b.append("  ");
			}
			b.append(" | ");
			for (int i = 0; i < len; ++i) {
				byte c = bytes[offset + i];
				if (c >= ' ' && c <= '~') {
					b.append((char) c);
				} else
					b.append('.');
			}

			b.append('\n');

			offset += len;

		}
		return b.toString();
	}

	public static String byteArrayToHexString(byte[] array, int len, int offset) {
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < len; ++i) {
			String byteStr = Integer.toHexString(ByteUtils
					.uByteToInt(array[offset + i]));
			if (byteStr.length() == 1)
				byteStr = "0" + byteStr;
			b.append(byteStr);
		}
		return b.toString();
	}

	/**
	 * 
	 * @throws NumberFormatException
	 */
	public static byte[] hexStringToByteArray(String s) {
		byte[] array = new byte[s.length() / 2];
		for (int i = 0; i < array.length; ++i) {
			array[i] = hexStringToByte(s.substring(i * 2, i * 2 + 2));
		}
		return array;
	}

	/**
	 * 
	 * @throws NumberFormatException
	 */
	public static byte hexStringToByte(String s) {
		return (byte) Integer.parseInt(s, RADIX_16);
	}

	private static final int RADIX_16 = 16;

	public static String byteArrayToBase64String(byte[] value) {
		final sun.misc.BASE64Encoder base64Encoder = new sun.misc.BASE64Encoder();
		return base64Encoder.encode(value);
	}

}

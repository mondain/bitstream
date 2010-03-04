package net.sf.fmj.media.codec.video.jpeg;

import net.sf.fmj.utility.ByteUtils;

/**
 * 
 * A special header is added to each packet that immediately follows the RTP
 * header:
 * 
 * 0 1 2 3 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ | Type
 * specific | Fragment Offset |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ | Type | Q
 * | Width | Height |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */
public class JpegRTPHeader {
	/**
	 * In bytes.
	 */
	public static final int HEADER_SIZE = 8;

	private final byte typeSpecific;
	private final int fragmentOffset;
	private final byte type;
	private final byte q;
	private final byte width;
	private final byte height;

	public JpegRTPHeader(final byte typeSpecific, final int fragmentOffset,
			final byte type, final byte q, final byte width, final byte height) {
		super();
		this.typeSpecific = typeSpecific;
		this.fragmentOffset = fragmentOffset;
		this.type = type;
		this.q = q;
		this.width = width;
		this.height = height;
	}

	public static JpegRTPHeader parse(byte[] data, int offset) {
		int i = offset;
		final byte typeSpecific = data[i++];
		int fragmentOffset = 0;
		for (int j = 0; j < 3; ++j) { // big-endian.
			fragmentOffset <<= 8;
			fragmentOffset += data[i++] & 0xff;
		}
		final byte type = data[i++];
		final byte q = data[i++];
		final byte width = data[i++];
		final byte height = data[i++];
		return new JpegRTPHeader(typeSpecific, fragmentOffset, type, q, width,
				height);

	}

	public byte[] toBytes() {
		byte[] data = new byte[HEADER_SIZE];
		int i = 0;
		data[i++] = typeSpecific;

		encode3ByteIntBE(fragmentOffset, data, i);
		i += 3;

		data[i++] = type;
		data[i++] = q;
		data[i++] = width;
		data[i++] = height;

		return data;

	}

	private static final int BITS_PER_BYTE = 8;

	private static final int MAX_SIGNED_BYTE = 127;
	private static final int MAX_BYTE = 0xFF;
	private static final int MAX_BYTE_PLUS1 = 256;

	private static void encode3ByteIntBE(int value, byte[] ba, int offset) {
		int length = 3;

		for (int i = 0; i < length; ++i) {
			int byteValue = value & MAX_BYTE;
			if (byteValue > MAX_SIGNED_BYTE)
				byteValue = byteValue - MAX_BYTE_PLUS1;

			ba[offset + (length - i - 1)] = (byte) byteValue;

			value = value >> BITS_PER_BYTE;
		}
	}

	public int getWidthInPixels() {
		return ByteUtils.uByteToInt(width) * 8;
	}

	public int getHeightInPixels() {
		return ByteUtils.uByteToInt(height) * 8;
	}

	public int getWidthInBlocks() {
		return ByteUtils.uByteToInt(width);
	}

	public int getHeightInBlocks() {
		return ByteUtils.uByteToInt(height);
	}

	public int getFragmentOffset() {
		return fragmentOffset;
	}

	public int getQ() {
		return ByteUtils.uByteToInt(q);
	}

	public int getType() {
		return ByteUtils.uByteToInt(type);
	}

	public int getTypeSpecific() {
		return ByteUtils.uByteToInt(typeSpecific);
	}

	public String toString() {
		return "typeSpecific=" + getTypeSpecific() + " fragmentOffset="
				+ getFragmentOffset() + " type=" + getType() + " q=" + getQ()
				+ " w=" + getWidthInPixels() + " h=" + getHeightInPixels();
	}

	public boolean equals(Object o) {
		if (!(o instanceof JpegRTPHeader))
			return false;
		final JpegRTPHeader oCast = (JpegRTPHeader) o;
		return this.typeSpecific == oCast.typeSpecific
				&& this.fragmentOffset == oCast.fragmentOffset
				&& this.type == oCast.type && this.q == oCast.q
				&& this.width == oCast.width && this.height == oCast.height;

	}

	public int hashCode() {
		return typeSpecific + fragmentOffset + type + q + width + height;
	}
}

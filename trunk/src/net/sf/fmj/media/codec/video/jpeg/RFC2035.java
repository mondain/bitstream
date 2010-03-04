package net.sf.fmj.media.codec.video.jpeg;

/**
 * Code from RFC 2035 - RTP Payload Format for JPEG Video. See
 * http://www.rfc-archive.org/getrfc.php?rfc=2035 Ported to Java from C by Ken
 * Larson. TODO: Obsoleted by RFC2435. See
 * http://rfc.sunsite.dk/rfc/rfc2435.html
 * 
 * @author Ken Larson
 * 
 */
public class RFC2035 {
	// Appendix A
	//
	// The following code can be used to create a quantization table from a
	// Q factor:

	// kenlars99: the sample code appears to have a mistake. Several places on
	// the
	// web refer to the q tables being in "zigzag" order.
	// http://www.obrador.com/essentialjpeg/headerinfo.htm
	// says "the quantization tables are stored in zigzag format". The RFC does
	// not say this, and the code
	// does not appear to do it.
	// Some LGPL code (JPEGVideoRTPSource.cpp) provides different tables, which
	// we will use here:

	// The default 'luma' and 'chroma' quantizer tables, in zigzag order:
	private static final int[] jpeg_luma_quantizer = new int[] {
			// luma table:
			16, 11, 12, 14, 12, 10, 16, 14, 13, 14, 18, 17, 16, 19, 24, 40, 26,
			24, 22, 22, 24, 49, 35, 37, 29, 40, 58, 51, 61, 60, 57, 51, 56, 55,
			64, 72, 92, 78, 64, 68, 87, 69, 55, 56, 80, 109, 81, 87, 95, 98,
			103, 104, 103, 62, 77, 113, 121, 112, 100, 120, 92, 101, 103, 99, };
	private static final int[] jpeg_chroma_quantizer = new int[] {
			// chroma table:
			17, 18, 18, 24, 21, 24, 47, 26, 26, 47, 99, 66, 56, 66, 99, 99, 99,
			99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
			99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99 };

	// non-zigzagged versions:
	// /**
	// * Table K.1 from JPEG spec.
	// */
	// private static final int[] jpeg_luma_quantizer = new int[]{
	// 16, 11, 10, 16, 24, 40, 51, 61,
	// 12, 12, 14, 19, 26, 58, 60, 55,
	// 14, 13, 16, 24, 40, 57, 69, 56,
	// 14, 17, 22, 29, 51, 87, 80, 62,
	// 18, 22, 37, 56, 68, 109, 103, 77,
	// 24, 35, 55, 64, 81, 104, 113, 92,
	// 49, 64, 78, 87, 103, 121, 120, 101,
	// 72, 92, 95, 98, 112, 100, 103, 99
	// };
	//
	// /**
	// * Table K.2 from JPEG spec.
	// */
	// private static final int[] jpeg_chroma_quantizer = new int[] {
	// 17, 18, 24, 47, 99, 99, 99, 99,
	// 18, 21, 26, 66, 99, 99, 99, 99,
	// 24, 26, 56, 99, 99, 99, 99, 99,
	// 47, 66, 99, 99, 99, 99, 99, 99,
	// 99, 99, 99, 99, 99, 99, 99, 99,
	// 99, 99, 99, 99, 99, 99, 99, 99,
	// 99, 99, 99, 99, 99, 99, 99, 99,
	// 99, 99, 99, 99, 99, 99, 99, 99
	// };

	/**
	 * Call MakeTables with the Q factor and two int[64] return arrays
	 */
	private static void MakeTables(int q, byte[] /* u_char * */lum_q,
			byte[] /* u_char * */chr_q) {
		int i;
		int factor = q;

		if (q < 1)
			factor = 1;
		if (q > 99)
			factor = 99;
		if (q < 50)
			q = 5000 / factor;
		else
			q = 200 - factor * 2;

		for (i = 0; i < 64; i++) {
			int lq = (jpeg_luma_quantizer[i] * q + 50) / 100;
			int cq = (jpeg_chroma_quantizer[i] * q + 50) / 100;

			/* Limit the quantizers to 1 <= q <= 255 */
			if (lq < 1)
				lq = 1;
			else if (lq > 255)
				lq = 255;
			lum_q[i] = (byte) lq;

			if (cq < 1)
				cq = 1;
			else if (cq > 255)
				cq = 255;
			chr_q[i] = (byte) cq;
		}
	}

	// Appendix B
	//
	// The following routines can be used to create the JPEG marker segments
	// corresponding to the table-specification data that is absent from the
	// RTP/JPEG body.

	private static final byte /* u_char */lum_dc_codelens[] = { 0, 1, 5, 1, 1, 1,
			1, 1, 1, 0, 0, 0, 0, 0, 0, 0, };

	private static final byte /* u_char */lum_dc_symbols[] = { 0, 1, 2, 3, 4, 5,
			6, 7, 8, 9, 10, 11, };

	private static final byte /* u_char */lum_ac_codelens[] = { 0, 2, 1, 3, 3, 2,
			4, 3, 5, 5, 4, 4, 0, 0, 1, (byte) 0x7d, };

	private static final byte /* u_char */lum_ac_symbols[] = { (byte) 0x01,
			(byte) 0x02, (byte) 0x03, (byte) 0x00, (byte) 0x04, (byte) 0x11,
			(byte) 0x05, (byte) 0x12, (byte) 0x21, (byte) 0x31, (byte) 0x41,
			(byte) 0x06, (byte) 0x13, (byte) 0x51, (byte) 0x61, (byte) 0x07,
			(byte) 0x22, (byte) 0x71, (byte) 0x14, (byte) 0x32, (byte) 0x81,
			(byte) 0x91, (byte) 0xa1, (byte) 0x08, (byte) 0x23, (byte) 0x42,
			(byte) 0xb1, (byte) 0xc1, (byte) 0x15, (byte) 0x52, (byte) 0xd1,
			(byte) 0xf0, (byte) 0x24, (byte) 0x33, (byte) 0x62, (byte) 0x72,
			(byte) 0x82, (byte) 0x09, (byte) 0x0a, (byte) 0x16, (byte) 0x17,
			(byte) 0x18, (byte) 0x19, (byte) 0x1a, (byte) 0x25, (byte) 0x26,
			(byte) 0x27, (byte) 0x28, (byte) 0x29, (byte) 0x2a, (byte) 0x34,
			(byte) 0x35, (byte) 0x36, (byte) 0x37, (byte) 0x38, (byte) 0x39,
			(byte) 0x3a, (byte) 0x43, (byte) 0x44, (byte) 0x45, (byte) 0x46,
			(byte) 0x47, (byte) 0x48, (byte) 0x49, (byte) 0x4a, (byte) 0x53,
			(byte) 0x54, (byte) 0x55, (byte) 0x56, (byte) 0x57, (byte) 0x58,
			(byte) 0x59, (byte) 0x5a, (byte) 0x63, (byte) 0x64, (byte) 0x65,
			(byte) 0x66, (byte) 0x67, (byte) 0x68, (byte) 0x69, (byte) 0x6a,
			(byte) 0x73, (byte) 0x74, (byte) 0x75, (byte) 0x76, (byte) 0x77,
			(byte) 0x78, (byte) 0x79, (byte) 0x7a, (byte) 0x83, (byte) 0x84,
			(byte) 0x85, (byte) 0x86, (byte) 0x87, (byte) 0x88, (byte) 0x89,
			(byte) 0x8a, (byte) 0x92, (byte) 0x93, (byte) 0x94, (byte) 0x95,
			(byte) 0x96, (byte) 0x97, (byte) 0x98, (byte) 0x99, (byte) 0x9a,
			(byte) 0xa2, (byte) 0xa3, (byte) 0xa4, (byte) 0xa5, (byte) 0xa6,
			(byte) 0xa7, (byte) 0xa8, (byte) 0xa9, (byte) 0xaa, (byte) 0xb2,
			(byte) 0xb3, (byte) 0xb4, (byte) 0xb5, (byte) 0xb6, (byte) 0xb7,
			(byte) 0xb8, (byte) 0xb9, (byte) 0xba, (byte) 0xc2, (byte) 0xc3,
			(byte) 0xc4, (byte) 0xc5, (byte) 0xc6, (byte) 0xc7, (byte) 0xc8,
			(byte) 0xc9, (byte) 0xca, (byte) 0xd2, (byte) 0xd3, (byte) 0xd4,
			(byte) 0xd5, (byte) 0xd6, (byte) 0xd7, (byte) 0xd8, (byte) 0xd9,
			(byte) 0xda, (byte) 0xe1, (byte) 0xe2, (byte) 0xe3, (byte) 0xe4,
			(byte) 0xe5, (byte) 0xe6, (byte) 0xe7, (byte) 0xe8, (byte) 0xe9,
			(byte) 0xea, (byte) 0xf1, (byte) 0xf2, (byte) 0xf3, (byte) 0xf4,
			(byte) 0xf5, (byte) 0xf6, (byte) 0xf7, (byte) 0xf8, (byte) 0xf9,
			(byte) 0xfa, };

	private static final byte /* u_char */chm_dc_codelens[] = { 0, 3, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 0, 0, 0, 0, 0, };

	private static final byte /* u_char */chm_dc_symbols[] = { 0, 1, 2, 3, 4, 5,
			6, 7, 8, 9, 10, 11, };

	private static final byte /* u_char */chm_ac_codelens[] = { 0, 2, 1, 2, 4, 4,
			3, 4, 7, 5, 4, 4, 0, 1, 2, (byte) 0x77, };

	private static final byte /* u_char */chm_ac_symbols[] = { (byte) 0x00,
			(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x11, (byte) 0x04,
			(byte) 0x05, (byte) 0x21, (byte) 0x31, (byte) 0x06, (byte) 0x12,
			(byte) 0x41, (byte) 0x51, (byte) 0x07, (byte) 0x61, (byte) 0x71,
			(byte) 0x13, (byte) 0x22, (byte) 0x32, (byte) 0x81, (byte) 0x08,
			(byte) 0x14, (byte) 0x42, (byte) 0x91, (byte) 0xa1, (byte) 0xb1,
			(byte) 0xc1, (byte) 0x09, (byte) 0x23, (byte) 0x33, (byte) 0x52,
			(byte) 0xf0, (byte) 0x15, (byte) 0x62, (byte) 0x72, (byte) 0xd1,
			(byte) 0x0a, (byte) 0x16, (byte) 0x24, (byte) 0x34, (byte) 0xe1,
			(byte) 0x25, (byte) 0xf1, (byte) 0x17, (byte) 0x18, (byte) 0x19,
			(byte) 0x1a, (byte) 0x26, (byte) 0x27, (byte) 0x28, (byte) 0x29,
			(byte) 0x2a, (byte) 0x35, (byte) 0x36, (byte) 0x37, (byte) 0x38,
			(byte) 0x39, (byte) 0x3a, (byte) 0x43, (byte) 0x44, (byte) 0x45,
			(byte) 0x46, (byte) 0x47, (byte) 0x48, (byte) 0x49, (byte) 0x4a,
			(byte) 0x53, (byte) 0x54, (byte) 0x55, (byte) 0x56, (byte) 0x57,
			(byte) 0x58, (byte) 0x59, (byte) 0x5a, (byte) 0x63, (byte) 0x64,
			(byte) 0x65, (byte) 0x66, (byte) 0x67, (byte) 0x68, (byte) 0x69,
			(byte) 0x6a, (byte) 0x73, (byte) 0x74, (byte) 0x75, (byte) 0x76,
			(byte) 0x77, (byte) 0x78, (byte) 0x79, (byte) 0x7a, (byte) 0x82,
			(byte) 0x83, (byte) 0x84, (byte) 0x85, (byte) 0x86, (byte) 0x87,
			(byte) 0x88, (byte) 0x89, (byte) 0x8a, (byte) 0x92, (byte) 0x93,
			(byte) 0x94, (byte) 0x95, (byte) 0x96, (byte) 0x97, (byte) 0x98,
			(byte) 0x99, (byte) 0x9a, (byte) 0xa2, (byte) 0xa3, (byte) 0xa4,
			(byte) 0xa5, (byte) 0xa6, (byte) 0xa7, (byte) 0xa8, (byte) 0xa9,
			(byte) 0xaa, (byte) 0xb2, (byte) 0xb3, (byte) 0xb4, (byte) 0xb5,
			(byte) 0xb6, (byte) 0xb7, (byte) 0xb8, (byte) 0xb9, (byte) 0xba,
			(byte) 0xc2, (byte) 0xc3, (byte) 0xc4, (byte) 0xc5, (byte) 0xc6,
			(byte) 0xc7, (byte) 0xc8, (byte) 0xc9, (byte) 0xca, (byte) 0xd2,
			(byte) 0xd3, (byte) 0xd4, (byte) 0xd5, (byte) 0xd6, (byte) 0xd7,
			(byte) 0xd8, (byte) 0xd9, (byte) 0xda, (byte) 0xe2, (byte) 0xe3,
			(byte) 0xe4, (byte) 0xe5, (byte) 0xe6, (byte) 0xe7, (byte) 0xe8,
			(byte) 0xe9, (byte) 0xea, (byte) 0xf2, (byte) 0xf3, (byte) 0xf4,
			(byte) 0xf5, (byte) 0xf6, (byte) 0xf7, (byte) 0xf8, (byte) 0xf9,
			(byte) 0xfa, };

	/**
	 * @return new offset in p.
	 */
	private static int /* u_char * */
	MakeQuantHeader(byte[] /* u_char * */p, int i, byte[] /* u_char * */qt,
			int tableNo) {
		p[i++] = (byte) 0xff;
		p[i++] = (byte) 0xdb; /* DQT */
		p[i++] = (byte) 0; /* length msb */
		p[i++] = (byte) 67; /* length lsb */
		p[i++] = (byte) tableNo;
		System.arraycopy(qt, 0, p, i, 64);
		i += 64;
		return i;
	}

	/**
	 * @return new offset in p.
	 */
	private static int /* u_char * */
	MakeHuffmanHeader(byte[] /* u_char * */p, int i,
			byte[] /* u_char * */codelens, int ncodes,
			byte[] /* u_char * */symbols, int nsymbols, int tableNo,
			int tableClass) {
		p[i++] = (byte) 0xff;
		p[i++] = (byte) 0xc4; /* DHT */
		p[i++] = (byte) 0; /* length msb */
		p[i++] = (byte) (3 + ncodes + nsymbols); /* length lsb */
		p[i++] = (byte) (tableClass << 4 | tableNo);
		System.arraycopy(codelens, 0, p, i, ncodes);
		i += ncodes;
		System.arraycopy(symbols, 0, p, i, nsymbols);
		i += nsymbols;
		return i;
	}

	/**
	 * Given an RTP/JPEG type code, q factor, width, and height, generate a
	 * frame and scan headers that can be prepended to the RTP/JPEG data payload
	 * to produce a JPEG compressed image in interchange format (except for
	 * possible trailing garbage and absence of an EOI marker to terminate the
	 * scan).
	 * 
	 * @param i
	 *            starting offset
	 * @param includeSOI
	 *            - kenlars99 - not in original RFC sample code, allows us to
	 *            control whether to include the initial SOI marker (0xFFD8).
	 *            Turn this off if there are other headers before these, such as
	 *            the JFIF header. If false, caller is responsible for the
	 *            initial SOI marker.
	 */
	public static int MakeHeaders(boolean includeSOI, byte[] /* u_char * */p,
			int i, int type, int q, int w, int h) {
		// byte[] /* u_char * */start = p;
		byte[] /* u_char */lqt = new byte[64];
		byte[] /* u_char */cqt = new byte[64];

		/* convert from blocks to pixels */
		w <<= 3;
		h <<= 3;

		MakeTables(q, lqt, cqt);

		if (includeSOI) {
			p[i++] = (byte) 0xff;
			p[i++] = (byte) 0xd8; /* SOI */
		}
		i = MakeQuantHeader(p, i, lqt, 0);

		i = MakeQuantHeader(p, i, cqt, 1);

		i = MakeHuffmanHeader(p, i, lum_dc_codelens, lum_dc_codelens.length,
				lum_dc_symbols, lum_dc_symbols.length, 0, 0);
		i = MakeHuffmanHeader(p, i, lum_ac_codelens, lum_ac_codelens.length,
				lum_ac_symbols, lum_ac_symbols.length, 0, 1);
		i = MakeHuffmanHeader(p, i, chm_dc_codelens, chm_dc_codelens.length,
				chm_dc_symbols, chm_dc_symbols.length, 1, 0);
		i = MakeHuffmanHeader(p, i, chm_ac_codelens, chm_ac_codelens.length,
				chm_ac_symbols, chm_ac_symbols.length, 1, 1);

		p[i++] = (byte) 0xff;
		p[i++] = (byte) 0xc0; /* SOF */
		p[i++] = (byte) 0; /* length msb */
		p[i++] = (byte) 17; /* length lsb */
		p[i++] = (byte) 8; /* 8-bit precision */
		p[i++] = (byte) (h >> 8); /* height msb */
		p[i++] = (byte) h; /* height lsb */
		p[i++] = (byte) (w >> 8); /* width msb */
		p[i++] = (byte) w; /* wudth lsb */
		p[i++] = (byte) 3; /* number of components */
		p[i++] = (byte) 0; /* comp 0 */
		if (type == 0)
			p[i++] = (byte) 0x21; /* hsamp = 2, vsamp = 1 */
		else
			p[i++] = (byte) 0x22; /* hsamp = 2, vsamp = 2 */
		p[i++] = (byte) 0; /* quant table 0 */
		p[i++] = (byte) 1; /* comp 1 */
		p[i++] = (byte) 0x11; /* hsamp = 1, vsamp = 1 */
		p[i++] = (byte) 1; /* quant table 1 */
		p[i++] = (byte) 2; /* comp 2 */
		p[i++] = (byte) 0x11; /* hsamp = 1, vsamp = 1 */
		p[i++] = (byte) 1; /* quant table 1 */

		p[i++] = (byte) 0xff;
		p[i++] = (byte) 0xda; /* SOS */
		p[i++] = (byte) 0; /* length msb */
		p[i++] = (byte) 12; /* length lsb */
		p[i++] = (byte) 3; /* 3 components */
		p[i++] = (byte) 0; /* comp 0 */

		p[i++] = (byte) 0; /* huffman table 0 */
		p[i++] = (byte) 1; /* comp 1 */
		p[i++] = (byte) 0x11; /* huffman table 1 */
		p[i++] = (byte) 2; /* comp 2 */
		p[i++] = (byte) 0x11; /* huffman table 1 */
		p[i++] = (byte) 0; /* first DCT coeff */
		p[i++] = (byte) 63; /* last DCT coeff */
		p[i++] = (byte) 0; /* sucessive approx. */

		return i;
	};

}
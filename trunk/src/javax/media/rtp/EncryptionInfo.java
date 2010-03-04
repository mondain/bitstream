package javax.media.rtp;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class EncryptionInfo implements java.io.Serializable {

	private int type;

	private byte[] key;

	public static final int NO_ENCRYPTION = 0;

	public static final int XOR = 1;

	public static final int MD5 = 2;

	public static final int DES = 3;

	public static final int TRIPLE_DES = 4;

	public EncryptionInfo(int type, byte[] key) {
		this.type = type;
		this.key = key;
	}

	public int getType() {
		return type;
	}

	public byte[] getKey() {
		return key;
	}
}

package javax.media.protocol;

import java.io.IOException;

/**
 * Coding complete.
 * 
 * @author Ken Larson
 * 
 */
public class InputSourceStream implements PullSourceStream {

	protected java.io.InputStream stream;

	protected boolean eosReached;
	private ContentDescriptor contentDescriptor;

	public InputSourceStream(java.io.InputStream s, ContentDescriptor type) {
		stream = s;
		contentDescriptor = type;
	}

	public ContentDescriptor getContentDescriptor() {
		return contentDescriptor;

	}

	public long getContentLength() {
		return LENGTH_UNKNOWN; // TODO

	}

	public boolean willReadBlock() {
		try {
			return stream.available() <= 0;
		} catch (IOException e) {
			return true;
		}

	}

	public int read(byte[] buffer, int offset, int length)
			throws java.io.IOException {
		int result = stream.read(buffer, offset, length);
		if (result == -1)
			eosReached = true;
		return result;

	}

	public void close() throws java.io.IOException {
		stream.close();

	}

	public boolean endOfStream() {
		return eosReached;

	}

	public Object[] getControls() {
		return new Object[0];
	}

	public Object getControl(String controlName) {
		return null;
	}
}

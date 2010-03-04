package javax.media.protocol;

import java.io.IOException;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface PushSourceStream extends SourceStream {
	/**
	 * According to API: Read from the stream without blocking. Returns -1 when
	 * the end of the media is reached. This implies that it can return zero if
	 * there is no data available.
	 */
	public int read(byte[] buffer, int offset, int length) throws IOException;

	public int getMinimumTransferSize();

	public void setTransferHandler(SourceTransferHandler transferHandler);
}

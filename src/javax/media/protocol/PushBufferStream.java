package javax.media.protocol;

import java.io.IOException;

import javax.media.Buffer;
import javax.media.Format;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface PushBufferStream extends SourceStream {
	public Format getFormat();

	public void read(Buffer buffer) throws IOException;

	public void setTransferHandler(BufferTransferHandler transferHandler);
}

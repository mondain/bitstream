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
public interface PullBufferStream extends SourceStream {

	public boolean willReadBlock();

	public void read(Buffer buffer) throws IOException;

	public Format getFormat();
}

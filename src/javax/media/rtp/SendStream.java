package javax.media.rtp;

import java.io.IOException;

import javax.media.rtp.rtcp.SourceDescription;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface SendStream extends RTPStream {

	public void setSourceDescription(SourceDescription[] sourceDesc);

	public void close();

	public void stop() throws IOException;

	public void start() throws IOException;

	public int setBitRate(int bitRate);

	public TransmissionStats getSourceTransmissionStats();
}

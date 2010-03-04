package javax.media.rtp;

import java.io.IOException;

import javax.media.protocol.PushSourceStream;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface RTPConnector {
	public PushSourceStream getDataInputStream() throws IOException;

	public OutputDataStream getDataOutputStream() throws IOException;

	public PushSourceStream getControlInputStream() throws IOException;

	public OutputDataStream getControlOutputStream() throws IOException;

	public void close();

	public void setReceiveBufferSize(int size) throws IOException;

	public int getReceiveBufferSize();

	public void setSendBufferSize(int size) throws IOException;

	public int getSendBufferSize();

	public double getRTCPBandwidthFraction();

	public double getRTCPSenderBandwidthFraction();

}

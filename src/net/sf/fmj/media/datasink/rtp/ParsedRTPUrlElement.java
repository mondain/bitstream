package net.sf.fmj.media.datasink.rtp;

/**
 * 
 * @author Ken Larson
 * 
 */
public class ParsedRTPUrlElement {
	// types:
	public static final String AUDIO = "audio";
	public static final String VIDEO = "video";

	public String host;
	public int port;
	public String type;
	public int ttl;

	public String toString() {
		return host + ":" + port + "/" + type + "/" + ttl;
	}
}

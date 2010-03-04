package javax.media.rtp;

import javax.media.rtp.event.ReceiveStreamEvent;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface ReceiveStreamListener extends java.util.EventListener {
	public void update(ReceiveStreamEvent event);
}

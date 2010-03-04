package javax.media.rtp;

import javax.media.rtp.event.SendStreamEvent;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface SendStreamListener extends java.util.EventListener {
	public void update(SendStreamEvent event);
}

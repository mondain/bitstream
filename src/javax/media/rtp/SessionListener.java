package javax.media.rtp;

import javax.media.rtp.event.SessionEvent;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface SessionListener extends java.util.EventListener {
	public void update(SessionEvent event);
}

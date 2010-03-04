package javax.media.rtp;

import javax.media.rtp.event.RemoteEvent;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface RemoteListener extends java.util.EventListener {
	public void update(RemoteEvent event);
}

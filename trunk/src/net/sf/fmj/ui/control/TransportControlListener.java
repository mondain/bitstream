package net.sf.fmj.ui.control;

/**
 * 
 * Allows TransportControlPanel to listen to state changes of a
 * TransportControl.
 * 
 * @author Ken Larson
 * 
 */
public interface TransportControlListener {
	void onStateChange(TransportControlState state);

	void onProgressChange(long nanos);

	void onDurationChange(long nanos);
}

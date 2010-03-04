package net.sf.fmj.ui.control;

/**
 * Abstraction of a player/controller for use by the TransportControlPanel.
 * 
 * @author Warren Bloomer
 * 
 */
public interface TransportControl {

	void start();

	void stop();

	void setPosition(double seconds);

	/**
	 * 1.0 is normal playback speed. 0 is stopped.
	 * 
	 * @param rate
	 */
	void setRate(float rate);

	void setGain(float value);

	void setMute(boolean value);

	void setTransportControlListener(TransportControlListener listener);
}

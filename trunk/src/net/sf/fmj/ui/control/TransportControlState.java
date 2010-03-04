package net.sf.fmj.ui.control;

/**
 * 
 * @author Ken Larson
 * 
 */
public class TransportControlState {
	private boolean allowStop;
	private boolean allowPlay;
	private boolean allowVolume;

	public boolean isAllowPlay() {
		return allowPlay;
	}

	public void setAllowPlay(boolean allowPlay) {
		this.allowPlay = allowPlay;
	}

	public boolean isAllowStop() {
		return allowStop;
	}

	public void setAllowStop(boolean allowStop) {
		this.allowStop = allowStop;
	}

	public boolean isAllowVolume() {
		return allowVolume;
	}

	public void setAllowVolume(boolean allowVolume) {
		this.allowVolume = allowVolume;
	}

}

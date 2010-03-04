package net.sf.fmj.media.control;

import javax.media.Control;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.FloatControl;

/**
 * A generic control over audio.
 * 
 * TODO provide listeners for notification of control changes
 * 
 * @author stormboy
 * @deprecated not used.
 * 
 */
public interface AudioControl extends Control {

	FloatControl getVolumeControl();

	BooleanControl getMuteControl();
}

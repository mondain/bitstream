package net.sf.fmj.media.renderer.audio;

import javax.sound.sampled.AudioFormat.Encoding;

/**
 * Subclass for 1.4 which makes constructor public. 1.5 does not have this
 * problem.
 * 
 * @author Ken Larson
 * 
 */
public class CustomEncoding extends Encoding {

	public CustomEncoding(String name) {
		super(name);
	}

}

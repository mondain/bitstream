package net.sf.fmj.media.multiplexer.audio;

import javax.media.protocol.FileTypeDescriptor;
import javax.sound.sampled.AudioFileFormat;

/**
 * 
 * TODO: doesn't work?
 * 
 * @author Ken Larson
 * 
 */
public class AIFFMux extends JavaSoundMux {

	public AIFFMux() {
		super(new FileTypeDescriptor(FileTypeDescriptor.AIFF),
				AudioFileFormat.Type.AIFF);
	}

}

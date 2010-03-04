package net.sf.fmj.media.multiplexer.audio;

import javax.media.protocol.FileTypeDescriptor;
import javax.sound.sampled.AudioFileFormat;

/**
 * 
 * @author Ken Larson
 * 
 */
public class AUMux extends JavaSoundMux {

	public AUMux() {
		super(new FileTypeDescriptor(FileTypeDescriptor.BASIC_AUDIO),
				AudioFileFormat.Type.AU);
	}

}

package javax.media.util;

import javax.media.format.VideoFormat;

/**
 * In progress. We implement in FMJ and extend, because if JMF is (ahead) in the
 * classpath, its BufferToImage would be used even for FMJ classes like
 * SimpleAWTRenderer. It fails on 4harmonic.mpg.
 * 
 * @author Ken Larson
 * 
 */
public class BufferToImage extends net.sf.fmj.media.util.BufferToImage {

	public BufferToImage(VideoFormat format) {
		super(format);
	}

}

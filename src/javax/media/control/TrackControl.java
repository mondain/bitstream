package javax.media.control;

import javax.media.Codec;
import javax.media.Controls;
import javax.media.NotConfiguredError;
import javax.media.Renderer;
import javax.media.UnsupportedPlugInException;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface TrackControl extends FormatControl, Controls {

	public void setCodecChain(Codec[] codecs)
			throws UnsupportedPlugInException, NotConfiguredError;

	public void setRenderer(Renderer renderer)
			throws UnsupportedPlugInException, NotConfiguredError;
}

package javax.media;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface CachingControl extends Control {
	public static final long LENGTH_UNKNOWN = Long.MAX_VALUE;

	public boolean isDownloading();

	public long getContentLength();

	public long getContentProgress();

	public java.awt.Component getProgressBarComponent();

	public java.awt.Component getControlComponent();

}

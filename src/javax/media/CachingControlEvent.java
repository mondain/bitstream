package javax.media;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class CachingControlEvent extends ControllerEvent {
	CachingControl cachingControl;
	long progress;

	public CachingControlEvent(Controller from, CachingControl cachingControl,
			long progress) {
		super(from);
		this.cachingControl = cachingControl;
		this.progress = progress;

	}

	public CachingControl getCachingControl() {
		return cachingControl;
	}

	public long getContentProgress() {
		return progress;
	}

	public String toString() {
		return getClass().getName() + "[source=" + getSource()
				+ ",cachingControl=" + cachingControl + ",progress=" + progress
				+ "]";
	}
}

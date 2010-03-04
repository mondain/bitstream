package javax.media;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface ExtendedCachingControl extends CachingControl {
	public void setBufferSize(Time t);

	public Time getBufferSize();

	public void pauseDownload();

	public void resumeDownload();

	public long getStartOffset();

	public long getEndOffset();

	public void addDownloadProgressListener(DownloadProgressListener l,
			int numKiloBytes);

	public void removeDownloadProgressListener(DownloadProgressListener l);

}

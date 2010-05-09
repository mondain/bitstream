package net.sf.fmj.media.protocol.file;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.Time;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PullDataSource;
import javax.media.protocol.PullSourceStream;
import javax.media.protocol.Seekable;
import javax.media.protocol.SourceCloneable;

import net.sf.fmj.media.MimeManager;
import net.sf.fmj.utility.LoggerSingleton;
import net.sf.fmj.utility.PathUtils;
import net.sf.fmj.utility.URLUtils;

/**
 * 
 * @author Ken Larson
 * 
 */
public class DataSource extends PullDataSource implements SourceCloneable {
	private static final Logger logger = LoggerSingleton.logger;

	protected RandomAccessFile raf;

	protected boolean connected = false;

	protected ContentDescriptor contentType;

	protected RAFPullSourceStream[] sources;

	public DataSource() {
	}

	// public URLDataSource(URL url) throws IOException
	// { this.url = url;
	// setLocator(new MediaLocator(url));
	//		
	// }

	public javax.media.protocol.DataSource createClone() {
		final DataSource d = new DataSource();
		d.setLocator(getLocator());
		if (connected) {
			try {
				d.connect(raf);
			} catch (IOException e) {
				logger.log(Level.WARNING, "" + e, e);
				return null; // according to the API, return null on failure.
			}
		}
		return d;
	}

	public PullSourceStream[] getStreams() {
		if (!connected)
			throw new Error("Unconnected source.");
		return sources;
	}

	private long contentLength = -1;

	public void connect(RandomAccessFile raf) throws IOException {
		// we allow a re-connection even if we are connected, due to an oddity
		// in the way Manager works. See comments there
		// in createPlayer(MediaLocator sourceLocator).

		try {
			final String path = URLUtils
					.extractValidPathFromFileUrl(getLocator().toExternalForm());
			if (path == null)
				throw new IOException(
						"Cannot determine valid file path from URL: "
								+ getLocator().toExternalForm());

			// logger.fine("Path: " + path);
			this.raf = raf;
			contentLength = raf.length();

			String s = getContentTypeFor(path); // TODO: use our own mime
												// mapping
			if (s == null)
				throw new IOException("Unknown content type for path: " + path);
			// TODO: what is the right place to apply
			// ContentDescriptor.mimeTypeToPackageName?
			contentType = new ContentDescriptor(ContentDescriptor
					.mimeTypeToPackageName(s));
			sources = new RAFPullSourceStream[1];
			sources[0] = new RAFPullSourceStream();

			connected = true;
		} catch (IOException e) {
			logger.log(Level.WARNING, "" + e, e);
			throw e;
		}
	}

	private static String getContentTypeFor(String path) {
		final String ext = PathUtils.extractExtension(path);
		// TODO: what if ext is null?
		String result = MimeManager.getMimeType(ext);

		// if we can't find it in our mime table, use URLConnection's

		if (result != null)
			return result;

		result = URLConnection.getFileNameMap().getContentTypeFor(path);
		return result;

	}

	// public static String getFileName(MediaLocator locator)
	// {
	// String path = getLocator().getRemainder();
	//		
	// // Try removing different #s of / from the beginning
	// if (path.startsWith("//"))
	// path = path.substring(2); // TODO: handle stripping
	// }

	public String getContentType() {
		if (!connected)
			throw new Error("Source is unconnected.");
		String path = getLocator().getRemainder();
		String s = getContentTypeFor(path); // TODO: use our own mime mapping
		return ContentDescriptor.mimeTypeToPackageName(s);
	}

	public void disconnect() {
		if (!connected)
			return;
		// TODO: what to do?
		connected = false;
	}

	public void start() throws java.io.IOException {
		// throw new UnsupportedOperationException(); // TODO - what to do?
	}

	public void stop() throws java.io.IOException { // throw new
													// UnsupportedOperationException();
													// // TODO - what to do?
	}

	public Time getDuration() {
		return Time.TIME_UNKNOWN; // TODO: any case where we know the duration?
	}

	public Object[] getControls() {
		return new Object[0];
	}

	public Object getControl(String controlName) {
		return null;
	}

	class RAFPullSourceStream implements PullSourceStream, Seekable {

		private boolean endOfStream = false;

		public boolean endOfStream() {
			return endOfStream;
		}

		public ContentDescriptor getContentDescriptor() {
			return contentType;
		}

		public long getContentLength() {
			return contentLength;
		}

		public int read(byte[] buffer, int offset, int length)
				throws IOException {
			final int result = raf.read(buffer, offset, length); // TODO: does
																	// this
																	// handle
																	// the
																	// requirement
																	// of not
																	// returning
																	// 0 unless
																	// passed in
																	// 0?
			if (result == -1) // end of stream
				endOfStream = true;

			return result;
		}

		public boolean willReadBlock() {
			return false;
		}

		public Object getControl(String controlType) {
			return null;
		}

		public Object[] getControls() {
			return new Object[0];
		}

		public boolean isRandomAccess() {
			return true;
		}

		public long seek(long where) {
			try {
				raf.seek(where);

				return raf.getFilePointer();
			} catch (IOException e) {
				logger.log(Level.WARNING, "" + e, e); // TODO: what to return
				throw new RuntimeException(e);
			}
		}

		public long tell() {
			try {
				return raf.getFilePointer();
			} catch (IOException e) {
				logger.log(Level.WARNING, "" + e, e); // TODO: what to return
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void connect() throws IOException {
		// we allow a re-connection even if we are connected, due to an oddity
		// in the way Manager works. See comments there
		// in createPlayer(MediaLocator sourceLocator).

		try {
			final String path = URLUtils
					.extractValidPathFromFileUrl(getLocator().toExternalForm());
			if (path == null)
				throw new IOException(
						"Cannot determine valid file path from URL: "
								+ getLocator().toExternalForm());

			// logger.fine("Path: " + path);
			this.raf = raf;
			contentLength = raf.length();

			String s = getContentTypeFor(path); // TODO: use our own mime
												// mapping
			if (s == null)
				throw new IOException("Unknown content type for path: " + path);
			// TODO: what is the right place to apply
			// ContentDescriptor.mimeTypeToPackageName?
			contentType = new ContentDescriptor(ContentDescriptor
					.mimeTypeToPackageName(s));
			sources = new RAFPullSourceStream[1];
			sources[0] = new RAFPullSourceStream();

			connected = true;
		} catch (IOException e) {
			logger.log(Level.WARNING, "" + e, e);
			throw e;
		}
		
	}
}

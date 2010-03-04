package net.sf.fmj.media.protocol;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.MediaLocator;
import javax.media.Time;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PullDataSource;
import javax.media.protocol.PullSourceStream;
import javax.media.protocol.SourceCloneable;

import net.sf.fmj.media.MimeManager;
import net.sf.fmj.utility.LoggerSingleton;
import net.sf.fmj.utility.PathUtils;

/**
 * 
 * @author Ken Larson
 * 
 */
public class URLDataSource extends PullDataSource implements SourceCloneable {
	private static final Logger logger = LoggerSingleton.logger;

	protected URLConnection conn;

	protected boolean connected = false;

	private String contentTypeStr;
	private ContentDescriptor contentType;

	protected URLSourceStream[] sources;

	protected URLDataSource() {
		super();
	}

	public URLDataSource(URL url) {
		setLocator(new MediaLocator(url));
	}

	public javax.media.protocol.DataSource createClone() {
		final URLDataSource d;
		try {
			d = new URLDataSource(getLocator().getURL());
		} catch (MalformedURLException e) {
			logger.log(Level.WARNING, "" + e, e);
			return null; // according to the API, return null on failure.
		}

		if (connected) {
			try {
				d.connect();
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

	/**
	 * Strips trailing ; and anything after it. Is generally only used for
	 * multipart content.
	 */
	private String stripTrailer(String contentType) {
		final int index = contentType.indexOf(";");
		if (index < 0)
			return contentType;
		final String result = contentType.substring(0, index);
		return result;

	}

	public void connect() throws IOException {
		// we allow a re-connection even if we are connected, due to an oddity
		// in the way Manager works. See comments there
		// in createPlayer(MediaLocator sourceLocator).
		// if (connected) // TODO: FMJ tends to call this twice. Check with JMF
		// to see if that is normal.
		// return;

		final URL url = getLocator().getURL();

		conn = url.openConnection();

		if (conn instanceof HttpURLConnection) { // TODO: this is probably why
													// JMF has explicit HTTP and
													// FTP data sources - so we
													// can check things
													// explicitly.
			final HttpURLConnection huc = (HttpURLConnection) conn;
			huc.connect();

			final int code = huc.getResponseCode();
			if (!(code >= 200 && code < 300)) {
				huc.disconnect();
				throw new IOException("HTTP response code: " + code);
			}

			// some web servers will give the wrong content type. It is hard to
			// say whether we should just
			// always ignore the web server's content type, and just check the
			// extension, or whether we should
			// only do it for clearly wrong types. For now, we'll just do it for
			// clearly wrong types.

			logger.finer("URL: " + url);
			logger.finer("Response code: " + code);
			logger.finer("Full content type: " + conn.getContentType());

			boolean contentTypeSet = false;
			if (stripTrailer(conn.getContentType()).equals("text/plain")) {
				// see if the URL has an extension, and guess based on that.
				final String ext = PathUtils.extractExtension(url.getPath()); // this
																				// is
																				// a
																				// bit
																				// of
																				// a
																				// hack,
																				// PathUtils.extractExtension
																				// was
																				// designed
																				// for
																				// file
																				// paths,
																				// but
																				// it
																				// works
																				// here.
				if (ext != null) {
					// TODO: what if ext is null?
					final String result = MimeManager.getMimeType(ext);
					if (result != null) {
						contentTypeStr = ContentDescriptor
								.mimeTypeToPackageName(result);
						contentTypeSet = true;

						logger.fine("Received content type "
								+ conn.getContentType()
								+ "; overriding based on extension, to: "
								+ result);
					}
				}

			}

			// TODO: what is the right place to apply
			// ContentDescriptor.mimeTypeToPackageName?
			if (!contentTypeSet)
				contentTypeStr = ContentDescriptor
						.mimeTypeToPackageName(stripTrailer(conn
								.getContentType()));

		} else {
			conn.connect();
			// TODO: what is the right place to apply
			// ContentDescriptor.mimeTypeToPackageName?
			contentTypeStr = ContentDescriptor.mimeTypeToPackageName(conn
					.getContentType());
		}

		contentType = new ContentDescriptor(contentTypeStr);
		sources = new URLSourceStream[1];
		sources[0] = new URLSourceStream();

		connected = true;
	}

	public String getContentType() {
		return contentTypeStr;
	}

	public void disconnect() {
		if (!connected)
			return;

		if (conn != null) {
			if (conn instanceof HttpURLConnection) {
				final HttpURLConnection huc = (HttpURLConnection) conn;
				huc.disconnect();
			}
			// TODO: others
		}

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

	class URLSourceStream implements PullSourceStream {

		private boolean endOfStream = false;

		public boolean endOfStream() {
			return endOfStream;
		}

		public ContentDescriptor getContentDescriptor() {
			return contentType;
		}

		public long getContentLength() {

			return conn.getContentLength(); // returns -1 if unknown, which is
											// the same as LENGTH_UNKNOWN
		}

		public int read(byte[] buffer, int offset, int length)
				throws IOException {
			final int result = conn.getInputStream().read(buffer, offset,
					length); // TODO: does this handle the requirement of not
								// returning 0 unless passed in 0?
			if (result == -1) // end of stream
				endOfStream = true;

			return result;
		}

		public boolean willReadBlock() {
			try {
				return conn.getInputStream().available() <= 0;
			} catch (IOException e) {
				return true;
			}
		}

		public Object getControl(String controlType) {
			return null;
		}

		public Object[] getControls() {
			return new Object[0];
		}
	}
}

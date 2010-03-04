package net.sf.fmj.media.multiplexer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.Format;
import javax.media.Time;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushDataSource;
import javax.media.protocol.PushSourceStream;

import net.sf.fmj.utility.IOUtils;
import net.sf.fmj.utility.LoggerSingleton;

import com.lti.utils.synchronization.CloseableThread;

/**
 * 
 * @author Ken Larson
 * 
 */
public class StreamCopyPushDataSource extends PushDataSource {
	private static final Logger logger = LoggerSingleton.logger;

	private final ContentDescriptor outputContentDescriptor;
	private final int numTracks;
	private final InputStream[] inputStreams;
	private final Format[] inputFormats;
	private InputStreamPushSourceStream[] pushSourceStreams;
	private WriterThread[] writerThreads;

	public StreamCopyPushDataSource(ContentDescriptor outputContentDescriptor,
			int numTracks, InputStream[] inputStreams, Format[] inputFormats) {
		super();
		this.outputContentDescriptor = outputContentDescriptor;
		this.numTracks = numTracks;
		this.inputStreams = inputStreams;
		this.inputFormats = inputFormats;

	}

	public PushSourceStream[] getStreams() {
		System.out.println(getClass().getSimpleName() + " getStreams");
		return pushSourceStreams;
	}

	public void connect() throws IOException {
		System.out.println(getClass().getSimpleName() + " connect");
		this.pushSourceStreams = new InputStreamPushSourceStream[numTracks];
		this.writerThreads = new WriterThread[numTracks];
		for (int track = 0; track < numTracks; ++track) {
			final StreamPipe p = new StreamPipe();
			pushSourceStreams[track] = new InputStreamPushSourceStream(
					outputContentDescriptor, p.getInputStream());
			writerThreads[track] = new WriterThread(track, inputStreams[track],
					p.getOutputStream(), inputFormats[track]);

		}

	}

	public void notifyDataAvailable(int track) {
		pushSourceStreams[track].notifyDataAvailable();
	}

	public void disconnect() {
		System.out.println(getClass().getSimpleName() + " disconnect");

	}

	public String getContentType() {
		System.out.println(getClass().getSimpleName() + " getContentType");
		return outputContentDescriptor.getContentType();
	}

	public Object getControl(String controlType) {
		System.out.println(getClass().getSimpleName() + " getControl");
		return null;
	}

	public Object[] getControls() {
		System.out.println(getClass().getSimpleName() + " getControls");
		return new Object[0];
	}

	public Time getDuration() {
		System.out.println(getClass().getSimpleName() + " getDuration");
		return Time.TIME_UNKNOWN; // TODO
	}

	public void start() throws IOException {
		System.out.println(getClass().getSimpleName() + " start");
		for (int track = 0; track < numTracks; ++track) {
			writerThreads[track].start();
		}
	}

	public void stop() throws IOException {
		System.out.println(getClass().getSimpleName() + " stop");
		for (int track = 0; track < numTracks; ++track) {
			writerThreads[track].close();
		}

		try {
			for (int track = 0; track < numTracks; ++track) {
				writerThreads[track].waitUntilClosed();
			}
		} catch (InterruptedException e) {
			throw new InterruptedIOException();
		}
	}

	public void waitUntilFinished() throws InterruptedException {
		try {
			for (int track = 0; track < numTracks; ++track) {
				writerThreads[track].waitUntilClosed();
			}
		} catch (InterruptedException e) {
			throw e;
		}
	}

	private class WriterThread extends CloseableThread {
		private final int trackID;
		private final InputStream in;
		private final OutputStream out;
		private Format format;

		public WriterThread(final int trackID, final InputStream in,
				final OutputStream out, Format format) {
			super();
			this.trackID = trackID;
			this.in = in;
			this.out = out;
			this.format = format;
		}

		public void run() {
			try {
				write(in, out, trackID);
				out.close();
			} catch (InterruptedIOException e) {
				e.printStackTrace();
				return;
			} catch (IOException e) {
				logger.log(Level.WARNING, "" + e, e);
				// TODO: how to propagate this?
			} finally {
				setClosed();
			}
		}
	}

	protected void write(InputStream in, OutputStream out, int track)
			throws IOException {
		IOUtils.copyStream(in, out);
	}

}
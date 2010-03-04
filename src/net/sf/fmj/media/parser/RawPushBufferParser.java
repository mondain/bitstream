package net.sf.fmj.media.parser;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.BadHeaderException;
import javax.media.Buffer;
import javax.media.Format;
import javax.media.IncompatibleSourceException;
import javax.media.Track;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;

import com.lti.utils.synchronization.SynchronizedBoolean;

import net.sf.fmj.media.AbstractDemultiplexer;
import net.sf.fmj.media.AbstractTrack;
import net.sf.fmj.utility.LoggerSingleton;

/**
 * Demux that doesn't really do much - since the stream does all the work of
 * providing the buffers. It may be that similar sun classes actually do
 * something meaningful. Very similar code could be used for
 * PullBufferDataSource.
 * 
 * @author Ken Larson
 * 
 */
public class RawPushBufferParser extends AbstractDemultiplexer {
	private static final Logger logger = LoggerSingleton.logger;

	private ContentDescriptor[] supportedInputContentDescriptors = new ContentDescriptor[] { new ContentDescriptor(
			ContentDescriptor.RAW) };

	private PushBufferDataSource source;

	private PushBufferStreamTrack[] tracks;

	// @Override
	public ContentDescriptor[] getSupportedInputContentDescriptors() {
		return supportedInputContentDescriptors;
	}

	// @Override
	public Track[] getTracks() throws IOException, BadHeaderException {
		return tracks;
	}

	// @Override
	public void setSource(DataSource source) throws IOException,
			IncompatibleSourceException {
		if (!(source instanceof PushBufferDataSource))
			throw new IncompatibleSourceException();

		this.source = (PushBufferDataSource) source;

	}

	// @Override
	public void start() throws IOException {
		source.start();
		if (tracks == null) {
			// we assume that streams do not change if we stop and restart.
			// TODO: is this true?

			final PushBufferStream[] streams = source.getStreams();

			tracks = new PushBufferStreamTrack[streams.length];
			for (int i = 0; i < streams.length; ++i) {
				tracks[i] = new PushBufferStreamTrack(streams[i]);
			}
		}
	}

	// @Override
	public void stop() {
		try {
			source.stop();
		} catch (IOException e) {
			logger.log(Level.WARNING, "" + e, e);
		}
	}

	private static final boolean USE_BUFFER_TRANSFER_HANDLER = true;
	private static final int POLL_INTERVAL = 1000; // how often to wake up and
													// read anyway, even if not
													// notified by handler. Kind
													// of an insurance policy.

	private class PushBufferStreamTrack extends AbstractTrack implements
			BufferTransferHandler {
		private final PushBufferStream stream;

		public PushBufferStreamTrack(PushBufferStream stream) {
			super();
			this.stream = stream;
			if (USE_BUFFER_TRANSFER_HANDLER)
				stream.setTransferHandler(this);
		}

		public Format getFormat() {
			return stream.getFormat();
		}

		private SynchronizedBoolean dataAvailable = new SynchronizedBoolean();

		public void transferData(PushBufferStream stream) {
			dataAvailable.setValue(true);

		}

		public void readFrame(Buffer buffer) {

			// TODO: use setTransferHandler to know when to read?
			// the transfer handler is not as useful as it might seem, since
			// read is (I think) a blocking method, and the reading all occurs
			// within
			// its own thread anyway. KAL.
			try {
				if (USE_BUFFER_TRANSFER_HANDLER) {
					try {
						synchronized (dataAvailable) {
							dataAvailable.waitUntil(true, POLL_INTERVAL);
							dataAvailable.setValue(false);
						}
					} catch (InterruptedException e) {
						throw new InterruptedIOException();
					}
				}

				stream.read(buffer);

			} catch (IOException e) {
				buffer.setEOM(true); // TODO
				buffer.setDiscard(true); // TODO
				buffer.setLength(0);
				if (!(e instanceof InterruptedIOException)) // logging
															// interruptions is
															// noisy.
					logger.log(Level.WARNING, "" + e, e);
			}

		}

	}

}

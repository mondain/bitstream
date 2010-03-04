package net.sf.fmj.media.parser;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.BadHeaderException;
import javax.media.Buffer;
import javax.media.Format;
import javax.media.IncompatibleSourceException;
import javax.media.Track;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.PullDataSource;
import javax.media.protocol.PullSourceStream;

import net.sf.fmj.media.AbstractDemultiplexer;
import net.sf.fmj.media.AbstractTrack;
import net.sf.fmj.utility.LoggerSingleton;

/**
 * There is a similar class in com.sun.media.parser
 * 
 * @author Ken Larson
 * 
 */
public class RawPullStreamParser extends AbstractDemultiplexer {
	private static final Logger logger = LoggerSingleton.logger;

	private ContentDescriptor[] supportedInputContentDescriptors = new ContentDescriptor[] { new ContentDescriptor(
			ContentDescriptor.RAW) };

	private PullDataSource source;

	private PullSourceStreamTrack[] tracks;

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
		if (!(source instanceof PullDataSource))
			throw new IncompatibleSourceException();

		this.source = (PullDataSource) source;

	}

	// @Override
	public void start() throws IOException {
		source.start();
		final PullSourceStream[] streams = source.getStreams();

		tracks = new PullSourceStreamTrack[streams.length];
		for (int i = 0; i < streams.length; ++i) {
			tracks[i] = new PullSourceStreamTrack(streams[i]);
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

	private class PullSourceStreamTrack extends AbstractTrack {
		private final PullSourceStream stream;

		public PullSourceStreamTrack(PullSourceStream stream) {
			super();
			this.stream = stream;
		}

		public Format getFormat() {
			return stream.getContentDescriptor(); // TODO: why not
													// stream.getFormat()?
		}

		public void readFrame(Buffer buffer) {
			final int BUFFER_SIZE = 10000; // TODO: how do we determine this
											// size? Is this what the
											// BufferControl control is for?
			if (buffer.getData() == null)
				buffer.setData(new byte[BUFFER_SIZE]);
			byte[] bytes = (byte[]) buffer.getData();

			// TODO: set other buffer fields, like format or sequenceNumber?
			try {
				int result = stream.read(bytes, 0, bytes.length);
				if (result < 0) {
					buffer.setEOM(true);
					buffer.setLength(0);
					return;
				}
				buffer.setLength(result);
				buffer.setOffset(0);
			} catch (IOException e) {
				buffer.setEOM(true); // TODO
				buffer.setDiscard(true); // TODO
				buffer.setLength(0);
				logger.log(Level.WARNING, "" + e, e);
			}

		}

	}

}

package net.sf.fmj.media.multiplexer;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.ResourceUnavailableException;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;

import net.sf.fmj.media.AbstractMultiplexer;
import net.sf.fmj.media.BufferQueueInputStream;
import net.sf.fmj.utility.LoggerSingleton;

/**
 * Mux that can be implemented simply by copying streams. Override
 * createInputStreamPushDataSource and create an overridden version of
 * StreamCopyPushDataSource overriding write.
 * 
 * @author Ken Larson
 * 
 */
public abstract class AbstractStreamCopyMux extends AbstractMultiplexer {
	private static final Logger logger = LoggerSingleton.logger;

	private BufferQueueInputStream[] bufferQueueInputStreams;
	private StreamCopyPushDataSource dataOutput;

	private final ContentDescriptor contentDescriptor;

	// TODO: deal with n tracks properly

	public AbstractStreamCopyMux(final ContentDescriptor contentDescriptor) {
		super();
		this.contentDescriptor = contentDescriptor;

	}

	public DataSource getDataOutput() {
		if (dataOutput == null)
			dataOutput = createInputStreamPushDataSource(
					outputContentDescriptor, numTracks,
					bufferQueueInputStreams, inputFormats);
		System.out.println(getClass().getSimpleName() + " getDataOutput");
		return dataOutput;
	}

	public abstract Format[] getSupportedInputFormats();

	public void close() {
		System.out.println(getClass().getSimpleName() + " close");
		super.close();

		if (dataOutput != null) {
			try {
				dataOutput.stop();
			} catch (IOException e) {
				e.printStackTrace();
			}
			dataOutput.disconnect();
		}
	}

	public void open() throws ResourceUnavailableException {
		System.out.println(getClass().getSimpleName() + " open");
		super.open();
	}

	public ContentDescriptor[] getSupportedOutputContentDescriptors(
			Format[] inputs) {
		// TODO: should this match the # of entries in inputs?
		return new ContentDescriptor[] { contentDescriptor };
	}

	public int process(Buffer buffer, int trackID) {
		System.out.println(getClass().getSimpleName() + "process " + buffer
				+ " " + trackID + " length " + buffer.getLength());
		// need a PushDataSource, with a PushSourceStream that reads from out

		if (!bufferQueueInputStreams[trackID].put(buffer))
			return INPUT_BUFFER_NOT_CONSUMED;

		try {
			if (buffer.isEOM()) {
				logger.fine("EOM, waitUntilFinished");
				if (dataOutput != null)
					dataOutput.waitUntilFinished();
				// wait until done processing
				logger.fine("EOM, finished");
			}

			if (dataOutput != null)
				dataOutput.notifyDataAvailable(trackID);

			return BUFFER_PROCESSED_OK;

		} catch (InterruptedException e) {
			e.printStackTrace();
			return BUFFER_PROCESSED_FAILED;
		}
	}

	public int setNumTracks(int numTracks) {
		numTracks = super.setNumTracks(numTracks);

		bufferQueueInputStreams = new BufferQueueInputStream[numTracks];
		for (int track = 0; track < numTracks; ++track) {
			bufferQueueInputStreams[track] = new BufferQueueInputStream();
		}
		return numTracks;
	}

	protected StreamCopyPushDataSource createInputStreamPushDataSource(
			ContentDescriptor outputContentDescriptor, int numTracks,
			InputStream[] inputStreams, Format[] inputFormats) {
		return new StreamCopyPushDataSource(outputContentDescriptor, numTracks,
				inputStreams, inputFormats);
	}

}

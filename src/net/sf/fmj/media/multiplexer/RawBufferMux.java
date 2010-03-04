package net.sf.fmj.media.multiplexer;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.logging.Logger;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.ResourceUnavailableException;
import javax.media.Time;
import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;

import net.sf.fmj.codegen.MediaCGUtils;
import net.sf.fmj.media.AbstractMultiplexer;
import net.sf.fmj.utility.LoggerSingleton;

import com.lti.utils.synchronization.ProducerConsumerQueue;

/**
 * 
 * @author Ken Larson
 * 
 */
public abstract class RawBufferMux extends AbstractMultiplexer {
	private final boolean TRACE = false;
	private static final Logger logger = LoggerSingleton.logger;

	private RawBufferDataSource dataOutput;
	private ProducerConsumerQueue[] queues;
	private RawBufferSourceStream[] streams;

	private final ContentDescriptor contentDescriptor;

	protected RawBufferMux(ContentDescriptor contentDescriptor) {
		super();
		this.contentDescriptor = contentDescriptor;
	}

	public RawBufferMux() {
		this(new ContentDescriptor(ContentDescriptor.RAW));
	}

	public DataSource getDataOutput() {
		if (dataOutput == null)
			dataOutput = new RawBufferDataSource();
		if (TRACE)
			System.out.println(getClass().getSimpleName() + " getDataOutput");
		return dataOutput;
	}

	public Format[] getSupportedInputFormats() {
		if (TRACE)
			System.out.println(getClass().getSimpleName()
					+ " getSupportedInputFormats");
		return new Format[] {
				new AudioFormat(null, -1.0, -1, -1, -1, -1, -1, -1.0,
						Format.byteArray),
				new VideoFormat(null, null, -1, Format.byteArray, -1.0f) };
	}

	public void close() {
		if (TRACE)
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
		if (TRACE)
			System.out.println("open");
		super.open();
	}

	public ContentDescriptor[] getSupportedOutputContentDescriptors(
			Format[] inputs) {
		// TODO: should this match the # of entries in inputs?
		return new ContentDescriptor[] { contentDescriptor };
	}

	public int process(Buffer buffer, int trackID) {
		if (TRACE)
			System.out.println("RawBufferMux process: "
					+ MediaCGUtils.bufferToStr(buffer));
		if (TRACE)
			System.out.println(getClass().getSimpleName() + " process "
					+ buffer + " " + trackID + " length " + buffer.getLength());

		try {
			queues[trackID].put((Buffer) buffer.clone());

			if (buffer.isEOM()) {
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
		if (TRACE)
			System.out.println("setNumTracks");

		numTracks = super.setNumTracks(numTracks);

		queues = new ProducerConsumerQueue[numTracks];
		for (int track = 0; track < numTracks; ++track) {
			queues[track] = new ProducerConsumerQueue();
		}
		streams = new RawBufferSourceStream[numTracks];
		for (int track = 0; track < numTracks; ++track) {
			streams[track] = new RawBufferSourceStream(queues[track]);
		}

		return numTracks;
	}

	public Format setInputFormat(Format format, int trackID) {
		if (streams != null) // TODO: if null, make sure we set later!
			streams[trackID].setFormat(format);
		return super.setInputFormat(format, trackID);
	}

	private class RawBufferDataSource extends PushBufferDataSource {

		public PushBufferStream[] getStreams() {
			if (TRACE)
				System.out.println("getStreams");
			return streams;
		}

		public void connect() throws IOException {
			if (TRACE)
				System.out.println(getClass().getSimpleName() + " connect");

		}

		public void disconnect() {
			if (TRACE)
				System.out.println(getClass().getSimpleName() + " disconnect");
		}

		public String getContentType() {
			return outputContentDescriptor.getContentType();
		}

		public Object getControl(String controlType) {
			if (TRACE)
				System.out.println("getControl");
			return null;
		}

		public Object[] getControls() {
			if (TRACE)
				System.out.println("getControls");
			return new Object[0];
		}

		public Time getDuration() {
			return DURATION_UNKNOWN;
		}

		public void start() throws IOException {
			if (TRACE)
				System.out.println(getClass().getSimpleName() + " start");
		}

		public void stop() throws IOException {
			if (TRACE)
				System.out.println(getClass().getSimpleName() + " stop");
		}

		public void notifyDataAvailable(int track) {
			streams[track].notifyDataAvailable();
		}
	}

	private class RawBufferSourceStream implements PushBufferStream {

		private final ProducerConsumerQueue queue;
		private boolean eos;
		private Format format;

		public RawBufferSourceStream(final ProducerConsumerQueue queue) {
			super();
			this.queue = queue;
		}

		public boolean endOfStream() {
			return eos;
		}

		public ContentDescriptor getContentDescriptor() {
			return outputContentDescriptor;
		}

		public long getContentLength() {
			return 0;
		}

		public Object getControl(String controlType) {
			return null;
		}

		public Object[] getControls() {
			return new Object[0];
		}

		void setFormat(Format f) {
			format = f;
		}

		public Format getFormat() {
			return format;
		}

		public void read(Buffer buffer) throws IOException {
			if (TRACE)
				System.out.println(getClass().getSimpleName() + " read");
			try {
				final Buffer next = (Buffer) queue.get();
				if (next == null || next.isEOM())
					eos = true;
				if (next != null) {
					if (buffer.getData() == null)
						buffer.copy(next, false);
					else {
						// according to the API, if the caller sets the
						// data in the buffer, we should not allocate it.
						// See
						// http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/protocol/PushBufferStream.html

						// we use the original offset in the buffer in this
						// case, since that is what is required
						// by FMJ's RTPSendStream.transferData(PushBufferStream
						// stream). This feature does not appear to be
						// defined in the API.

						final Object originalData = buffer.getData();
						final int originalOffset = buffer.getOffset();
						final int originalLength = buffer.getLength();
						buffer.copy(next, false);
						buffer.setData(originalData);
						buffer.setOffset(originalOffset);
						// length is set in copy

						if (next.getLength() > 0) {
							// TODO: what if original data is not big enough?
							if (next.getLength() > originalLength) {
								throw new IllegalArgumentException(
										"Buffer passed in has length: "
												+ buffer.getLength()
												+ "; needs to be at least: "
												+ next.getLength());
							}
							System.arraycopy(next.getData(), next.getOffset(),
									originalData, originalOffset, next
											.getLength());
						}

					}
				} else {
					System.out.println("RawBufferMux EOS");
					System.out.flush();
					buffer.setEOM(true);
					buffer.setLength(0);
					buffer.setOffset(0);
				}
			} catch (InterruptedException e) {
				throw new InterruptedIOException();
			}
		}

		private BufferTransferHandler transferHandler;

		public void setTransferHandler(BufferTransferHandler transferHandler) {
			this.transferHandler = transferHandler;
		}

		public void notifyDataAvailable() {
			if (transferHandler != null)
				transferHandler.transferData(this);
		}

	}

}

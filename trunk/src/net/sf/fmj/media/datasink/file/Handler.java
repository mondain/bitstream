package net.sf.fmj.media.datasink.file;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.IncompatibleSourceException;
import javax.media.datasink.DataSinkErrorEvent;
import javax.media.datasink.EndOfStreamEvent;
import javax.media.protocol.DataSource;
import javax.media.protocol.PushDataSource;
import javax.media.protocol.PushSourceStream;
import javax.media.protocol.Seekable;
import javax.media.protocol.SourceTransferHandler;

import net.sf.fmj.media.AbstractDataSink;
import net.sf.fmj.utility.LoggerSingleton;
import net.sf.fmj.utility.URLUtils;

import com.lti.utils.synchronization.CloseableThread;
import com.lti.utils.synchronization.SynchronizedBoolean;

/**
 * 
 * @author Ken Larson
 * 
 */
public class Handler extends AbstractDataSink {
	private static final Logger logger = LoggerSingleton.logger;

	private PushDataSource source;
	private WriterThread writerThread;

	// TODO: additional listener notifications?

	public Object getControl(String controlType) {
		logger.warning("TODO: getControl " + controlType);
		return null;
	}

	public Object[] getControls() {
		logger.warning("TODO: getControls");
		return new Object[0];
	}

	public void close() {
		try {
			stop();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// TODO: disconnect source?
	}

	public String getContentType() {
		// TODO: do we get this from the source, or the outputLocator?
		if (source != null)
			return source.getContentType();
		else
			return null;
	}

	public void open() throws IOException, SecurityException {
		if (getOutputLocator() == null)
			throw new IOException("Output locator not set");

		final String path = URLUtils
				.extractValidNewFilePathFromFileUrl(getOutputLocator()
						.toExternalForm());
		if (path == null)
			throw new IOException("Cannot determine path from URL: "
					+ getOutputLocator().toExternalForm());

		// System.out.println("open - " + path);
		final RandomAccessFile f = new RandomAccessFile(path, "rw"); // TODO:
																		// ensure
																		// closed
																		// even
																		// if
																		// start/stop
																		// never
																		// called.

		// TODO: check that there is at least 1 stream.
		// TODO: move this code to start() ?
		PushSourceStream[] streams = source.getStreams();
		// System.out.println("streams: " + streams.length);

		source.connect();

		writerThread = new WriterThread(source.getStreams()[0], f); // TODO
																	// other
																	// tracks?

	}

	public void start() throws IOException {
		source.start();

		writerThread.start();
	}

	public void stop() throws IOException {
		if (writerThread != null) {
			writerThread.close();
			try {
				writerThread.waitUntilClosed();
			} catch (InterruptedException e) {
				throw new InterruptedIOException();
			} finally {
				writerThread = null;
			}
		}

		if (source != null)
			source.stop();

	}

	public void setSource(DataSource source) throws IOException,
			IncompatibleSourceException {
		logger.finer("setSource: " + source);
		if (!(source instanceof PushDataSource))
			throw new IncompatibleSourceException();
		this.source = (PushDataSource) source;
	}

	// if we don't implement Seekable, Sun's code will throw a class cast
	// exception.
	// very strange that we need to be able to seek just because we call
	// setTransferHandler.
	private class WriterThread extends CloseableThread implements
			SourceTransferHandler, Seekable {
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

		private final PushSourceStream sourceStream;
		private final RandomAccessFile raf;

		private SynchronizedBoolean dataAvailable = new SynchronizedBoolean();

		private static final boolean USE_TRANSFER_HANDLER = true;
		private static final int DEFAULT_BUFFER_SIZE = 10000;

		public WriterThread(final PushSourceStream sourceStream,
				RandomAccessFile raf) {
			super();
			this.sourceStream = sourceStream;
			this.raf = raf;
			if (USE_TRANSFER_HANDLER)
				sourceStream.setTransferHandler(this);
		}

		public void transferData(PushSourceStream stream) {
			dataAvailable.setValue(true);
		}

		public void run() {
			try {
				logger.fine("getMinimumTransferSize: "
						+ sourceStream.getMinimumTransferSize());
				final byte[] buffer = new byte[sourceStream
						.getMinimumTransferSize() > DEFAULT_BUFFER_SIZE ? sourceStream
						.getMinimumTransferSize()
						: DEFAULT_BUFFER_SIZE];

				boolean eos = false;
				while (!isClosing() && !eos) {
					if (USE_TRANSFER_HANDLER) {
						synchronized (dataAvailable) {
							dataAvailable.waitUntil(true);
							dataAvailable.setValue(false);
						}
					}

					while (true) // read as long as data keeps coming in
					{
						int read = sourceStream.read(buffer, 0, buffer.length);
						if (read == 0) {
							break;
						} else if (read < 0) {
							eos = true;
							raf.close();
							logger.fine("EOS");
							notifyDataSinkListeners(new EndOfStreamEvent(
									Handler.this, "EOS")); // TODO: needed?
							break;
						} else {
							raf.write(buffer, 0, read);
						}
					}

				}
				if (!eos)
					logger.warning("Closed before EOS");
			} catch (IOException e) {
				logger.log(Level.WARNING, "" + e, e);
				notifyDataSinkListeners(new DataSinkErrorEvent(Handler.this, e
						.getMessage()));

			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				setClosed();
			}
		}

	}

}

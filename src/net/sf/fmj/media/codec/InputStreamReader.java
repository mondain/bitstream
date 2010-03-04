package net.sf.fmj.media.codec;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

import javax.media.Buffer;

import com.lti.utils.synchronization.CloseableThread;
import com.lti.utils.synchronization.ProducerConsumerQueue;

/**
 * Class to wrap an InputStream and do reads in a background thread, so that
 * read never blocks. Used for badly behaving input streams where available() is
 * not working right or useless.
 * 
 * @author Ken Larson
 * 
 */
public class InputStreamReader extends InputStream {
	private final InputStream is;
	private final int bufferSize;
	private ReaderThread readerThread;

	private final ProducerConsumerQueue emptyQueue = new ProducerConsumerQueue();
	private final ProducerConsumerQueue fullQueue = new ProducerConsumerQueue();

	public InputStreamReader(final InputStream is, final int bufferSize) {
		super();
		this.is = is;
		this.bufferSize = bufferSize;

		for (int i = 0; i < 2; ++i) {
			Buffer b = new Buffer();
			b.setData(new byte[bufferSize]);
			try {
				emptyQueue.put(b);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		readerThread = new ReaderThread(emptyQueue, fullQueue, is, bufferSize);

	}

	private boolean readerThreadStarted;

	public void startReaderThread() {
		if (!readerThreadStarted) {
			readerThread.start();
			readerThreadStarted = true;
		}
	}

	public void close() throws IOException {
		super.close();

		if (readerThread != null) {
			readerThread.close();
			readerThread = null;
		}
	}

	public int available() throws IOException {

		if (readException != null)
			throw readException;
		if (readBuffer != null && readBuffer.getLength() > 0)
			return readBuffer.getLength();
		else
			return 0;
	}

	public int read() throws IOException {
		byte[] ba = new byte[1];
		int result = read(ba, 0, 1);
		if (result == -1)
			return -1;
		return ba[0] & 0xff;

	}

	private Buffer readBuffer;
	private IOException readException;

	public int read(byte[] b, int off, int len) throws IOException {
		try {
			if (readBuffer == null && readException == null) {
				Object o = fullQueue.get();
				if (o instanceof IOException)
					readException = (IOException) o;
				else
					readBuffer = (Buffer) o;
			}

			if (readException != null)
				throw readException;

			if (readBuffer.isEOM())
				return -1;
			final byte[] readBufferData = (byte[]) readBuffer.getData();

			final int lenToCopy = readBuffer.getLength() < len ? readBuffer
					.getLength() : len;
			System.arraycopy(readBufferData, readBuffer.getOffset(), b, off,
					lenToCopy);
			readBuffer.setOffset(readBuffer.getOffset() + lenToCopy);
			readBuffer.setLength(readBuffer.getLength() - lenToCopy);
			if (readBuffer.getLength() == 0) {
				emptyQueue.put(readBuffer);
				readBuffer = null;
			}
			return lenToCopy;
		} catch (InterruptedException e) {
			throw new InterruptedIOException();
		}
	}

	private static class ReaderThread extends CloseableThread {
		private final ProducerConsumerQueue emptyQueue;
		private final ProducerConsumerQueue fullQueue;
		private final InputStream is;
		private final int bufferSize;

		public ReaderThread(final ProducerConsumerQueue emptyQueue,
				final ProducerConsumerQueue fullQueue, final InputStream is,
				final int bufferSize) {
			super();
			this.emptyQueue = emptyQueue;
			this.fullQueue = fullQueue;
			this.is = is;
			this.bufferSize = bufferSize;
		}

		public void run() {
			try {
				while (!isClosing()) {
					Buffer b = (Buffer) emptyQueue.get();
					b.setEOM(false);
					b.setLength(0);
					b.setOffset(0);

					int len = is.read((byte[]) b.getData(), 0, bufferSize);
					if (len < 0)
						b.setEOM(true);
					else
						b.setLength(len);
					fullQueue.put(b);

				}
			} catch (InterruptedException e) {
			} catch (IOException e) {
				try {
					fullQueue.put(e);
				} catch (InterruptedException e1) {
				}
			} finally {
				setClosed();
			}
		}
	}

}
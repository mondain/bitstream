package net.sf.fmj.media.multiplexer;

import java.io.IOException;
import java.io.InputStream;

import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushSourceStream;
import javax.media.protocol.SourceTransferHandler;

/**
 * 
 * @author Ken Larson
 * 
 */
public class InputStreamPushSourceStream implements PushSourceStream {

	private final ContentDescriptor outputContentDescriptor;
	private final InputStream is;

	public InputStreamPushSourceStream(
			ContentDescriptor outputContentDescriptor, final InputStream is) {
		super();
		this.outputContentDescriptor = outputContentDescriptor;
		this.is = is;
	}

	public int getMinimumTransferSize() {
		System.out.println(getClass().getSimpleName()
				+ " getMinimumTransferSize");
		return 0;
	}

	private boolean eos;

	public int read(byte[] buffer, int offset, int length) throws IOException {
		// System.out.println(getClass().getSimpleName() + " read");
		int result = is.read(buffer, offset, length);
		if (result < 0)
			eos = true;
		return result;
	}

	private SourceTransferHandler transferHandler;

	public void setTransferHandler(SourceTransferHandler transferHandler) {
		System.out.println(getClass().getSimpleName() + " setTransferHandler");
		this.transferHandler = transferHandler;
	}

	public void notifyDataAvailable() {
		if (transferHandler != null) // TODO; synchronization issues on
										// transferHandler
			transferHandler.transferData(this);
	}

	public boolean endOfStream() {
		System.out.println(getClass().getSimpleName() + " endOfStream");
		return eos;
	}

	public ContentDescriptor getContentDescriptor() {
		System.out
				.println(getClass().getSimpleName() + " getContentDescriptor");
		return outputContentDescriptor;
	}

	public long getContentLength() {
		System.out.println(getClass().getSimpleName() + " getContentLength");
		return 0; // TODO
	}

	public Object getControl(String controlType) {
		System.out.println(getClass().getSimpleName() + " getControl");
		return null;
	}

	public Object[] getControls() {
		System.out.println(getClass().getSimpleName() + " getControls");
		return new Object[0];
	}
}
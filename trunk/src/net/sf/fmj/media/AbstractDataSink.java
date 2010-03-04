package net.sf.fmj.media;

import java.util.ArrayList;
import java.util.List;

import javax.media.DataSink;
import javax.media.MediaLocator;
import javax.media.datasink.DataSinkEvent;
import javax.media.datasink.DataSinkListener;

/**
 * 
 * @author Ken Larson
 * 
 */
public abstract class AbstractDataSink implements DataSink {

	private final List listeners = new ArrayList(); // of DataSinkListener

	public void addDataSinkListener(DataSinkListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	public void removeDataSinkListener(DataSinkListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	protected void notifyDataSinkListeners(DataSinkEvent event) {
		final List listenersCopy = new ArrayList();

		synchronized (listeners) {
			listenersCopy.addAll(listeners);
		}

		for (int i = 0; i < listenersCopy.size(); ++i) {
			DataSinkListener listener = (DataSinkListener) listenersCopy.get(i);
			listener.dataSinkUpdate(event);
		}
	}

	protected MediaLocator outputLocator;

	public void setOutputLocator(MediaLocator output) {
		this.outputLocator = output;
	}

	public MediaLocator getOutputLocator() {
		return outputLocator;
	}

}

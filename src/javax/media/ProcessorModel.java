package javax.media;

import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;

/**
 * Coding complete.
 * 
 * @author Ken Larson
 * 
 */
public class ProcessorModel {
	private MediaLocator inputLocator;
	/**
	 * Output formats.
	 */
	private Format[] formats;
	private ContentDescriptor outputContentDescriptor;
	private DataSource inputDataSource;

	public ProcessorModel() {
	}

	public ProcessorModel(Format[] formats,
			ContentDescriptor outputContentDescriptor) {
		this.formats = formats;
		this.outputContentDescriptor = outputContentDescriptor;
	}

	public ProcessorModel(DataSource inputDataSource, Format[] formats,
			ContentDescriptor outputContentDescriptor) {
		this.inputDataSource = inputDataSource;

		this.formats = formats;
		this.outputContentDescriptor = outputContentDescriptor;
	}

	public ProcessorModel(MediaLocator inputLocator, Format[] formats,
			ContentDescriptor outputContentDescriptor) {
		this.inputLocator = inputLocator;
		this.formats = formats;
		this.outputContentDescriptor = outputContentDescriptor;

	}

	public int getTrackCount(int availableTrackCount) {
		if (formats == null)
			return -1;
		return formats.length; // TODO: what is input param for?
	}

	public Format getOutputTrackFormat(int tIndex) {
		if (formats == null)
			return null;
		if (tIndex < 0 || tIndex >= formats.length)
			return null;

		return formats[tIndex];
	}

	public boolean isFormatAcceptable(int tIndex, Format tFormat) {
		if (formats == null)
			return true;
		if (tIndex < 0 || tIndex >= formats.length)
			return true;

		return tFormat.matches(formats[tIndex]);

	}

	public ContentDescriptor getContentDescriptor() {
		return outputContentDescriptor;
	}

	public DataSource getInputDataSource() {
		return inputDataSource;
	}

	public MediaLocator getInputLocator() {
		// if (inputDataSource != null)
		// return inputDataSource.getLocator(); // TODO: it appears we return
		// null if a data source was specified.
		return inputLocator;
	}

}

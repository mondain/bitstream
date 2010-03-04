package net.sf.fmj.media;

import java.util.logging.Logger;

import javax.media.Format;
import javax.media.Multiplexer;
import javax.media.protocol.ContentDescriptor;

import net.sf.fmj.utility.LoggerSingleton;

/**
 * 
 * @author Ken Larson
 * 
 */
public abstract class AbstractMultiplexer extends AbstractPlugIn implements
		Multiplexer {
	private static final Logger logger = LoggerSingleton.logger;

	protected ContentDescriptor outputContentDescriptor;

	public ContentDescriptor setContentDescriptor(
			ContentDescriptor outputContentDescriptor) {
		this.outputContentDescriptor = outputContentDescriptor;
		return outputContentDescriptor;
	}

	protected Format[] inputFormats;

	public Format setInputFormat(Format format, int trackID) {
		logger.finer("setInputFormat " + format + " " + trackID);
		if (inputFormats != null) // TODO: should we save this somewhere and
									// apply once inputFormats is not null?
			inputFormats[trackID] = format;

		return format;
	}

	protected int numTracks;

	public int setNumTracks(int numTracks) {
		logger.finer("setNumTracks " + numTracks);
		inputFormats = new Format[numTracks];

		this.numTracks = numTracks;
		return numTracks;
	}
}

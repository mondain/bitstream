/**
 * 
 */
package net.sf.fmj.filtergraph;

import java.io.IOException;

import javax.media.Buffer;
import javax.media.Demultiplexer;
import javax.media.Format;
import javax.media.ResourceUnavailableException;
import javax.media.Track;

/**
 * A node in a filter graph for a Demultiplexer.
 * 
 * @author Ken Larson
 * 
 */
public class DemuxNode extends FilterGraphNode {
	private final Format inputFormat;
	private final Demultiplexer demux;
	/**
	 * Must have the same number of entries as dest.
	 */
	private Track[] tracks;

	public long sequenceNumber = 0L; // TODO: 1 per track

	public DemuxNode(Format format, Demultiplexer demux, Track[] tracks) {
		super(demux);
		this.inputFormat = format;
		this.demux = demux;
		this.setTracks(tracks);
	}

	public FilterGraphNode duplicate() {
		return propagateDuplicate(new DemuxNode(getInputFormat(), getDemux(),
				getTracks()));

	}

	public void setTracks(Track[] tracks) {
		this.tracks = tracks;
	}

	public Track[] getTracks() {
		return tracks;
	}

	public Format getInputFormat() {
		return inputFormat;
	}

	public Demultiplexer getDemux() {
		return demux;
	}

	public void open() throws ResourceUnavailableException {
		getDemux().open();
	}

	@Override
	public void start() throws IOException {
		getDemux().start(); // TODO: what if already started?
	}

	@Override
	public void stop() {
		getDemux().stop();
	}

	public int process(final Buffer input, final int sourceTrackNumber,
			final int destTrackNumber, final int flags) {
		for (int i = 0; i < getTracks().length; ++i) {
			if (sourceTrackNumber >= 0 && i != sourceTrackNumber) { // nCast.setBuffer(i,
																	// null);
				continue;
			}
			final FilterGraphLink destLink = getDestLink(i);
			if (destLink == null) { // nCast.setBuffer(i, null);
				continue;
			}

			if ((flags & FilterGraph.SUPPRESS_TRACK_READ) == 0) {
				// JMF re-uses the previous buffer
				if (getOutputBuffer(i) == null) {
					setOutputBuffer(i, new Buffer());
				}
				final Buffer buffer1 = (Buffer) getOutputBuffer(i);
				buffer1.setLength(0);
				buffer1.setSequenceNumber(sequenceNumber++); // TODO: 1 seq #
																// per track is
																// needed
				buffer1.setFlags(0); // clear all flags. TODO: does JMF do this?
										// Or must the demux itself clear the
										// flags?

				// // It does not appear that JMF sets the timestamp to what it
				// thinks it is going to be.
				// although this is theoretically possible knowing the
				// frame/buffer # and the framerate.

				// according to the docs, Each track has a sequence number that
				// is updated by the Demultiplexer for each frame
				// however, the OggDemux does not do this, and it appears that
				// JMF sets the sequence number before giving it to the demux.
				// TODO: other fields to clear?

				getTracks()[i].readFrame(buffer1);
				if (buffer1.getFormat() == null)
					buffer1.setFormat(getTracks()[i].getFormat()); // TODO: is
																	// this
																	// right?
																	// JMF
																	// appears
																	// to set
																	// the
																	// format in
																	// between
																	// demux
																	// track
																	// read adnd
																	// codec
																	// process.

			}

		}

		return Demultiplexer.BUFFER_PROCESSED_OK; // not really meaningful here

	}

}
/**
 * 
 */
package net.sf.fmj.filtergraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.PlugIn;
import javax.media.ResourceUnavailableException;

import net.sf.fmj.utility.LoggerSingleton;

/**
 * Base class for all filter graph nodes.
 * 
 * @author Ken Larson
 * 
 */
public abstract class FilterGraphNode {

	protected static final Logger logger = LoggerSingleton.logger;

	/**
	 * Destination nodes.
	 */
	private List/* <FilterGraphLink> */destLinks = new ArrayList/*
																 * <FilterGraphLink
																 * >
																 */();

	// processing state:
	/**
	 * Output buffer n is for destination node n. For a node which is a renderer
	 * node, the corresponding buffer will be null. Also, if a particular
	 * track/dest is not being processed at all by a graph, the corresponding
	 * buffer will be null.
	 */
	private List/* <Buffer> */outputBuffers = new ArrayList();

	private final PlugIn plugIn;

	public FilterGraphNode(final PlugIn plugIn) {
		super();
		this.plugIn = plugIn;
	}

	public int getNumDestLinks() {
		return destLinks.size();
	}

	public FilterGraphLink getDestLink(int i) {
		try {
			return (FilterGraphLink) destLinks.get(i);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public Buffer getOutputBuffer(int i) {
		try {
			return (Buffer) outputBuffers.get(i);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public void setOutputBuffer(int i, Buffer b) {
		outputBuffers.set(i, b);
	}

	public void addDestLink(FilterGraphLink n) {
		destLinks.add(n);
		outputBuffers.add(null);
	}

	public abstract FilterGraphNode duplicate();

	public abstract void open() throws ResourceUnavailableException;

	// TODO: close() method
	/**
	 * sourceTrackNumber only used for demux, and destTrackNumber only used for
	 * mux.
	 */
	public abstract int process(Buffer input, int sourceTrackNumber,
			int destTrackNumber, int flags);

	/**
	 * Intended to be called by subclass implementations of duplicate, to
	 * duplicate the destinations.
	 * 
	 * @param result
	 *            FilterGraphNode that is to be returned by duplicate();
	 */
	protected FilterGraphNode propagateDuplicate(FilterGraphNode result) {
		for (int i = 0; i < getNumDestLinks(); ++i) {
			FilterGraphLink link = getDestLink(i);
			if (link != null) {
				result.addDestLink(new FilterGraphLink(link.getDestNode()
						.duplicate(), link.getDestTrack()));
			}
		}
		return result;
	}

	public void print(Logger logger, String prefix) {
		final String inputFormatStr = getInputFormat() == null ? ""
				: (getInputFormat().getClass().getSimpleName() + " ["
						+ getInputFormat() + "]");
		logger.info(prefix + inputFormatStr);
		logger.info(prefix + getPlugIn().getClass().getName());
	}

	public abstract Format getInputFormat();

	public PlugIn getPlugIn() {
		return plugIn;
	}

	public void start() throws IOException { // do nothing by default
	}

	public void stop() { // do nothing by default
	}

}
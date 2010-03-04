package net.sf.fmj.filtergraph;

/**
 * A link to a destination node, specifying a specific track at the dest. Track
 * is only relevant for a mux.
 * 
 * @author Ken Larson
 * 
 */
public class FilterGraphLink {
	private FilterGraphNode destNode; // may not be null.
	private final int destTrack;

	public FilterGraphLink(FilterGraphNode destNode, final int destTrack) {
		super();
		if (destNode == null)
			throw new NullPointerException();
		if (destNode instanceof MuxNode && destTrack < 0)
			throw new IllegalArgumentException();
		this.destNode = destNode;
		this.destTrack = destTrack;
	}

	public FilterGraphLink(FilterGraphNode destNode) {
		super();
		if (destNode == null)
			throw new NullPointerException();
		if (destNode instanceof MuxNode)
			throw new IllegalArgumentException();
		this.destNode = destNode;
		this.destTrack = -1;
	}

	public FilterGraphNode getDestNode() {
		return destNode;
	}

	public int getDestTrack() {
		return destTrack;
	}
}

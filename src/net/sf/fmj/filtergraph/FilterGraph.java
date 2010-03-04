package net.sf.fmj.filtergraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.BadHeaderException;
import javax.media.Buffer;
import javax.media.Codec;
import javax.media.Demultiplexer;
import javax.media.Format;
import javax.media.IncompatibleSourceException;
import javax.media.Multiplexer;
import javax.media.PlugIn;
import javax.media.PlugInManager;
import javax.media.Renderer;
import javax.media.ResourceUnavailableException;
import javax.media.Track;
import javax.media.control.BufferControl;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;

import net.sf.fmj.utility.LoggerSingleton;

import com.lti.utils.ObjUtils;

/**
 * Build and execute functions on filter graphs. Does not actually represent the
 * graph itself, that would be simply a FilterGraphNode being used as a root.
 * TODO: if a demux for example sets both discard and eom, what are the
 * semantics? It appears that we will discard the EOM.
 * 
 * @author Ken Larson
 * 
 */
public final class FilterGraph {
	private static final int MAX_GRAPH_DEPTH = 10; // prevent searches from
													// taking too long.
	private static final int TYPICAL_GRAPH_DEPTH = 0; // disable this feature,
														// too slow.
	// Most graphs have 4 or less nodes: demux, codec, renderer is typical, but
	// we'll add an extra in case there are 2 codecs.
	// for a mux, this might be demux, codec, encoder, packetizer, mux
	private static final int BEST_FIRST_SEARCH_DEPTH = 8; // 0 will disable (but
															// this is slow)
	private static final int BEST_FIRST_SEARCH_BREADTH = 5;

	private static final Logger logger = LoggerSingleton.logger;

	private static final boolean TRACE = true;

	private FilterGraph() {
		super();
	}

	public static FilterGraphNode getTail(FilterGraphNode n) {
		if (n == null) {
			return n;
		}
		while (n.getNumDestLinks() > 0)
			n = n.getDestLink(0).getDestNode(); // TODO: this doesn't handle
												// demuxs
		return n;
	}

	public static FilterGraphNode getBeforeTail(FilterGraphNode n) {
		while (n.getNumDestLinks() > 0
				&& n.getDestLink(0).getDestNode().getNumDestLinks() > 0) {
			n = n.getDestLink(0).getDestNode(); // TODO: this doesn't handle
												// demuxs
		}
		return n;
	}

	public static void start(FilterGraphNode n) throws IOException {
		n.start();

		for (int i = 0; i < n.getNumDestLinks(); ++i) {
			final FilterGraphLink destLink = n.getDestLink(i);
			if (destLink != null)
				start(destLink.getDestNode()); // track doesn't matter for this.
												// TODO: prevent starting same
												// node twice.
		}

	}

	public static void stop(FilterGraphNode n) throws IOException {
		n.stop();

		for (int i = 0; i < n.getNumDestLinks(); ++i) {
			final FilterGraphLink destLink = n.getDestLink(i);
			if (destLink != null)
				stop(destLink.getDestNode()); // track doesn't matter for this.
												// TODO: prevent starting same
												// node twice.
		}

	}

	public static void open(FilterGraphNode n)
			throws ResourceUnavailableException {
		n.open();

		for (int i = 0; i < n.getNumDestLinks(); ++i) {
			final FilterGraphLink destLink = n.getDestLink(i);
			if (destLink != null)
				open(destLink.getDestNode()); // track doesn't matter for this.
												// TODO: prevent opening same
												// node twice.
		}

	}

	private static String tabs(int i) {
		StringBuffer b = new StringBuffer();
		while (i-- > 0)
			b.append('\t');
		return b.toString();
	}

	public static void print(FilterGraphNode n, int tabs) {
		print(new FilterGraphLink(n), tabs);
	}

	public static void print(FilterGraphLink link, int tabs) {
		final FilterGraphNode n = link.getDestNode();

		String trackStr = "";
		if (link.getDestTrack() >= 0)
			trackStr = "[Track " + link.getDestTrack() + " of] ";
		n.print(logger, tabs(tabs) + trackStr);

		for (int j = 0; j < n.getNumDestLinks(); ++j) {
			final FilterGraphLink linkChild = n.getDestLink(j);
			if (linkChild != null)
				print(linkChild, tabs + 1);
		}

	}

	public static final int PROCESS_DEFAULT = 0;
	public static final int SUPPRESS_TRACK_READ = 0x0001; // used to be able to
															// re-display a
															// frame. For
															// example, when we
															// first open a
															// movie, we usually
															// want to get the
															// first frame to
															// see how big it is

	/**
	 * Process a graph starting with n
	 * 
	 * @param n
	 * @param input
	 *            the source buffer sourceTrackNumber only used for demux, and
	 *            destTrackNumber only used for mux
	 */
	public static void process(final FilterGraphNode n, final Buffer input,
			final int sourceTrackNumber, final int destTrackNumber,
			final int flags) {
		// if (sourceTrackNumber < 0)
		// throw new IllegalArgumentException();
		// EOM propagation needs to go all the way to renderer, that's what JMF
		// does.

		int result = PlugIn.BUFFER_PROCESSED_OK;

		do {
			// if (result == PlugIn.INPUT_BUFFER_NOT_CONSUMED)
			// {
			// try
			// {
			// Thread.sleep(20);
			// } catch (InterruptedException e)
			// {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// }
			result = n
					.process(input, sourceTrackNumber, destTrackNumber, flags);
			// TODO: sleep on retry??

			if (result != PlugIn.BUFFER_PROCESSED_OK
					&& result != PlugIn.INPUT_BUFFER_NOT_CONSUMED) // TODO
				return; // TODO: error code return?

			for (int i = 0; i < n.getNumDestLinks(); ++i) {
				if (n instanceof DemuxNode && sourceTrackNumber >= 0
						&& i != sourceTrackNumber) {
					continue;
				}

				final FilterGraphLink linkDest = n.getDestLink(i);
				if (linkDest != null) {
					if (n.getOutputBuffer(i) == null)
						throw new NullPointerException("Buffer " + i
								+ " is null, trackNumber=" + sourceTrackNumber
								+ ", flags=" + flags);
					final Buffer b = (Buffer) n.getOutputBuffer(i);
					// TODO: what is the proper place to check for and/or
					// propagate EOM/discard?
					// if (b.isEOM())
					// continue;
					if (b.isDiscard()) {
						// try
						// {
						// Thread.sleep(20);
						// } catch (InterruptedException e)
						// {
						// // TODO Auto-generated catch block
						// e.printStackTrace();
						// }
						continue;
					}
					// linkDest.getDestNode().process(b, -1,
					// linkDest.getDestTrack(), flags);
					process(linkDest.getDestNode(), b, -1, linkDest
							.getDestTrack(), flags);
				}
			}
		} while (result == PlugIn.INPUT_BUFFER_NOT_CONSUMED);

	}

	// does not set renderer's input format or open
	private static Renderer findRenderer(final Format f) {
		final Vector renderers = PlugInManager.getPlugInList(f, null,
				PlugInManager.RENDERER);

		if (renderers.size() == 0)
			if (TRACE)
				logger.fine("No renderers found for: " + f);

		for (int k = 0; k < renderers.size(); ++k) {
			final String rendererClassName = (String) renderers.get(k);
			if (TRACE)
				logger.fine("Found renderer for " + f + ": "
						+ rendererClassName);
		}

		for (int k = 0; k < renderers.size(); ++k) {
			final String rendererClassName = (String) renderers.get(k);
			if (TRACE)
				logger.fine("Trying renderer for " + f + ": "
						+ rendererClassName);

			// TODO: instantiate renderer, take first successful one for each
			// track.
			// Or, how do we pick the "best" one? // how do we pick the "best"
			// format?

			final Renderer renderer = (Renderer) instantiate(rendererClassName);
			if (renderer == null)
				continue;

			return renderer;

		}

		return null;
	}

	// does not set mux's input format or open
	public static Multiplexer findMux(/* final Format f, */final Format destFormat) {
		// TODO: normally we would pass in f as the first parameter. But all the
		// mux's are registered with
		// an empty input format array. Not sure whether getPlugInList should
		// deal with this.
		final Vector muxs = PlugInManager.getPlugInList(null, destFormat,
				PlugInManager.MULTIPLEXER);

		if (muxs.size() == 0)
			if (TRACE)
				logger.fine("No muxs found for: " + destFormat);

		for (int k = 0; k < muxs.size(); ++k) {
			final String muxClassName = (String) muxs.get(k);
			if (TRACE)
				logger
						.fine("Found mux for " + destFormat + ": "
								+ muxClassName);
		}

		for (int k = 0; k < muxs.size(); ++k) {
			final String muxClassName = (String) muxs.get(k);
			if (TRACE)
				logger.fine("Trying mux for " + destFormat + ": "
						+ muxClassName);

			// TODO: instantiate mux, take first successful one for each track.
			// Or, how do we pick the "best" one? // how do we pick the "best"
			// format?

			final Multiplexer mux = (Multiplexer) instantiate(muxClassName);
			if (mux == null)
				continue;

			return mux;
		}

		return null;
	}

	/**
	 * Get all multiplexers
	 */
	public static List/* <Multiplexer> */findMuxs() {
		// TODO: normally we would pass in f as the first parameter. But all the
		// mux's are registered with
		// an empty input format array. Not sure whether getPlugInList should
		// deal with this.
		final Vector muxs = PlugInManager.getPlugInList(null, null,
				PlugInManager.MULTIPLEXER);

		if (muxs.size() == 0)
			if (TRACE)
				logger.fine("No muxs found");

		for (int k = 0; k < muxs.size(); ++k) {
			final String muxClassName = (String) muxs.get(k);
			if (TRACE)
				logger.fine("Found mux: " + muxClassName);
		}

		final List result = new ArrayList();

		for (int k = 0; k < muxs.size(); ++k) {
			final String muxClassName = (String) muxs.get(k);
			if (TRACE)
				logger.fine("Trying mux: " + muxClassName);

			final Multiplexer mux = (Multiplexer) instantiate(muxClassName);
			if (mux == null)
				continue;

			result.add(mux);
		}

		return result;
	}

	private static Format negotiate(Format f, Multiplexer dest,
			Format muxInputFormat, int muxDestTrack, PlugIn src) {
		// format must equal mux input format exactly to match. No negotiation
		// possible since
		// input format of mux is already fixed.
		// TODO: what if mux input format is incompletely specified? should we
		// use a match?
		if (muxInputFormat.matches(f)) { // System.out.println("" + src +
											// " will use " + muxInputFormat);
			return muxInputFormat;
		} else
			return null; // will need codec(s) between demux and mux.

	}

	private static Format negotiate(Format f, Renderer dest, PlugIn src) {
		int iterations = 0;
		while (true) {
			if (f == null)
				return null;
			if (iterations >= 1000) { // just a sanity check, not sure it can or
										// will ever happen.
				logger
						.warning("negotiate iterated 1000 times, probably stuck in infinite loop - abandoning format negotiation");
				logger.warning("src=" + src);
				logger.warning("dest=" + dest);
				logger.warning("f=" + f);
				return null;
			}
			++iterations;

			final Format f2;

			f2 = dest.setInputFormat(f);
			if (f2 == null) {
				logger.warning("Input format rejected by " + dest + ": " + f); // this
																				// shouldn't
																				// happen,
																				// since
																				// we
																				// chose
																				// which
																				// nodes
																				// to
																				// try
																				// based
																				// on
																				// the
																				// input
																				// formats
																				// they
																				// offered.
				return null;
			}
			final Format f3;
			if (src instanceof Codec) {
				f3 = ((Codec) src).setOutputFormat(f2);
			} else {
				return f2; // no other plug ins can have their output formats
							// set? TODO
			}

			if (f2.equals(f3)) {
				// dest.setInputFormat(f3); // TODO: is this necessary?
				return f3;
			}

			f = f3;

		}

	}

	private static String indent(int depth) {
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < depth; ++i)
			b.append(' ');
		return b.toString();
	}

	/**
	 * First tries a best-first search. Then does Breadth-first search by doing
	 * depth-first searches of different depths. Generally, we want the
	 * best-first search to succeed, because it is fast. Breadth-first can be
	 * unacceptably slow.
	 * 
	 * Standard Best-first can find non-shortest graphs, for example:
	 * 
	 * net.sf.fmj.media.parser.JavaSoundParser [Track 0 of] AudioFormat [LINEAR,
	 * 22050.0 Hz, 8-bit, Mono, Unsigned, 22050.0 frame rate, FrameSize=8 bits]
	 * [Track 0 of] net.sf.fmj.media.codec.audio.RateConverter [Track 0 of]
	 * AudioFormat [LINEAR, 8000.0 Hz, 8-bit, Mono, Signed, 8000.0 frame rate,
	 * FrameSize=8 bits] [Track 0 of] net.sf.fmj.media.codec.JavaSoundCodec
	 * [Track 0 of] AudioFormat [LINEAR, 8000.0 Hz, 8-bit, Mono, Unsigned,
	 * 8000.0 frame rate, FrameSize=8 bits] [Track 0 of]
	 * net.sf.fmj.media.codec.audio.RateConverter [Track 0 of] AudioFormat
	 * [LINEAR, 8000.0 Hz, 16-bit, Mono, BigEndian, Signed, 8000.0 frame rate,
	 * FrameSize=16 bits] [Track 0 of] net.sf.fmj.media.codec.audio.ulaw.Encoder
	 * [Track 0 of] AudioFormat [ULAW, 8000.0 Hz, 8-bit, Mono, Signed, 8000.0
	 * frame rate, FrameSize=8 bits] [Track 0 of]
	 * com.sun.media.codec.audio.ulaw.Packetizer [Track 0 of] AudioFormat
	 * [ULAW/rtp, 8000.0 Hz, 8-bit, Mono] this is because there is no search
	 * from the destination format backwards.
	 * 
	 * TODO: this can be unacceptably slow, need to find other ways to improve.
	 */
	private static FilterGraphNode findCodecPathTo(int destPlugInType,
			final Format f, final PlugIn from, final Multiplexer mux,
			final Format muxInputFormat, final int muxDestTrack, final int depth) {
		// first, increasing depths of breadth-first search, up to a shorter
		// depth.
		for (int cutoffDepth = 0; cutoffDepth < TYPICAL_GRAPH_DEPTH; ++cutoffDepth) { // System.out.println("Trying depth "
																						// +
																						// cutoffDepth);
			FilterGraphNode n = findCodecPathTo(destPlugInType, f, from,
					new HashSet(), mux, muxInputFormat, muxDestTrack, depth,
					cutoffDepth, -1);
			if (n != null)
				return n;
		}

		if (BEST_FIRST_SEARCH_DEPTH > 0) {
			// now, increasing depths of best-first search.
			// the reason for not doing a straight best-first search is that
			// suboptimal graphs like the above (in comments) can be found.
			for (int cutoffDepth = 0; cutoffDepth < BEST_FIRST_SEARCH_DEPTH; ++cutoffDepth) {
				// first, do best-first search
				FilterGraphNode n = findCodecPathTo(destPlugInType, f, from,
						new HashSet(), mux, muxInputFormat, muxDestTrack,
						depth, cutoffDepth, BEST_FIRST_SEARCH_BREADTH);
				if (n != null) {
					logger.fine("Graph found with best-first search.");
					return n;
				}
			}
			logger
					.fine("Graph not found with best-first search, trying incrementally deeper breadth-first searches");
		}

		// now, increasing depths of breadth-first search.
		for (int cutoffDepth = TYPICAL_GRAPH_DEPTH; cutoffDepth < MAX_GRAPH_DEPTH; ++cutoffDepth) { // System.out.println("Trying depth "
																									// +
																									// cutoffDepth);
			FilterGraphNode n = findCodecPathTo(destPlugInType, f, from,
					new HashSet(), mux, muxInputFormat, muxDestTrack, depth,
					cutoffDepth, -1);
			if (n != null)
				return n;
		}

		logger.warning("Filter graph search depth at " + MAX_GRAPH_DEPTH
				+ ", abandoning deeper search"); // just to prevent infinite
													// search. Not sure this can
													// actually happen, this is
													// a safeguard.
		return null;
	}

	private static final boolean SKIP_NON_FORMAT_CHANGING_CODECS = true;

	// TODO: format between one node's output and another node's input must be
	// negotiated!
	// A->B: f = a.getOutputFormat(), f = b.setInputFormat(f), if f differs, f =
	// f.setOutputFormat(f), etc!
	// excludeFormatAtDepths is used to incrementally add plug ins which are a
	// dead end - no path to renderer, to avoid retracing our steps.
	// maxBestCodecs is used for best-first search. Only applies when the
	// muxInputFormat is known. If not -1, only the top x codecs will be tried.
	// this limits the breadth of the search tree and allows us to go deeper.
	private static FilterGraphNode findCodecPathTo(int destPlugInType,
			final Format f, final PlugIn from, final Set excludeFormatAtDepths,
			final Multiplexer mux, final Format muxInputFormat,
			final int muxDestTrack, final int depth, final int cutoffDepth,
			final int maxBestCodecs) {
		if (f == null)
			throw new NullPointerException();

		if (depth >= cutoffDepth) // for our search, we have reached the depth
									// limit.
		{
			return null;
		}

		if (excludeFormatAtDepths.contains(new FormatAtDepth(f, depth))) // already
																			// tried
																			// this
																			// format
																			// at
																			// this
																			// depth,
																			// no
																			// luck.
			return null;

		if (destPlugInType == PlugInManager.RENDERER) {
			final Renderer renderer = findRenderer(f);
			if (renderer != null) {
				final Format fAccepted = negotiate(f, renderer, from);
				if (fAccepted != null) {
					BufferControl bufferControl = (BufferControl) renderer
							.getControl("javax.media.control.BufferControl");
					if (bufferControl != null) {
						bufferControl.setBufferLength(2000); // TODO: hard-coded
																// for testing
					}

					return new RendererNode(renderer, fAccepted);
				}
			}
		} else if (destPlugInType == PlugInManager.MULTIPLEXER) {
			final Format fAccepted = negotiate(f, mux, muxInputFormat,
					muxDestTrack, from);
			if (fAccepted != null) {
				return new MuxNode(mux, fAccepted);
			}

		} else
			throw new IllegalArgumentException();

		// if we call again recursively, we will hit the depth limit, so just
		// stop here. We've already checked
		// whether we can make a terminal node with a renderer/mux. If we get
		// here, it would have to add
		// a codec below to the chain, before a renderer/mux, which would be 2
		// nodes.
		if (depth >= cutoffDepth - 1) // for our search, we have reached the
										// depth limit.
		{
			return null;
		}

		final Vector codecs = PlugInManager.getPlugInList(f, null,
				PlugInManager.CODEC);

		for (int j = 0; j < codecs.size(); ++j) {
			final String codecClassName = (String) codecs.get(j);
			if (TRACE)
				logger.finest(indent(depth) + "Found codec for " + f + ": "
						+ codecClassName);
		}

		// first, put all the possible codecs and output formats as pairs in a
		// list of CodecFormatPair, which we
		// will then sort so that we can try the best ones first:
		final List codecFormatPairs = new ArrayList(); // of CodecFormatPair

		for (int j = 0; j < codecs.size(); ++j) {
			final String codecClassName = (String) codecs.get(j);

			if (TRACE)
				logger.finer(indent(depth) + "Trying " + codecClassName);

			final Codec codec = (Codec) instantiate(codecClassName);
			if (codec == null)
				continue;

			// TODO: should we call setInputFormat here, or below? (as we are
			// doing now)
			// final Format fAccepted = codec.setInputFormat(f);
			//	    	
			// if (fAccepted == null)
			// { logger.warning("Codec " + codec + " rejected input format " +
			// f);
			// continue;
			// }

			final Format[] codecOutputFormats = codec
					.getSupportedOutputFormats(f);

			for (int codecOutputFormatIndex = 0; codecOutputFormatIndex < codecOutputFormats.length; ++codecOutputFormatIndex) {
				final Format codecOutputFormat = codecOutputFormats[codecOutputFormatIndex];
				if (TRACE)
					logger.finest(indent(depth) + "Found Codec output format: "
							+ codecOutputFormat);
			}
			for (int codecOutputFormatIndex = 0; codecOutputFormatIndex < codecOutputFormats.length; ++codecOutputFormatIndex) {
				final Format codecOutputFormat = codecOutputFormats[codecOutputFormatIndex];
				if (codecOutputFormat == null) {
					logger.finer(indent(depth) + "Skipping null Codec ("
							+ codec.getClass()
							+ ") output format, input format: " + f);
					continue;
				}
				if (codecOutputFormat.equals(f)) {
					if (TRACE)
						logger
								.finest(indent(depth)
										+ (SKIP_NON_FORMAT_CHANGING_CODECS ? "YES "
												: "NOT ")
										+ "Skipping Codec output format, same as input format: "
										+ codecOutputFormat);
					if (SKIP_NON_FORMAT_CHANGING_CODECS)
						continue; // no need to have a codec that does not
									// change formats. this can happen in the
									// case of
					// something like com.sun.media.codec.audio.rc.RateCvrt,
					// which will offer an output format
					// the same as an input format.
				}

				// instantiate a new copy of the codec for each pair.
				codecFormatPairs
						.add(new CodecFormatPair(
								(Codec) instantiate(codecClassName),
								codecOutputFormat));
			}

		}

		// now that we have all of the codec/format pairs, sort them (if we know
		// what format we are trying to reach)
		if (muxInputFormat != null) {
			Collections.sort(codecFormatPairs,
					new CodecFormatPairProximityComparator(muxInputFormat));

			// for (int j = 0; j < codecFormatPairs.size(); ++j)
			// {
			// final CodecFormatPair codecFormatPair = (CodecFormatPair)
			// codecFormatPairs.get(j);
			// final Codec codec =codecFormatPair.getCodec();
			// final Format codecOutputFormat = codecFormatPair.getFormat();
			//		    	
			// if (TRACE) logger.fine(indent(depth) + j + ". Will try " +
			// codec.getClass().getName() + " with output format: " +
			// codecOutputFormat);
			//	
			// }

			// best-first enabled.
			if (maxBestCodecs > 1) {
				while (codecFormatPairs.size() > maxBestCodecs)
					codecFormatPairs.remove(maxBestCodecs);
			}

			for (int j = 0; j < codecFormatPairs.size(); ++j) {
				final CodecFormatPair codecFormatPair = (CodecFormatPair) codecFormatPairs
						.get(j);
				final Codec codec = codecFormatPair.getCodec();
				final Format codecOutputFormat = codecFormatPair.getFormat();

				if (TRACE)
					logger.finer(indent(depth) + j + ". Will try "
							+ codec.getClass().getName()
							+ " with output format: " + codecOutputFormat);

			}
		}
		for (int j = 0; j < codecFormatPairs.size(); ++j) {
			final CodecFormatPair codecFormatPair = (CodecFormatPair) codecFormatPairs
					.get(j);
			final Codec codec = codecFormatPair.getCodec();
			final Format codecOutputFormat = codecFormatPair.getFormat();

			if (TRACE)
				logger.finer(indent(depth) + "Trying "
						+ codec.getClass().getName() + " with output format: "
						+ codecOutputFormat);
			{

				final Format fAccepted = codec.setInputFormat(f);
				if (fAccepted == null) {
					logger.warning("Codec " + codec + " rejected input format "
							+ f);
					continue;
				}

				// TODO: the output format may be partially unspecified, so it
				// has to be negotiated.
				// also, if we are connecting to something with a more specific
				// format (say, a mux), then
				// we need to be able to constrain this output format using
				// that.
				final Format codecOutputFormatAccepted = codec
						.setOutputFormat(codecOutputFormat); // TODO: this
																// should be set
																// only in
																// negotiation
				if (codecOutputFormatAccepted == null) {
					logger.warning("Codec " + codec
							+ " rejected output format " + codecOutputFormat);
					continue;
				}

				if (false && codecOutputFormatAccepted.equals(f)) {
					if (TRACE)
						logger
								.finer(indent(depth)
										+ (SKIP_NON_FORMAT_CHANGING_CODECS ? ""
												: "NOT ")
										+ "Skipping accepted Codec output format, same as input format: "
										+ codecOutputFormatAccepted);
					if (SKIP_NON_FORMAT_CHANGING_CODECS)
						continue; // no need to have a codec that does not
									// change formats. this can happen in the
									// case of
					// something like com.sun.media.codec.audio.rc.RateCvrt,
					// which will offer an output format
					// the same as an input format.
				}

				if (TRACE)
					logger.finer(indent(depth) + "ACCEPT "
							+ codecOutputFormatAccepted);

				// for any depth between this one and the cutoff depth, we no
				// longer need to search.
				for (int i = depth; i <= cutoffDepth; ++i) {
					excludeFormatAtDepths.add(new FormatAtDepth(f, i));
				}
				final FilterGraphNode tail = findCodecPathTo(destPlugInType,
						codecOutputFormatAccepted, codec,
						excludeFormatAtDepths, mux, muxInputFormat,
						muxDestTrack, depth + 1, cutoffDepth, maxBestCodecs);
				if (tail != null) {
					final CodecNode codecNode = new CodecNode(codec, fAccepted);
					codecNode.addDestLink(new FilterGraphLink(tail,
							muxDestTrack));
					return codecNode;
				}

			}
		}

		return null;

	}

	/**
	 * A combination of Format and depth, used to keep track of where we have
	 * already searched.
	 */
	private static class FormatAtDepth {
		private final Format f;
		private final int depth;

		public FormatAtDepth(final Format f, final int depth) {
			super();
			this.f = f;
			this.depth = depth;
		}

		public boolean equals(Object obj) {
			if (!(obj instanceof FormatAtDepth))
				return false;

			FormatAtDepth oCast = (FormatAtDepth) obj;
			return oCast.depth == this.depth && ObjUtils.equal(oCast.f, this.f);

		}

		public int hashCode() {
			// TODO Auto-generated method stub
			int result = depth;
			if (f != null)
				result += f.toString().hashCode(); // Format.hashCode is not
													// implemented in JMF, so
													// let's not call it.
			return result;
		}

	}

	// does not check any deeper than demux node.
	public static Demultiplexer getSourceCompatibleDemultiplexer(
			DataSource source) {
		if (TRACE)
			logger.fine("Content type: " + source.getContentType());

		// loop through demuxs for content type:
		final ContentDescriptor contentDescriptor = new ContentDescriptor(
				source.getContentType());
		final Vector demuxs = PlugInManager.getPlugInList(contentDescriptor,
				null, PlugInManager.DEMULTIPLEXER);
		if (TRACE)
			logger.fine("Num demux: " + demuxs.size());
		for (int i = 0; i < demuxs.size(); ++i) {

			final String demuxClassName = (String) demuxs.get(i);
			if (TRACE)
				logger.fine("Demux class name found: " + demuxClassName);

			final Demultiplexer demux = (Demultiplexer) instantiate(demuxClassName);
			if (demux == null)
				continue;

			try {
				demux.setSource(source);
			} catch (IncompatibleSourceException e) {
				logger.warning("Skipping demux " + demuxClassName + ": "
						+ e.getMessage());
				continue;
			} catch (IOException e) {
				logger.warning("Skipping demux " + demuxClassName + ": "
						+ e.getMessage());
				continue;
			}

			return demux;

		}

		return null;
	}

	// public static DemuxNode buildGraph(DataSource source)
	// {
	// final Demultiplexer demux = getSourceCompatibleDemultiplexer(source);
	// if (demux == null)
	// return null;
	// return buildGraph(new ContentDescriptor(source.getContentType()), demux);
	//		
	// }

	public static DemuxNode buildGraphToRenderer(
			ContentDescriptor contentDescriptor, Demultiplexer demux) {
		return buildGraphTo(PlugInManager.RENDERER, contentDescriptor, demux,
				null, null, -1);
	}

	public static DemuxNode buildGraphToMux(
			ContentDescriptor contentDescriptor, Demultiplexer demux,
			final Multiplexer mux, final Format muxInputFormat,
			final int muxDestTrack) {
		return buildGraphTo(PlugInManager.MULTIPLEXER, contentDescriptor,
				demux, mux, muxInputFormat, muxDestTrack);
	}

	// TODO: throw exception instead of returning null;
	/** demux should be opened and started before calling */
	private static DemuxNode buildGraphTo(int pluginType,
			ContentDescriptor contentDescriptor, Demultiplexer demux,
			final Multiplexer mux, final Format muxInputFormat,
			final int muxDestTrack) {

		final Track[] tracks;
		try {
			tracks = demux.getTracks();
		} catch (BadHeaderException e) {
			logger.log(Level.WARNING, "" + e, e);
			return null;
		} catch (IOException e) {
			logger.log(Level.WARNING, "" + e, e);
			return null;
		}

		if (tracks == null) {
			logger.warning("demux " + demux + ": " + "no tracks");
			return null;
		}

		final DemuxNode demuxNode = new DemuxNode(contentDescriptor, demux,
				tracks);

		if (TRACE)
			logger.fine("Number of tracks: " + demuxNode.getTracks().length);

		int tracksComplete = 0;
		for (int trackIndex = 0; trackIndex < demuxNode.getTracks().length; ++trackIndex) {
			final Track t = demuxNode.getTracks()[trackIndex];

			if (TRACE)
				logger.fine("Track format: " + t.getFormat());
			// if (!(t.getFormat() instanceof VideoFormat))
			// { demuxNode.addDest(null);
			// if (TRACE) logger.fine("Skipping non-video track");
			// continue; // only video for now.
			// }

			// now we have to find a path to the renderer.
			final FilterGraphNode n = findCodecPathTo(pluginType,
					t.getFormat(), demux, mux, muxInputFormat, muxDestTrack, 0); // TODO:
																					// specify
																					// which
																					// track
																					// it
																					// is
																					// from?
			if (n == null) {
				demuxNode.addDestLink(null);
				continue;
			}

			demuxNode.addDestLink(new FilterGraphLink(n, muxDestTrack));
			++tracksComplete;

		}

		if (tracksComplete > 0) {

			return demuxNode; // TODO: we really only want to return true if ALL
								// tracks are complete.
		}

		return null;
	}

	private static Object instantiate(String className) {
		final Class clazz;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			logger.warning("Unable to instantiate " + className + ": "
					+ e.getMessage());
			return null;
		}
		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
			logger.warning("Unable to instantiate " + className + ": "
					+ e.getMessage());
			return null;
		} catch (IllegalAccessException e) {
			logger.warning("Unable to instantiate " + className + ": "
					+ e.getMessage());
			return null;
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "Unable to instantiate " + className
					+ ": " + e.getMessage(), e);
			return null;
		}

	}

}

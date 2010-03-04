/**
 * 
 */
package net.sf.fmj.filtergraph;

import java.util.logging.Level;

import javax.media.Buffer;
import javax.media.Clock;
import javax.media.Format;
import javax.media.Prefetchable;
import javax.media.Renderer;
import javax.media.ResourceUnavailableException;
import javax.media.Time;

import net.sf.fmj.media.SleepHelper;

/**
 * A node in a filter graph for a Renderer. dest is empty.
 * 
 * @author Ken Larson
 * 
 */
public class RendererNode extends FilterGraphNode {
	private final Renderer renderer;
	private final Format inputFormat;
	private boolean prefetched; // Only for Renderers implementing Prefetchable.
	private final SleepHelper sleepHelper = new SleepHelper();

	public RendererNode(Renderer renderer, Format inputFormat) {
		super(renderer);
		this.renderer = renderer;
		this.inputFormat = inputFormat;
	}

	public FilterGraphNode duplicate() {
		return propagateDuplicate(new RendererNode(getRenderer(),
				getInputFormat()));

	}

	public Renderer getRenderer() {
		return renderer;
	}

	public Format getInputFormat() {
		return inputFormat;
	}

	public void setPrefetched(boolean prefetched) {
		this.prefetched = prefetched;
	}

	public boolean isPrefetched() {
		return prefetched;
	}

	public void open() throws ResourceUnavailableException {
		getRenderer().open();
		sleepHelper.reset();
	}

	@Override
	public void stop() {
		getRenderer().stop();
		sleepHelper.reset();

	}

	@Override
	public void start() {
		sleepHelper.reset();
		getRenderer().start();

	}

	public int process(final Buffer input, final int sourceTrackNumber,
			final int destTrackNumber, final int flags) {
		final Renderer renderer = getRenderer();
		if (input.getFormat() == null)
			input.setFormat(getInputFormat()); // TODO: is this right?

		if (renderer instanceof Prefetchable) {
			// TODO: where should this be done? In the Handler's prefetch()?
			final Prefetchable rendererAsPrefetchable = (Prefetchable) renderer;
			if (!isPrefetched() && rendererAsPrefetchable.isPrefetched()) {
				if (renderer instanceof Clock) {
					final Clock rendererAsClock = (Clock) renderer;
					// TODO: what do we set the time to be?
					rendererAsClock.syncStart(new Time(rendererAsClock
							.getMediaNanoseconds()));
				}
				setPrefetched(true);
			}
		}

		{
			int processResult;

			// TODO: the caller should take care of this loop.
			do {
				int oldOffset = input.getOffset();

				// calculate and sleep any sleep needed:
				try {
					sleepHelper.sleep(input.getTimeStamp());
				} catch (InterruptedException e) {
					logger.log(Level.WARNING, "" + e, e);
					return Renderer.BUFFER_PROCESSED_FAILED;
				}

				// long start = System.currentTimeMillis();
				processResult = renderer.process(input); // TODO: check return
															// code
				// long stop = System.currentTimeMillis();
				// System.out.println("Renderer process took " + (stop - start)
				// + " ms");

				if (processResult == Renderer.INPUT_BUFFER_NOT_CONSUMED) {
					logger
							.fine("processResult == Renderer.INPUT_BUFFER_NOT_CONSUMED");
					logger.fine("oldOffset = " + oldOffset);
					logger.fine("newoffset = " + input.getOffset());

					break;
				}

				if (processResult != Renderer.BUFFER_PROCESSED_OK) {
					logger.warning("Renderer process result (loop): "
							+ processResult);
				}
			} while (processResult == Renderer.INPUT_BUFFER_NOT_CONSUMED);
			// INPUT_BUFFER_NOT_CONSUMED: The input Buffer chunk was not fully
			// consumed. The plug-in should update the offset + length fields of
			// the Buffer. The plug-in will be called later with the same input
			// Buffer.

			if (processResult != Renderer.BUFFER_PROCESSED_OK) {
				logger.warning("Renderer process result: " + processResult);
				return processResult; // TODO: what do we do with the error? set
										// a flag in the buffer?
			}

		}

		return Renderer.BUFFER_PROCESSED_OK;

	}

	public void addDestLink(FilterGraphLink n) {
		throw new UnsupportedOperationException();
	}

}
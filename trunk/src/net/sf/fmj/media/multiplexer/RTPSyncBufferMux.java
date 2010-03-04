package net.sf.fmj.media.multiplexer;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.Multiplexer;
import javax.media.Renderer;
import javax.media.ResourceUnavailableException;
import javax.media.protocol.ContentDescriptor;

import net.sf.fmj.media.SleepHelper;
import net.sf.fmj.utility.LoggerSingleton;

/**
 * Sun's RTPSyncBufferMux extends RawSynBufferMux. Not sure what any of these
 * really do, for now all we need is a RawBufferMux with the correct content
 * descriptor. TODO: implement the "sync" part of this. This means we sleep to
 * match the timestamps.
 * 
 * @author Ken Larson
 * 
 */
public class RTPSyncBufferMux extends RawBufferMux {
	private static final Logger logger = LoggerSingleton.logger;

	public RTPSyncBufferMux() {
		super(new ContentDescriptor(ContentDescriptor.RAW_RTP));
	}

	public String getName() {
		return "RTP Sync Buffer Multiplexer";
	}

	public Format setInputFormat(Format format, int trackID) {
		// This is what Sun's RTPSyncBufferMux does, but it calls the equivalent
		// method on
		// com.sun.media.rtp.RTPSessionMgr, which seems to be a bit of a hack,
		// since it
		// makes assumptions about which RTPSessionMgr is actually being used.
		// Should
		// be determined by RTPManager.newInstance().
		// if (!net.sf.fmj.media.rtp.RTPSessionMgr.formatSupported(format))
		return null;
		// return super.setInputFormat(format, trackID);
	}

	private final SleepHelper sleepHelper = new SleepHelper();

	@Override
	public void open() throws ResourceUnavailableException {
		super.open();
		sleepHelper.reset();
	}

	@Override
	public int process(Buffer buffer, int trackID) {
		final int result = super.process(buffer, trackID);

		// TODO: this is just a hack for now. Without this, streaming RTP
		// from a file will just send everything as fast as possible, instead of
		// delaying between packets.
		// Really, this needs to be also syncing audio and video together.
		if (result == Multiplexer.BUFFER_PROCESSED_OK) {
			// calculate and sleep any sleep needed:
			try {
				sleepHelper.sleep(buffer.getTimeStamp());
			} catch (InterruptedException e) {
				logger.log(Level.WARNING, "" + e, e);
				return Renderer.BUFFER_PROCESSED_FAILED;
			}

		}
		return result;
	}
}

package net.sf.fmj.utility;

import java.util.logging.Logger;

import javax.media.Codec;
import javax.media.Demultiplexer;
import javax.media.Effect;
import javax.media.Format;
import javax.media.Multiplexer;
import javax.media.PlugInManager;
import javax.media.Renderer;
import javax.media.protocol.ContentDescriptor;

/**
 * Helper class to register plugins with the PlugInManager, but only requiring
 * the class name. The rest is done by reflection/instantiation/querying.
 * 
 * @author Ken Larson
 * 
 */
public class PlugInUtility {
	private static final Logger logger = LoggerSingleton.logger;

	private static final boolean TRACE = false;

	public static boolean registerPlugIn(String className) {

		final Object o;
		try {
			final Class clazz = Class.forName(className);
			o = clazz.newInstance();

			if (o instanceof Demultiplexer) {
				if (TRACE)
					logger.fine("PlugInUtility: Registering demultiplexer: "
							+ className);
				final Demultiplexer oCast = (Demultiplexer) o;
				final ContentDescriptor[] contentDescriptors = oCast
						.getSupportedInputContentDescriptors();
				final Format[] formats = new Format[contentDescriptors.length];
				for (int i = 0; i < contentDescriptors.length; ++i) {
					if (TRACE)
						logger.fine("\t" + contentDescriptors[i]);
					formats[i] = contentDescriptors[i];
				}

				return PlugInManager.addPlugIn(className, formats,
						new Format[] {}, PlugInManager.DEMULTIPLEXER);

			} else if (o instanceof Codec) {
				if (TRACE)
					logger.fine("PlugInUtility: Registering codec: "
							+ className);
				final Codec oCast = (Codec) o;
				final Format[] inputFormats = oCast.getSupportedInputFormats();

				final Format[] outputFormats = oCast
						.getSupportedOutputFormats(null); // this is what
															// JMRegistry does

				return PlugInManager.addPlugIn(className, inputFormats,
						outputFormats,
						(o instanceof Effect) ? PlugInManager.EFFECT
								: PlugInManager.CODEC);

			} else if (o instanceof Renderer) {
				if (TRACE)
					logger.fine("PlugInUtility: Registering renderer: "
							+ className);
				final Renderer oCast = (Renderer) o;
				final Format[] inputFormats = oCast.getSupportedInputFormats();
				return PlugInManager.addPlugIn(className, inputFormats,
						new Format[] {}, PlugInManager.RENDERER);
			} else if (o instanceof Multiplexer) {
				if (TRACE)
					logger.fine("PlugInUtility: Registering Multiplexer: "
							+ className);
				final Multiplexer oCast = (Multiplexer) o;
				// JMF Multiplexers always have nothing for the input formats.
				return PlugInManager.addPlugIn(className, new Format[] {},
						oCast.getSupportedOutputContentDescriptors(null),
						PlugInManager.MULTIPLEXER);
			} else {
				logger
						.warning("PlugInUtility: Unknown or unsupported plug-in: "
								+ o.getClass());
				return false;
			}

		} catch (Throwable e) // catch Throwable instead of Exception because
								// some plug-ins load native libraries, and if
								// the native libraries are missing, we can get
								// exceptions not caught by Exception
		{
			logger.fine("PlugInUtility: Unable to register plugin " + className
					+ ": " + e);
			return false;
		}

	}
}

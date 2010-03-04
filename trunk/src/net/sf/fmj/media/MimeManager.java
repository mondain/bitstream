package net.sf.fmj.media;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.fmj.utility.Registry;
import net.sf.fmj.utility.LoggerSingleton;

/**
 * Intended to function as Sun's version in com.sun.media.
 * 
 * @author Ken Larson
 * 
 */
public class MimeManager // not final so we can extend it to implement one in
							// com.sun.media
{
	private static final Logger logger = LoggerSingleton.logger;

	protected MimeManager() {
		super();
	}

	private static final MimeTable defaultMimeTable = new MimeTable();

	private static void put(String ext, String type) {
		defaultMimeTable.addMimeType(ext, type);

	}

	static {

		put("mvr", "application/mvr");
		put("aif", "audio/x_aiff"); // reordered so it won't be the default in
									// reverse lookup
		put("aiff", "audio/x_aiff");
		put("midi", "audio/midi");
		put("jmx", "application/x_jmx");
		put("mpv", "video/mpeg"); // reordered so it won't be the default in
									// reverse lookup
		put("mpg", "video/mpeg");
		// put("aif", "audio/x_aiff");
		put("wav", "audio/x_wav");
		put("mp3", "audio/mpeg");
		put("mpa", "audio/mpeg"); // reordered so it won't be the default in
									// reverse lookup
		put("mp2", "audio/mpeg");
		// put("mpa", "audio/mpeg");
		put("spl", "application/futuresplash");
		put("viv", "video/vivo");
		put("au", "audio/basic");
		put("g729", "audio/g729");
		put("mov", "video/quicktime");
		put("avi", "video/x_msvideo");
		put("g728", "audio/g728");
		put("cda", "audio/cdaudio");
		put("g729a", "audio/g729a");
		put("gsm", "audio/x_gsm");
		put("mid", "audio/midi");
		// put("mpv", "video/mpeg");
		put("swf", "application/x-shockwave-flash");
		put("rmf", "audio/rmf");

		boolean jmfDefaults = false;

		try {
			jmfDefaults = System.getProperty(
					"net.sf.fmj.utility.JmfRegistry.JMFDefaults", "false")
					.equals("true");
		} catch (SecurityException e) { // we must be an applet.
		}

		// end of JMF-standard types. Now, extensions that are added by FMJ
		if (!jmfDefaults) {
			// see http://wiki.xiph.org/index.php/MIME_Types_and_File_Extensions
			// for ogg extensions.

			put("ogg", "audio/ogg"); // this is somewhat problematic, since .ogg
										// extension is often used for
										// audio-only files, but is also used
										// for video files.
			put("ogx", "application/ogg");
			put("oga", "audio/ogg");
			put("ogv", "video/ogg");

			// include other types from the xiph wiki, regardless of whether we
			// actually support them:
			put("spx", "audio/ogg");
			put("flac", "application/flac");
			put("anx", "application/annodex");
			put("axa", "audio/annodex");
			put("axv", "video/annodex");
			put("xspf", "application/xspf+xml ");

			// microsoft types, see http://support.microsoft.com/kb/288102

			put("asf", "video/x-ms-asf");
			put("asx", "video/x-ms-asf");
			put("wma", "audio/x-ms-wma");
			put("wax", "audio/x-ms-wax");
			put("wmv", "video/x-ms-wmv"); // this is incorrectly specified as
											// audio/... on the above link.
											// Gnome desktop has this as
											// video/x-ms-asf
			put("wvx", "video/x-ms-wvx");
			put("wm", "video/x-ms-wm");
			put("wmx", "video/x-ms-wmx");
			put("wmz", "application/x-ms-wmz");
			put("wmd", "application/x-ms-wmd");

			// mpeg4:
			put("mpeg4", "video/mpeg"); // TODO: video/mpeg4?
			put("mp4", "video/mpeg"); // TODO: video/mpeg4?
			put("3gp", "video/3gpp");
			put("3g2", "video/3gpp");

			// mpeg2ps:
			put("m2v", "video/mp2p");

			// flash:
			put("flv", "video/x-flv");
		}

	}

	public static final boolean addMimeType(String fileExtension,
			String mimeType) {
		if (defaultMimeTable.getMimeType(fileExtension) != null) {
			logger.warning("Cannot override default mime-table entries");
			return false;
		}

		Registry.getInstance().addMimeType(fileExtension, mimeType);
		return true;
	}

	public static final boolean removeMimeType(String fileExtension) {
		return Registry.getInstance().removeMimeType(fileExtension);

	}

	public static final String getMimeType(String fileExtension) {
		String result = (String) Registry.getInstance().getMimeType(
				fileExtension);
		if (result != null)
			return result;
		result = (String) defaultMimeTable.getMimeType(fileExtension);
		return result;
	}

	public static final Hashtable<String, String> getMimeTable() {
		final Hashtable<String, String> result = new Hashtable<String, String>();
		result.putAll(defaultMimeTable.getMimeTable());
		result.putAll(Registry.getInstance().getMimeTable());
		return result;
	}

	public static final Hashtable getDefaultMimeTable() {
		return defaultMimeTable.getMimeTable();
	}

	public static final String getDefaultExtension(String mimeType) {
		final String result = Registry.getInstance().getDefaultExtension(
				mimeType);
		if (result != null)
			return result;
		return defaultMimeTable.getDefaultExtension(mimeType);

	}

	public static final List<String> getExtensions(String mimeType) {
		final List<String> result = new ArrayList<String>();
		result.addAll(defaultMimeTable.getExtensions(mimeType));
		result.addAll(Registry.getInstance().getExtensions(mimeType));
		return result;

	}

	public static void commit() {
		try {
			Registry.getInstance().commit();
		} catch (IOException e) {
			logger.log(Level.WARNING, "" + e, e);
		}
	}

}

package net.sf.fmj.media;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.media.Format;
import javax.media.PackageManager;
import javax.media.format.AudioFormat;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.format.YUVFormat;
import javax.media.pim.PlugInManager;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.FileTypeDescriptor;

import net.sf.fmj.utility.OSUtils;
import net.sf.fmj.utility.PlugInUtility;

/**
 * Defaults for the FMJ registry. Broken out into fmj, jmf, and third-party. fmj
 * ones are fmj-specific. jmf ones are to duplicate what is in jmf (useful if
 * jmf is in the classpath). third-party ones are those that are not included
 * with fmj but might be in the classpath (like fobs4jmf). The flags give us the
 * flexibility to make the registry the same as JMF's or JMF's + FMJ's, or just
 * FMJ's. Making it the same as JMF's is useful for unit testing.
 * 
 * @author Ken Larson
 * 
 */
public class RegistryDefaults {

	public static final int JMF = 0x0001;
	public static final int FMJ = 0x0002;
	public static final int THIRD_PARTY = 0x0004;
	public static final int ALL = JMF | FMJ | THIRD_PARTY;

	public static void registerAll(int flags) {
		registerProtocolPrefixList(flags);
		registerContentPrefixList(flags);
		registerPlugins(flags);
	}

	public static void unRegisterAll(int flags) {
		unRegisterProtocolPrefixList(flags);
		unRegisterContentPrefixList(flags);
		unRegisterPlugins(flags);
	}

	public static void registerProtocolPrefixList(int flags) {
		final Vector v = PackageManager.getProtocolPrefixList();
		final List<String> add = protocolPrefixList(flags);
		for (String s : add) {
			if (!v.contains(s))
				v.add(s);
		}

		PackageManager.setProtocolPrefixList(v);

	}

	public static void registerContentPrefixList(int flags) {
		final Vector v = PackageManager.getContentPrefixList();
		final List<String> add = contentPrefixList(flags);
		for (String s : add) {
			if (!v.contains(s))
				v.add(s);
		}

		PackageManager.setContentPrefixList(v);
	}

	public static void unRegisterProtocolPrefixList(int flags) {
		final Vector v = PackageManager.getProtocolPrefixList();
		final List<String> add = protocolPrefixList(flags);
		for (String s : add) {
			if (v.contains(s))
				v.remove(s);
		}

		PackageManager.setProtocolPrefixList(v);

	}

	public static void unRegisterContentPrefixList(int flags) {
		final Vector v = PackageManager.getContentPrefixList();
		final List<String> add = contentPrefixList(flags);
		for (String s : add) {
			if (v.contains(s))
				v.remove(s);
		}

		PackageManager.setContentPrefixList(v);
	}

	public static List<String> protocolPrefixList(int flags) {
		final List<String> protocolPrefixList = new ArrayList<String>();

		if ((flags & JMF) != 0) {
			protocolPrefixList.add("javax");
			protocolPrefixList.add("com.sun");
			protocolPrefixList.add("com.ibm");
		}
		if ((flags & FMJ) != 0) {
			if (OSUtils.isMacOSX()) {
				// Quicktime:
				protocolPrefixList.add("net.sf.fmj.qt");
			}
			if (OSUtils.isWindows()) {
				// DirectShow:
				protocolPrefixList.add("net.sf.fmj.ds");
			}
			if (OSUtils.isLinux()) // TODO: we could add these for other OS's,
									// as gstreamer is cross-platform.
			{
				// GStreamer:
				protocolPrefixList.add("net.sf.fmj.gst");
			}

			protocolPrefixList.add("net.sf.fmj");
		}
		if ((flags & THIRD_PARTY) != 0) {
			protocolPrefixList.add("com.omnividea"); // FOBS4JMF: may not be in
														// classpath
		}

		return protocolPrefixList;
	}

	public static List<String> contentPrefixList(int flags) {
		final List<String> contentPrefixList = new ArrayList<String>();

		if ((flags & JMF) != 0) {
			contentPrefixList.add("javax");
			contentPrefixList.add("com.sun");
			contentPrefixList.add("com.ibm");
		}
		if ((flags & FMJ) != 0) {
			if (OSUtils.isMacOSX()) {
				// Quicktime:
				contentPrefixList.add("net.sf.fmj.qt");
			}
			if (OSUtils.isWindows()) {
				// DirectShow:
				contentPrefixList.add("net.sf.fmj.ds");
				contentPrefixList.add("net.sf.fmj.gst");
				
			}
			if (OSUtils.isLinux()) // TODO: we could add these for other OS's,
									// as gstreamer is cross-platform.
			{
				// DirectShow:
				contentPrefixList.add("net.sf.fmj.gst");
			}

			contentPrefixList.add("net.sf.fmj");

		}
		if ((flags & THIRD_PARTY) != 0) {
			// none to add
		}
		return contentPrefixList;
	}

	public static void registerPlugins(int flags) {
		// TODO: if JMF is in the classpath ahead of FMJ, we get:
		// Problem adding net.sf.fmj.media.codec.video.jpeg.Packetizer to plugin
		// table.
		// Already hash value of 8706141154469562557 in plugin table for class
		// name of com.sun.media.codec.video.jpeg.Packetizer
		// Problem adding net.sf.fmj.media.codec.video.jpeg.DePacketizer to
		// plugin table.
		// Already hash value of 3049617401990556986 in plugin table for class
		// name of com.sun.media.codec.video.jpeg.DePacketizer
		// Problem adding net.sf.fmj.media.renderer.audio.JavaSoundRenderer to
		// plugin table.
		// Already hash value of 1262232571547748861 in plugin table for class
		// name of com.sun.media.renderer.audio.JavaSoundRenderer
		// Problem adding net.sf.fmj.media.multiplexer.RTPSyncBufferMux to
		// plugin table.
		// Already hash value of -2095741743343195187 in plugin table for class
		// name of com.sun.media.multiplexer.RTPSyncBufferMux

		// PlugInManager.DEMULTIPLEXER:
		if ((flags & JMF) != 0) {
			PlugInManager.addPlugIn("com.ibm.media.parser.video.MpegParser",
					new Format[] { new ContentDescriptor("audio.mpeg"),
							new ContentDescriptor("video.mpeg"),
							new ContentDescriptor("audio.mpeg"), },
					new Format[] {}, PlugInManager.DEMULTIPLEXER);
			PlugInManager.addPlugIn("com.sun.media.parser.audio.WavParser",
					new Format[] { new ContentDescriptor("audio.x_wav"), },
					new Format[] {}, PlugInManager.DEMULTIPLEXER);
			PlugInManager.addPlugIn("com.sun.media.parser.audio.AuParser",
					new Format[] { new ContentDescriptor("audio.basic"), },
					new Format[] {}, PlugInManager.DEMULTIPLEXER);
			PlugInManager.addPlugIn("com.sun.media.parser.audio.AiffParser",
					new Format[] { new ContentDescriptor("audio.x_aiff"), },
					new Format[] {}, PlugInManager.DEMULTIPLEXER);
			PlugInManager.addPlugIn("com.sun.media.parser.audio.GsmParser",
					new Format[] { new ContentDescriptor("audio.x_gsm"), },
					new Format[] {}, PlugInManager.DEMULTIPLEXER);
			
		}
		// FMJ overrides to come before SUN ones:
		// this one needs to be after the audio parsers, otherwise it will be
		// used instead of them in some cases.
		// TODO: why does the sun one get an NPE?
		// TODO: this causes audio not to play properly:
		if ((flags & FMJ) != 0) {
			PlugInManager.addPlugIn(
					"net.sf.fmj.media.parser.RawPushBufferParser",
					new Format[] { new ContentDescriptor("raw"), },
					new Format[] {}, PlugInManager.DEMULTIPLEXER);
			// end FMJ override.
		}
		if ((flags & JMF) != 0) {
			PlugInManager.addPlugIn("com.sun.media.parser.RawStreamParser",
					new Format[] { new ContentDescriptor("raw"), },
					new Format[] {}, PlugInManager.DEMULTIPLEXER);
			PlugInManager.addPlugIn("com.sun.media.parser.RawBufferParser",
					new Format[] { new ContentDescriptor("raw"), },
					new Format[] {}, PlugInManager.DEMULTIPLEXER);
			PlugInManager.addPlugIn("com.sun.media.parser.RawPullStreamParser",
					new Format[] { new ContentDescriptor("raw"), },
					new Format[] {}, PlugInManager.DEMULTIPLEXER);
			PlugInManager.addPlugIn("com.sun.media.parser.RawPullBufferParser",
					new Format[] { new ContentDescriptor("raw"), },
					new Format[] {}, PlugInManager.DEMULTIPLEXER);
			PlugInManager.addPlugIn(
					"com.sun.media.parser.video.QuicktimeParser",
					new Format[] { new ContentDescriptor("video.quicktime"), },
					new Format[] {}, PlugInManager.DEMULTIPLEXER);
			PlugInManager.addPlugIn("com.sun.media.parser.video.AviParser",
					new Format[] { new ContentDescriptor("video.x_msvideo"), },
					new Format[] {}, PlugInManager.DEMULTIPLEXER);
			PlugInManager.addPlugIn("com.omnividea.media.parser.video.Parser",
					new Format[] { new ContentDescriptor("video.ffmpeg"), },
					new Format[] {}, PlugInManager.DEMULTIPLEXER);
			
			// PlugInManager.CODEC:
			PlugInManager.addPlugIn(
					"com.sun.media.codec.audio.mpa.JavaDecoder", new Format[] {
							new AudioFormat("mpegaudio", 16000.0, -1, -1, -1,
									1, -1, -1.0, Format.byteArray),
							new AudioFormat("mpegaudio", 22050.0, -1, -1, -1,
									1, -1, -1.0, Format.byteArray),
							new AudioFormat("mpegaudio", 24000.0, -1, -1, -1,
									1, -1, -1.0, Format.byteArray),
							new AudioFormat("mpegaudio", 32000.0, -1, -1, -1,
									1, -1, -1.0, Format.byteArray),
							new AudioFormat("mpegaudio", 44100.0, -1, -1, -1,
									1, -1, -1.0, Format.byteArray),
							new AudioFormat("mpegaudio", 48000.0, -1, -1, -1,
									1, -1, -1.0, Format.byteArray), },
					new Format[] { new AudioFormat("LINEAR", -1.0, -1, -1, -1,
							-1, -1, -1.0, Format.byteArray), },
					PlugInManager.CODEC);
			PlugInManager
					.addPlugIn("com.sun.media.codec.video.cinepak.JavaDecoder",
							new Format[] { new VideoFormat("cvid", null, -1,
									Format.byteArray, -1.0f), },
							new Format[] { new RGBFormat(null, -1,
									Format.intArray, -1.0f, 32, 0xff, 0xff00,
									0xff0000, 1, -1, 0, -1), },
							PlugInManager.CODEC);
			PlugInManager
					.addPlugIn("com.ibm.media.codec.video.h263.JavaDecoder",
							new Format[] {
									new VideoFormat("h263", null, -1,
											Format.byteArray, -1.0f),
									new VideoFormat("h263/rtp", null, -1,
											Format.byteArray, -1.0f), },
							new Format[] { new RGBFormat(null, -1, null, -1.0f,
									-1, 0xffffffff, 0xffffffff, 0xffffffff, -1,
									-1, -1, -1), }, PlugInManager.CODEC);
			PlugInManager
					.addPlugIn(
							"com.sun.media.codec.video.colorspace.JavaRGBConverter",
							new Format[] { new RGBFormat(null, -1, null, -1.0f,
									-1, 0xffffffff, 0xffffffff, 0xffffffff, -1,
									-1, -1, -1), },
							new Format[] { new RGBFormat(null, -1, null, -1.0f,
									-1, 0xffffffff, 0xffffffff, 0xffffffff, -1,
									-1, -1, -1), }, PlugInManager.CODEC);
			PlugInManager.addPlugIn(
					"com.sun.media.codec.video.colorspace.JavaRGBToYUV",
					new Format[] {
							new RGBFormat(null, -1, Format.byteArray, -1.0f,
									24, 0xffffffff, 0xffffffff, 0xffffffff, -1,
									-1, -1, -1),
							new RGBFormat(null, -1, Format.intArray, -1.0f, 32,
									0xff0000, 0xff00, 0xff, 1, -1, -1, -1),
							new RGBFormat(null, -1, Format.intArray, -1.0f, 32,
									0xff, 0xff00, 0xff0000, 1, -1, -1, -1), },
					new Format[] { new YUVFormat(null, -1, Format.byteArray,
							-1.0f, 2, -1, -1, -1, -1, -1), },
					PlugInManager.CODEC);
			PlugInManager.addPlugIn("com.ibm.media.codec.audio.PCMToPCM",
					new Format[] {
							new AudioFormat("LINEAR", -1.0, 16, 1, -1, -1, -1,
									-1.0, Format.byteArray),
							new AudioFormat("LINEAR", -1.0, 16, 2, -1, -1, -1,
									-1.0, Format.byteArray),
							new AudioFormat("LINEAR", -1.0, 8, 1, -1, -1, -1,
									-1.0, Format.byteArray),
							new AudioFormat("LINEAR", -1.0, 8, 2, -1, -1, -1,
									-1.0, Format.byteArray), },
					new Format[] { new AudioFormat("LINEAR", -1.0, -1, -1, -1,
							-1, -1, -1.0, Format.byteArray), },
					PlugInManager.CODEC);
			PlugInManager.addPlugIn("com.ibm.media.codec.audio.rc.RCModule",
					new Format[] {
							new AudioFormat("LINEAR", -1.0, 16, 2, -1, -1, -1,
									-1.0, Format.byteArray),
							new AudioFormat("LINEAR", -1.0, 16, 1, -1, -1, -1,
									-1.0, Format.byteArray),
							new AudioFormat("LINEAR", -1.0, 8, 2, -1, -1, -1,
									-1.0, Format.byteArray),
							new AudioFormat("LINEAR", -1.0, 8, 1, -1, -1, -1,
									-1.0, Format.byteArray), }, new Format[] {
							new AudioFormat("LINEAR", 8000.0, 16, 2, 0, 1, -1,
									-1.0, Format.byteArray),
							new AudioFormat("LINEAR", 8000.0, 16, 1, 0, 1, -1,
									-1.0, Format.byteArray), },
					PlugInManager.CODEC);
			PlugInManager.addPlugIn("com.sun.media.codec.audio.rc.RateCvrt",
					new Format[] { new AudioFormat("LINEAR", -1.0, -1, -1, -1,
							-1, -1, -1.0, Format.byteArray), },
					new Format[] { new AudioFormat("LINEAR", -1.0, -1, -1, -1,
							-1, -1, -1.0, Format.byteArray), },
					PlugInManager.CODEC);
			PlugInManager.addPlugIn(
					"com.sun.media.codec.audio.msadpcm.JavaDecoder",
					new Format[] { new AudioFormat("msadpcm", -1.0, -1, -1, -1,
							-1, -1, -1.0, Format.byteArray), },
					new Format[] { new AudioFormat("LINEAR", -1.0, -1, -1, -1,
							-1, -1, -1.0, Format.byteArray), },
					PlugInManager.CODEC);
			PlugInManager.addPlugIn(
					"com.ibm.media.codec.audio.ulaw.JavaDecoder",
					new Format[] { new AudioFormat("ULAW", -1.0, -1, -1, -1,
							-1, -1, -1.0, Format.byteArray), },
					new Format[] { new AudioFormat("LINEAR", -1.0, -1, -1, -1,
							-1, -1, -1.0, Format.byteArray), },
					PlugInManager.CODEC);
			PlugInManager.addPlugIn(
					"com.ibm.media.codec.audio.alaw.JavaDecoder",
					new Format[] { new AudioFormat("alaw", -1.0, -1, -1, -1,
							-1, -1, -1.0, Format.byteArray), },
					new Format[] { new AudioFormat("LINEAR", -1.0, -1, -1, -1,
							-1, -1, -1.0, Format.byteArray), },
					PlugInManager.CODEC);
			PlugInManager.addPlugIn(
					"com.ibm.media.codec.audio.dvi.JavaDecoder",
					new Format[] { new AudioFormat("dvi/rtp", -1.0, -1, -1, -1,
							-1, -1, -1.0, Format.byteArray), },
					new Format[] { new AudioFormat("LINEAR", -1.0, -1, -1, -1,
							-1, -1, -1.0, Format.byteArray), },
					PlugInManager.CODEC);
			PlugInManager.addPlugIn(
					"com.ibm.media.codec.audio.g723.JavaDecoder", new Format[] {
							new AudioFormat("g723", -1.0, -1, -1, -1, -1, -1,
									-1.0, Format.byteArray),
							new AudioFormat("g723/rtp", -1.0, -1, -1, -1, -1,
									-1, -1.0, Format.byteArray), },
					new Format[] { new AudioFormat("LINEAR", -1.0, -1, -1, -1,
							-1, -1, -1.0, Format.byteArray), },
					PlugInManager.CODEC);
			PlugInManager.addPlugIn(
					"com.ibm.media.codec.audio.gsm.JavaDecoder", new Format[] {
							new AudioFormat("gsm", -1.0, -1, -1, -1, -1, -1,
									-1.0, Format.byteArray),
							new AudioFormat("gsm/rtp", -1.0, -1, -1, -1, -1,
									-1, -1.0, Format.byteArray), },
					new Format[] { new AudioFormat("LINEAR", -1.0, -1, -1, -1,
							-1, -1, -1.0, Format.byteArray), },
					PlugInManager.CODEC);
			PlugInManager.addPlugIn(
					"com.ibm.media.codec.audio.gsm.JavaDecoder_ms",
					new Format[] { new AudioFormat("gsm/ms", -1.0, -1, -1, -1,
							-1, -1, -1.0, Format.byteArray), },
					new Format[] { new AudioFormat("LINEAR", -1.0, -1, -1, -1,
							-1, -1, -1.0, Format.byteArray), },
					PlugInManager.CODEC);
			PlugInManager.addPlugIn(
					"com.ibm.media.codec.audio.ima4.JavaDecoder",
					new Format[] { new AudioFormat("ima4", -1.0, -1, -1, -1,
							-1, -1, -1.0, Format.byteArray), },
					new Format[] { new AudioFormat("LINEAR", -1.0, -1, -1, -1,
							-1, -1, -1.0, Format.byteArray), },
					PlugInManager.CODEC);
			PlugInManager.addPlugIn(
					"com.ibm.media.codec.audio.ima4.JavaDecoder_ms",
					new Format[] { new AudioFormat("ima4/ms", -1.0, -1, -1, -1,
							-1, -1, -1.0, Format.byteArray), },
					new Format[] { new AudioFormat("LINEAR", -1.0, -1, -1, -1,
							-1, -1, -1.0, Format.byteArray), },
					PlugInManager.CODEC);
			PlugInManager.addPlugIn(
					"com.ibm.media.codec.audio.ulaw.JavaEncoder", new Format[] {
							new AudioFormat("LINEAR", -1.0, 16, 1, -1, -1, -1,
									-1.0, Format.byteArray),
							new AudioFormat("LINEAR", -1.0, 16, 2, -1, -1, -1,
									-1.0, Format.byteArray),
							new AudioFormat("LINEAR", -1.0, 8, 1, -1, -1, -1,
									-1.0, Format.byteArray),
							new AudioFormat("LINEAR", -1.0, 8, 2, -1, -1, -1,
									-1.0, Format.byteArray), },
					new Format[] { new AudioFormat("ULAW", 8000.0, 8, 1, -1,
							-1, -1, -1.0, Format.byteArray), },
					PlugInManager.CODEC);
			PlugInManager.addPlugIn(
					"com.ibm.media.codec.audio.dvi.JavaEncoder",
					new Format[] { new AudioFormat("LINEAR", -1.0, 16, 1, 0, 1,
							-1, -1.0, Format.byteArray), },
					new Format[] { new AudioFormat("dvi/rtp", -1.0, 4, 1, -1,
							-1, -1, -1.0, Format.byteArray), },
					PlugInManager.CODEC);
			PlugInManager
					.addPlugIn("com.ibm.media.codec.audio.gsm.JavaEncoder",
							new Format[] { new AudioFormat("LINEAR", -1.0, 16,
									1, 0, 1, -1, -1.0, Format.byteArray), },
							new Format[] { new AudioFormat("gsm", -1.0, -1, -1,
									-1, -1, -1, -1.0, Format.byteArray), },
							PlugInManager.CODEC);
			PlugInManager.addPlugIn(
					"com.ibm.media.codec.audio.gsm.JavaEncoder_ms",
					new Format[] { new AudioFormat("LINEAR", -1.0, 16, 1, 0, 1,
							-1, -1.0, Format.byteArray), },
					new Format[] { new com.sun.media.format.WavAudioFormat(
							"gsm/ms", -1.0, -1, -1, -1, -1, -1, -1, -1.0f,
							Format.byteArray, null), }, PlugInManager.CODEC);
			PlugInManager.addPlugIn(
					"com.ibm.media.codec.audio.ima4.JavaEncoder",
					new Format[] { new AudioFormat("LINEAR", -1.0, 16, -1, 0,
							1, -1, -1.0, Format.byteArray), },
					new Format[] { new AudioFormat("ima4", -1.0, -1, -1, -1,
							-1, -1, -1.0, Format.byteArray), },
					PlugInManager.CODEC);
			PlugInManager.addPlugIn(
					"com.ibm.media.codec.audio.ima4.JavaEncoder_ms",
					new Format[] { new AudioFormat("LINEAR", -1.0, 16, -1, 0,
							1, -1, -1.0, Format.byteArray), },
					new Format[] { new com.sun.media.format.WavAudioFormat(
							"ima4/ms", -1.0, -1, -1, -1, -1, -1, -1, -1.0f,
							Format.byteArray, null), }, PlugInManager.CODEC);
			PlugInManager.addPlugIn(
					"com.sun.media.codec.audio.ulaw.Packetizer",
					new Format[] { new AudioFormat("ULAW", -1.0, 8, 1, -1, -1,
							8, -1.0, Format.byteArray), },
					new Format[] { new AudioFormat("ULAW/rtp", -1.0, 8, 1, -1,
							-1, 8, -1.0, Format.byteArray), },
					PlugInManager.CODEC);
			PlugInManager.addPlugIn(
					"com.sun.media.codec.audio.ulaw.DePacketizer",
					new Format[] { new AudioFormat("ULAW/rtp", -1.0, -1, -1,
							-1, -1, -1, -1.0, Format.byteArray), },
					new Format[] { new AudioFormat("ULAW", -1.0, -1, -1, -1,
							-1, -1, -1.0, Format.byteArray), },
					PlugInManager.CODEC);
			PlugInManager.addPlugIn("com.sun.media.codec.audio.mpa.Packetizer",
					new Format[] {
							new AudioFormat("mpeglayer3", 16000.0, -1, -1, -1,
									1, -1, -1.0, Format.byteArray),
							new AudioFormat("mpeglayer3", 22050.0, -1, -1, -1,
									1, -1, -1.0, Format.byteArray),
							new AudioFormat("mpeglayer3", 24000.0, -1, -1, -1,
									1, -1, -1.0, Format.byteArray),
							new AudioFormat("mpeglayer3", 32000.0, -1, -1, -1,
									1, -1, -1.0, Format.byteArray),
							new AudioFormat("mpeglayer3", 44100.0, -1, -1, -1,
									1, -1, -1.0, Format.byteArray),
							new AudioFormat("mpeglayer3", 48000.0, -1, -1, -1,
									1, -1, -1.0, Format.byteArray),
							new AudioFormat("mpegaudio", 16000.0, -1, -1, -1,
									1, -1, -1.0, Format.byteArray),
							new AudioFormat("mpegaudio", 22050.0, -1, -1, -1,
									1, -1, -1.0, Format.byteArray),
							new AudioFormat("mpegaudio", 24000.0, -1, -1, -1,
									1, -1, -1.0, Format.byteArray),
							new AudioFormat("mpegaudio", 32000.0, -1, -1, -1,
									1, -1, -1.0, Format.byteArray),
							new AudioFormat("mpegaudio", 44100.0, -1, -1, -1,
									1, -1, -1.0, Format.byteArray),
							new AudioFormat("mpegaudio", 48000.0, -1, -1, -1,
									1, -1, -1.0, Format.byteArray), },
					new Format[] { new AudioFormat("mpegaudio/rtp", -1.0, -1,
							-1, -1, -1, -1, -1.0, Format.byteArray), },
					PlugInManager.CODEC);
			PlugInManager.addPlugIn(
					"com.sun.media.codec.audio.mpa.DePacketizer",
					new Format[] { new AudioFormat("mpegaudio/rtp", -1.0, -1,
							-1, -1, -1, -1, -1.0, Format.byteArray), },
					new Format[] {
							new AudioFormat("mpegaudio", 44100.0, 16, -1, 1, 1,
									-1, -1.0, Format.byteArray),
							new AudioFormat("mpegaudio", 48000.0, 16, -1, 1, 1,
									-1, -1.0, Format.byteArray),
							new AudioFormat("mpegaudio", 32000.0, 16, -1, 1, 1,
									-1, -1.0, Format.byteArray),
							new AudioFormat("mpegaudio", 22050.0, 16, -1, 1, 1,
									-1, -1.0, Format.byteArray),
							new AudioFormat("mpegaudio", 24000.0, 16, -1, 1, 1,
									-1, -1.0, Format.byteArray),
							new AudioFormat("mpegaudio", 16000.0, 16, -1, 1, 1,
									-1, -1.0, Format.byteArray),
							new AudioFormat("mpegaudio", 11025.0, 16, -1, 1, 1,
									-1, -1.0, Format.byteArray),
							new AudioFormat("mpegaudio", 12000.0, 16, -1, 1, 1,
									-1, -1.0, Format.byteArray),
							new AudioFormat("mpegaudio", 8000.0, 16, -1, 1, 1,
									-1, -1.0, Format.byteArray),
							new AudioFormat("mpeglayer3", 44100.0, 16, -1, 1,
									1, -1, -1.0, Format.byteArray),
							new AudioFormat("mpeglayer3", 48000.0, 16, -1, 1,
									1, -1, -1.0, Format.byteArray),
							new AudioFormat("mpeglayer3", 32000.0, 16, -1, 1,
									1, -1, -1.0, Format.byteArray),
							new AudioFormat("mpeglayer3", 22050.0, 16, -1, 1,
									1, -1, -1.0, Format.byteArray),
							new AudioFormat("mpeglayer3", 24000.0, 16, -1, 1,
									1, -1, -1.0, Format.byteArray),
							new AudioFormat("mpeglayer3", 16000.0, 16, -1, 1,
									1, -1, -1.0, Format.byteArray),
							new AudioFormat("mpeglayer3", 11025.0, 16, -1, 1,
									1, -1, -1.0, Format.byteArray),
							new AudioFormat("mpeglayer3", 12000.0, 16, -1, 1,
									1, -1, -1.0, Format.byteArray),
							new AudioFormat("mpeglayer3", 8000.0, 16, -1, 1, 1,
									-1, -1.0, Format.byteArray), },
					PlugInManager.CODEC);
			PlugInManager.addPlugIn("com.ibm.media.codec.audio.gsm.Packetizer",
					new Format[] { new AudioFormat("gsm", 8000.0, -1, 1, -1,
							-1, 264, -1.0, Format.byteArray), },
					new Format[] { new AudioFormat("gsm/rtp", 8000.0, -1, 1,
							-1, -1, 264, -1.0, Format.byteArray), },
					PlugInManager.CODEC);
			PlugInManager.addPlugIn(
					"com.ibm.media.codec.audio.g723.Packetizer",
					new Format[] { new AudioFormat("g723", 8000.0, -1, 1, -1,
							-1, 192, -1.0, Format.byteArray), },
					new Format[] { new AudioFormat("g723/rtp", 8000.0, -1, 1,
							-1, -1, 192, -1.0, Format.byteArray), },
					PlugInManager.CODEC);
		}
		if ((flags & FMJ) != 0) {
			PlugInManager.addPlugIn(
					"net.sf.fmj.media.codec.video.jpeg.Packetizer",
					new Format[] { new VideoFormat("jpeg", null, -1,
							Format.byteArray, -1.0f), },
					new Format[] { new VideoFormat("jpeg/rtp", null, -1,
							Format.byteArray, -1.0f), }, PlugInManager.CODEC);
		}
		if ((flags & JMF) != 0) {

			PlugInManager.addPlugIn(
					"com.sun.media.codec.video.jpeg.Packetizer",
					new Format[] { new VideoFormat("jpeg", null, -1,
							Format.byteArray, -1.0f), },
					new Format[] { new VideoFormat("jpeg/rtp", null, -1,
							Format.byteArray, -1.0f), }, PlugInManager.CODEC);
		}
		if ((flags & FMJ) != 0) {
			PlugInManager.addPlugIn(
					"net.sf.fmj.media.codec.video.jpeg.DePacketizer",
					new Format[] { new VideoFormat("jpeg/rtp", null, -1,
							Format.byteArray, -1.0f), },
					new Format[] { new VideoFormat("jpeg", null, -1,
							Format.byteArray, -1.0f), }, PlugInManager.CODEC);
		}
		if ((flags & JMF) != 0) {

			PlugInManager.addPlugIn(
					"com.sun.media.codec.video.jpeg.DePacketizer",
					new Format[] { new VideoFormat("jpeg/rtp", null, -1,
							Format.byteArray, -1.0f), },
					new Format[] { new VideoFormat("jpeg", null, -1,
							Format.byteArray, -1.0f), }, PlugInManager.CODEC);
			PlugInManager.addPlugIn(
					"com.sun.media.codec.video.mpeg.Packetizer",
					new Format[] { new VideoFormat("mpeg", null, -1,
							Format.byteArray, -1.0f), },
					new Format[] { new VideoFormat("mpeg/rtp", null, -1,
							Format.byteArray, -1.0f), }, PlugInManager.CODEC);
			PlugInManager.addPlugIn(
					"com.sun.media.codec.video.mpeg.DePacketizer",
					new Format[] { new VideoFormat("mpeg/rtp", null, -1,
							Format.byteArray, -1.0f), },
					new Format[] { new VideoFormat("mpeg", null, -1,
							Format.byteArray, -1.0f), }, PlugInManager.CODEC);

			
			
		}
		// PlugInManager.EFFECT:

		// PlugInManager.RENDERER:
		if ((flags & JMF) != 0) {
			PlugInManager.addPlugIn(
					"com.sun.media.renderer.audio.JavaSoundRenderer",
					new Format[] {
							new AudioFormat("LINEAR", -1.0, -1, -1, -1, -1, -1,
									-1.0, Format.byteArray),
							new AudioFormat("ULAW", -1.0, -1, -1, -1, -1, -1,
									-1.0, Format.byteArray), },
					new Format[] {}, PlugInManager.RENDERER);
			PlugInManager.addPlugIn(
					"com.sun.media.renderer.audio.SunAudioRenderer",
					new Format[] { new AudioFormat("ULAW", 8000.0, 8, 1, -1,
							-1, -1, -1.0, Format.byteArray), },
					new Format[] {}, PlugInManager.RENDERER);
			PlugInManager.addPlugIn("com.sun.media.renderer.video.AWTRenderer",
					new Format[] {
							new RGBFormat(null, -1, Format.intArray, -1.0f, 32,
									0xff0000, 0xff00, 0xff, 1, -1, 0, -1),
							new RGBFormat(null, -1, Format.intArray, -1.0f, 32,
									0xff, 0xff00, 0xff0000, 1, -1, 0, -1), },
					new Format[] {}, PlugInManager.RENDERER);
			PlugInManager.addPlugIn(
					"com.sun.media.renderer.video.LightWeightRenderer",
					new Format[] {
							new RGBFormat(null, -1, Format.intArray, -1.0f, 32,
									0xff0000, 0xff00, 0xff, 1, -1, 0, -1),
							new RGBFormat(null, -1, Format.intArray, -1.0f, 32,
									0xff, 0xff00, 0xff0000, 1, -1, 0, -1), },
					new Format[] {}, PlugInManager.RENDERER);

			PlugInManager.addPlugIn(
					"com.sun.media.renderer.video.JPEGRenderer",
					new Format[] { new VideoFormat("jpeg", null, -1,
							Format.byteArray, -1.0f), }, new Format[] {},
					PlugInManager.RENDERER);

			// PlugInManager.MULTIPLEXER:
			PlugInManager.addPlugIn("com.sun.media.multiplexer.RawBufferMux",
					new Format[] {},
					new Format[] { new ContentDescriptor("raw"), },
					PlugInManager.MULTIPLEXER);
			PlugInManager.addPlugIn(
					"com.sun.media.multiplexer.RawSyncBufferMux",
					new Format[] {},
					new Format[] { new ContentDescriptor("raw"), },
					PlugInManager.MULTIPLEXER);
			PlugInManager.addPlugIn(
					"com.sun.media.multiplexer.RTPSyncBufferMux",
					new Format[] {}, new Format[] { new ContentDescriptor(
							"raw.rtp"), }, PlugInManager.MULTIPLEXER);
			PlugInManager.addPlugIn("com.sun.media.multiplexer.audio.GSMMux",
					new Format[] {}, new Format[] { new FileTypeDescriptor(
							"audio.x_gsm"), }, PlugInManager.MULTIPLEXER);
			PlugInManager.addPlugIn("com.sun.media.multiplexer.audio.MPEGMux",
					new Format[] {}, new Format[] { new FileTypeDescriptor(
							"audio.mpeg"), }, PlugInManager.MULTIPLEXER);
			PlugInManager.addPlugIn("com.sun.media.multiplexer.audio.WAVMux",
					new Format[] {}, new Format[] { new FileTypeDescriptor(
							"audio.x_wav"), }, PlugInManager.MULTIPLEXER);
			PlugInManager.addPlugIn("com.sun.media.multiplexer.audio.AIFFMux",
					new Format[] {}, new Format[] { new FileTypeDescriptor(
							"audio.x_aiff"), }, PlugInManager.MULTIPLEXER);
			PlugInManager.addPlugIn("com.sun.media.multiplexer.audio.AUMux",
					new Format[] {}, new Format[] { new FileTypeDescriptor(
							"audio.basic"), }, PlugInManager.MULTIPLEXER);
			PlugInManager.addPlugIn("com.sun.media.multiplexer.video.AVIMux",
					new Format[] {}, new Format[] { new FileTypeDescriptor(
							"video.x_msvideo"), }, PlugInManager.MULTIPLEXER);
			PlugInManager.addPlugIn(
					"com.sun.media.multiplexer.video.QuicktimeMux",
					new Format[] {}, new Format[] { new FileTypeDescriptor(
							"video.quicktime"), }, PlugInManager.MULTIPLEXER);
		}

		if ((flags & FMJ) != 0) {

			PlugInUtility
					.registerPlugIn("net.sf.fmj.media.renderer.video.SimpleSwingRenderer");
			PlugInUtility
					.registerPlugIn("net.sf.fmj.media.renderer.video.SimpleAWTRenderer");
			PlugInUtility
					.registerPlugIn("net.sf.fmj.media.renderer.video.Java2dRenderer");

			PlugInUtility
					.registerPlugIn("net.sf.fmj.media.parser.JavaSoundParser");
			PlugInUtility
					.registerPlugIn("net.sf.fmj.media.codec.JavaSoundCodec");
			PlugInUtility
					.registerPlugIn("net.sf.fmj.media.renderer.audio.JavaSoundRenderer");

			PlugInUtility
					.registerPlugIn("net.sf.fmj.media.codec.audio.ulaw.Decoder");
			PlugInUtility
					.registerPlugIn("net.sf.fmj.media.codec.audio.ulaw.Encoder");
			PlugInUtility
					.registerPlugIn("net.sf.fmj.media.codec.audio.ulaw.DePacketizer");
			PlugInUtility
					.registerPlugIn("net.sf.fmj.media.codec.audio.ulaw.Packetizer");
			PlugInUtility
					.registerPlugIn("net.sf.fmj.media.codec.audio.RateConverter");

			PlugInUtility
					.registerPlugIn("net.sf.fmj.media.codec.audio.alaw.Decoder");
			PlugInUtility
					.registerPlugIn("net.sf.fmj.media.codec.audio.alaw.Encoder");
			PlugInUtility
					.registerPlugIn("net.sf.fmj.media.codec.audio.alaw.DePacketizer");
			PlugInUtility
					.registerPlugIn("net.sf.fmj.media.codec.audio.alaw.Packetizer");
			PlugInUtility
					.registerPlugIn("net.sf.fmj.media.codec.video.jpeg.JpegEncoder");

			PlugInUtility
					.registerPlugIn("net.sf.fmj.media.parser.RawPushBufferParser");
			PlugInUtility
					.registerPlugIn("net.sf.fmj.media.multiplexer.RTPSyncBufferMux");
			PlugInUtility
					.registerPlugIn("net.sf.fmj.media.multiplexer.RawBufferMux");
			PlugInUtility
					.registerPlugIn("net.sf.fmj.media.multiplexer.audio.AIFFMux");
			PlugInUtility
					.registerPlugIn("net.sf.fmj.media.multiplexer.audio.AUMux");
			PlugInUtility
					.registerPlugIn("net.sf.fmj.media.multiplexer.audio.WAVMux");

			PlugInUtility
					.registerPlugIn("net.sf.fmj.media.codec.video.ImageScaler");

			// ffmpeg-java parser: may not be in classpath
			PlugInUtility.registerPlugIn("net.sf.fmj.ffmpeg_java.FFMPEGParser");
			PlugInUtility
					.registerPlugIn("net.sf.fmj.theora_java.NativeOggParser");
			PlugInUtility
					.registerPlugIn("net.sf.fmj.theora_java.JavaOggParser");

			// if (OSUtils.isMacOSX())
			// {
			// PlugInUtility.registerPlugIn("net.sf.fmj.qt.QTParser");
			// }

			PlugInUtility
					.registerPlugIn("net.sf.fmj.media.parser.MultipartMixedReplaceParser");

			// SIP communicator packetizers/depacketizers.
			PlugInUtility
					.registerPlugIn("net.java.sip.communicator.impl.media.codec.audio.speex.JavaEncoder");
			PlugInUtility
					.registerPlugIn("net.java.sip.communicator.impl.media.codec.audio.speex.JavaDecoder");
			PlugInUtility
					.registerPlugIn("net.java.sip.communicator.impl.media.codec.audio.ilbc.JavaEncoder");
			PlugInUtility
					.registerPlugIn("net.java.sip.communicator.impl.media.codec.audio.ilbc.JavaDecoder");

		}

		if ((flags & THIRD_PARTY) != 0) {
			// JFFMPEG: may not be in classpath
			// JFFMPEG is not needed for ogg playback, because JavaSound with an
			// spi can handle
			// ogg audio files. net.sourceforge.jffmpeg.demux.ogg.OggDemux does
			// not appear to split
			// out the video stream, so if this demux gets used instead of
			// net.sf.fmj.theora_java.OGGParser,
			// audio will play but no video.
			// PlugInUtility.registerPlugIn("net.sourceforge.jffmpeg.demux.ogg.OggDemux");
			// PlugInUtility.registerPlugIn("net.sourceforge.jffmpeg.AudioDecoder");

			// PlugInUtility.registerPlugIn("net.sourceforge.jffmpeg.demux.avi.AviDemux");
			// PlugInUtility.registerPlugIn("net.sourceforge.jffmpeg.VideoDecoder");
			// PlugInUtility.registerPlugIn("net.sourceforge.jffmpeg.AudioDecoder");
			// PlugInManager.removePlugIn("com.sun.media.parser.video.AviParser",
			// PlugInManager.DEMULTIPLEXER);

			// FOBS4JMF: may not be in classpath
			PlugInUtility
					.registerPlugIn("com.omnividea.media.parser.video.Parser");
			PlugInUtility
					.registerPlugIn("com.omnividea.media.codec.video.NativeDecoder");
			PlugInUtility
					.registerPlugIn("com.omnividea.media.codec.audio.NativeDecoder");
			PlugInUtility
					.registerPlugIn("com.omnividea.media.codec.video.JavaDecoder");
			// protocol: com.omnividea - also added in JmfRegistry
		}

		if ((flags & FMJ) != 0) {
			// remove the audio/mpeg from ibm's: - this can result in a demux
			// with no renderer.
			if (PlugInManager.removePlugIn(
					"com.ibm.media.parser.video.MpegParser",
					PlugInManager.DEMULTIPLEXER)) {
				PlugInManager.addPlugIn(
						"com.ibm.media.parser.video.MpegParser", new Format[] {
						// new ContentDescriptor("audio.mpeg"),
						new ContentDescriptor("video.mpeg"),
						// new ContentDescriptor("audio.mpeg"),
						}, new Format[] {}, PlugInManager.DEMULTIPLEXER);
			}
		}
	}

	public static void unRegisterPlugins(int flags) {
		final int[] types = new int[] { PlugInManager.MULTIPLEXER,
				PlugInManager.CODEC, PlugInManager.EFFECT,
				PlugInManager.RENDERER, PlugInManager.MULTIPLEXER };

		for (int type : types) {
			final Vector v = PlugInManager.getPlugInList(null, null, type);
			for (Object o : v) {
				final String className = (String) o;

				boolean remove = false;
				if ((flags & JMF) != 0) {
					if (className.startsWith("com.ibm.")
							|| className.startsWith("com.sun.")
							|| className.startsWith("javax.media."))
						remove = true;
				}
				if ((flags & FMJ) != 0) {
					if (className.startsWith("net.sf.fmj")
							|| className
									.startsWith("net.java.sip.communicator.impl.media."))
						remove = true;
				}
				if ((flags & THIRD_PARTY) != 0) {
					if (className.startsWith("com.omnividea.media."))
						remove = true;
				}
				if (remove) {
					PlugInManager.removePlugIn(className, type);
				}
			}
		}

	}
}

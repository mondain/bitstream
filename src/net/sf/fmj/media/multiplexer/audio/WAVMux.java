package net.sf.fmj.media.multiplexer.audio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.media.Format;
import javax.media.format.AudioFormat;
import javax.media.protocol.FileTypeDescriptor;
import javax.sound.sampled.AudioFileFormat;

import net.sf.fmj.media.codec.JavaSoundCodec;
import net.sf.fmj.utility.IOUtils;

/**
 * 
 * @author Ken Larson
 * 
 */
public class WAVMux extends JavaSoundMux {

	public WAVMux() {
		super(new FileTypeDescriptor(FileTypeDescriptor.WAVE),
				AudioFileFormat.Type.WAVE);
	}

	public Format setInputFormat(Format format, int trackID) {
		AudioFormat af = (AudioFormat) format;
		if (af.getSampleSizeInBits() == 8
				&& af.getSigned() == AudioFormat.SIGNED)
			return null; // 8-bit is always unsigned for Wav.

		if (af.getSampleSizeInBits() == 16
				&& af.getSigned() == AudioFormat.UNSIGNED)
			return null; // 16-bit is always signed for Wav.

		return super.setInputFormat(format, trackID);
	}

	protected void write(InputStream in, OutputStream out,
			javax.sound.sampled.AudioFormat javaSoundFormat) throws IOException {
		if (true) {
			super.write(in, out, javaSoundFormat);

		} else { // alternative to JavaSound - not necessary.
			try {
				byte[] header = JavaSoundCodec.createWavHeader(javaSoundFormat); // TODO:
																					// no
																					// length
				if (header == null)
					throw new IOException("Unable to create wav header");

				// System.out.println("WAVMux Header: " + header.length);
				out.write(header);

				IOUtils.copyStream(in, out);

				// TODO: go back and write header.
			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			}
		}
	}

}

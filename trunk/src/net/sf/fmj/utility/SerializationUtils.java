package net.sf.fmj.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import net.sf.fmj.codegen.CGUtils;

/**
 * Utilities for serializing/de-serializing objects using standard Java
 * serialization.
 * 
 * @author Ken Larson
 * 
 */
public class SerializationUtils {
	public static String serialize(javax.media.Format f) throws IOException {
		final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		final ObjectOutputStream output = new ObjectOutputStream(buffer);
		output.writeObject(f);
		output.close();
		buffer.close();
		return CGUtils.byteArrayToHexString(buffer.toByteArray());

	}

	public static javax.media.Format deserialize(String s) throws IOException,
			ClassNotFoundException {
		final byte[] ba = CGUtils.hexStringToByteArray(s);
		final ByteArrayInputStream inbuf = new ByteArrayInputStream(ba);
		final ObjectInputStream input = new ObjectInputStream(inbuf);
		final Object oRead = input.readObject();
		input.close();
		inbuf.close();
		return (javax.media.Format) oRead;

	}
}

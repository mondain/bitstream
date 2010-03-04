package com.sun.media;

import javax.media.Buffer;
import javax.media.Codec;
import javax.media.Format;
import javax.media.ResourceUnavailableException;

/**
 * TODO: incomplete.
 * 
 * @author Ken Larson
 */
public abstract class BasicCodec extends BasicPlugIn implements Codec {
	// TODO: it is really hard to figure out comprehensively what is being done
	// with these various methods.
	// I think we may have enough for FFMPEG.

	/*
	 * ffmpeg uses: inputFormat, outputFormat, inputFormats, outputFormats, //
	 * if (isEOM(inBuffer)) { // propagateEOM(outBuffer);
	 * 
	 * Object inData = getInputData(inBuffer); long inDataBytes =
	 * getNativeData(inData);
	 * 
	 * Object outData = getOutputData(outBuffer);
	 * 
	 * outData = validateData(outBuffer, outputH263Length, true / *allow native*
	 * /); long outDataBytes = getNativeData(outData);
	 * 
	 * opened
	 */

	private static final boolean DEBUG = true;
	protected Format inputFormat = null;
	protected Format outputFormat = null;
	protected boolean opened = false;
	protected Format[] inputFormats = new Format[0];
	protected Format[] outputFormats = new Format[0];
	protected boolean pendingEOM = false;

	public BasicCodec() {
		super();
	}

	public Format setInputFormat(Format input) {
		this.inputFormat = input;
		return this.inputFormat;
	}

	public Format setOutputFormat(Format output) {
		this.outputFormat = output;
		return this.outputFormat;
	}

	protected Format getInputFormat() {
		return inputFormat;
	}

	protected Format getOutputFormat() {
		return outputFormat;
	}

	public void reset() {
		// TODO: does not appear to do anything.
	}

	public Format[] getSupportedInputFormats() {
		return inputFormats;
	}

	protected boolean isEOM(Buffer inputBuffer) {
		return inputBuffer.isEOM();
	}

	protected void propagateEOM(Buffer outputBuffer) {
		outputBuffer.setFormat(outputFormat);
		outputBuffer.setLength(0);
		outputBuffer.setOffset(0);
		outputBuffer.setEOM(true);

	}

	protected void updateOutput(Buffer outputBuffer, Format format, int length,
			int offset) {
		outputBuffer.setFormat(format);
		outputBuffer.setLength(length);
		outputBuffer.setOffset(offset);

	}

	protected boolean checkInputBuffer(Buffer inputBuffer) {
		// JMF appears to call isEOM on the buffer.
		if (inputBuffer.isEOM())
			return true;

		final Format f = inputBuffer.getFormat();

		inputBuffer.getFormat(); // TODO: why does JMF call this twice?

		// JMF appears to call checkFormat
		return f != null && checkFormat(f); // TODO: anything else to check?
	}

	protected boolean checkFormat(Format format) {
		return true; // TODO: anything to check? does not appear to check
						// anything.
	}

	protected int checkEOM(Buffer inputBuffer, Buffer outputBuffer) {
		throw new UnsupportedOperationException(); // TODO
	}

	protected int processAtEOM(Buffer inputBuffer, Buffer outputBuffer) {
		throw new UnsupportedOperationException(); // TODO
	}

	protected int getArrayElementSize(Class type) {
		if (type == byte[].class)
			return 1;
		else if (type == short[].class)
			return 2;
		else if (type == int[].class)
			return 4;
		else
			return 0;

	}

	public abstract int process(Buffer input, Buffer output);

	public abstract Format[] getSupportedOutputFormats(Format input);

	// @Override
	public void close() {
		opened = false;
	}

	// @Override
	public void open() throws ResourceUnavailableException {
		opened = true;
	}

}

package net.sf.fmj.media.codec.audio;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.format.AudioFormat;

import net.sf.fmj.codegen.MediaCGUtils;
import net.sf.fmj.media.AbstractCodec;
import net.sf.fmj.utility.LoggerSingleton;
import net.sf.fmj.utility.UnsignedUtils;

/**
 * 
 * Converts between different linear audio formats. Able to change
 * signed/unsigned, endian-ness, bits per sample, sample rate, channels. TODO:
 * optimize. TODO: improve quality of conversions. See for example
 * http://www.fmjsoft.com/atquality.html (name fmjsoft is coincidental). TODO:
 * change stereo to mono by averaging, rather than by omission of 2nd channel.
 * TODO: handle data types besides byte arrays This converter has so many
 * formats that it causes a big slowdown in filter graph building, both for FMJ
 * and JMF.
 * 
 * @author Ken Larson
 * 
 */
public class RateConverter extends AbstractCodec {
	private static final boolean ONLY_CHANGE_1_PARAMETER = false; // TODO: more
																	// efficient
																	// if false.
																	// Still
																	// needs
																	// testing
																	// to make
																	// sure
																	// changing
																	// multiple
																	// works.

	private static final Logger logger = LoggerSingleton.logger;

	public String getName() {
		return "Rate Converter";
	}

	public RateConverter() {
		super();
		this.inputFormats = new Format[] { new AudioFormat(AudioFormat.LINEAR,
				-1.0, -1, -1, -1, -1, -1, -1.0, Format.byteArray) };

	}

	// TODO: move to base class?
	protected Format[] outputFormats = new Format[] { new AudioFormat(
			AudioFormat.LINEAR, -1.0, -1, -1, -1, -1, -1, -1.0,
			Format.byteArray) };

	// supported parameter values for conversions:
	// common audio sample rates. Others could be added with no impact.
	private static final double[] sampleRateValues = new double[] { 8000.0,
			11025.0, 22050.0, 44100.0, 48000.0 };
	private static final int[] sampleSizesInBits = new int[] { 8, 16, 24, 32 }; // TODO:
																				// test
																				// 24
																				// and
																				// 32.
	private static final int[] endianValues = new int[] {
			AudioFormat.BIG_ENDIAN, AudioFormat.LITTLE_ENDIAN };
	private static final int[] signedValues = new int[] { AudioFormat.SIGNED,
			AudioFormat.UNSIGNED };
	private static final int[] channelsValues = new int[] { 1, 2 };

	public Format[] getSupportedOutputFormats(Format input) {
		if (input == null)
			return outputFormats;
		else {
			if (!(input instanceof AudioFormat)) {
				logger
						.warning(this.getClass().getSimpleName()
								+ ".getSupportedOutputFormats: input format does not match, returning format array of {null} for "
								+ input); // this can cause an NPE in JMF if it
											// ever happens.
				return new Format[] { null };
			}
			final AudioFormat inputCast = (AudioFormat) input;
			if (!inputCast.getEncoding().equals(AudioFormat.LINEAR)
					|| (inputCast.getDataType() != null && inputCast
							.getDataType() != Format.byteArray)) {
				logger
						.warning(this.getClass().getSimpleName()
								+ ".getSupportedOutputFormats: input format does not match, returning format array of {null} for "
								+ input); // this can cause an NPE in JMF if it
											// ever happens.
				return new Format[] { null };
			}

			final List resultList = new ArrayList();

			// copy this.sampleRateValues, add input sample rate to it, in case
			// it is not in the supported list.
			final double[] sampleRateValues = new double[RateConverter.sampleRateValues.length + 1];
			sampleRateValues[0] = inputCast.getSampleRate();
			for (int i = 0; i < RateConverter.sampleRateValues.length; ++i)
				sampleRateValues[i + 1] = RateConverter.sampleRateValues[i];

			for (int i = 0; i < sampleRateValues.length; ++i) {
				final double sampleRate = sampleRateValues[i];
				final boolean sampleRateChanged = sampleRate != inputCast
						.getSampleRate();

				for (int j = 0; j < sampleSizesInBits.length; ++j) {
					final int sampleSizeInBits = sampleSizesInBits[j];
					final boolean sampleSizeInBitsChanged = sampleSizeInBits != inputCast
							.getSampleSizeInBits();

					for (int k = 0; k < endianValues.length; ++k) {
						// endian is only meaningful if there are more than 8
						// bits
						final int endian = sampleSizeInBits <= 8 ? inputCast
								.getEndian() : endianValues[k];
						final boolean endianChanged = (sampleSizeInBits > 8 && inputCast
								.getSampleSizeInBits() > 8)
								&& // only consider endian changed if neither of
									// the formsts is 8 bits
								endian != inputCast.getEndian();

						for (int m = 0; m < signedValues.length; ++m) {
							final int signed = signedValues[m];
							final boolean signedChanged = signed != inputCast
									.getSigned();

							for (int n = 0; n < channelsValues.length; ++n) {
								final int channels = channelsValues[n];
								final boolean channelsChanged = channels != inputCast
										.getChannels();

								int numChanged = 0;
								if (sampleRateChanged)
									++numChanged;
								if (sampleSizeInBitsChanged)
									++numChanged;
								if (endianChanged)
									++numChanged;
								if (signedChanged)
									++numChanged;
								if (channelsChanged)
									++numChanged;

								if (numChanged < 1)
									continue;

								if (ONLY_CHANGE_1_PARAMETER && numChanged != 1)
									continue;

								{
									AudioFormat f = new AudioFormat(
											AudioFormat.LINEAR, sampleRate,
											sampleSizeInBits, channels, endian,
											signed,
											channels * sampleSizeInBits,
											sampleRate, Format.byteArray);
									// Note: for linear, frame size in bits is
									// always the same as for sample size in
									// bits * the number of channels.

									if (!resultList.contains(f))
										resultList.add(f);
								}
							}
						}
					}
				}
			}

			final Format[] result = new Format[resultList.size()];
			for (int i = 0; i < resultList.size(); ++i)
				result[i] = (Format) resultList.get(i);
			return result;
		}
	}

	public void open() {
		for (Averager a : averagers)
			a.reset();
	}

	public void close() {
	}

	private static final boolean TRACE = false;

	private final Averager averagers[] = new Averager[] { new Averager(),
			new Averager() };

	private static class Averager {
		public double accumulatedSample = 0.0; // used only for averaging, if
												// output sample rate is less
												// than input
		public double numAccumulatedSamples = 0.0; // ditto

		public void reset() {
			accumulatedSample = 0.0;
			numAccumulatedSamples = 0.0;
		}
	}

	public int process(Buffer inputBuffer, Buffer outputBuffer) {
		// if (TRACE) dump("input ", inputBuffer);

		if (!checkInputBuffer(inputBuffer)) {
			return BUFFER_PROCESSED_FAILED;
		}

		if (isEOM(inputBuffer)) {
			propagateEOM(outputBuffer); // TODO: what about data? can there be
										// any?
			return BUFFER_PROCESSED_OK;
		}

		// TODO: check in checkInputBuffer?
		if (!inputBuffer.getFormat().equals(inputFormat)) {
			throw new RuntimeException(
					"Expected inputBuffer.getFormat().equals(inputFormat): ["
							+ inputBuffer.getFormat() + "] [" + inputFormat
							+ "]");
		}
		final AudioFormat inputAudioFormat = ((AudioFormat) inputBuffer
				.getFormat());
		final AudioFormat outputAudioFormat = (AudioFormat) outputFormat;
		final boolean sampleRateChanged = inputAudioFormat.getSampleRate() != outputAudioFormat
				.getSampleRate();
		final boolean sampleSizeInBitsChanged = inputAudioFormat
				.getSampleSizeInBits() != outputAudioFormat
				.getSampleSizeInBits();
		final boolean endianChanged = (outputAudioFormat.getSampleSizeInBits() > 8 && inputAudioFormat
				.getSampleSizeInBits() > 8)
				&& // only consider endian changed if neither of the formats is
					// 8 bits
				inputAudioFormat.getEndian() != outputAudioFormat.getEndian();
		final boolean signedChanged = inputAudioFormat.getSigned() != outputAudioFormat
				.getSigned();
		final boolean channelsChanged = inputAudioFormat.getChannels() != outputAudioFormat
				.getChannels();

		// System.out.println("RateConvert \n\t\t from: " + inputFormat +
		// "\n\t\t to:" + outputFormat);

		int numChanged = 0;
		if (sampleRateChanged)
			++numChanged;
		if (sampleSizeInBitsChanged)
			++numChanged;
		if (endianChanged)
			++numChanged;
		if (signedChanged)
			++numChanged;
		if (channelsChanged)
			++numChanged;

		if (ONLY_CHANGE_1_PARAMETER && numChanged > 1) {
			// in this case, we only support doing one thing in a particular
			// instance of this codec.
			// this keeps it simple. Converting multiple format attributes is
			// achieved then by chaining multiple instances
			// of this codec, with different input and output formats.
			logger.warning("Input format:  " + inputAudioFormat);
			logger.warning("Output format: " + outputAudioFormat);

			throw new RuntimeException(
					"Expected RateConverter to change no more than one attribute: "
							+ numChanged + "; sampleRateChanged="
							+ sampleRateChanged + " sampleSizeInBitsChanged="
							+ sampleSizeInBitsChanged + " endianChanged="
							+ endianChanged + " signedChanged=" + signedChanged);
		}

		// TODO: check these in setInputFormat and setOutputFormat
		if (outputAudioFormat.getSampleSizeInBits() % 8 != 0)
			throw new RuntimeException(
					"RateConverter only supports output sample sizes that are a multiple of 8: "
							+ outputAudioFormat.getSampleSizeInBits());
		if (inputAudioFormat.getSampleSizeInBits() % 8 != 0)
			throw new RuntimeException(
					"RateConverter only supports input sample sizes that are a multiple of 8: "
							+ inputAudioFormat.getSampleSizeInBits());

		if (inputAudioFormat.getSampleSizeInBits() > 8
				&& inputAudioFormat.getEndian() == -1)
			throw new RuntimeException(
					"RateConverter (input format) requires endian to be specified if sample size in bits is greater than 8");
		if (outputAudioFormat.getSampleSizeInBits() > 8
				&& outputAudioFormat.getEndian() == -1)
			throw new RuntimeException(
					"RateConverter (output format) requires endian to be specified if sample size in bits is greater than 8");

		// if (inputAudioFormat.getFrameSizeInBits() !=
		// inputAudioFormat.getChannels() *
		// inputAudioFormat.getSampleSizeInBits())
		// throw new
		// RuntimeException("Expected (input format) frame size in bits to be sample size in bits * number of channels");
		// if (outputAudioFormat.getFrameSizeInBits() !=
		// outputAudioFormat.getChannels() *
		// outputAudioFormat.getSampleSizeInBits())
		// throw new
		// RuntimeException("Expected (output format) frame size in bits to be sample size in bits * number of channels");

		// output : input
		final double sampleRateRatio = outputAudioFormat.getSampleRate()
				/ inputAudioFormat.getSampleRate();
		final int sampleRateRatioWhole = (int) sampleRateRatio;
		final double sampleRateRatioRemainder = sampleRateRatio
				- sampleRateRatioWhole; // TODO: this is most certainly wrong.
		// input : output
		final double sampleRateInverseRatio = inputAudioFormat.getSampleRate()
				/ outputAudioFormat.getSampleRate();
		final int sampleRateInverseRatioWhole = (int) sampleRateInverseRatio;
		final int sampleRateInverseRatioCeil = (int) Math
				.ceil(sampleRateInverseRatio);
		// sampleRateAveragingErrorIncrement is the amount that our sample rate
		// error increases with each input sample.
		// say input sample rate is 22050 and output is 8000. Then this is
		// 2.76:1.
		// without compensation, we will output a sample for every 3 input
		// samples. So we need to output an extra sample
		// every so often to bring the output to 2.76 for every 3. So 3-2.76 is
		// the error in the "input" side. To
		// convert this to an error in the "output" side, we have to divide by
		// the ratio, or 2.76 (helps to draw triangles to visualize this). So
		// the increment is (3-2.76)/2.76.
		final double sampleRateAveragingErrorIncrement = (sampleRateInverseRatioCeil - sampleRateInverseRatio)
				/ sampleRateInverseRatio;

		final int inputSampleSizeInBytes = inputAudioFormat
				.getSampleSizeInBits() / 8;
		final int inputSamples = inputBuffer.getLength()
				/ inputSampleSizeInBytes; // this implies that we will only
											// process whole samples in the
											// input buffer.
		final int outputSampleSizeInBytes = outputAudioFormat
				.getSampleSizeInBits() / 8;

		int requiredOutputBufferLength;
		if (sampleRateChanged) {
			// allocate 1 extra output sample, for rounding errors. The actual
			// number of bytes put in the buffer will be correct, based on
			// outputSampleIndex * outputSampleSizeInBytes
			requiredOutputBufferLength = (int) ((inputSamples * outputSampleSizeInBytes) * Math
					.ceil(sampleRateRatio)); // TODO: rounds way up
			// TODO: what if this truncates some samples?
		} else if (sampleSizeInBitsChanged) { // we only support changing of
												// sample sizes
			requiredOutputBufferLength = inputSamples * outputSampleSizeInBytes;
			// TODO: what if this truncates some samples?
		} else
			requiredOutputBufferLength = inputBuffer.getLength(); // same number
																	// of bytes
																	// output as
																	// input

		boolean stereoToMono = false;
		boolean monoToStereo = false;
		int outputChannelRepeatCount = 1; // equal to 2 only if monoToStereo is
											// true.
		if (channelsChanged) {
			if (inputAudioFormat.getChannels() == 2
					&& outputAudioFormat.getChannels() == 1)
				stereoToMono = true;
			else if (inputAudioFormat.getChannels() == 1
					&& outputAudioFormat.getChannels() == 2) {
				outputChannelRepeatCount = 2;
				monoToStereo = true;
			} else
				throw new RuntimeException("Unsupported number of channels"); // TODO:
																				// check
																				// in
																				// setInputFormat/setOutputFormat
		}

		if (stereoToMono)
			requiredOutputBufferLength /= 2;
		else if (monoToStereo)
			requiredOutputBufferLength *= 2;

		// get/allocate output buffer:
		byte[] outputBufferData = (byte[]) outputBuffer.getData();
		if (outputBufferData == null
				|| outputBufferData.length < requiredOutputBufferLength) {
			outputBufferData = new byte[requiredOutputBufferLength];
			outputBuffer.setData(outputBufferData);
		}

		final byte[] inputBufferData = (byte[]) inputBuffer.getData();

		// so for example if getSampleSizeInBits is 8, this value is 128.
		final long outputSignedUnsignedDifference = 1 << (outputAudioFormat
				.getSampleSizeInBits() - 1);
		// so for example if getSampleSizeInBits is 8, this value is 255.
		final long outputUnsignedMax = (1 << outputAudioFormat
				.getSampleSizeInBits()) - 1;

		final long inputSignedUnsignedDifference = 1 << (inputAudioFormat
				.getSampleSizeInBits() - 1);

		// if getSampleSizeInBits is 8, this value is 255:
		final long inputUnsignedMax = (1 << inputAudioFormat
				.getSampleSizeInBits()) - 1;
		// if getSampleSizeInBits is 8, this value is 127:
		final long inputSignedMax = (1 << (inputAudioFormat
				.getSampleSizeInBits() - 1)) - 1;
		// if getSampleSizeInBits is 8, this value is -128:
		final long inputSignedMin = (inputSignedMax + 1) * -1;

		double accumulatedRateChangeError = 0.0; // because in the case of
													// non-integral ratios of
													// sample rate change, we
													// have to
		// output samples occasionally to maintain the correct "slope". This is
		// much
		// like drawing a diagonal line on the screen.
		int outputSampleIndex = 0; // index of next sample in output buffer
		for (int i = 0; i < inputSamples; ++i) {
			if (stereoToMono && i % 2 == 1)
				continue; // for now, just omit one channel. TODO: average.

			// get the ith sample, converted to a long, taking into account
			// sample size in bits, endian-ness, and signed/unsigned.
			final int byteOffsetOfSample = inputBuffer.getOffset() + i
					* inputSampleSizeInBytes;

			final int inputSampleLiteral = getSample(inputBufferData,
					byteOffsetOfSample, inputSampleSizeInBytes,
					inputAudioFormat.getEndian());
			final long inputSampleLongWithoutSign = UnsignedUtils
					.uIntToLong(inputSampleLiteral);
			final long inputSampleLongWithSign;
			if (inputAudioFormat.getSigned() == AudioFormat.UNSIGNED)
				inputSampleLongWithSign = inputSampleLongWithoutSign;
			else if (inputAudioFormat.getSigned() == AudioFormat.SIGNED) {
				if (inputSampleLongWithoutSign > inputSignedMax)
					inputSampleLongWithSign = inputSampleLongWithoutSign
							- inputUnsignedMax - 1;
				else
					inputSampleLongWithSign = inputSampleLongWithoutSign;
			} else
				throw new RuntimeException("input format signed not specified");

			// inputSample is now the literal binary value of the sample (0s in
			// unused MSBs), while inputSampleLong is now the literal numeric
			// value of the sample (reflecting sign if applicable).

			// now perform some conversions to get the desired output sample:
			final long outputSampleLongWithSign;

			// apply sign change difference if needed:
			if (outputAudioFormat.getSigned() == AudioFormat.SIGNED
					&& inputAudioFormat.getSigned() == AudioFormat.UNSIGNED)
				outputSampleLongWithSign = inputSampleLongWithSign
						- inputSignedUnsignedDifference;
			else if (outputAudioFormat.getSigned() == AudioFormat.UNSIGNED
					&& inputAudioFormat.getSigned() == AudioFormat.SIGNED)
				outputSampleLongWithSign = inputSampleLongWithSign
						+ inputSignedUnsignedDifference;
			else
				outputSampleLongWithSign = inputSampleLongWithSign;

			// now we have to deal with sample rate issues. we either have more
			// or less samples. If we have more in the output, then
			// we repeat the input (a better solution would be to smooth). if we
			// have less, we average.
			if (sampleRateRatio == 1.0) { // no sample rate change: output a
											// single sample for each input
											// sample
				final long outputSampleLongWithoutSign = getOutputSampleLongWithoutSign(
						outputSampleLongWithSign, inputUnsignedMax,
						inputAudioFormat, outputAudioFormat);
				for (int c = 0; c < outputChannelRepeatCount; ++c)
					putSample(outputSampleLongWithoutSign, outputBufferData,
							(outputSampleIndex++) * outputSampleSizeInBytes,
							outputSampleSizeInBytes, outputAudioFormat
									.getEndian());

			} else if (sampleRateRatio > 1.0) {
				// more output than input - repeat samples

				// figure out which channel we are outputting to, so we know
				// which Averager to use:
				final int outputChannel;
				if (outputAudioFormat.getChannels() == 1)
					outputChannel = 0;
				else
					outputChannel = i % 2; // this requires that a buffer always
											// has a complete frame, that is,
											// the buffer always starts with
											// channel 0.
				final Averager a = averagers[outputChannel];

				// the input sample can be divided into 2 parts:
				// 1. the part that will be output as part of the current output
				// samples (combined with whatever has been accumulated)
				// 2. the rest, which will be accumulated for the next time.

				// the averager is used as follows:
				// sampleWeight is expressed as a fraction of the input sample
				// size.

				final double[] sampleWeights; // TODO: don't allocate array each
												// time.
				if ((a.numAccumulatedSamples + 1) > 1.0) { // we are going to be
															// averaging 2 input
															// samples for the
															// output sample(s)
					final double sampleWeight = sampleRateInverseRatio
							- a.numAccumulatedSamples;
					final double sampleWeight2 = 1.0 - sampleWeight;
					sampleWeights = new double[] { sampleWeight, sampleWeight2 };
				} else {
					final double sampleWeight = 1.0;
					sampleWeights = new double[] { sampleWeight };
				}

				for (double sampleWeight : sampleWeights) {
					a.accumulatedSample += outputSampleLongWithSign
							* sampleWeight;
					a.numAccumulatedSamples += sampleWeight;

					final int repeatCount = (int) (a.numAccumulatedSamples * sampleRateRatio);
					if (repeatCount > 0) // this will always be > 0, because
											// sampleRateRatio > 1.0
					{
						final long outputSampleLongWithSignAvg = (long) Math
								.round(a.accumulatedSample
										/ a.numAccumulatedSamples);

						final long outputSampleLongWithoutSign = getOutputSampleLongWithoutSign(
								outputSampleLongWithSignAvg, inputUnsignedMax,
								inputAudioFormat, outputAudioFormat);

						for (int j = 0; j < repeatCount; ++j) {
							for (int c = 0; c < outputChannelRepeatCount; ++c)
								putSample(outputSampleLongWithoutSign,
										outputBufferData, (outputSampleIndex++)
												* outputSampleSizeInBytes,
										outputSampleSizeInBytes,
										outputAudioFormat.getEndian());

							final double oldAccumulatedSamples = a.numAccumulatedSamples;
							// TODO: this introduces small errors, subtracting
							// each time
							a.numAccumulatedSamples -= sampleRateInverseRatio;
							a.accumulatedSample = a.accumulatedSample
									* a.numAccumulatedSamples
									/ oldAccumulatedSamples;
						}
					}
				}

			} else if (sampleRateInverseRatio > 1.0) {
				// more input than output - average samples
				// figure out which channel we are outputting to, so we know
				// which Averager to use:
				final int outputChannel;
				if (outputAudioFormat.getChannels() == 1)
					outputChannel = 0;
				else
					outputChannel = i % 2; // this requires that a buffer always
											// has a complete frame, that is,
											// the buffer always starts with
											// channel 0.
				final Averager a = averagers[outputChannel];

				final double sampleWeight;
				if ((a.numAccumulatedSamples + 1) * sampleRateRatio > 1.0) // if
																			// we
																			// will
																			// output
																			// a
																			// sample,
																			// and
																			// another
																			// full
																			// sample
																			// would
																			// spill
																			// over
																			// into
																			// the
																			// output
																			// sample
																			// after
																			// this
																			// one
					sampleWeight = sampleRateInverseRatio
							- a.numAccumulatedSamples; // on a scale from 0 to
														// 1, how much of the
														// new input sample
														// overlaps the output
														// sample
				else
					sampleWeight = 1.0;
				a.accumulatedSample += outputSampleLongWithSign * sampleWeight;
				a.numAccumulatedSamples += sampleWeight;

				final boolean doOutput = a.numAccumulatedSamples
						* sampleRateRatio >= 1.0;
				if (doOutput) {
					final long outputSampleLongWithSignAvg = (long) Math
							.round(a.accumulatedSample
									/ a.numAccumulatedSamples);
					a.accumulatedSample = outputSampleLongWithSignAvg
							* (1.0 - sampleWeight); // un-averaged part of
													// current sample
					a.numAccumulatedSamples = (1.0 - sampleWeight);
					final long outputSampleLongWithoutSign = getOutputSampleLongWithoutSign(
							outputSampleLongWithSignAvg, inputUnsignedMax,
							inputAudioFormat, outputAudioFormat);
					for (int c = 0; c < outputChannelRepeatCount; ++c)
						putSample(outputSampleLongWithoutSign,
								outputBufferData, (outputSampleIndex++)
										* outputSampleSizeInBytes,
								outputSampleSizeInBytes, outputAudioFormat
										.getEndian());

				}

			}

		}

		outputBuffer.setLength(outputSampleIndex * outputSampleSizeInBytes);
		outputBuffer.setOffset(0);
		outputBuffer.setFormat(outputFormat);

		final int result = BUFFER_PROCESSED_OK;

		if (TRACE) {
			dump("input ", inputBuffer);
			dump("output", outputBuffer);

			System.out.println("Result="
					+ MediaCGUtils.plugInResultToStr(result));
		}
		return result;
	}

	private double fractional(double d) {
		return d - (Math.floor(d));
	}

	private static long getOutputSampleLongWithoutSign(
			long outputSampleLongWithSign, long inputUnsignedMax,
			AudioFormat inputAudioFormat, AudioFormat outputAudioFormat) {
		// here we want -1 to become 255 for an 8-bit value.
		long outputSampleLongWithoutSign;
		if (outputSampleLongWithSign >= 0)
			outputSampleLongWithoutSign = outputSampleLongWithSign;
		else
			outputSampleLongWithoutSign = inputUnsignedMax + 1
					+ outputSampleLongWithSign;

		return getOutputSampleLongWithoutSign(outputSampleLongWithoutSign,
				inputAudioFormat, outputAudioFormat);

	}

	private static long getOutputSampleLongWithoutSign(
			long outputSampleLongWithoutSign, AudioFormat inputAudioFormat,
			AudioFormat outputAudioFormat) {
		// do calculation with unsigned long, so that sign bits are not shifted
		// in.
		// apply sample size (truncates, does not round, when going to smaller
		// sample size)
		if (outputAudioFormat.getSampleSizeInBits() > inputAudioFormat
				.getSampleSizeInBits()) {
			outputSampleLongWithoutSign <<= (outputAudioFormat
					.getSampleSizeInBits() - inputAudioFormat
					.getSampleSizeInBits());
		} else if (inputAudioFormat.getSampleSizeInBits() > outputAudioFormat
				.getSampleSizeInBits()) {
			outputSampleLongWithoutSign >>= (inputAudioFormat
					.getSampleSizeInBits() - outputAudioFormat
					.getSampleSizeInBits());
		}
		return outputSampleLongWithoutSign;
	}

	/**
	 * bit-wise literal. Is in general unsigned, but may be signed if all 32
	 * bits are used.
	 */
	private static int getSample(byte[] inputBufferData,
			int byteOffsetOfSample, int inputSampleSizeInBytes, int inputEndian) {
		int sample = 0;
		for (int j = 0; j < inputSampleSizeInBytes; ++j) {
			// offset within sample handles endian-ness:
			final int offsetWithinSample = inputEndian == AudioFormat.BIG_ENDIAN ? j
					: (inputSampleSizeInBytes - 1 - j);
			final byte b = inputBufferData[byteOffsetOfSample
					+ offsetWithinSample];
			sample <<= 8;
			sample |= b & 0xff;
		}
		// handle signed-ness.
		return sample;
	}

	private static void putSample(long sampleLong, byte[] inputBufferData,
			int byteOffsetOfSample, int outputSampleSizeInBytes,
			int outputEndian) {
		// handle signed-ness.
		int sample = (int) sampleLong;
		for (int j = 0; j < outputSampleSizeInBytes; ++j) {
			// offset within sample handles endian-ness:
			final int offsetWithinSample = outputEndian == AudioFormat.LITTLE_ENDIAN ? j
					: (outputSampleSizeInBytes - 1 - j);
			final byte b = (byte) ((sample >> (8 * j)) & 0xff);
			try {
				inputBufferData[byteOffsetOfSample + offsetWithinSample] = b;
			} catch (ArrayIndexOutOfBoundsException e) {
				throw e;
			}
		}
	}

	public Format setInputFormat(Format arg0) {
		// TODO: force sample size, etc
		if (TRACE)
			System.out.println("setInputFormat: "
					+ MediaCGUtils.formatToStr(arg0));
		return super.setInputFormat(arg0);
	}

	public Format setOutputFormat(Format arg0) {
		if (TRACE)
			System.out.println("setOutputFormat: "
					+ MediaCGUtils.formatToStr(arg0));
		return super.setOutputFormat(arg0);
	}

}

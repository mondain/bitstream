package javax.media;

/**
 * 
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface Codec extends PlugIn {
	public Format[] getSupportedInputFormats();

	public Format[] getSupportedOutputFormats(Format input);

	public int process(Buffer input, Buffer output);

	public Format setInputFormat(Format format);

	public Format setOutputFormat(Format format);
}

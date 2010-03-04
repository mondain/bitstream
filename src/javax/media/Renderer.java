package javax.media;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface Renderer extends PlugIn {
	public Format[] getSupportedInputFormats();

	public Format setInputFormat(Format format);

	public void start();

	public void stop();

	public int process(Buffer buffer);
}

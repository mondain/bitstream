package javax.media;

/**
 * 
 * Complete. Note: JMF will call close if open() fails. Observed on a
 * Demultiplexer.
 * 
 * @author Ken Larson
 * 
 */
public interface PlugIn extends Controls {
	public static int BUFFER_PROCESSED_OK = 0;
	public static int BUFFER_PROCESSED_FAILED = 1;
	public static int INPUT_BUFFER_NOT_CONSUMED = 2;
	public static int OUTPUT_BUFFER_NOT_FILLED = 4;
	public static int PLUGIN_TERMINATED = 8;

	public void open() throws ResourceUnavailableException;

	public void close();

	public String getName();

	public void reset();

}

package javax.media.protocol;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface RateConfigureable {
	public RateConfiguration[] getRateConfigurations();

	public RateConfiguration setRateConfiguration(RateConfiguration config);
}

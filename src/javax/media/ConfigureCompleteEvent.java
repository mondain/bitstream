package javax.media;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class ConfigureCompleteEvent extends TransitionEvent {

	public ConfigureCompleteEvent(Controller processor, int previous,
			int current, int target) {
		super(processor, previous, current, target);
	}
}

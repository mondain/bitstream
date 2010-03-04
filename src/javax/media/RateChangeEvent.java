package javax.media;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class RateChangeEvent extends ControllerEvent {

	float rate;

	public RateChangeEvent(Controller from, float newRate) {
		super(from);
		this.rate = newRate;
	}

	public float getRate() {
		return rate;
	}

	public String toString() {
		return getClass().getName() + "[source=" + getSource() + ",rate="
				+ rate + "]";
	}
}

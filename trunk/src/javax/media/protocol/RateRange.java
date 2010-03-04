package javax.media.protocol;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class RateRange implements java.io.Serializable {

	private float value;
	private float min;
	private float max;
	private boolean exact;

	public RateRange(RateRange r) {
		this(r.value, r.min, r.max, r.exact);
	}

	public RateRange(float init, float min, float max, boolean isExact) {
		this.value = init;
		this.min = min;
		this.max = max;
		this.exact = isExact;
	}

	public float setCurrentRate(float rate) {
		// do not enforce min/max
		this.value = rate;

		return this.value;
	}

	public float getCurrentRate() {
		return value;
	}

	public float getMinimumRate() {
		return min;
	}

	public float getMaximumRate() {
		return max;
	}

	public boolean inRange(float rate) {
		if (true)
			throw new UnsupportedOperationException(); // TODO
		return rate >= min && rate <= max; // TODO: boundaries?
	}

	public boolean isExact() {
		return exact;
	}
}

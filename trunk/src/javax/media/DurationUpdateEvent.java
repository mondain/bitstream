package javax.media;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class DurationUpdateEvent extends ControllerEvent {

	Time duration;

	public DurationUpdateEvent(Controller from, Time newDuration) {
		super(from);
		this.duration = newDuration;
	}

	public Time getDuration() {
		return duration;
	}

	public String toString() {
		return getClass().getName() + "[source=" + getSource() + ",duration="
				+ duration + "]";

	}
}

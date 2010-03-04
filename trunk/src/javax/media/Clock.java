package javax.media;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface Clock {

	public static final Time RESET = new Time(Long.MAX_VALUE);

	public void setTimeBase(TimeBase master)
			throws IncompatibleTimeBaseException;

	public void syncStart(Time at);

	public void stop();

	public void setStopTime(Time stopTime);

	public Time getStopTime();

	public void setMediaTime(Time now);

	public Time getMediaTime();

	public long getMediaNanoseconds();

	public Time getSyncTime();

	public TimeBase getTimeBase();

	public Time mapToTimeBase(Time t) throws ClockStoppedException;

	public float getRate();

	public float setRate(float factor);
}

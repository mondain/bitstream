package javax.media.protocol;

import javax.media.Time;

/**
 * 
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface Positionable {
	public static final int RoundUp = 1;

	public static final int RoundDown = 2;

	public static final int RoundNearest = 3;

	public Time setPosition(Time where, int rounding);

	public boolean isRandomAccess();
}

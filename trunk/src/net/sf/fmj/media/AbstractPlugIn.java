package net.sf.fmj.media;

import javax.media.PlugIn;
import javax.media.ResourceUnavailableException;

import net.sf.fmj.utility.ClassUtils;

/**
 * Abstract implementation of PlugIn, useful for subclassing.
 * 
 * @author Ken Larson
 * 
 */
public abstract class AbstractPlugIn extends AbstractControls implements PlugIn {

	private boolean opened = false;

	public void close() {
		opened = false;
	}

	public String getName() {
		return ClassUtils.getShortClassName(getClass()); // override to provide
															// a better name
	}

	public void open() throws ResourceUnavailableException {
		opened = true;
	}

	public void reset() { // TODO
	}

}

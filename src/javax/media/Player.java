package javax.media;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface Player extends MediaHandler, Controller {

	public java.awt.Component getVisualComponent();

	public GainControl getGainControl();

	public java.awt.Component getControlPanelComponent();

	public void start();

	public void addController(Controller newController)
			throws IncompatibleTimeBaseException;

	public void removeController(Controller oldController);
}

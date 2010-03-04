package javax.media.renderer;

import javax.media.Renderer;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface VideoRenderer extends Renderer {
	public java.awt.Component getComponent();

	public boolean setComponent(java.awt.Component comp);

	public void setBounds(java.awt.Rectangle rect);

	public java.awt.Rectangle getBounds();
}

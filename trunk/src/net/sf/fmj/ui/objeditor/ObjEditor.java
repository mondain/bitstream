package net.sf.fmj.ui.objeditor;

import java.awt.Component;

/**
 * Generic interface for a control which edits an object.
 * 
 * @author Ken Larson
 * 
 */
public interface ObjEditor {

	public void setObjectAndUpdateControl(Object o);

	public Object getObject();

	public boolean validateAndUpdateObj();

	public Component getComponent();

}

package net.sf.fmj.ui.utils;

import java.awt.Component;

import javax.swing.JOptionPane;

/**
 * 
 * @author Ken Larson
 * 
 */
public class ErrorDialog {

	public static void showError(Component c, Throwable e) {
		showError(c, "" + e);
	}

	public static void showError(Component c, String e) {
		JOptionPane.showMessageDialog(c, e, "Error",
				JOptionPane.WARNING_MESSAGE);
	}
}

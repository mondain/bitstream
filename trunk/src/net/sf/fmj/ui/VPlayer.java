package net.sf.fmj.ui;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JSlider;


public class VPlayer {

	private JFrame frame = new JFrame();
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JSlider jSlider = null;

	/**
	 * This is the default constructor
	 */
	public VPlayer() {
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		frame.setSize(300, 200);
		frame.setContentPane(getJContentPane());
		frame.setTitle("JFrame");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			//jContentPane.add(new PlayerPanel());
			jContentPane.add(getJSlider(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jSlider	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	private JSlider getJSlider() {
		if (jSlider == null) {
			jSlider = new JSlider();
		}
		return jSlider;
	}

	public static void main(String[] args) {
			VPlayer main = new VPlayer();
	}
	
}

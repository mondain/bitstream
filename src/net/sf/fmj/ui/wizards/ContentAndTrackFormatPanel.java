/*
 * ContentAndTrackFormatPanel.java
 *
 * Created on June 12, 2007, 4:35 PM
 */

package net.sf.fmj.ui.wizards;

import java.util.HashMap;
import java.util.Map;

import javax.media.format.AudioFormat;

/**
 * 
 * @author Ken Larson
 */
public class ContentAndTrackFormatPanel extends javax.swing.JPanel {

	private Map trackControlPanels = new HashMap(); // of
													// Integer->TrackControlPanel

	/** Creates new form ContentAndTrackFormatPanel */
	public ContentAndTrackFormatPanel() {
		initComponents();

		audioTrackControlPanel = new TrackControlPanel();
		tabbedPane.addTab("Audio", audioTrackControlPanel);

		audioFormatPanel = new AudioFormatPanel();
		audioTrackControlPanel.setAudioFormatPanel(audioFormatPanel);

	}

	private TrackControlPanel audioTrackControlPanel;
	private AudioFormatPanel audioFormatPanel;

	public void addTrack(int track, boolean enabled, AudioFormat f) {

		audioFormatPanel.setAudioFormat(f);

		audioTrackControlPanel.getCheckBoxEnableTrack().setSelected(enabled);

		trackControlPanels.put(new Integer(track), audioTrackControlPanel);

	}

	public TrackControlPanel getTrackControlPanel(int track) {
		return (TrackControlPanel) trackControlPanels.get(new Integer(track));
	}

	public TrackControlPanel getAudioTrackControlPanel() {
		return audioTrackControlPanel;
	}

	public AudioFormatPanel getAudioFormatPanel() {
		return audioFormatPanel;
	}

	public javax.swing.JComboBox getComboFormat() {
		return comboFormat;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed"
	// desc=" Generated Code ">//GEN-BEGIN:initComponents
	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		labelInstructions = new javax.swing.JLabel();
		labelFormat = new javax.swing.JLabel();
		comboFormat = new javax.swing.JComboBox();
		tabbedPane = new javax.swing.JTabbedPane();

		setLayout(new java.awt.GridBagLayout());

		labelInstructions
				.setText("Specify the content type and parameters for output:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
		add(labelInstructions, gridBagConstraints);

		labelFormat.setText("Format:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
		add(labelFormat, gridBagConstraints);

		comboFormat.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] {}));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
		add(comboFormat, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
		add(tabbedPane, gridBagConstraints);

	}// </editor-fold>//GEN-END:initComponents

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JComboBox comboFormat;
	private javax.swing.JLabel labelFormat;
	private javax.swing.JLabel labelInstructions;
	private javax.swing.JTabbedPane tabbedPane;
	// End of variables declaration//GEN-END:variables

}

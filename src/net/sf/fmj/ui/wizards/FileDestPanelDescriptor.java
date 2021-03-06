/*
 * FileDestPanelDescriptor.java
 *
 * Created on June 20, 2007, 12:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.fmj.ui.wizards;

import java.io.File;
import java.util.logging.Logger;
import net.sf.fmj.ui.wizard.WizardPanelDescriptor;
import net.sf.fmj.utility.LoggerSingleton;
import net.sf.fmj.utility.URLUtils;

/**
 * 
 * @author Ken Larson
 */
public class FileDestPanelDescriptor extends WizardPanelDescriptor {

	private static final Logger logger = LoggerSingleton.logger;

	public static final String IDENTIFIER = FileDestPanelDescriptor.class
			.getName();

	private final TranscodeWizardResult result;
	private final TranscodeWizardConfig config;

	public FileDestPanelDescriptor(final TranscodeWizardConfig config,
			TranscodeWizardResult result) {
		super(IDENTIFIER, new FileDestPanel());
		this.config = config;
		this.result = result;
	}

	public Object getNextPanelDescriptor() {
		return FINISH;
	}

	public Object getBackPanelDescriptor() {
		return ContentAndTrackFormatPanelDescriptor.IDENTIFIER;
	}

	public FileDestPanel getFileDestPanel() {
		return (FileDestPanel) getPanelComponent();
	}

	public boolean aboutToDisplayPanel(Object prevId) {
		if (prevId == getBackPanelDescriptor()) {
			if (config.destUrl != null)
				getFileDestPanel()
						.getTextFile()
						.setText(
								URLUtils
										.extractValidNewFilePathFromFileUrl(config.destUrl));

			return true;
		}
		return super.aboutToDisplayPanel(prevId);
	}

	public boolean aboutToHidePanel(Object idOfNext) {
		if (idOfNext == getNextPanelDescriptor()) { // forward transition

			String path = getFileDestPanel().getTextFile().getText();
			if (path == null || path.equals("")) {
				showError("Destination file path may not be blank");
				return false;
			}

			try {
				config.destUrl = URLUtils.createUrlStr(new File(path));
				result.step4_setDestUrlAndStart(config);
			} catch (WizardStepException e1) {
				showError(e1);
				return false;
			}

			return true;
		} else {
			return super.aboutToHidePanel(idOfNext);
		}
	}
}
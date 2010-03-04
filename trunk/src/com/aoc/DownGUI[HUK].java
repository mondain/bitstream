package com.aoc;

import jBittorrentAPI.ExampleDownloadFiles;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Date;

import javax.swing.Timer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.internal.win32.DLLVERSIONINFO;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DownGUI extends SelectionAdapter {

	private Shell shell = null;
	private Text tField = null;
	private Button okButton = null;

	// public Thread update = null;
	public static boolean shown = false;

	public DownGUI(Shell parent, String label) {
		shell = new Shell(parent);
		shell.setText(label);
		shell.setLocation(parent.getLocation().x + 100,
				parent.getLocation().y + 150);
		shell.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				shown = false;
				shell.dispose();
			}

		});
		shell.setLayout(new RowLayout(SWT.HORIZONTAL));
		tField = new Text(shell, SWT.BORDER | SWT.SINGLE);
		FontData fd = tField.getFont().getFontData()[0];
		fd.setHeight(Constants.TEXT_HEIGHT);
		Font font = new Font(shell.getDisplay(), fd);
		tField.setFont(font);
		tField.setToolTipText("Enter URL of the torrent");
		tField.setLayoutData(new RowData(400, 21));

		Button bButton = new Button(shell, SWT.NONE);
		bButton.setText("Browse");
		bButton.setLayoutData(new RowData(70, SWT.DEFAULT));
		bButton.addSelectionListener(this);

		okButton = new Button(shell, SWT.NONE);
		okButton.setText("OK");
		okButton.setLayoutData(new RowData(50, SWT.DEFAULT));
		okButton.addSelectionListener(this);

		shell.pack();
	}

	public void show() {
		if (!shown) {
			shown = true;
			shell.open();
			Display display = Main.getInstance().getDisplay();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} else {
			return;
		}
	}

	public String fileBrowse() {
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setText("Browse for a File");
		dialog.setFilterPath(History.getInstance().getLastDownDir());
		dialog.setFilterExtensions(new String[] { "*.torrent", "*.*" });
		dialog.setFilterNames(new String[] { "Torrent Files (*.torrent)",
		"All Files (*.*)" });
		return dialog.open();
	}

	boolean exit = false;
	ExampleDownloadFiles edf = new ExampleDownloadFiles();

	@Override
	public void widgetSelected(SelectionEvent se) {
		// TODO Auto-generated method stub
		if (((Button) se.widget).getText().equals("OK")) {
			downloadFiles();
		} else if (((Button) se.widget).getText().equals("Browse")) {
			String path = fileBrowse();
			if (path == null) {
				return;
			}

			try {
				// retain selected directory
				File f = new File(path);
				History.getInstance().setLastDownDir(f.getParent());
			} catch (Exception ioe) {
				History.getInstance().setLastDownDir(
						System.getProperty("user.home"));
			}
			tField.setText(path);
		}
	}

	int complete;
	public void downloadFiles() {
		final String path = tField.getText();
		if (!(path.length() > 0)) {
			return;
		}
		// TODO: validate path here
		System.out.println(path);
		final Main m = Main.getInstance();
		shell.dispose();
		final Download d = new Download(path, new Date());
		m.getDownloadTable().addDownload(d);
		m.getDownloadTable().addToTable(d);

		Thread download = new Thread(new Runnable() {

			@Override
			public void run() {
				System.out.println("download called");
				// download the file
				String to = PrefGUI.downloadTo;
				if (to.charAt(to.length() - 1) != '\\') {
					to += "\\";
				}
				d.setDownloadTo(to);
				String[] params = new String[] { path, to };
				edf.DownloadFiles(params);
				d.setEDF(edf);
				exit = true;
			}
		});
		download.start();

		complete = 0;
		Runnable update = new Runnable() {

			@Override
			public void run() {
				if (edf == null) {}
				else if (edf.dm != null) {
					complete = (int) edf.getCompleted();
					System.out.println(edf.getCompleted());
					d.setProgress(complete);
					d.setSize(edf.dm.torrent.total_length / (1024 * 1024));
					d.setDownloaded(edf.dm.getTotal());
					d.setFileNames(edf.dm.torrent.name);
					m.getDownloadTable().updateTable(d);
					if(complete < 100) {
						m.getDisplay().timerExec(100, this);
					}
				}

			}
		};

		m.getDisplay().timerExec(100, update);

		/*

				int complete = 0;
				while (complete < 100 && !exit) {
					if (edf == null) {
						continue;
					}
					if (edf.dm != null) {
						if (edf.dm.getComplete()) {
							complete = 100;
							d.updatePBar(100);
							d.setDownloaded(100);
							d.setSize(edf.dm.torrent.total_length
									/ (1024 * 1024));
							d.setFileNames(edf.dm.torrent.name);
							m.getDownloadTable().updateTable(d);
							break;
						}
						complete = (int) edf.getCompleted();
						d.setProgress(complete);
						d.setSize(edf.dm.torrent.total_length / (1024 * 1024));
						d.setDownloaded(edf.dm.getTotal());
						d.setFileNames(edf.dm.torrent.name);
						percentage = complete;
						m.getDownloadTable().updateTable(d);
					}
				}


			}
		};

		m.getDisplay().asyncExec(download);
		 */
	}
}

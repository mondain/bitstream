package com.aoc;

import jBittorrentAPI.ExampleDownloadFiles;

import java.io.File;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
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

		Label l1 = new Label(shell, SWT.None);
		l1.setAlignment(SWT.CENTER);
		l1.setLayoutData(new RowData(80, 27));
		l1.setText("Torrent File: ");

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

	ExampleDownloadFiles edf = new ExampleDownloadFiles();

	@Override
	public void widgetSelected(SelectionEvent se) {
		// TODO Auto-generated method stub
		if (((Button) se.widget).getText().equals("OK")) {
			final String path = tField.getText();
			if (!(path.length() > 0)) {
				return;
			}
			// TODO: validate path here
			System.out.println(path);
			Thread run = new Thread(new Runnable() {

				@Override
				public void run() {
					downloadFiles(path);
				}
			});
			shell.dispose();
			Main.getInstance().getDisplay().timerExec(100, run);
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

	public void downloadFiles(final String path) {
		final Main m = Main.getInstance();
		final Download d = new Download(path, new Date());
		m.getDownloadTable().addToTable(d);
		m.getDownloadTable().addDownload(d);
		Thread download = new Thread(new Runnable() {

			@Override
			public void run() {
				// download the file
				String to = PrefGUI.downloadTo;
				if (to.charAt(to.length() - 1) != '\\') {
					to += "\\";
				}
				d.setEDF(edf);
				d.setDownloadTo(to);
				String[] params = new String[] { path, to };
				edf.DownloadFiles(params);
				d.setStopped(true);
			}
		});
		download.start();
		final Thread update = new Thread() {
			@Override
			public void run() {
				int complete = 0;
				while (complete < 100 && !d.isStopped()) {
					if(d.isStopped()) {
						interrupt();
					}
					if (edf == null) {
						continue;
					}
					if (edf.dm != null) {
						if (edf.dm.getComplete()) {
							complete = 100;
							d.setDLRate(edf.dm.getDLRate());
							d.setProgress(100);
							d.setDownloaded(100);
							d.setSize(edf.dm.torrent.total_length
									/ (1024 * 1024));
							d.setFileNames(edf.dm.torrent.name);
							if (!m.getDisplay().isDisposed()) {
								m.getDisplay().asyncExec(new Runnable() {

									@Override
									public void run() {
										if (!d.isStopped()) {
											m.getDownloadTable().updateTable(d);
										}
									}
								});
							}
							break;
						}
						complete = (int) edf.getCompleted();
						d.setDLRate(edf.dm.getDLRate());
						d.setProgress(complete);
						d.setDownloaded(edf.dm.getTotal());
						d.setSize(edf.dm.torrent.total_length / (1024 * 1024));
						d.setFileNames(edf.dm.torrent.name);
						if (!m.getDisplay().isDisposed()) {
							m.getDisplay().asyncExec(new Runnable() {

								@Override
								public void run() {
									if (!d.isStopped()) {
										m.getDownloadTable().updateTable(d);
									}
								}
							});
						}
					}
				}
			}
		};
		update.start();
	}
}

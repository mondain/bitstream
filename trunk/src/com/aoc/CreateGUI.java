package com.aoc;

import jBittorrentAPI.ExampleCreateTorrent;
import jBittorrentAPI.ExamplePublish;
import jBittorrentAPI.ExampleShareFiles;

import java.awt.BorderLayout;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CreateGUI extends SelectionAdapter {

	private Shell shell = null;
	private Text tField = null;
	private Text trackerField = null;

	public static boolean shown = false;

	public CreateGUI(Shell parent, String label) {
		shell = new Shell(parent);
		shell.setText(label);
		// shell.setSize(450, 200);
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
		tField.setToolTipText("Enter path of the torrent file");
		tField.setLayoutData(new RowData(400, 21));

		trackerField = new Text(shell, SWT.BORDER | SWT.SINGLE);
		trackerField.setFont(font);
		trackerField.setToolTipText("Enter URL of the tracker");
		trackerField.setLayoutData(new RowData(400, 21));
		
		Button bButton = new Button(shell, SWT.NONE);
		bButton.setText("Browse");
		bButton.setLayoutData(new RowData(70, SWT.DEFAULT));
		bButton.addSelectionListener(this);

		Button okButton = new Button(shell, SWT.NONE);
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
		dialog.setFilterPath(History.getInstance().getLastCreateDir());
		dialog.setFilterExtensions(new String[] { "*.*" });
		dialog.setFilterNames(new String[] { "All Files (*.*)" });
		return dialog.open();
	}

	public void doInBackground(String path, String tURL) {
		String torrentPath = "example/client1/funvideo.torrent";
		// create torrent
		String[] params = new String[] { torrentPath,
				tURL , "256", path, "..",
				"John Lynch", "..", "this is a fun video" };
		ExampleCreateTorrent.main(params);
		// publish torrent
		String[] params2 = new String[] { torrentPath,
				"http://localhost:8081/upload", "none", "none",
				"this is a fun video" };

		ExamplePublish.main(params2);
		// share the file
		String downTo = "example/client1/";
		String[] params3 = new String[] { torrentPath, downTo };
		ExampleShareFiles.main(params3);
	}

	@Override
	public void widgetSelected(SelectionEvent se) {
		// TODO Auto-generated method stub
		if (((Button) se.widget).getText().equals("OK")) {
			final String path = tField.getText();
			final String trackerURL = trackerField.getText();
			if (!(path.length() > 0)) {
				return;
			}
			// TODO: validate path here
			System.out.println(path);
			new Thread(new Runnable() {

				@Override
				public void run() {
					doInBackground(path, trackerURL);
				}
			}).start();

			shell.dispose();
		} else if (((Button) se.widget).getText().equals("Browse")) {
			String path = fileBrowse();
			if (path == null) {
				return;
			}

			try {
				File f = new File(path);
				History.getInstance().setLastCreateDir(f.getParent());
			} catch (Exception ioe) {
				History.getInstance().setLastCreateDir(
						System.getProperty("user.home"));
			}

			tField.setText(path);
		}
	}

}

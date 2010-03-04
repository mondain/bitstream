package com.aoc;

import jBittorrentAPI.ExampleDownloadFiles;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

public class Download implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// keeps the details about a download
	private String name = null; // name of the file
	private int progress = 0; // shows percentage of downloaded part
	private float downloaded = 0.0f; // downloaded so far
	private float size = 0.0f; // total size of the file
	public int status = 0; // set to 'downloading' by default
	private String added = null; // the date and time the download was added
	private String downloadTo = PrefGUI.downloadTo; // set to default download
	private float downSpeed = 0.0f;
	
	// path
	private boolean shown = false;
	private transient ExampleDownloadFiles edf = null;
	private ArrayList<String> fileNames = null;

	private transient ProgressBar pBar = null;
	private transient TableEditor editor = null;

	@SuppressWarnings("deprecation")
	public Download(String n, Date a) {
		this.name = n;
		this.added = a.toLocaleString();
	}

	public ProgressBar createPBar(Table t) {
		if (pBar == null) {
			pBar = new ProgressBar(t, SWT.NONE);
		}
		//pBar.
		return pBar;
	}

	public String getSize() {
		return this.size + " MB";
	}

	public String getName() {
		return this.name;
	}

	public int getProgress() {
		return this.progress;
	}

	public float getDownloaded() {
		return roundFloat(this.downloaded);
	}

	public String getTime() {
		return this.added;
	}

	public void setProgress(int p) {
		this.progress = p;
	}

	public void updatePBar(int p) {
		try {
			if (pBar != null) {
				this.progress = p;
				pBar.setSelection((int) progress);
			} else {
				System.err.println("ERROR: Progress Bar not initialized");
			}
		} catch (SWTException swte) {
		}
	}

	public void setSize(float size) {
		this.size = roundFloat(size);
	}

	public void setDownloaded(float update) {
		this.downloaded = roundFloat(update);
	}

	public void setDownloadTo(String path) {
		this.downloadTo = path;
	}

	public void setEditor(TableEditor te) {
		this.editor = te;
	}
	
	public TableEditor getEditor() {
		return this.editor;
	}
	
	public String getDownloadTo() {
		return this.downloadTo;
	}

	public void setEDF(ExampleDownloadFiles edf) {
		this.edf = edf;
	}

	public void setDLRate(float speed) {
		this.downSpeed = roundFloat(speed);
	}
	
	public float getDLRate() {
		return roundFloat(this.downSpeed);
	}
	
	private float roundFloat(float f) {
		DecimalFormat df2 = new DecimalFormat("#,###,###,##0.00");
		float rounded = new Float(df2.format(f)).floatValue();
		return rounded;
	}

	public void setFileNames(ArrayList<String> list) {
		fileNames = list;
		if(fileNames==null) {
			System.out.println(">>>>>>>>>> File names not initialized");
		}
	}

	@SuppressWarnings("unchecked")
	public void showFiles() {
		if (shown) {
			System.out.println("Already shown");
			return;
		}
		final Shell shell = new Shell(Main.getInstance().getShell(),
				SWT.DIALOG_TRIM);

		int style = SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL;
		final List list = new List(shell, style);
		if (edf != null && fileNames == null) {
			System.out.println("edf is null");
			fileNames = edf.dm.torrent.name;
		}
		System.out.println("In showFiles");
		if (fileNames == null) {
			System.out.println("fileName is null");
			return;
		}

		String title = name;
		if (name.length() > 45) {
			title = name.substring(0, 15) + "..."
					+ name.substring(name.length() - 30);
		}
		shell.setText(title);
		
		for (int i = 0; i < fileNames.size(); i++) {
			list.add(fileNames.get(i));
		}
		shown = true;
		list.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseDown(MouseEvent arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				// TODO Auto-generated method stub
				String toPlay = fileNames.get(list.getSelectionIndex());
				String absPath = downloadTo + toPlay;
				System.out.println("Path: " + absPath);
				///////////// Remove the following line ////////////
				throw new Error(" >>>>>> Thrown error just for highlighting purpose <<<<<");
				////////////////// Thrown error just for highlighting purpose ////////////
				// TODO: play this file
			}
		});
		list.setSize(400, 200);
		list.setBackground(shell.getBackground());
		FontData fd = list.getFont().getFontData()[0];
		fd.setHeight(Constants.TEXT_HEIGHT);
		Font font = new Font(shell.getDisplay(), fd);
		list.setFont(font);
		shell.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				shown = false;
				shell.dispose();
			}
		});
		shell.setLocation(shell.getParent().getLocation().x + 100, shell
				.getParent().getLocation().y + 50);
		shell.pack();
		shell.open();
		Display display = Main.getInstance().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		shell.dispose();
	}
}

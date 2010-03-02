/**
 * 
 */
package com.aoc;

/**
 * @author Saleem
 *
 */

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class Main {
	// / GUI Components ///
	private Display display = null;
	private Shell shell = null;

	// / Logic Fields ///
	private static Main INSTANCE = null;
	private String fileName = "data.ser";
	private DownloadTable dTable = null;

	public static void main(String[] args) {
		new Main();
	}

	public Main() {
		INSTANCE = this;
		History.getInstance();
		initGUI();
	}

	public void initGUI() {

		display = new Display();
		shell = new Shell(display);
		shell.setText("BitStream");

		addMenuBar();
		//addTable();
		dTable = new DownloadTable(shell);
		shell.setSize(600, 400);

		shell.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				// save vector
				try {
					FileOutputStream fout = new FileOutputStream(fileName);
					ObjectOutputStream oout = new ObjectOutputStream(fout);
					oout.writeObject(dTable.getAllDownloads());
					oout.flush();
					oout.writeUTF(PrefGUI.downloadTo);
					oout.flush();
					oout.writeUTF(History.getInstance().getLastCreateDir());
					oout.flush();
					oout.writeUTF(History.getInstance().getLastDownDir());
					oout.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				//if (downScreen != null) {
				//	if (downScreen.update != null) {
				//		downScreen.update.interrupt();
				//		display.disposeExec(downScreen.update);
				//	}
				//}
			}

		});

		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		display.dispose();
		System.exit(0);
	}

	public void addMenuBar() {
		// add menu
		Menu menuBar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menuBar);

		MenuItem fileItem = new MenuItem(menuBar, SWT.CASCADE);
		fileItem.setText("&File");

		// File menu
		Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
		fileItem.setMenu(fileMenu);

		MenuHandler mh = new MenuHandler();

		MenuItem createItem = new MenuItem(fileMenu, SWT.PUSH);
		createItem.setText("&Create Torrent\tCtrl+C");
		createItem.setAccelerator(SWT.MOD1 + 'C');
		createItem.addSelectionListener(mh);

		MenuItem downItem = new MenuItem(fileMenu, SWT.PUSH);
		downItem.setText("&Download Torrent\tCtrl+D");
		downItem.setAccelerator(SWT.MOD1 + 'D');
		downItem.addSelectionListener(mh);

		MenuItem exitItem = new MenuItem(fileMenu, SWT.PUSH);
		exitItem.setText("E&xit");
		exitItem.addSelectionListener(mh);

		// Edit menu
		MenuItem editItem = new MenuItem(menuBar, SWT.CASCADE);
		editItem.setText("&Edit");
		Menu editMenu = new Menu(shell, SWT.DROP_DOWN);
		editItem.setMenu(editMenu);

		MenuItem prefItem = new MenuItem(editMenu, SWT.PUSH);
		prefItem.setText("&Preferences");
		prefItem.setAccelerator(SWT.MOD1 + 'P');
		prefItem.addSelectionListener(mh);

	}


	

	class MenuHandler extends SelectionAdapter {

		public MenuHandler() {
		}

		@Override
		public void widgetSelected(SelectionEvent se) {
			System.out.println("Selected: " + ((MenuItem) se.widget).getText());
			if (((MenuItem) se.widget).getText().equals(
					"&Create Torrent\tCtrl+C")) {
				// create torrent
				if (!CreateGUI.shown) {
					new CreateGUI(shell, "Create Torrent").show();
				}
			} else if (((MenuItem) se.widget).getText().equals(
					"&Download Torrent\tCtrl+D")) {
				// download torrent
				if (!DownGUI.shown) {
					new DownGUI(shell, "Download Torrent").show();
				}
			} else if (((MenuItem) se.widget).getText().equals("E&xit")) {
				// exit program
				shell.close();
			} else if (((MenuItem) se.widget).getText().equals("&Preferences")) {
				// show preferences
				new PrefGUI(shell).show();
			}
		}
	}

	public String getFileName() {
		return this.fileName;
	}
	
	public DownloadTable getDownloadTable() {
		return this.dTable;
	}
	
	public Display getDisplay() {
		return this.display;
	}

	public Shell getShell() {
		return this.shell;
	}

	public static Main getInstance() {
		return INSTANCE;
	}
}

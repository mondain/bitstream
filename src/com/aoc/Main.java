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
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class Main {
	// / GUI Components ///
	private Display display = null;
	private Shell shell = null;
	private Table table = null;

	// / Logic Fields ///
	private static Main INSTANCE = null;
	private PersistData allDownloads = null;
	String fileName = "data.ser";
	// CoolBarExamples cb = null;

	//private DownGUI downScreen = null;
	//private CreateGUI createScreen = null;
	//private PrefGUI prefScreen = null;

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
		shell.setText("SPROJ");

		addMenuBar();
		addTable();
		shell.setSize(600, 400);

		shell.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				// save vector
				try {
					FileOutputStream fout = new FileOutputStream(fileName);
					ObjectOutputStream oout = new ObjectOutputStream(fout);
					oout.writeObject(allDownloads);
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

	private void addTable() {
		FormLayout f = new FormLayout();
		shell.setLayout(f);

		FormData textData = new FormData();
		textData.left = new FormAttachment(0);
		textData.right = new FormAttachment(100);
		// textData.top = new FormAttachment(cb.getCoolBar());
		textData.top = new FormAttachment(0);
		textData.bottom = new FormAttachment(97);

		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		table.setHeaderVisible(true);
		table.setLinesVisible(false);
		table.setLayoutData(textData);
		table.setBackground(display.getSystemColor(SWT.COLOR_GRAY));

		table.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseDown(MouseEvent arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseDoubleClick(MouseEvent me) {
				int index = table.getSelectionIndex();
				System.out.println("Selected: " + index);
				if (index < 0) {
					return;
				}
				Download d = (Download) allDownloads.get(index);
				d.showFiles();
			}
		});
		TableColumn[] column = new TableColumn[5];
		column[0] = new TableColumn(table, SWT.CENTER);
		column[0].setText("File Name");

		column[1] = new TableColumn(table, SWT.CENTER);
		column[1].setText("File Size");

		column[2] = new TableColumn(table, SWT.CENTER);
		column[2].setText("Downloaded");

		column[3] = new TableColumn(table, SWT.CENTER);
		column[3].setText("Added");

		column[4] = new TableColumn(table, SWT.CENTER);
		column[4].setText("Progress");
		// column[3].setMoveable(true);

		column[0].setWidth(100);
		column[1].setWidth(80);
		column[2].setWidth(80);
		column[3].setWidth(150);
		column[4].setWidth(128);

		resurrect();
	}

	public Table getTable() {
		return this.table;
	}

	public void addToTable(Download d) {
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(new String[] { d.getName(), d.getSize(),
				d.getDownloaded() + " %", d.getTime() });
		// add progress bar
		TableEditor editor = new TableEditor(table);
		editor.grabHorizontal = editor.grabVertical = true;
		ProgressBar b = d.createPBar(table);
		editor.setEditor(b, item, 4);
	}

	public void addDownload(Download d) {
		if (allDownloads == null) {
			allDownloads = new PersistData();
		}
		allDownloads.add(d);
	}

	public Download getDownload(int index) {
		return allDownloads.get(index);
	}

	private void resurrect() {
		try {
			FileInputStream fin = new FileInputStream(fileName);
			ObjectInputStream oin = new ObjectInputStream(fin);
			allDownloads = (PersistData) oin.readObject();
			PrefGUI.downloadTo = oin.readUTF();
			History.getInstance().setLastCreateDir(oin.readUTF());
			History.getInstance().setLastDownDir(oin.readUTF());
			System.out.println("Downloads To: " + PrefGUI.downloadTo);
			oin.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			allDownloads = new PersistData();
		}
		System.out.println("Num downloads = " + allDownloads.size());
		for (int i = 0; i < allDownloads.size(); i++) {
			Download d = (Download) allDownloads.get(i);
			d.createPBar(table);
			d.updatePBar(d.getProgress());
			addToTable(d);
		}
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
					//if (createScreen == null)
						//createScreen = new CreateGUI(shell, "Create Torrent");
					//createScreen.show();
					new CreateGUI(shell, "Create Torrent").show();
				}
			} else if (((MenuItem) se.widget).getText().equals(
					"&Download Torrent\tCtrl+D")) {
				// download torrent
				if (!DownGUI.shown) {
					//if (downScreen == null) {
						//downScreen = new DownGUI(shell, "Download Torrent");
					//}
					//downScreen.show();
					new DownGUI(shell, "Download Torrent").show();
				}
			} else if (((MenuItem) se.widget).getText().equals("E&xit")) {
				// exit program
				shell.close();
			} else if (((MenuItem) se.widget).getText().equals("&Preferences")) {
				// show preferences
				//if (prefScreen == null) {
					//prefScreen = new PrefGUI(shell);
				//}
				//prefScreen.show();
				new PrefGUI(shell).show();
			}
		}
	}

	public void updateTable(Download d) {
		int index = allDownloads.indexOf(d);
		table.getItem(index).setText(new String[] { d.getName(), d.getSize(),
				d.getDownloaded() + " %", d.getTime() });
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

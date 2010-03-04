package com.aoc;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class DownloadTable {

	private Table table = null;
	private PersistData allDownloads = null;

	public DownloadTable(final Shell shell) {
		allDownloads = new PersistData();
		FormLayout f = new FormLayout();
		shell.setLayout(f);

		FormData textData = new FormData();
		textData.left = new FormAttachment(0);
		textData.right = new FormAttachment(100);
		textData.top = new FormAttachment(Main.getInstance().getCoolBar());
		// textData.top = new FormAttachment(0);
		textData.bottom = new FormAttachment(100);

		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		table.setHeaderVisible(true);
		table.setLinesVisible(false);
		table.setLayoutData(textData);
		table.setBackground(Main.getInstance().getDisplay().getSystemColor(
				SWT.COLOR_GRAY));

		table.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent me) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseDown(MouseEvent me) {

				// TODO Auto-generated method stub
			}

			@Override
			public void mouseDoubleClick(MouseEvent me) {
				if (me.button != 1) {
					return;
				}
				int index = table.getSelectionIndex();
				System.out.println("Selected: " + index);
				if (index < 0) {
					return;
				}
				Download d = (Download) allDownloads.get(index);
				if (d == null) {
					System.err.println("Error: Invalid selection or error in file(s) -- returning");
					return;
				}
				d.showFiles();
			}
		});

		TableColumn[] column = new TableColumn[6];
		column[0] = new TableColumn(table, SWT.CENTER);
		column[0].setText("File Name");

		column[1] = new TableColumn(table, SWT.CENTER);
		column[1].setText("File Size");

		column[2] = new TableColumn(table, SWT.CENTER);
		column[2].setText("Downloaded");

		column[3] = new TableColumn(table, SWT.CENTER);
		column[3].setText("Added");

		column[4] = new TableColumn(table, SWT.CENTER);
		column[4].setText("Speed");

		column[5] = new TableColumn(table, SWT.CENTER);
		column[5].setText("Progress");
		// column[3].setMoveable(true);

		column[0].setWidth(100);
		column[1].setWidth(80);
		column[2].setWidth(80);
		column[3].setWidth(150);
		column[4].setWidth(80);
		column[5].setWidth(128);

		resurrect();
	}

	public Table getTable() {
		return this.table;
	}

	public void resurrect() {
		try {
			FileInputStream fin = new FileInputStream(Main.getInstance()
					.getFileName());
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

	public void addToTable(Download d) {
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(new String[] { d.getName(), d.getSize(),
				d.getDownloaded() + " %", d.getTime(), d.getDLRate() + "" });
		// add progress bar
		TableEditor editor = new TableEditor(table);
		d.setEditor(editor);
		editor.grabHorizontal = true;
		ProgressBar b = d.createPBar(table);
		editor.setEditor(b, item, 5);
	}

	public void updateTable(final Download d) {
		try {
			if (table.isDisposed()) {
				return;
			}
			int index = allDownloads.indexOf(d);
			table.getItem(index).setText(
					new String[] { d.getName(), d.getSize(),
							d.getDownloaded() + " %", d.getTime(),
							d.getDLRate() + "" });
			d.updatePBar(d.getProgress());
			table.update();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * public void updateTable(final Download d) { Runnable update = new
	 * Runnable() {
	 * 
	 * @Override public void run() { // TODO Auto-generated method stub if
	 * (table.isDisposed()) { return; } int index = allDownloads.indexOf(d);
	 * table.getItem(index).setText( new String[] { d.getName(), d.getSize(),
	 * d.getDownloaded() + " %", d.getTime() }); d.updatePBar(d.getProgress());
	 * table.update(); } }; Main.getInstance().getDisplay().asyncExec(update); }
	 */

	public void addDownload(Download d) {
		if (allDownloads == null) {
			allDownloads = new PersistData();
		}
		allDownloads.add(d);
	}

	public void deleteSelected() {
		try {
			int index = table.getSelectionIndex();
			Download d = allDownloads.get(index);
			table.remove(index);
			d.getEditor().getEditor().dispose();
			allDownloads.remove(index);
			table.update();
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	public Download getDownload(int index) {
		Download d = null;
		try {
			d = allDownloads.get(index);
		} catch (Exception e) {
		}
		return d;
	}

	public PersistData getAllDownloads() {
		return this.allDownloads;
	}
}

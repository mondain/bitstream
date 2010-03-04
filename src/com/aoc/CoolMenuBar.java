package com.aoc;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class CoolMenuBar {

	private CoolBar coolBar = null;
	private ToolBar toolBar;

	public CoolMenuBar(final Shell shell) {
		coolBar = new CoolBar(shell, SWT.NONE);
		createItem(coolBar);
		FormData coolData = new FormData();
		coolData.left = new FormAttachment(0);
		coolData.right = new FormAttachment(100);
		coolData.top = new FormAttachment(0);
		coolBar.setLayoutData(coolData);
		coolBar.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event event) {
				shell.layout();
			}
		});
	}

	public void setListener(SelectionListener sl) {
		System.out.println("Setting listener");
		for (int i = 0; i < this.coolBar.getItemCount(); i++) {
			this.toolBar.getItem(0).addSelectionListener(sl);
		}
	}

	public CoolBar getCoolBar() {
		return this.coolBar;
	}

	CoolItem createItem(final CoolBar coolBar) {
		toolBar = new ToolBar(coolBar, SWT.WRAP);
		// final String labels[] = new String[] { "  Play  ", "  Delete  ",
		// "  dummy  ", "  dummy  " };
		final ArrayList<String> labels = new ArrayList<String>();
		labels.add("Play");
		labels.add("Delete");
		labels.add("dummy");
		labels.add("dummy");
		labels.add("dummy");

		for (int i = 0; i < labels.size(); i++) {
			ToolItem item = new ToolItem(toolBar, SWT.PUSH);
			item.setText(labels.get(i));
			item.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent se) {
					// TODO Auto-generated method stub
					String label = ((ToolItem) se.widget).getText();
					// int index = Arrays.binarySearch(labels, label);
					int index = labels.indexOf(label);
					switch (index) {
					case 0:
						System.out.println("Play selected");
						Download d = Main
								.getInstance()
								.getDownloadTable()
								.getDownload(
										Main.getInstance().getDownloadTable()
												.getTable().getSelectionIndex());
						if (d == null) {
							System.err.println("Error: No Download selected or Error in Download -- returning");
							return;
						}
						d.showFiles();
						break;
					case 1:
						System.out.println("Delete selected");
						Main.getInstance().getDownloadTable().deleteSelected();
						break;
					default:
						System.out.println(label + " selected. Index = "
								+ index);
						break;
					}
				}
			});
		}
		toolBar.pack();
		Point size = toolBar.getSize();
		final CoolItem item = new CoolItem(coolBar, SWT.DROP_DOWN);
		item.addListener(SWT.Selection, new Listener() {
			Menu menu = null;

			public void handleEvent(Event e) {
				if (e.detail != SWT.ARROW)
					return;
				int i = 0;
				ToolItem[] items = toolBar.getItems();
				Rectangle client = toolBar.getClientArea();
				while (i < items.length) {
					Rectangle rect1 = items[i].getBounds();
					Rectangle rect2 = rect1.intersection(client);
					if (!rect1.equals(rect2))
						break;
					i++;
				}
				if (i == items.length)
					return;
				Shell shell = toolBar.getShell();
				if (menu != null)
					menu.dispose();
				menu = new Menu(shell, SWT.POP_UP);
				for (int j = i; j < items.length; j++) {
					MenuItem item = new MenuItem(menu, SWT.PUSH);
					item.setText(items[j].getText());
				}
				Point pt = e.display.map(coolBar, null, e.x, e.y);
				menu.setLocation(pt);
				menu.setVisible(true);
				System.out.println("Aleradyasl,dfj");
			}
		});
		item.setControl(toolBar);
		Point preferred = item.computeSize(size.x, size.y);
		item.setPreferredSize(preferred);
		Rectangle minimum = toolBar.getItems()[0].getBounds();
		item.setMinimumSize(minimum.width, minimum.height);
		return item;
	}
}
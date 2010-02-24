package com.aoc;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class PrefGUI extends SelectionAdapter {

	private Text tField = null;
	private Shell shell = null;
	public static String downloadTo = System.getProperty("user.home");
	public static boolean shown = false;

	public PrefGUI(Shell s) {
		shell = new Shell(s);
		CTabFolder folder = new CTabFolder(shell, SWT.NONE);

		final CTabItem genTab = new CTabItem(folder, SWT.NONE);
		genTab.setText("General");

		// /// General Tab /////
		Composite general = new Composite(folder, SWT.BORDER);
		GridLayout layout = new GridLayout();
		general.setLayout(layout);

		Group g = new Group(general, SWT.NONE);
		g.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL));
		g.setText("Directory Locations");
		g.setLayout(new RowLayout(SWT.HORIZONTAL));
		Label label = new Label(g, SWT.CENTER);
		FontData fd = label.getFont().getFontData()[0];
		fd.setHeight(10);
		Font font = new Font(g.getDisplay(), fd);
		label.setFont(font);
		label.setText("Download to: ");
		label.setLayoutData(new RowData(90, SWT.DEFAULT));

		fd.setHeight(9);
		Font font2 = new Font(g.getDisplay(), fd);
		tField = new Text(g, SWT.BORDER | SWT.SINGLE);
		tField.setText(downloadTo);
		tField.setFont(font2);
		tField.setBackground(g.getBackground());
		tField.setEditable(false);
		tField.setToolTipText("Enter path of the torrent file");
		tField.setLayoutData(new RowData(230, 17));

		Button bButton = new Button(g, SWT.NONE);
		bButton.setText("...");
		bButton.setLayoutData(new RowData(25, 21));
		bButton.addSelectionListener(this);
		genTab.setControl(general);

		Button okButton = new Button(general, SWT.NONE);
		okButton.setText("OK");
		GridData d = new GridData();
		d.widthHint = 70;
		d.horizontalAlignment = GridData.END;
		d.grabExcessVerticalSpace = true;
		d.verticalAlignment = GridData.END;
		okButton.setLayoutData(d);
		okButton.addSelectionListener(this);

		// / Network Tab /////
		Composite network = new Composite(folder, SWT.BORDER);
		CTabItem netTab = new CTabItem(folder, SWT.NONE);
		netTab.setText("Network");
		netTab.setControl(network);

		folder.setSize(400, 300);
		folder.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent se) {
				CTabFolder folder = (CTabFolder) (se.getSource());
				CTabItem selected = folder.getSelection();
				FontData selFD = selected.getFont().getFontData()[0];
				selFD.setStyle(SWT.BOLD);
				Font selFont = new Font(selected.getDisplay(), selFD);
				selected.setFont(selFont);

				for (int i = 0; i < folder.getItemCount(); i++) {
					CTabItem temp = folder.getItem(i);
					if (temp.getText().compareTo(selected.getText()) == 0) {
						// continue;
					} else {
						FontData fd = temp.getFont().getFontData()[0];
						fd.setStyle(SWT.NONE);
						Font f = new Font(temp.getDisplay(), fd);
						temp.setFont(f);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent se) {
				// TODO Auto-generated method stub
			}
		});

		Point p = shell.getParent().getLocation();
		shell.setLocation(p.x + 30, p.y + 30);
		shell.pack();

		shell.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				shown = false;
				shell.dispose();
			}

		});
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
		DirectoryDialog dialog = new DirectoryDialog(shell);
		dialog.setText("Choose Location");
		// dialog.setMessage("Message");
		dialog.setFilterPath(System.getProperty("user.home"));
		return dialog.open();
	}

	@Override
	public void widgetSelected(SelectionEvent se) {
		if (((Button) se.widget).getText().equals("...")) {
			String path = fileBrowse();
			if (path == null) {
				return;
			}
			tField.setText(path);
		} else if (((Button) se.widget).getText().equals("OK")) {
			downloadTo = tField.getText();
			shell.dispose();
		}
	}
}

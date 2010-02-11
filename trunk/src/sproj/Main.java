/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sproj;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.RowFilter.Entry;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;




/**
 *
 * @author Saleem
 */
public class Main extends WindowAdapter implements ActionListener {

    private JFrame mainFrame = null;
    private Container container = null;
    private JPanel prgPanel = null;
    private GridBagLayout gridBag = null;
    private JMenuBar menuBar = null;
    private JMenu mainMenu = null;
    private JMenuItem crtMenu = null;
    private JMenuItem dwnMenu = null;
    private static final Color evenColor = new Color(250, 250, 250);
    private final MTableModel model = new MTableModel();
    private final TableRowSorter<MTableModel> sorter = new TableRowSorter<MTableModel>(model);
    private JTable table = null;
    private final TreeSet<Integer> set = new TreeSet<Integer>();

    public Main() {
        init();
    }

    private void init() {
        mainFrame = new JFrame("SPROJ Demo");
        mainFrame.addWindowListener(this);
        container = mainFrame.getContentPane();   // container to add things into later

        //// prepare menu ////
        crtMenu = new JMenuItem("Create Torrent");
        dwnMenu = new JMenuItem("Download Torrent");
        crtMenu.addActionListener(this);
        dwnMenu.addActionListener(this);
        mainMenu = new JMenu("Menu");
        mainMenu.add(crtMenu);
        mainMenu.add(dwnMenu);
        menuBar = new JMenuBar();
        menuBar.setVisible(true);
        menuBar.add(mainMenu);
        mainFrame.setJMenuBar(menuBar);

        gridBag = new GridBagLayout();
        container.setLayout(gridBag);
        GridBagConstraints gbc = new GridBagConstraints();

        /////////////// Add JTable to prgPanel ///////
        tableInit(gbc);

        ////// Add prgPanel ///////
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        container.add(prgPanel, gbc);

        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    public void tableInit(GridBagConstraints gbc) {
        prgPanel = new JPanel(new GridBagLayout());
        table = new JTable(model) {

            @Override
            public Component prepareRenderer(TableCellRenderer tcr, int row, int column) {
                Component c = super.prepareRenderer(tcr, row, column);
                if (isRowSelected(row)) {
                    c.setForeground(getSelectionForeground());
                    c.setBackground(getSelectionBackground());
                } else {
                    c.setForeground(getForeground());
                    c.setBackground((row % 2 == 0) ? evenColor : getBackground());
                }
                return c;
            }
        };
        table.setRowSorter(sorter);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension());
        table.setShowGrid(false);

        TableColumn column = table.getColumnModel().getColumn(0);
        column.setMaxWidth(30);
        column.setMinWidth(30);
        column.setResizable(false);
        column.setCellRenderer(new CenterAligner());
        column = table.getColumnModel().getColumn(1);
        column.setCellRenderer(new CenterAligner());
        column = table.getColumnModel().getColumn(2);
        column.setCellRenderer(new ProgressRenderer());

        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        prgPanel.add(scrollPane, gbc);
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridy = 1;
        prgPanel.add(new JButton(new DeleteAction("Delete", null)), gbc);
    }

    public class CenterAligner extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            return this;
        }
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == crtMenu) {
            if (!CreateTorrent.isShown) {
                new CreateTorrent(this);
            }
        } else if (ae.getSource() == dwnMenu) {
            if (!DownloadTorrent.isShown) {
                dt = new DownloadTorrent(this);
            }
        }
    }
    
    DownloadTorrent dt;

    @Override
    public void windowClosing(WindowEvent we) {
        System.exit(0);
    }

    public void addDownload() {
        final int key = model.getRowCount();
        SwingWorker<Integer, Integer> worker = new SwingWorker<Integer, Integer>() {

            private int sleepDummy = new Random().nextInt(100) + 1;
            private int lengthOfTask = 120;

            @Override
            protected Integer doInBackground() {
                while (dt.getCompleted() < (float)100) {
                    if (!table.isDisplayable()) {
                        return -1;
                    }
                
                    publish((int)dt.getCompleted());
                    //System.out.println("hello");
                }
                return sleepDummy * lengthOfTask;
            }

            @Override
            protected void process(List<Integer> c) {
                model.setValueAt(c.get(c.size() - 1), key, 2);
            }

            @Override
            protected void done() {
                String text;
                int i = -1;
                if (isCancelled()) {
                    text = "Cancelled";
                } else {
                    try {
                        i = get();
                        text = (i >= 0) ? "Done" : "Disposed";
                    } catch (Exception ignore) {
                        ignore.printStackTrace();
                        text = ignore.getMessage();
                    }
                }
                System.out.println(key + ":" + text + "(" + i + "ms)");
            }
        };
        model.addTest(new Download("Example", 0), worker);
        worker.execute();
    }

    public synchronized void cancelDownload() {
        int[] selection = table.getSelectedRows();
        if (selection == null || selection.length <= 0) {
            return;
        }
        for (int i = 0; i < selection.length; i++) {
            int midx = table.convertRowIndexToModel(selection[i]);
            SwingWorker worker = model.getSwingWorker(midx);
            if (worker != null && !worker.isDone()) {
                worker.cancel(true);
            }
            worker = null;
        }
        table.repaint();
    }

    class DeleteAction extends AbstractAction {

        public DeleteAction(String label, Icon icon) {
            super(label, icon);
        }

        public void actionPerformed(ActionEvent e) {
            deleteDownload();
        }
    }

    public synchronized void deleteDownload() {
    	dt.setExit();
        int[] selection = table.getSelectedRows();
        if (selection == null || selection.length <= 0) {
            return;
        }
        for (int i = 0; i < selection.length; i++) {
            int midx = table.convertRowIndexToModel(selection[i]);
            set.add(midx);
            SwingWorker worker = model.getSwingWorker(midx);
            if (worker != null && !worker.isDone()) {
                worker.cancel(true);
            }
            worker = null;
        }
        final RowFilter<MTableModel, Integer> filter = new RowFilter<MTableModel, Integer>() {

            @Override
            public boolean include(Entry<? extends MTableModel, ? extends Integer> entry) {
                return !set.contains(entry.getIdentifier());
            }
        };
        sorter.setRowFilter(filter);
        table.repaint();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        new Main();
    }
}

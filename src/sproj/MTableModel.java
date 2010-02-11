package sproj;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


public class MTableModel extends DefaultTableModel {

    private static final ColumnContext[] columnArray = {
        new ColumnContext("No.", Integer.class, false),
        new ColumnContext("File Name", String.class, false),
        new ColumnContext("Progress", Integer.class, false)
    };
    private final Map<Integer, SwingWorker> swmap = new HashMap<Integer, SwingWorker>();
    private int number = 1;

    public void addTest(Download t, SwingWorker worker) {
        Object[] obj = {number, t.getName() + number, t.getProgress()};
        super.addRow(obj);
        swmap.put(number, worker);
        number++;
    }

    public synchronized SwingWorker getSwingWorker(int identifier) {
        Integer key = (Integer) getValueAt(identifier, 0);
        return swmap.get(key);
    }

    public Download getTest(int identifier) {
        return new Download((String) getValueAt(identifier, 1), (Integer) getValueAt(identifier, 2));
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return columnArray[col].isEditable;
    }

    @Override
    public Class<?> getColumnClass(int modelIndex) {
        return columnArray[modelIndex].columnClass;
    }

    @Override
    public int getColumnCount() {
        return columnArray.length;
    }

    @Override
    public String getColumnName(int modelIndex) {
        return columnArray[modelIndex].columnName;
    }

    private static class ColumnContext {

        public final String columnName;
        public final Class columnClass;
        public final boolean isEditable;

        public ColumnContext(String columnName, Class columnClass, boolean isEditable) {
            this.columnName = columnName;
            this.columnClass = columnClass;
            this.isEditable = isEditable;
        }
    }
}
class Download {

    private String name;
    private Integer progress;

    public Download(String name, Integer progress) {
        this.name = name;
        this.progress = progress;
    }

    public void setName(String str) {
        name = str;
    }

    public void setProgress(Integer str) {
        progress = str;
    }

    public String getName() {
        return name;
    }

    public Integer getProgress() {
        return progress;
    }
}

class ProgressRenderer extends DefaultTableCellRenderer {

    private final JProgressBar pBar = new JProgressBar(0, 100);

    public ProgressRenderer() {
        super();
        setOpaque(true);
        pBar.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {
        Integer i = (Integer) value;
        String text = "Done";
        if (i < 0) {
            text = "Cancelled";
        } else if (i < 100) {
            pBar.setValue(i);
            return pBar;
        }
        super.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);
        setHorizontalAlignment(SwingConstants.CENTER);
        return this;
    }
}

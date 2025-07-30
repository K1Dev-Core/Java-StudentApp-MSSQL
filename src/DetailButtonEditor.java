import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DetailButtonEditor extends DefaultCellEditor {

    protected JButton btn;

    public DetailButtonEditor(JCheckBox checkBox) {
        super(checkBox);
        btn = new JButton();
        btn.setOpaque(true);
        btn.setText("detail");

        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JTable table = (JTable) SwingUtilities.getAncestorOfClass(JTable.class, btn);
                if (table != null) {
                    int row = table.getSelectedRow();
                    Object id = table.getValueAt(row, 0);

                    StudentApp app = (StudentApp) SwingUtilities.getWindowAncestor(table);
                    if (app != null) {
                        app.showDetail(id.toString());
                    }
                }
            }
        });
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        return btn;
    }

    public Object getCellEditorValue() {
        return "detail";
    }
}
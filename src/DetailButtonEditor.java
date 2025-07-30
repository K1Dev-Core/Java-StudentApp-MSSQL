import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DetailButtonEditor extends DefaultCellEditor {

    protected JButton detailButton;
    private StudentApp mainApp;

    public DetailButtonEditor(JCheckBox checkBox) {
        super(checkBox);
        detailButton = new JButton();
        detailButton.setOpaque(true);
        detailButton.setText("detail");

        detailButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JTable table = (JTable) SwingUtilities.getAncestorOfClass(JTable.class, detailButton);
                if (table != null) {
                    int selectedRow = table.getSelectedRow();
                    Object studentId = table.getValueAt(selectedRow, 0);

                    StudentApp app = (StudentApp) SwingUtilities.getWindowAncestor(table);
                    if (app != null) {
                        app.showStudentDetail(studentId.toString());
                    }
                }
            }
        });
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        return detailButton;
    }

    public Object getCellEditorValue() {
        return "detail";
    }
}
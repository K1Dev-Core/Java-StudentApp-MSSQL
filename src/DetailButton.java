import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class DetailButton extends JButton implements TableCellRenderer {

    public DetailButton() {
        setOpaque(true);
        setText("detail");
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return this;
    }
}
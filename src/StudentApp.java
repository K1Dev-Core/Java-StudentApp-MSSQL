import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class StudentApp extends JFrame {

    private JTable dataTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private JLabel statusLabel;
    private JTextField searchBox;
    private JButton searchButton;

    private String server = "202.28.34.202";
    private String database = "PRYMANIA_DB";
    private String username = "db_67011212055";
    private String password = "db_67011212055";

    public StudentApp() {
        createWindow();
        createComponents();
        setupLayout();
        addButtonActions();
        loadAllData();
    }

    private void createWindow() {
        setTitle("ระบบข้อมูลนิสิต");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(248, 249, 250));
    }

    private void createComponents() {
        tableModel = new DefaultTableModel();
        dataTable = new JTable(tableModel);
        refreshButton = new JButton("รีเฟรชข้อมูล");
        statusLabel = new JLabel("เริ่มต้นระบบ...", JLabel.LEFT);
        searchBox = new JTextField(15);
        searchButton = new JButton("ค้นหา");
        styleAllComponents();
    }

    private void styleAllComponents() {
        dataTable.setFont(new Font("Tahoma", Font.PLAIN, 13));
        dataTable.setRowHeight(35);
        dataTable.setSelectionBackground(new Color(230, 240, 255));
        dataTable.setSelectionForeground(Color.BLACK);
        dataTable.setGridColor(new Color(230, 230, 230));
        dataTable.setShowVerticalLines(true);
        dataTable.setShowHorizontalLines(true);

        dataTable.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));
        dataTable.getTableHeader().setBackground(new Color(52, 73, 94));
        dataTable.getTableHeader().setForeground(Color.WHITE);
        dataTable.getTableHeader().setPreferredSize(new Dimension(0, 40));

        refreshButton.setFont(new Font("Tahoma", Font.BOLD, 14));
        refreshButton.setPreferredSize(new Dimension(130, 35));
        refreshButton.setBorder(BorderFactory.createEmptyBorder());
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        searchBox.setFont(new Font("Tahoma", Font.PLAIN, 14));
        searchBox.setPreferredSize(new Dimension(150, 35));
        searchBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        searchButton.setFont(new Font("Tahoma", Font.BOLD, 14));
        searchButton.setBackground(new Color(52, 152, 219));
        searchButton.setPreferredSize(new Dimension(80, 35));
        searchButton.setBorder(BorderFactory.createEmptyBorder());
        searchButton.setFocusPainted(false);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        statusLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(100, 100, 100));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel topPanel = createTopPanel();
        JScrollPane centerPanel = createCenterPanel();
        JPanel bottomPanel = createBottomPanel();

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        JLabel titleLabel = new JLabel("ระบบจัดการข้อมูลนิสิต");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(Color.WHITE);

        JLabel searchLabel = new JLabel("รหัสนิสิต:");
        searchLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        searchLabel.setForeground(new Color(100, 100, 100));

        rightPanel.add(searchLabel);
        rightPanel.add(searchBox);
        rightPanel.add(searchButton);
        rightPanel.add(Box.createHorizontalStrut(10));
        rightPanel.add(refreshButton);

        mainPanel.add(titleLabel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        return mainPanel;
    }

    private JScrollPane createCenterPanel() {
        JScrollPane scrollPane = new JScrollPane(dataTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
        scrollPane.setBackground(new Color(248, 249, 250));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        return scrollPane;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 249, 250));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(5, 0, 5, 0)
        ));
        panel.add(statusLabel, BorderLayout.WEST);
        return panel;
    }

    private void addButtonActions() {
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadAllData();
            }
        });

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchData();
            }
        });

        searchBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchData();
            }
        });
    }

    private Connection connectDatabase() throws SQLException {
        String url = "jdbc:sqlserver://" + server + ":1433;" +
                "databaseName=" + database + ";" +
                "encrypt=false;trustServerCertificate=true";
        return DriverManager.getConnection(url, username, password);
    }

    private void clearTable() {
        tableModel.setColumnCount(0);
        tableModel.setRowCount(0);
    }

    private void setupTableColumns(ResultSetMetaData metaData) throws SQLException {
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            tableModel.addColumn(metaData.getColumnName(i));
        }
        tableModel.addColumn("detail");
        dataTable.getColumn("detail").setCellRenderer(new DetailButton());
        dataTable.getColumn("detail").setCellEditor(new DetailButtonEditor(new JCheckBox()));
        dataTable.getColumn("detail").setPreferredWidth(60);
    }

    private int addTableRows(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        int rowCount = 0;

        while (resultSet.next()) {
            Object[] rowData = new Object[columnCount + 1];
            for (int i = 1; i <= columnCount; i++) {
                Object value = resultSet.getObject(i);
                if (value == null) {
                    rowData[i - 1] = "null";
                    if (i == 6) {
                        rowData[i - 1] = "0";
                    }
                } else {
                    rowData[i - 1] = value;
                }
            }
            rowData[columnCount] = "detail";
            tableModel.addRow(rowData);
            rowCount++;
        }
        return rowCount;
    }

    private void showStatus(String message) {
        statusLabel.setText(message);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
    }

    private void searchData() {
        String searchText = searchBox.getText().trim();
        showStatus("กำลังค้นหาข้อมูล...");
        searchButton.setEnabled(false);

        try {
            Connection conn = connectDatabase();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM STUDENT WHERE sid = ? or name LIKE ? or major LIKE ?");
            stmt.setString(1, searchText);
            stmt.setString(2, searchText + "%");
            stmt.setString(3, searchText + "%");
            ResultSet resultSet = stmt.executeQuery();

            clearTable();
            setupTableColumns(resultSet.getMetaData());
            int foundCount = addTableRows(resultSet);
            conn.close();

            if (foundCount > 0) {
                showStatus("พบข้อมูล " + foundCount + " รายการ");
            } else {
                showStatus("ไม่พบข้อมูลสำหรับ: " + searchText);
            }

        } catch (SQLException e) {
            showError("ค้นหาไม่สำเร็จ: " + e.getMessage());
            showStatus("เกิดข้อผิดพลาดในการค้นหา");
        }
        searchButton.setEnabled(true);
    }

    private void loadAllData() {
        showStatus("กำลังโหลดข้อมูล...");
        refreshButton.setEnabled(false);

        try {
            Connection conn = connectDatabase();
            showStatus("เชื่อมต่อสำเร็จ กำลังดึงข้อมูล...");
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT *, DATEDIFF(YEAR, birthday, GETDATE()) AS age FROM STUDENT");

            clearTable();
            setupTableColumns(resultSet.getMetaData());
            int totalCount = addTableRows(resultSet);
            conn.close();

            showStatus("โหลดข้อมูลสำเร็จ - ทั้งหมด " + totalCount + " รายการ");

        } catch (SQLException e) {
            showStatus("เชื่อมต่อไม่สำเร็จ");
            showError("ไม่สามารถเชื่อมต่อฐานข้อมูลได้\n\n" + e.getMessage());
        }
        refreshButton.setEnabled(true);
    }

    public void showStudentDetail(String studentId) {
        try {
            Connection conn = connectDatabase();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM ENROLL WHERE stdid = '" + studentId + "'");

            while (rs.next()) {
                System.out.println(rs.getString(1) + " | " + rs.getString(2) + " | " + rs.getString(3) + " | " );
            }
            conn.close();

        } catch (SQLException e) {
            showStatus("เชื่อมต่อไม่สำเร็จ");
            showError("ไม่สามารถเชื่อมต่อฐานข้อมูลได้\n\n" + e.getMessage());
        }
    }

    public JTable getDataTable() {
        return dataTable;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new StudentApp().setVisible(true);
            }
        });
    }
}
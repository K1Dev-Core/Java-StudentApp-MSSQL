import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import com.formdev.flatlaf.*;

public class StudentApp extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JButton refreshBtn;
    private JLabel status;
    private JTextField searchBox;
    private JButton searchBtn;

    private String server = "";
    private String database = "PRYMANIA_DB";
    private String username = "";
    private String password = "";

    public StudentApp() {
        setupTheme();
        createWindow();
        createComponents();
        setupLayout();
        addActions();
        loadData();
    }

    private void setupTheme() {
        try {

            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createWindow() {
        setTitle("Student System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void createComponents() {
        model = new DefaultTableModel();
        table = new JTable(model);
        refreshBtn = new JButton("Refresh");
        status = new JLabel("Starting...", JLabel.LEFT);
        searchBox = new JTextField(15);
        searchBtn = new JButton("Search");

        setupStyles();
    }

    private void setupStyles() {
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));

        refreshBtn.setFont(new Font("Arial", Font.BOLD, 14));
        refreshBtn.setPreferredSize(new Dimension(100, 35));

        searchBox.setFont(new Font("Arial", Font.PLAIN, 14));
        searchBox.setPreferredSize(new Dimension(150, 35));

        searchBtn.setFont(new Font("Arial", Font.BOLD, 14));
        searchBtn.setPreferredSize(new Dimension(80, 35));

        status.setFont(new Font("Arial", Font.PLAIN, 12));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("Student Management System");
        title.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.add(new JLabel("Search:"));
        rightPanel.add(searchBox);
        rightPanel.add(searchBtn);
        rightPanel.add(refreshBtn);

        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 25, 10, 25));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 25, 5, 25));
        bottomPanel.add(status, BorderLayout.WEST);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void addActions() {
        refreshBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadData();
            }
        });

        searchBtn.addActionListener(new ActionListener() {
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

    private Connection getConnection() throws SQLException {
        String url = "jdbc:sqlserver://" + server + ":1433;" +
                "databaseName=" + database + ";" +
                "encrypt=false;trustServerCertificate=true";
        return DriverManager.getConnection(url, username, password);
    }

    private void clearTable() {
        model.setColumnCount(0);
        model.setRowCount(0);
    }

    private void setupColumns(ResultSetMetaData meta) throws SQLException {
        int cols = meta.getColumnCount();
        for (int i = 1; i <= cols; i++) {
            model.addColumn(meta.getColumnName(i));
        }
        model.addColumn("detail");
        table.getColumn("detail").setCellRenderer(new DetailButton());
        table.getColumn("detail").setCellEditor(new DetailButtonEditor(new JCheckBox()));
        table.getColumn("detail").setPreferredWidth(60);
    }

    private int addRows(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();
        int count = 0;

        while (rs.next()) {
            Object[] row = new Object[cols + 1];
            for (int i = 1; i <= cols; i++) {
                Object value = rs.getObject(i);
                if (value == null) {
                    row[i - 1] = "null";
                    if (i == 6) {
                        row[i - 1] = "0";
                    }
                } else {
                    row[i - 1] = value;
                }
            }
            row[cols] = "detail";
            model.addRow(row);
            count++;
        }
        return count;
    }

    private void updateStatus(String msg) {
        status.setText(msg);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void searchData() {
        String text = searchBox.getText().trim();
        updateStatus("Searching...");
        searchBtn.setEnabled(false);

        try {
            Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT *,DATEDIFF(YEAR, birthday, GETDATE()) AS age  FROM STUDENT WHERE sid = ? or name LIKE ? or major LIKE ?");
            stmt.setString(1, text);
            stmt.setString(2, text + "%");
            stmt.setString(3, text + "%");
            ResultSet rs = stmt.executeQuery();

            clearTable();
            setupColumns(rs.getMetaData());
            int found = addRows(rs);
            conn.close();

            if (found > 0) {
                updateStatus("Found " + found + " records");
            } else {
                updateStatus("No records found for: " + text);
            }

        } catch (SQLException e) {
            showError("Search failed: " + e.getMessage());
            updateStatus("Search error");
        }
        searchBtn.setEnabled(true);
    }

    private void loadData() {
        updateStatus("Loading data...");
        refreshBtn.setEnabled(false);

        try {
            Connection conn = getConnection();
            updateStatus("Connected, getting data...");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT *, DATEDIFF(YEAR, birthday, GETDATE()) AS age FROM STUDENT");

            clearTable();
            setupColumns(rs.getMetaData());
            int total = addRows(rs);
            conn.close();

            updateStatus("Loaded " + total + " records");

        } catch (SQLException e) {
            updateStatus("Connection failed");
            showError("Cannot connect to database\n\n" + e.getMessage());
        }
        refreshBtn.setEnabled(true);
    }

    public void showDetail(String studentId) {
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM ENROLL WHERE stdid = '" + studentId + "'");

            while (rs.next()) {
                System.out.println(rs.getString(1) + " | " + rs.getString(2) + " | " + rs.getString(3) + " | " );
            }
            conn.close();

        } catch (SQLException e) {
            updateStatus("Connection failed");
            showError("Cannot connect to database\n\n" + e.getMessage());
        }
    }

    public JTable getTable() {
        return table;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new StudentApp().setVisible(true);
            }
        });
    }
}

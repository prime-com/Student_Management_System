import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ViewFrame extends JFrame {

private final String DB_URL  = "jdbc:mysql://localhost:3306/sms_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private final String DB_USER = "sms_user";
    private final String DB_PASS = "tanmay@123";

    private JTable table;
    private DefaultTableModel model;

    private String loggedRoll = null;  
    private JButton btnBack;

    public ViewFrame() {
        initUI();
        loadAllStudents();
    }

    
    public ViewFrame(String rollNo) {
        this.loggedRoll = rollNo;
        initUI();
        loadStudent(rollNo);
    }

    private void initUI() {
        setTitle("Student Records");
        setSize(900, 520);
        setLocationRelativeTo(null);

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(245,247,250));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                performBackAction();
            }
        });

        // ----------- TITLE ----------
        JLabel lbl = new JLabel("Student Records", SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 22));
        lbl.setBounds(200, 20, 500, 40);
        add(lbl);

        // ----------- TABLE -----------
        model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Roll No");
        model.addColumn("Name");
        model.addColumn("Marks/100");

        table = new JTable(model);
        table.setFillsViewportHeight(true);

        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(50, 80, 800, 330);
        add(sp);

        // ----------- BACK BUTTON ----------
        btnBack = new JButton("Back");
        btnBack.setBounds(380, 430, 120, 40);
        btnBack.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnBack.setFocusPainted(false);
        add(btnBack);

        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performBackAction();
            }
        });

        setVisible(true);
    }

    private void performBackAction() {
        dispose();

        if (loggedRoll == null) {
          
            try {
                new ManagementFrame().setVisible(true);
            } catch (Throwable t) {
               
                try { new WelcomeFrame().setVisible(true); }
                catch (Throwable tt) { /* ignore */ }
            }
        } else {
            try { new WelcomeFrame().setVisible(true); }
            catch (Throwable t) { /* ignore */ }
        }
    }

    private void loadAllStudents() {
        model.setRowCount(0);

        String sql = "SELECT id, roll_no, name, marks FROM students " +
                     "ORDER BY CAST(roll_no AS UNSIGNED) ASC, roll_no ASC";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id"),
                        rs.getString("roll_no"),
                        rs.getString("name"),
                        rs.getInt("marks")
                };
                model.addRow(row);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading students: " + ex.getMessage());
        }
    }

    private void loadStudent(String rollNo) {
        model.setRowCount(0);

        String sql = "SELECT id, roll_no, name, marks FROM students WHERE roll_no=? LIMIT 1";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, rollNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Object[] row = {
                            rs.getInt("id"),
                            rs.getString("roll_no"),
                            rs.getString("name"),
                            rs.getInt("marks")
                    };
                    model.addRow(row);
                } else {
                    JOptionPane.showMessageDialog(this, "No record found for roll: " + rollNo);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ViewFrame().setVisible(true));
    }
}

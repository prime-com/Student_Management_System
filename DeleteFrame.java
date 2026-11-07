import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DeleteFrame extends JFrame {

private final String DB_URL  = "jdbc:mysql://localhost:3306/sms_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private final String DB_USER = "sms_user";
    private final String DB_PASS = "tanmay@123";

    private JTextField rollField;

    public DeleteFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("Delete Student");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(245,247,250));

        // Top blue bar
        JPanel top = new JPanel(null);
        top.setBackground(new Color(25,118,210));
        top.setBounds(0, 0, 1000, 80);
        JLabel ttl = new JLabel("Student Management System");
        ttl.setForeground(Color.WHITE);
        ttl.setFont(new Font("SansSerif", Font.BOLD, 26));
        ttl.setBounds(20, 15, 600, 50);
        top.add(ttl);
        add(top);

        // Title
        JLabel title = new JLabel("Delete Student", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setBounds(350, 110, 300, 40);
        add(title);

        // Roll no label + field
        JLabel lRoll = new JLabel("Roll No:");
        lRoll.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lRoll.setBounds(320, 180, 80, 25);
        add(lRoll);

        rollField = new JTextField();
        rollField.setBounds(410, 180, 300, 32);
        rollField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(rollField);

        // Delete button
        JButton btnDelete = new JButton("Delete");
        btnDelete.setBounds(410, 240, 140, 40);
        btnDelete.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(btnDelete);

        // Back button
        JButton btnBack = new JButton("Back");
        btnBack.setBounds(570, 240, 140, 40);
        btnBack.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(btnBack);

        // Actions
        btnDelete.addActionListener(e -> deleteStudentAndUser());
        btnBack.addActionListener(e -> {
            dispose();
            try { new ManagementFrame().setVisible(true); } catch (Throwable t) {}
        });

        setVisible(true);
    }

    private void deleteStudentAndUser() {
        String roll = rollField.getText().trim();
        if (roll.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter roll number.", "Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete student with roll no: " + roll + " ?\nThis will also remove the student's login account.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        Connection conn = null;
        PreparedStatement delStudent = null;
        PreparedStatement delUser = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            conn.setAutoCommit(false); // start transaction

            // Delete from students
            delStudent = conn.prepareStatement("DELETE FROM students WHERE roll_no = ?");
            delStudent.setString(1, roll);
            int studentRows = delStudent.executeUpdate();

            // Delete from users (only student role)
            delUser = conn.prepareStatement("DELETE FROM users WHERE username = ? AND role = 'student'");
            delUser.setString(1, roll);
            int userRows = delUser.executeUpdate();

            // commit if at least student row deleted (we allow userRows to be 0 if no user existed)
            if (studentRows > 0) {
                conn.commit();
                String msg = "Student record deleted.";
                if (userRows > 0) msg += " Login account removed.";
                else msg += " No login account found.";
                JOptionPane.showMessageDialog(this, msg, "Deleted", JOptionPane.INFORMATION_MESSAGE);
                rollField.setText("");
            } else {
                // no student deleted -> rollback
                conn.rollback();
                JOptionPane.showMessageDialog(this, "No student found with roll number: " + roll, "Not found", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception ex) {
            try { if (conn != null) conn.rollback(); } catch (Exception ignored) {}
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during delete: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { if (delStudent != null) delStudent.close(); } catch (Exception e) {}
            try { if (delUser != null) delUser.close(); } catch (Exception e) {}
            try { if (conn != null) conn.setAutoCommit(true); conn.close(); } catch (Exception e) {}
        }
    }

    // quick main for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DeleteFrame().setVisible(true));
    }
}

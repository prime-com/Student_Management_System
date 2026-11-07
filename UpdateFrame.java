import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class UpdateFrame extends JFrame {
    private JTextField tfRoll, tfName, tfMarks;
    private JButton btnFetch, btnUpdate, btnBack;

    // DB info
private final String DB_URL  = "jdbc:mysql://localhost:3306/sms_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private final String DB_USER = "sms_user";
    private final String DB_PASS = "tanmay@123";

    public UpdateFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("Update Student");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(245,247,250));

        // BLUE TOP BAR
        JPanel top = new JPanel(null);
        top.setBackground(new Color(25,118,210));
        top.setBounds(0, 0, 1000, 80);
        JLabel ttl = new JLabel("Student Management System");
        ttl.setForeground(Color.WHITE);
        ttl.setFont(new Font("SansSerif", Font.BOLD, 26));
        ttl.setBounds(20, 15, 600, 50);
        top.add(ttl);
        add(top);

        // TITLE
        JLabel lbl = new JLabel("Update Student", SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 30));
        lbl.setBounds(350, 110, 300, 40);
        add(lbl);

        JLabel sub = new JLabel("Enter Roll No and Fetch, then update fields", SwingConstants.CENTER);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sub.setForeground(new Color(90,90,90));
        sub.setBounds(300, 150, 400, 25);
        add(sub);

        // Roll No
        JLabel lRoll = new JLabel("Roll No:");
        lRoll.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lRoll.setBounds(300, 200, 80, 25);
        add(lRoll);

        tfRoll = new JTextField();
        tfRoll.setBounds(390, 200, 200, 30);
        add(tfRoll);

        btnFetch = new JButton("Fetch");
        btnFetch.setBounds(610, 200, 100, 30);
        add(btnFetch);

        // Name
        JLabel lName = new JLabel("Name:");
        lName.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lName.setBounds(300, 250, 80, 25);
        add(lName);

        tfName = new JTextField();
        tfName.setBounds(390, 250, 320, 30);
        add(tfName);

        // Marks
        JLabel lMarks = new JLabel("Marks:");
        lMarks.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lMarks.setBounds(300, 300, 80, 25);
        add(lMarks);

        tfMarks = new JTextField();
        tfMarks.setBounds(390, 300, 320, 30);
        add(tfMarks);

        // Update button
        btnUpdate = new JButton("Update");
        btnUpdate.setBounds(390, 350, 320, 40);
        btnUpdate.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnUpdate.setBackground(new Color(33,150,243));
        btnUpdate.setForeground(Color.BLACK);
        btnUpdate.setFocusPainted(false);
        add(btnUpdate);

        // Back
        btnBack = new JButton("Back");
        btnBack.setBounds(390, 410, 320, 36);
        add(btnBack);

        // actions
        btnFetch.addActionListener(e -> fetchStudent());
        btnUpdate.addActionListener(e -> updateStudent());
        btnBack.addActionListener(e -> {
            dispose();
            new ManagementFrame();
        });

        // Enter key on roll triggers fetch, on marks triggers update
        tfRoll.addActionListener(e -> btnFetch.doClick());
        tfMarks.addActionListener(e -> btnUpdate.doClick());

        setVisible(true);
    }

    private void fetchStudent() {
        String roll = tfRoll.getText().trim();
        if (roll.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter roll number.", "Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // clear fields while loading
        tfName.setText("");
        tfMarks.setText("");

        new Thread(() -> {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                     PreparedStatement ps = conn.prepareStatement("SELECT name, marks FROM students WHERE roll_no = ? LIMIT 1")) {
                    ps.setString(1, roll);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            String name = rs.getString("name");
                            int marks = rs.getInt("marks");
                            SwingUtilities.invokeLater(() -> {
                                tfName.setText(name);
                                tfMarks.setText(String.valueOf(marks));
                            });
                        } else {
                            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "No student found with that roll no.", "Not found", JOptionPane.INFORMATION_MESSAGE));
                        }
                    }
                }
            } catch (ClassNotFoundException cnf) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "JDBC driver not found.", "Error", JOptionPane.ERROR_MESSAGE));
            } catch (SQLException sq) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "DB error: " + sq.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    private void updateStudent() {
        String roll = tfRoll.getText().trim();
        String name = tfName.getText().trim();
        String marksStr = tfMarks.getText().trim();

        if (roll.isEmpty() || name.isEmpty() || marksStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter all fields.", "Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int marks;
        try {
            marks = Integer.parseInt(marksStr);
            if (marks < 0) throw new NumberFormatException("negative");
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Marks must be a non-negative integer.", "Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        new Thread(() -> {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                     PreparedStatement ps = conn.prepareStatement("UPDATE students SET name = ?, marks = ? WHERE roll_no = ?")) {
                    ps.setString(1, name);
                    ps.setInt(2, marks);
                    ps.setString(3, roll);
                    int updated = ps.executeUpdate();
                    if (updated > 0) {
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Student updated.", "Success", JOptionPane.INFORMATION_MESSAGE));
                    } else {
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "No student found to update.", "Not found", JOptionPane.INFORMATION_MESSAGE));
                    }
                }
            } catch (ClassNotFoundException cnf) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "JDBC driver not found.", "Error", JOptionPane.ERROR_MESSAGE));
            } catch (SQLException sq) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "DB error: " + sq.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    // quick main
    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new UpdateFrame()); }
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AddFrame extends JFrame {

private final String DB_URL  = "jdbc:mysql://localhost:3306/sms_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    private final String DB_USER = "sms_user";
    private final String DB_PASS = "tanmay@123";

    private JTextField rollField;
    private JTextField nameField;
    private JTextField marksField;

    public AddFrame() {
        initUI();
    }

    private void initUI() {

        setTitle("Add Student");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(245,247,250));

        // ===================== TOP BLUE BAR (same as other frames) =====================
        JPanel top = new JPanel(null);
        top.setBackground(new Color(25,118,210));
        top.setBounds(0, 0, 1000, 80);

        JLabel ttl = new JLabel("Student Management System");
        ttl.setForeground(Color.WHITE);
        ttl.setFont(new Font("SansSerif", Font.BOLD, 26));
        ttl.setBounds(20, 15, 600, 50);

        top.add(ttl);
        add(top);
        // ==============================================================================

        // Title under top bar
        JLabel title = new JLabel("Add Student", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setBounds(350, 110, 300, 40);
        add(title);

        // Roll No
        JLabel lblRoll = new JLabel("Roll No:");
        lblRoll.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblRoll.setBounds(320, 180, 80, 25);
        add(lblRoll);

        rollField = new JTextField();
        rollField.setBounds(410, 180, 300, 32);
        rollField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(rollField);

        // allow digits only
        rollField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar())) e.consume();
            }
        });

        // Name
        JLabel lblName = new JLabel("Name:");
        lblName.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblName.setBounds(320, 230, 80, 25);
        add(lblName);

        nameField = new JTextField();
        nameField.setBounds(410, 230, 300, 32);
        nameField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(nameField);

        // Marks
        JLabel lblMarks = new JLabel("Marks:");
        lblMarks.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblMarks.setBounds(320, 280, 80, 25);
        add(lblMarks);

        marksField = new JTextField();
        marksField.setBounds(410, 280, 300, 32);
        marksField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(marksField);

        // allow digits only
        marksField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar())) e.consume();
            }
        });

        // Add Button
        JButton btnAdd = new JButton("Add");
        btnAdd.setBounds(410, 340, 140, 40);
        btnAdd.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(btnAdd);

        // Back Button
        JButton btnBack = new JButton("Back");
        btnBack.setBounds(570, 340, 140, 40);
        btnBack.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(btnBack);

        // actions
        btnAdd.addActionListener(e -> addStudent());
        btnBack.addActionListener(e -> {
            dispose();
            try { new ManagementFrame().setVisible(true); }
            catch (Throwable t) {}
        });

        setVisible(true);
    }

    private void addStudent() {
        String roll = rollField.getText().trim();
        String name = nameField.getText().trim();
        String marksText = marksField.getText().trim();

        if (roll.isEmpty() || name.isEmpty() || marksText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }

        int marks;
        try { marks = Integer.parseInt(marksText); }
        catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Marks must be a number.");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            // Duplicate roll
            PreparedStatement check = conn.prepareStatement("SELECT COUNT(*) FROM students WHERE roll_no=?");
            check.setString(1, roll);
            ResultSet rs = check.executeQuery();
            rs.next();

            if (rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "Roll number already exists.");
                conn.close();
                return;
            }

            // Insert student
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO students (roll_no, name, marks) VALUES (?, ?, ?)"
            );
            ps.setString(1, roll);
            ps.setString(2, name);
            ps.setInt(3, marks);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Student added successfully.");
            conn.close();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AddFrame().setVisible(true));
    }
}

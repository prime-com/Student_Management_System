import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SignUpFrame extends JFrame {

private final String DB_URL  = "jdbc:mysql://localhost:3306/sms_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private final String DB_USER = "sms_user";
    private final String DB_PASS = "tanmay@123";

    private JTextField usernameField;
    private JTextField nameField;
    private JPasswordField passField;

    private String role; // "student" or "admin"

    public SignUpFrame() { this.role = null; initUI(); }
    public SignUpFrame(String role) { this.role = role; initUI(); }

    private void initUI() {
        String titleText = (role == null) ? "Sign Up" : (role.equalsIgnoreCase("student") ? "Student Sign Up" : "Admin Sign Up");

        setTitle(titleText);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) { performBack(); }
        });

        setLayout(null);
        getContentPane().setBackground(new Color(245,247,250));

        // top bar
        JPanel top = new JPanel(null);
        top.setBackground(new Color(25,118,210));
        top.setBounds(0,0,1000,80);
        JLabel ttl = new JLabel("Student Management System");
        ttl.setForeground(Color.WHITE);
        ttl.setFont(new Font("SansSerif", Font.BOLD, 26));
        ttl.setBounds(20, 15, 600, 50);
        top.add(ttl);
        add(top);

        // title
        JLabel title = new JLabel(titleText, SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setBounds(350,110,300,40);
        add(title);

        // username / roll
        JLabel lUser = new JLabel(role!=null && role.equalsIgnoreCase("student") ? "Roll No:" : "Username:");
        lUser.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lUser.setBounds(320,200,100,25);
        add(lUser);

        usernameField = new JTextField();
        usernameField.setBounds(410,200,240,30);
        add(usernameField);

        // name (for student)
        JLabel lName = new JLabel("Name:");
        lName.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lName.setBounds(320,250,100,25);
        add(lName);

        nameField = new JTextField();
        nameField.setBounds(410,250,240,30);
        add(nameField);

        if (role!=null && role.equalsIgnoreCase("admin")) {
            lName.setVisible(false);
            nameField.setVisible(false);
        }

        // password
        JLabel lPass = new JLabel("Password:");
        lPass.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lPass.setBounds(320,300,100,25);
        add(lPass);

        passField = new JPasswordField();
        passField.setBounds(410,300,240,30);
        add(passField);

        // buttons
        JButton btnCreate = new JButton("Create Account");
        btnCreate.setBounds(410,360,240,40);
        btnCreate.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(btnCreate);

        JButton btnBack = new JButton("Back");
        btnBack.setBounds(410,420,240,34);
        btnBack.setFont(new Font("SansSerif", Font.PLAIN, 13));
        add(btnBack);

        btnCreate.addActionListener(e -> createAccount());
        btnBack.addActionListener(e -> performBack());

        setVisible(true);
    }

    private void performBack() {
        dispose();
        try { new LoginFrame(role).setVisible(true); }
        catch (Throwable t) {
            try { new WelcomeFrame().setVisible(true); } catch (Throwable ignored) {}
        }
    }

    private void createAccount() {
        String user = usernameField.getText().trim();
        String name = nameField.getText().trim();
        String pass = new String(passField.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username/Roll and Password required.", "Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if ("student".equalsIgnoreCase(role) && name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name required for student.", "Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            conn.setAutoCommit(false); // safe multi-step

            // Check if username exists in users table
            ps = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE username = ?");
            ps.setString(1, user);
            rs = ps.executeQuery();
            rs.next();
            int userCount = rs.getInt(1);
            rs.close(); ps.close();

            if (userCount > 0) {
                // user already exists — definite duplicate
                JOptionPane.showMessageDialog(this, "Account already exists. Please login.", "Duplicate", JOptionPane.ERROR_MESSAGE);
                conn.rollback();
                conn.close();
                return;
            }

            // Check if students table has this roll_no
            ps = conn.prepareStatement("SELECT COUNT(*) FROM students WHERE roll_no = ?");
            ps.setString(1, user);
            rs = ps.executeQuery();
            rs.next();
            int studentCount = rs.getInt(1);
            rs.close(); ps.close();

            if (studentCount > 0) {
                // student record exists but user did not — create only users row
                ps = conn.prepareStatement("INSERT INTO users (username, password, role) VALUES (?, ?, 'student')");
                ps.setString(1, user);
                ps.setString(2, pass);
                ps.executeUpdate();
                ps.close();

                conn.commit();
                JOptionPane.showMessageDialog(this, "Account created.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new LoginFrame("student").setVisible(true);
                return;
            }

            // student does not exist
            if ("student".equalsIgnoreCase(role)) {
                // insert both student and user
                ps = conn.prepareStatement("INSERT INTO students (roll_no, name, marks) VALUES (?, ?, 0)");
                ps.setString(1, user);
                ps.setString(2, name);
                ps.executeUpdate();
                ps.close();

                ps = conn.prepareStatement("INSERT INTO users (username, password, role) VALUES (?, ?, 'student')");
                ps.setString(1, user);
                ps.setString(2, pass);
                ps.executeUpdate();
                ps.close();

                conn.commit();
                JOptionPane.showMessageDialog(this, "Student record and account created.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new LoginFrame("student").setVisible(true);
                return;
            } else {
                // admin signup
                ps = conn.prepareStatement("INSERT INTO users (username, password, role) VALUES (?, ?, 'admin')");
                ps.setString(1, user);
                ps.setString(2, pass);
                ps.executeUpdate();
                ps.close();

                conn.commit();
                JOptionPane.showMessageDialog(this, "Admin account created.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new LoginFrame("admin").setVisible(true);
                return;
            }

        } catch (SQLIntegrityConstraintViolationException dup) {
            try { if (conn != null) conn.rollback(); } catch (Exception ignored) {}
            JOptionPane.showMessageDialog(this, "Duplicate entry error. That roll already exists.", "Duplicate", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            try { if (conn != null) conn.rollback(); } catch (Exception ignored) {}
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (ps != null) ps.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SignUpFrame().setVisible(true));
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField tfUser;
    private JPasswordField pfPass;
    private String role;

    // DB info
    private final String DB_URL  =
        "jdbc:mysql://localhost:3306/sms_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private final String DB_USER = "sms_user";
    private final String DB_PASS = "tanmay@123";

    public LoginFrame() { this(null); }
    public LoginFrame(String role) {
        this.role = role;
        initUI();
    }

    private void initUI() {
        setTitle(role == null ? "Login" :
                (role.equalsIgnoreCase("admin") ? "Admin Login" : "Student Login"));
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(245, 247, 250));

        // top bar
        JPanel top = new JPanel(null);
        top.setBackground(new Color(25,118,210));
        top.setBounds(0, 0, 1000, 80);
        JLabel ttl = new JLabel("Student Management System");
        ttl.setForeground(Color.WHITE);
        ttl.setFont(new Font("SansSerif", Font.BOLD, 26));
        ttl.setBounds(20, 15, 600, 50);
        top.add(ttl);
        add(top);

        // center title
        JLabel lblTitle = new JLabel(
                role == null ? "Login" :
                        (role.equalsIgnoreCase("admin") ? "Admin Login" : "Student Login"),
                SwingConstants.CENTER
        );
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 30));
        lblTitle.setBounds(350, 120, 300, 40);
        add(lblTitle);

        JLabel lblSub = new JLabel("Sign in to continue", SwingConstants.CENTER);
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblSub.setForeground(new Color(90, 90, 90));
        lblSub.setBounds(350, 160, 300, 25);
        add(lblSub);

        // Username / Roll label
        JLabel lUser = new JLabel(
                role != null && role.equalsIgnoreCase("student") ? "Roll No:" : "Username:"
        );
        lUser.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lUser.setBounds(320, 220, 80, 25);
        add(lUser);

        tfUser = new JTextField();
        tfUser.setBounds(410, 220, 240, 30);
        add(tfUser);

        // Password
        JLabel lPass = new JLabel("Password:");
        lPass.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lPass.setBounds(320, 270, 80, 25);
        add(lPass);

        pfPass = new JPasswordField();
        pfPass.setBounds(410, 270, 240, 30);
        add(pfPass);

        // Login button
        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(410, 320, 240, 40);
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnLogin.setFocusPainted(false);
        add(btnLogin);

        // Sign Up button
        JButton btnSignup = new JButton("Sign Up");
        btnSignup.setBounds(410, 380, 110, 34);
        btnSignup.setFont(new Font("SansSerif", Font.PLAIN, 13));
        add(btnSignup);

        // Tooltip ONLY for admin
        if (role != null && role.equalsIgnoreCase("admin")) {
            btnSignup.setToolTipText(
                "Contact college administrator for unlocking it"
            );
        }

        // Back button
        JButton btnBack = new JButton("Back");
        btnBack.setBounds(540, 380, 110, 34);
        btnBack.setFont(new Font("SansSerif", Font.PLAIN, 13));
        add(btnBack);

        // footer
        JLabel info = new JLabel(
                "No account? Use Sign Up to create one.",
                SwingConstants.CENTER
        );
        info.setBounds(300, 430, 400, 20);
        info.setForeground(new Color(110,110,110));
        add(info);

        // Actions
        btnLogin.addActionListener(e -> tryLogin());
        tfUser.addActionListener(e -> btnLogin.doClick());
        pfPass.addActionListener(e -> btnLogin.doClick());

        btnBack.addActionListener(e -> {
            dispose();
            new WelcomeFrame().setVisible(true);
        });

      
        btnSignup.addActionListener(e -> {

            if (role != null && role.equalsIgnoreCase("admin")) {
                JOptionPane.showMessageDialog(
                        this,
                        "Contact college administrator for unlocking it",
                        "Access Restricted",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return; // 🔑 STOP here
            }

            dispose();
            new SignUpFrame(role).setVisible(true);
        });

        setVisible(true);
    }

    // Login logic (unchanged)
    private void tryLogin() {
        String user = tfUser.getText().trim();
        String pass = new String(pfPass.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Enter username/roll number and password.",
                    "Input",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        SwingUtilities.invokeLater(() -> {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                try (Connection conn =
                        DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

                    String sql =
                        "SELECT role FROM users WHERE username=? AND password=? LIMIT 1";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, user);
                        ps.setString(2, pass);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                String foundRole = rs.getString("role");

                                if (role != null &&
                                    !role.equalsIgnoreCase(foundRole)) {
                                    JOptionPane.showMessageDialog(
                                            this,
                                            "This account is not a " + role + ".",
                                            "Role mismatch",
                                            JOptionPane.ERROR_MESSAGE
                                    );
                                    return;
                                }

                                openAfterLogin(foundRole, user);
                            } else {
                                JOptionPane.showMessageDialog(
                                        this,
                                        "Invalid credentials.",
                                        "Login failed",
                                        JOptionPane.ERROR_MESSAGE
                                );
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Login error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }

    private void openAfterLogin(String foundRole, String user) {
        dispose();

        if (foundRole.equalsIgnoreCase("admin")) {
            new ManagementFrame().setVisible(true);
            return;
        }

        // student
        try {
            new ViewFrame(user).setVisible(true);
        } catch (Throwable t) {
            new ViewFrame().setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}

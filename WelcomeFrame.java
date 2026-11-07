import javax.swing.*;
import java.awt.*;

public class WelcomeFrame extends JFrame {

    public WelcomeFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("Welcome - Student Management System");
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
        JLabel title = new JLabel("Welcome", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setBounds(350, 120, 300, 40);
        add(title);

        JLabel sub = new JLabel("Choose login type to continue", SwingConstants.CENTER);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sub.setForeground(new Color(90, 90, 90));
        sub.setBounds(350, 160, 300, 25);
        add(sub);

        // Admin button
        JButton btnAdmin = new JButton("Admin Login");
        btnAdmin.setBounds(360, 240, 280, 60);
        btnAdmin.setFont(new Font("SansSerif", Font.BOLD, 18));
        btnAdmin.setFocusPainted(false);
        add(btnAdmin);

        // Student button
        JButton btnStudent = new JButton("Student Login");
        btnStudent.setBounds(360, 320, 280, 60);
        btnStudent.setFont(new Font("SansSerif", Font.BOLD, 18));
        btnStudent.setFocusPainted(false);
        add(btnStudent);

        // small helper message under buttons
        JLabel helper = new JLabel("If you are a new student or admin, click Sign Up on the next screen.", SwingConstants.CENTER);
        helper.setFont(new Font("SansSerif", Font.PLAIN, 13));
        helper.setForeground(new Color(110,110,110));
        helper.setBounds(250, 400, 500, 20);
        add(helper);

        // actions
        btnAdmin.addActionListener(e -> {
            dispose();
            new LoginFrame("admin").setVisible(true);
        });

        btnStudent.addActionListener(e -> {
            dispose();
            new LoginFrame("student").setVisible(true);
        });

        setVisible(true);
    }

    // quick test main
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WelcomeFrame().setVisible(true));
    }
}

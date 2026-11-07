import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ManagementFrame extends JFrame {

    private JButton btnAdd, btnView, btnUpdate, btnDelete, btnLogout;

    public ManagementFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("Admin — Student Management");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // TOP BLUE BAR
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(25,118,210));
        top.setPreferredSize(new Dimension(1000, 80));
        JLabel ttl = new JLabel("Student Management System");
        ttl.setForeground(Color.WHITE);
        ttl.setFont(new Font("SansSerif", Font.BOLD, 28));
        ttl.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        top.add(ttl, BorderLayout.WEST);
        add(top, BorderLayout.NORTH);

        // CENTER: card with buttons
        JPanel centerWrap = new JPanel(new GridBagLayout());
        centerWrap.setBackground(new Color(245,247,250));
        add(centerWrap, BorderLayout.CENTER);

        JPanel card = new JPanel(new GridLayout(2, 2, 20, 20));
        card.setPreferredSize(new Dimension(760, 340));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230,230,230)),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        btnAdd = makeBtn("📥  Add Student");
        btnView = makeBtn("📄  View Students");
        btnUpdate = makeBtn("✏️  Update Student");
        btnDelete = makeBtn("🗑️  Delete Student");

        card.add(btnAdd);
        card.add(btnView);
        card.add(btnUpdate);
        card.add(btnDelete);

        centerWrap.add(card);

        // BOTTOM: logout
        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(245,247,250));
        btnLogout = new JButton("🚪  Logout");
        btnLogout.setPreferredSize(new Dimension(140, 44));
        btnLogout.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnLogout.setFocusPainted(false);
        bottom.add(btnLogout);
        add(bottom, BorderLayout.SOUTH);

        // ACTIONS (open frames and close this)
        btnAdd.addActionListener(e -> {
            dispose();
            new AddFrame();
        });

        btnView.addActionListener(e -> {
            dispose();
            new ViewFrame();
        });

        btnUpdate.addActionListener(e -> {
            dispose();
            new UpdateFrame();
        });

        btnDelete.addActionListener(e -> {
            dispose();
            new DeleteFrame();
        });

        btnLogout.addActionListener(e -> {
            dispose();
            new WelcomeFrame();
        });

        setVisible(true);
    }

    private JButton makeBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 20));
        b.setBackground(Color.WHITE);
        b.setForeground(new Color(33,33,33));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(new Color(220,220,220)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // quick tester
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ManagementFrame());
    }
}

package view;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

    public Dashboard() {
        setTitle("Hotel Room Management");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // MAIN PANEL
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(245, 247, 250));

        // top bar (Gradient Style)
        JPanel top = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(52, 152, 219),
                        getWidth(), 0, new Color(155, 89, 182));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        top.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("🏨 Hotel Lanka");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        //Right buttons
        JPanel right = new JPanel();
        right.setOpaque(false);

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");

        styleButton(loginBtn);
        styleButton(registerBtn);

        right.add(loginBtn);
        right.add(registerBtn);

        top.add(title, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);

        // center area
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(new Color(245, 247, 250));

        // Modern card(rounded + shadow effect)
        JPanel card = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 25, 25);

                // Card
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 25, 25);
            }
        };

        card.setOpaque(false);
        card.setPreferredSize(new Dimension(420, 230));
        card.setLayout(new GridLayout(3, 1));

        JLabel icon = new JLabel("🏨", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 55));

        JLabel welcome = new JLabel("Welcome to Hotel Lanka", SwingConstants.CENTER);
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JLabel subtitle = new JLabel("Manage your hotel system easily", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);

        card.add(icon);
        card.add(welcome);
        card.add(subtitle);

        center.add(card);

        // button actions
        loginBtn.addActionListener(e -> new LoginForm());
        registerBtn.addActionListener(e -> new RegisterForm());

        //  add
        main.add(top, BorderLayout.NORTH);
        main.add(center, BorderLayout.CENTER);

        add(main);
        setVisible(true);
    }

    //  Modern button  style
    private void styleButton(JButton btn) {
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(52, 73, 94));
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void main(String[] args) {
        new Dashboard();
    }
}
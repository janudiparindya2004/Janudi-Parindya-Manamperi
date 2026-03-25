package view;

import javax.swing.*;
import java.awt.*;
import controller.UserController;
import model.User;

public class LoginForm extends JFrame {

    public LoginForm() {
        setTitle("Login");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel p = new JPanel(null);
        p.setBackground(new Color(34, 49, 63));

        JLabel t = new JLabel("Hotel Management System");
        t.setBounds(80, 20, 250, 30);
        t.setForeground(Color.WHITE);
        t.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel loginLabel = new JLabel("Login");
        loginLabel.setBounds(170, 60, 100, 30);
        loginLabel.setForeground(Color.WHITE);
        loginLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField userField = new JTextField();
        userField.setBounds(50, 110, 300, 40);
        userField.setBorder(BorderFactory.createTitledBorder("Username"));

        JPasswordField pass = new JPasswordField();
        pass.setBounds(50, 170, 300, 40);
        pass.setBorder(BorderFactory.createTitledBorder("Password"));

        // combobox
        String[] roles = {"Customer", "Staff", "Admin"};
        JComboBox<String> roleBox = new JComboBox<>(roles);
        roleBox.setBounds(50, 230, 300, 45);
        roleBox.setBorder(BorderFactory.createTitledBorder("Select Role"));
        roleBox.setBackground(new Color(52, 73, 94));
        roleBox.setForeground(Color.WHITE);

        JButton btn = new JButton("Login");
        btn.setBounds(50, 290, 300, 45);
        btn.setBackground(new Color(52, 73, 94));
        btn.setForeground(Color.WHITE);

        JLabel registerLabel = new JLabel("Don't have an account?");
        registerLabel.setBounds(130, 350, 150, 20);
        registerLabel.setForeground(Color.WHITE);

        JButton registerBtn = new JButton("Create New Account");
        registerBtn.setBounds(110, 375, 180, 35);
        registerBtn.setBackground(new Color(46, 204, 113));
        registerBtn.setForeground(Color.WHITE);

        p.add(t);
        p.add(loginLabel);
        p.add(userField);
        p.add(pass);
        p.add(roleBox);
        p.add(btn);
        p.add(registerLabel);
        p.add(registerBtn);

        add(p);

        UserController controller = new UserController();

        // 🔑 LOGIN BUTTON
        btn.addActionListener(e -> {

            String username = userField.getText().trim();
            String password = new String(pass.getPassword());
            String selectedRole = roleBox.getSelectedItem().toString();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter username & password ❌");
                return;
            }

            // admin check
            if (username.equals("admin") && password.equals("2004")
                    && selectedRole.equalsIgnoreCase("Admin")) {

                JOptionPane.showMessageDialog(this, "Welcome Admin ✅");
                new AdminDashboard().setVisible(true);
                dispose();
                return;
            }

            // database login
            User user = controller.login(username, password);

            if (user != null) {

                // check role
                if (!user.getRole().equalsIgnoreCase(selectedRole)) {
                    JOptionPane.showMessageDialog(this,
                            "Role mismatch ❌",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JOptionPane.showMessageDialog(this,
                        "Welcome " + user.getFullName() + " ✅");

                // role based dashboard
                switch (selectedRole.toLowerCase()) {

                    case "customer":
                        new CustomerDashboard(user).setVisible(true);
                        break;

                    case "staff":
                        new StaffDashboard(user).setVisible(true);
                        break;

                    case "admin":
                        new AdminDashboard().setVisible(true);
                        break;

                    default:
                        JOptionPane.showMessageDialog(this,
                                "Unknown role ❌");
                        return;
                }

                dispose();

            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid Login ❌",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);

                pass.setText("");
                userField.requestFocus();
            }
        });

        //  Register button
        registerBtn.addActionListener(e -> {
            new RegisterForm().setVisible(true);
            dispose();
        });

        pass.addActionListener(e -> btn.doClick());
        userField.addActionListener(e -> btn.doClick());

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm());
    }
}
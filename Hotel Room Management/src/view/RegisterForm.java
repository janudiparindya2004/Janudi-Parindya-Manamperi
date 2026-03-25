package view;

import javax.swing.*;
import java.awt.*;
import controller.UserController;
import model.User;

public class RegisterForm extends JFrame {

    public RegisterForm() {
        setTitle("Register");
        setSize(550, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(40, 40, 80));

        JLabel title = new JLabel("Register");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setBounds(200, 20, 200, 40);

        JTextField fullNameField = new JTextField();
        fullNameField.setBounds(150, 80, 250, 35);
        fullNameField.setBorder(BorderFactory.createTitledBorder("Full Name"));

        JTextField usernameField = new JTextField();
        usernameField.setBounds(150, 130, 250, 35);
        usernameField.setBorder(BorderFactory.createTitledBorder("Username"));

        JTextField emailField = new JTextField();
        emailField.setBounds(150, 180, 250, 35);
        emailField.setBorder(BorderFactory.createTitledBorder("Email"));

        JTextField phoneField = new JTextField();
        phoneField.setBounds(150, 230, 250, 35);
        phoneField.setBorder(BorderFactory.createTitledBorder("Phone"));

        JTextArea addressArea = new JTextArea();
        JScrollPane scroll = new JScrollPane(addressArea);
        scroll.setBounds(150, 280, 250, 60);
        scroll.setBorder(BorderFactory.createTitledBorder("Address"));

        JPasswordField passField = new JPasswordField();
        passField.setBounds(150, 360, 250, 35);
        passField.setBorder(BorderFactory.createTitledBorder("Password"));

        String[] roles = {"Customer", "Staff"};
        JComboBox<String> roleCombo = new JComboBox<>(roles);
        roleCombo.setBounds(150, 410, 250, 35);

        JButton btn = new JButton("Register");
        btn.setBounds(150, 470, 250, 40);
        btn.setBackground(new Color(0, 200, 150));
        btn.setForeground(Color.WHITE);

        panel.add(title);
        panel.add(fullNameField);
        panel.add(usernameField);
        panel.add(emailField);
        panel.add(phoneField);
        panel.add(scroll);
        panel.add(passField);
        panel.add(roleCombo);
        panel.add(btn);

        add(panel);

        UserController controller = new UserController();

        btn.addActionListener(e -> {

            String fullName = fullNameField.getText().trim();
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressArea.getText().trim();
            String password = new String(passField.getPassword());
            String role = (String) roleCombo.getSelectedItem();

            if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Required fields missing ❌");
                return;
            }

            if (controller.isUsernameExists(username)) {
                JOptionPane.showMessageDialog(this, "Username already exists ❌");
                return;
            }

            User user = new User(username, password, fullName, email, phone, address, role);

            if (controller.register(user)) {
                JOptionPane.showMessageDialog(this, "Registration Successful ✅");
                new LoginForm();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Registration Failed ❌");
            }
        });

        setVisible(true);
    }
}
package view;

import controller.*;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerDashboard extends JFrame {

    private final User user;
    private final RoomController roomCtrl = new RoomController();
    private final BookingController bookingCtrl = new BookingController();
    private final PaymentController paymentCtrl = new PaymentController();
    private final UserController userCtrl = new UserController();

    private DefaultTableModel roomModel, bookingModel, paymentModel;
    private JTextField fullNameField, emailField, phoneField;
    private JPasswordField passwordField;
    private JTextArea addressArea;

    public CustomerDashboard(User user) {
        this.user = user;
        setTitle("Customer Dashboard - " + user.getUsername());
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header Section
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(52, 152, 219));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel title = new JLabel("Customer Dashboard");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        JLabel userLabel = new JLabel("Welcome, " + user.getFullName());
        userLabel.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);
        header.add(userLabel, BorderLayout.EAST);

        // Tabs Section
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("🏠 Rooms", createRoomPanel());
        tabs.addTab("📅 My Bookings", createBookingPanel());
        tabs.addTab("💳 Payments", createPaymentPanel());
        tabs.addTab("👤 Profile", createProfilePanel());

        // Footer Section
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(44, 62, 80));
        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> { dispose(); new LoginForm().setVisible(true); });
        footer.add(logout, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        loadRooms();
        loadBookings();
        loadPayments();
        setVisible(true);
    }

    private JPanel createRoomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        roomModel = new DefaultTableModel(new String[]{"ID","Room No","Type","Price","Status"},0);
        JTable table = new JTable(roomModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton bookBtn = new JButton("Confirm Booking");
        bookBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Select a room!"); return; }
            int roomId = (int) roomModel.getValueAt(row, 0);
            Booking b = new Booking();
            b.setUserId(user.getId());
            b.setRoomId(roomId);
            b.setStatus("Confirmed");
            b.setCheckInDate(new java.sql.Date(System.currentTimeMillis()));
            b.setCheckOutDate(new java.sql.Date(System.currentTimeMillis() + 86400000));
            if (bookingCtrl.createBooking(b) > 0) {
                JOptionPane.showMessageDialog(this, "Booking Successful!");
                loadBookings(); loadRooms();
            }
        });
        panel.add(bookBtn, BorderLayout.SOUTH);
        return panel;
    }

    private void loadRooms() {
        roomModel.setRowCount(0);
        List<Room> list = roomCtrl.getAllRooms();
        if (list != null) for (Room r : list) roomModel.addRow(new Object[]{r.getId(), r.getRoomNumber(), r.getType(), r.getPrice(), r.getStatus()});
    }

    private JPanel createBookingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        bookingModel = new DefaultTableModel(new String[]{"Booking ID","Room No","Check-In","Status"},0);
        JTable table = new JTable(bookingModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void loadBookings() {
        bookingModel.setRowCount(0);
        List<Booking> list = bookingCtrl.getBookingsByUser(user.getId());
        if (list != null) {
            for (Booking b : list) {
                Room r = roomCtrl.getRoomById(b.getRoomId());
                bookingModel.addRow(new Object[]{b.getId(), (r != null ? r.getRoomNumber() : "N/A"), b.getCheckInDate(), b.getStatus()});
            }
        }
    }

    private JPanel createPaymentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        paymentModel = new DefaultTableModel(new String[]{"ID","Booking ID","Amount","Method","Status"},0);
        JTable table = new JTable(paymentModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton payBtn = new JButton("Proceed to Payment");
        payBtn.addActionListener(e -> {
            int row = bookingModel.getRowCount() > 0 ? 0 : -1;
            if (row == -1) { JOptionPane.showMessageDialog(this, "No bookings found to pay!"); return; }

            int bId = (int) bookingModel.getValueAt(0, 0);
            new PaymentForm(bId, 5000.0).addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    loadPayments();
                }
            });
        });
        panel.add(payBtn, BorderLayout.SOUTH);
        return panel;
    }

    public void loadPayments() {
        paymentModel.setRowCount(0);
        List<Payment> list = paymentCtrl.getPaymentsByUser(user.getId());
        if(list != null){
            for(Payment p : list){
                paymentModel.addRow(new Object[]{p.getId(), p.getBookingId(), p.getAmount(), p.getMethod(), p.getStatus()});
            }
        }
    }

    private JPanel createProfilePanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(236, 240, 241));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Fields Initialization
        fullNameField = new JTextField(user.getFullName(), 20);
        emailField = new JTextField(user.getEmail(), 20);
        phoneField = new JTextField(user.getPhone(), 20);
        passwordField = new JPasswordField(user.getPassword(), 20);
        addressArea = new JTextArea(user.getAddress(), 3, 20);
        addressArea.setLineWrap(true);
        addressArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        // Add Components to Form
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1; formPanel.add(fullNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Email Address:"), gbc);
        gbc.gridx = 1; formPanel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1; formPanel.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; formPanel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1; formPanel.add(new JScrollPane(addressArea), gbc);

        // Update Button
        JButton updateBtn = new JButton("Update Profile");
        updateBtn.setBackground(new Color(46, 204, 113));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));

        updateBtn.addActionListener(e -> {
            // Update the user object with new data
            user.setFullName(fullNameField.getText().trim());
            user.setEmail(emailField.getText().trim());
            user.setPhone(phoneField.getText().trim());
            user.setAddress(addressArea.getText().trim());
            user.setPassword(new String(passwordField.getPassword()));

            // Send to Controller
            if (userCtrl.updateUser(user)) {
                JOptionPane.showMessageDialog(this, "Profile Updated Successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to Update Profile.");
            }
        });

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(updateBtn, gbc);

        mainPanel.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        return mainPanel;
    }
}
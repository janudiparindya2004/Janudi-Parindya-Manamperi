package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import controller.RoomController;
import controller.UserController;
import model.Room;
import model.User;
import java.util.List;

public class AdminDashboard extends JFrame {
    private JTabbedPane tabbedPane;
    private RoomController roomController;
    private UserController userController;

    // Room Management Components
    private JTable roomTable;
    private DefaultTableModel roomTableModel;
    private JTextField roomNumberField, priceField;
    private JComboBox<String> roomTypeCombo, roomStatusCombo;
    private JTextArea descriptionArea;

    // User Management Components
    private JTable userTable;
    private DefaultTableModel userTableModel;

    public AdminDashboard() {
        // Initialize controllers (they handle null connections internally)
        roomController = new RoomController();
        userController = new UserController();

        setTitle("Admin Dashboard - Hotel Management System");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(34, 49, 63));

        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));

        // Add tabs
        tabbedPane.addTab("🏠 Dashboard Overview", createDashboardPanel());
        tabbedPane.addTab("🛏️ Room Management", createRoomManagementPanel());
        tabbedPane.addTab("👥 User Management", createUserManagementPanel());
        tabbedPane.addTab("📊 Reports", createReportsPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Status bar
        JPanel statusBar = createStatusBar();
        mainPanel.add(statusBar, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);

        // Load data in background to avoid UI freeze
        SwingUtilities.invokeLater(() -> {
            loadRooms();
            loadUsers();
        });

        System.out.println("✅ AdminDashboard opened successfully!");
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(52, 73, 94));
        header.setPreferredSize(new Dimension(1200, 80));

        JLabel title = new JLabel("Hotel Management System - Admin Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        header.add(title, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(new Color(52, 73, 94));

        JLabel timeLabel = new JLabel();
        timeLabel.setForeground(Color.WHITE);
        updateTime(timeLabel);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(231, 76, 60));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 12));
        logoutBtn.addActionListener(e -> logout());

        rightPanel.add(timeLabel);
        rightPanel.add(logoutBtn);
        header.add(rightPanel, BorderLayout.EAST);

        // Timer for updating time
        new Timer(1000, e -> updateTime(timeLabel)).start();

        return header;
    }

    private void updateTime(JLabel label) {
        java.util.Date now = new java.util.Date();
        label.setText(String.format("%tT", now));
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(236, 240, 241));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Stats Cards
        String[] stats = {"Total Rooms", "Available Rooms", "Total Users", "Active Bookings"};
        int[] values = {getTotalRooms(), getAvailableRooms(), getTotalUsers(), getActiveBookings()};
        Color[] colors = {new Color(52, 152, 219), new Color(46, 204, 113),
                new Color(241, 196, 15), new Color(155, 89, 182)};

        for (int i = 0; i < stats.length; i++) {
            gbc.gridx = i % 2;
            gbc.gridy = i / 2;
            JPanel card = createStatCard(stats[i], String.valueOf(values[i]), colors[i]);
            panel.add(card, gbc);
        }

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(color, 3));
        card.setPreferredSize(new Dimension(250, 150));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 32));
        valueLabel.setForeground(color);
        valueLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 15, 10));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    // Room Management

    private JPanel createRoomManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(236, 240, 241));

        JPanel formPanel = createRoomFormPanel();
        panel.add(formPanel, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);

        String[] columns = {"ID", "Room Number", "Type", "Price", "Status", "Description"};
        roomTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        roomTable = new JTable(roomTableModel);
        roomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedRoom();
            }
        });

        JScrollPane scrollPane = new JScrollPane(roomTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = createRoomButtonPanel();
        tablePanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(tablePanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRoomFormPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 4, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Room Details"));
        panel.setBackground(Color.WHITE);

        panel.add(new JLabel("Room Number:"));
        roomNumberField = new JTextField();
        panel.add(roomNumberField);

        panel.add(new JLabel("Room Type:"));
        roomTypeCombo = new JComboBox<>(new String[]{"Single", "Double", "Suite", "Deluxe", "Presidential"});
        panel.add(roomTypeCombo);

        panel.add(new JLabel("Price per Night:"));
        priceField = new JTextField();
        panel.add(priceField);

        panel.add(new JLabel("Status:"));
        roomStatusCombo = new JComboBox<>(new String[]{"Available", "Booked", "Maintenance", "Cleaning"});
        panel.add(roomStatusCombo);

        panel.add(new JLabel("Description:"));
        descriptionArea = new JTextArea(2, 20);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        panel.add(descScroll);

        return panel;
    }

    private JPanel createRoomButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(Color.WHITE);

        JButton addBtn = new JButton("➕ Add Room");
        addBtn.setBackground(new Color(46, 204, 113));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Arial", Font.BOLD, 12));
        addBtn.addActionListener(e -> addRoom());

        JButton updateBtn = new JButton("✏️ Update Room");
        updateBtn.setBackground(new Color(52, 152, 219));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFont(new Font("Arial", Font.BOLD, 12));
        updateBtn.addActionListener(e -> updateRoom());

        JButton deleteBtn = new JButton("🗑️ Delete Room");
        deleteBtn.setBackground(new Color(231, 76, 60));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFont(new Font("Arial", Font.BOLD, 12));
        deleteBtn.addActionListener(e -> deleteRoom());

        JButton clearBtn = new JButton("🔄 Clear Form");
        clearBtn.setBackground(new Color(149, 165, 166));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setFont(new Font("Arial", Font.BOLD, 12));
        clearBtn.addActionListener(e -> clearRoomForm());

        JButton refreshBtn = new JButton("⟳ Refresh");
        refreshBtn.setBackground(new Color(241, 196, 15));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 12));
        refreshBtn.addActionListener(e -> loadRooms());

        panel.add(addBtn);
        panel.add(updateBtn);
        panel.add(deleteBtn);
        panel.add(clearBtn);
        panel.add(refreshBtn);

        return panel;
    }

    private void addRoom() {
        try {
            if (roomNumberField.getText().trim().isEmpty() || priceField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Room Number and Price are required!",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Room room = new Room();
            room.setRoomNumber(roomNumberField.getText().trim());
            room.setType((String) roomTypeCombo.getSelectedItem());
            room.setPrice(Double.parseDouble(priceField.getText().trim()));
            room.setStatus((String) roomStatusCombo.getSelectedItem());
            room.setDescription(descriptionArea.getText().trim());

            if (roomController.addRoom(room)) {
                JOptionPane.showMessageDialog(this, "Room added successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadRooms();
                clearRoomForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add room! Check database connection.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid price format!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateRoom() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room to update!",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int roomId = (int) roomTableModel.getValueAt(selectedRow, 0);
            Room room = new Room();
            room.setId(roomId);
            room.setRoomNumber(roomNumberField.getText().trim());
            room.setType((String) roomTypeCombo.getSelectedItem());
            room.setPrice(Double.parseDouble(priceField.getText().trim()));
            room.setStatus((String) roomStatusCombo.getSelectedItem());
            room.setDescription(descriptionArea.getText().trim());

            if (roomController.updateRoom(room)) {
                JOptionPane.showMessageDialog(this, "Room updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadRooms();
                clearRoomForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update room!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid price format!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteRoom() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room to delete!",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this room?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int roomId = (int) roomTableModel.getValueAt(selectedRow, 0);
            if (roomController.deleteRoom(roomId)) {
                JOptionPane.showMessageDialog(this, "Room deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadRooms();
                clearRoomForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete room!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadRooms() {
        try {
            roomTableModel.setRowCount(0);
            List<Room> rooms = roomController.getAllRooms();
            for (Room room : rooms) {
                roomTableModel.addRow(new Object[]{
                        room.getId(),
                        room.getRoomNumber(),
                        room.getType(),
                        String.format("$%.2f", room.getPrice()),
                        room.getStatus(),
                        room.getDescription()
                });
            }
        } catch (Exception e) {
            System.err.println("Error loading rooms: " + e.getMessage());
            // Don't show dialog, just log
        }
    }

    private void loadSelectedRoom() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow != -1) {
            roomNumberField.setText((String) roomTableModel.getValueAt(selectedRow, 1));
            roomTypeCombo.setSelectedItem(roomTableModel.getValueAt(selectedRow, 2));
            String priceStr = ((String) roomTableModel.getValueAt(selectedRow, 3)).replace("$", "");
            priceField.setText(priceStr);
            roomStatusCombo.setSelectedItem(roomTableModel.getValueAt(selectedRow, 4));
            descriptionArea.setText((String) roomTableModel.getValueAt(selectedRow, 5));
        }
    }

    private void clearRoomForm() {
        roomNumberField.setText("");
        priceField.setText("");
        descriptionArea.setText("");
        roomTypeCombo.setSelectedIndex(0);
        roomStatusCombo.setSelectedIndex(0);
        roomTable.clearSelection();
    }

    // User Management

    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(236, 240, 241));

        String[] columns = {"ID", "Username", "Email", "Phone", "Role", "Created Date"};
        userTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(userTableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton addStaffBtn = new JButton("➕ Add Staff Member");
        addStaffBtn.setBackground(new Color(46, 204, 113));
        addStaffBtn.setForeground(Color.WHITE);
        addStaffBtn.setFont(new Font("Arial", Font.BOLD, 12));
        addStaffBtn.addActionListener(e -> showAddUserDialog("Staff"));

        JButton addCustomerBtn = new JButton("👤 Add Customer");
        addCustomerBtn.setBackground(new Color(52, 152, 219));
        addCustomerBtn.setForeground(Color.WHITE);
        addCustomerBtn.setFont(new Font("Arial", Font.BOLD, 12));
        addCustomerBtn.addActionListener(e -> showAddUserDialog("Customer"));

        JButton deleteUserBtn = new JButton("🗑️ Delete User");
        deleteUserBtn.setBackground(new Color(231, 76, 60));
        deleteUserBtn.setForeground(Color.WHITE);
        deleteUserBtn.setFont(new Font("Arial", Font.BOLD, 12));
        deleteUserBtn.addActionListener(e -> deleteUser());

        JButton refreshBtn = new JButton("⟳ Refresh");
        refreshBtn.setBackground(new Color(241, 196, 15));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 12));
        refreshBtn.addActionListener(e -> loadUsers());

        buttonPanel.add(addStaffBtn);
        buttonPanel.add(addCustomerBtn);
        buttonPanel.add(deleteUserBtn);
        buttonPanel.add(refreshBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadUsers() {
        try {
            userTableModel.setRowCount(0);
            List<User> users = userController.getAllUsers();
            for (User user : users) {
                userTableModel.addRow(new Object[]{
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getPhone() != null ? user.getPhone() : "N/A",
                        user.getRole(),
                        user.getCreatedDate()
                });
            }
        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }

    private void showAddUserDialog(String role) {
        JDialog dialog = new JDialog(this, "Add " + role, true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JTextField emailField = new JTextField(20);
        JTextField phoneField = new JTextField(20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        dialog.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        dialog.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        dialog.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        dialog.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        dialog.add(phoneField, gbc);

        JButton addBtn = new JButton("Add " + role);
        addBtn.setBackground(new Color(46, 204, 113));
        addBtn.setForeground(Color.WHITE);
        addBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();

            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Username, Password and Email are required!");
                return;
            }

            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);
            user.setPhone(phone);
            user.setRole(role);

            if (userController.register(user)) {
                JOptionPane.showMessageDialog(dialog, role + " added successfully!");
                loadUsers();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to add " + role + "!");
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        dialog.add(addBtn, gbc);

        dialog.setVisible(true);
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete!",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) userTableModel.getValueAt(selectedRow, 0);
        String username = (String) userTableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete user: " + username + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (userController.deleteUser(userId)) {
                JOptionPane.showMessageDialog(this, "User deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete user!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Reports

    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(236, 240, 241));

        JTextArea reportArea = new JTextArea();
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reportArea.setEditable(false);
        reportArea.setBackground(Color.WHITE);

        JButton generateReportBtn = new JButton("📊 Generate Report");
        generateReportBtn.setBackground(new Color(52, 152, 219));
        generateReportBtn.setForeground(Color.WHITE);
        generateReportBtn.setFont(new Font("Arial", Font.BOLD, 14));
        generateReportBtn.addActionListener(e -> {
            String report = generateReport();
            reportArea.setText(report);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(236, 240, 241));
        buttonPanel.add(generateReportBtn);

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(reportArea), BorderLayout.CENTER);

        return panel;
    }

    private String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("=".repeat(50)).append("\n");
        report.append("     HOTEL MANAGEMENT SYSTEM REPORT\n");
        report.append("=".repeat(50)).append("\n\n");
        report.append("Generated on: ").append(new java.util.Date()).append("\n\n");

        // Room statistics
        List<Room> rooms = roomController.getAllRooms();
        int totalRooms = rooms.size();
        int availableRooms = 0;
        int bookedRooms = 0;
        int maintenanceRooms = 0;

        for (Room room : rooms) {
            switch (room.getStatus()) {
                case "Available":
                    availableRooms++;
                    break;
                case "Booked":
                    bookedRooms++;
                    break;
                case "Maintenance":
                    maintenanceRooms++;
                    break;
            }
        }

        report.append("📊 ROOM STATISTICS\n");
        report.append("-".repeat(40)).append("\n");
        report.append(String.format("Total Rooms: %d\n", totalRooms));
        report.append(String.format("Available Rooms: %d\n", availableRooms));
        report.append(String.format("Booked Rooms: %d\n", bookedRooms));
        report.append(String.format("Maintenance Rooms: %d\n", maintenanceRooms));
        report.append(String.format("Occupancy Rate: %.1f%%\n\n",
                totalRooms > 0 ? (bookedRooms * 100.0 / totalRooms) : 0));

        // Room type distribution
        report.append("🏨 ROOM TYPE DISTRIBUTION\n");
        report.append("-".repeat(40)).append("\n");
        String[] types = {"Single", "Double", "Suite", "Deluxe", "Presidential"};
        for (String type : types) {
            long count = rooms.stream().filter(r -> r.getType().equals(type)).count();
            report.append(String.format("%-15s: %d rooms\n", type, count));
        }

        // User statistics
        List<User> users = userController.getAllUsers();
        long staffCount = users.stream().filter(u -> u.getRole().equals("Staff")).count();
        long customerCount = users.stream().filter(u -> u.getRole().equals("Customer")).count();
        long adminCount = users.stream().filter(u -> u.getRole().equals("Admin")).count();

        report.append("\n👥 USER STATISTICS\n");
        report.append("-".repeat(40)).append("\n");
        report.append(String.format("Total Users: %d\n", users.size()));
        report.append(String.format("Administrators: %d\n", adminCount));
        report.append(String.format("Staff Members: %d\n", staffCount));
        report.append(String.format("Customers: %d\n", customerCount));

        report.append("\n" + "=".repeat(50));
        report.append("\n          END OF REPORT\n");
        report.append("=".repeat(50));

        return report.toString();
    }

    // Utility Methods

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(44, 62, 80));
        statusBar.setPreferredSize(new Dimension(1200, 30));

        JLabel statusLabel = new JLabel(" Hotel Management System v1.0 - Admin Mode");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        statusBar.add(statusLabel, BorderLayout.WEST);

        JLabel dateLabel = new JLabel(new java.text.SimpleDateFormat("EEEE, MMMM d, yyyy").format(new java.util.Date()));
        dateLabel.setForeground(Color.WHITE);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        statusBar.add(dateLabel, BorderLayout.EAST);

        return statusBar;
    }

    private int getTotalRooms() {
        try {
            return roomController.getAllRooms().size();
        } catch (Exception e) {
            return 0;
        }
    }

    private int getAvailableRooms() {
        try {
            return (int) roomController.getAllRooms().stream()
                    .filter(r -> "Available".equals(r.getStatus())).count();
        } catch (Exception e) {
            return 0;
        }
    }

    private int getTotalUsers() {
        try {
            return userController.getAllUsers().size();
        } catch (Exception e) {
            return 0;
        }
    }

    private int getActiveBookings() {
        // Placeholder for booking system
        return 0;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            new LoginForm();
            dispose();
        }
    }
}
package view;

import controller.BookingController;
import controller.RoomController;
import controller.PaymentController;
import model.Booking;
import model.Room;
import model.User;
import model.Payment;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class StaffDashboard extends JFrame {

    private User user;
    private CardLayout cardLayout;
    private JPanel contentPanel;

    // Table Models
    private DefaultTableModel roomTableModel, paymentTableModel;

    // Form Components
    private JTextField txtRoomNum, txtPrice;
    private JComboBox<String> comboType, comboStatus;

    // Controllers
    private RoomController roomController = new RoomController();
    private BookingController bookingController = new BookingController();
    private PaymentController paymentController = new PaymentController();

    public StaffDashboard(User user) {
        this.user = user;

        setTitle("Hotel Room Management - Staff Dashboard");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        //Header Section
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(41, 128, 185));
        header.setPreferredSize(new Dimension(1000, 70));
        header.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

        JLabel title = new JLabel("STAFF CONTROL PANEL");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        JLabel userLabel = new JLabel("Logged in as: " + user.getUsername());
        userLabel.setForeground(Color.WHITE);

        header.add(title, BorderLayout.WEST);
        header.add(userLabel, BorderLayout.EAST);

        // Navigation Section
        JPanel navPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        navPanel.setBackground(new Color(236, 240, 241));
        navPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JButton btnView = createNavButton("View Bookings");
        JButton btnConfirm = createNavButton("Confirm Bookings");
        JButton btnPayments = createNavButton("Payments");
        JButton btnRooms = createNavButton("Manage Rooms");

        navPanel.add(btnView); navPanel.add(btnConfirm);
        navPanel.add(btnPayments); navPanel.add(btnRooms);

        // Content Panel (CardLayout)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        contentPanel.add(createWelcomePanel(), "HOME");
        contentPanel.add(createManageRoomsPanel(), "ROOMS");
        contentPanel.add(createBookingListPanel("All Bookings", false), "BOOKINGS");
        contentPanel.add(createBookingListPanel("Pending Approvals", true), "CONFIRM");
        contentPanel.add(createPaymentsPanel(), "PAYMENTS");

        // Button Actions
        btnView.addActionListener(e -> {
            contentPanel.add(createBookingListPanel("All Bookings", false), "BOOKINGS"); // Refresh data
            cardLayout.show(contentPanel, "BOOKINGS");
        });

        btnConfirm.addActionListener(e -> {
            contentPanel.add(createBookingListPanel("Pending Approvals", true), "CONFIRM"); // Refresh data
            cardLayout.show(contentPanel, "CONFIRM");
        });

        btnPayments.addActionListener(e -> {
            cardLayout.show(contentPanel, "PAYMENTS");
            loadPaymentsData();
        });

        btnRooms.addActionListener(e -> {
            cardLayout.show(contentPanel, "ROOMS");
            loadRoomsData();
        });

        // Footer Section
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(231, 76, 60));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.addActionListener(e -> { new LoginForm().setVisible(true); dispose(); });
        footer.add(logoutBtn);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(header, BorderLayout.NORTH);
        topContainer.add(navPanel, BorderLayout.SOUTH);

        add(topContainer, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        loadRoomsData();
        setVisible(true);
    }

    private JPanel createManageRoomsPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Update Room Details"));
        form.setPreferredSize(new Dimension(350, 400));

        txtRoomNum = new JTextField(); txtRoomNum.setEditable(false);
        txtPrice = new JTextField();
        comboType = new JComboBox<>(new String[]{"Single", "Double", "Suite", "Luxury"});
        comboStatus = new JComboBox<>(new String[]{"Available", "Occupied", "Cleaning"});

        JButton updateBtn = new JButton("Update Room Info");
        updateBtn.setBackground(new Color(46, 204, 113));
        updateBtn.setForeground(Color.WHITE);

        form.add(new JLabel("Room ID:")); form.add(txtRoomNum);
        form.add(new JLabel("Type:")); form.add(comboType);
        form.add(new JLabel("Price:")); form.add(txtPrice);
        form.add(new JLabel("Status:")); form.add(comboStatus);
        form.add(new JLabel("")); form.add(updateBtn);

        roomTableModel = new DefaultTableModel(new String[]{"Room ID", "Type", "Price", "Status"}, 0);
        JTable roomTable = new JTable(roomTableModel);

        roomTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = roomTable.getSelectedRow();
                txtRoomNum.setText(roomTableModel.getValueAt(row, 0).toString());
                comboType.setSelectedItem(roomTableModel.getValueAt(row, 1).toString());
                txtPrice.setText(roomTableModel.getValueAt(row, 2).toString());
                comboStatus.setSelectedItem(roomTableModel.getValueAt(row, 3).toString());
            }
        });

        updateBtn.addActionListener(e -> {
            try {
                if (roomController.updateRoomByStaff(txtRoomNum.getText(), comboType.getSelectedItem().toString(),
                        Double.parseDouble(txtPrice.getText()), comboStatus.getSelectedItem().toString())) {
                    JOptionPane.showMessageDialog(this, "Success!");
                    loadRoomsData();
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Invalid Input!"); }
        });

        panel.add(form, BorderLayout.WEST);
        panel.add(new JScrollPane(roomTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPaymentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel lbl = new JLabel("Transaction History (All Customer Payments)");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(lbl, BorderLayout.NORTH);

        paymentTableModel = new DefaultTableModel(new String[]{"ID", "Booking ID", "Amount", "Method", "Date", "Transaction ID"}, 0);
        JTable paymentTable = new JTable(paymentTableModel);
        panel.add(new JScrollPane(paymentTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBookingListPanel(String title, boolean showConfirmBtn) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(lbl, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(new String[]{"Booking ID", "Room ID", "Check-In", "Status"}, 0);
        JTable table = new JTable(model);

        List<Booking> bookings = bookingController.getAllBookings();
        for (Booking b : bookings) {
            // Confirm mode එකේදී 'Pending' ඒවා පමණක් පෙන්වන්න
            if (showConfirmBtn && !b.getStatus().equalsIgnoreCase("Pending")) continue;
            model.addRow(new Object[]{b.getId(), b.getRoomId(), b.getCheckInDate(), b.getStatus()});
        }

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        if (showConfirmBtn) {
            JButton confirmBtn = new JButton("Confirm Selected Booking");
            confirmBtn.setBackground(new Color(52, 152, 219));
            confirmBtn.setForeground(Color.WHITE);
            confirmBtn.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    int id = (int) model.getValueAt(row, 0);
                    if (bookingController.confirmBooking(id)) {
                        JOptionPane.showMessageDialog(this, "Booking ID " + id + " Confirmed!");
                        // Refresh current panel
                        contentPanel.add(createBookingListPanel("Pending Approvals", true), "CONFIRM");
                        cardLayout.show(contentPanel, "CONFIRM");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a booking to confirm!");
                }
            });
            panel.add(confirmBtn, BorderLayout.SOUTH);
        }
        return panel;
    }

    private void loadRoomsData() {
        roomTableModel.setRowCount(0);
        List<Room> rooms = roomController.getAllRooms();
        for (Room r : rooms) {
            roomTableModel.addRow(new Object[]{r.getId(), r.getType(), r.getPrice(), r.getStatus()});
        }
    }

    private void loadPaymentsData() {
        paymentTableModel.setRowCount(0);
        List<Payment> payments = paymentController.getAllPayments();
        if (payments != null) {
            for (Payment p : payments) {
                paymentTableModel.addRow(new Object[]{
                        p.getId(), p.getBookingId(), p.getAmount(), p.getMethod(), p.getPaymentDate(), p.getTransactionId()
                });
            }
        }
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.add(new JLabel("Welcome to the Staff System. Select an option above."));
        return panel;
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(52, 152, 219));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return btn;
    }
}

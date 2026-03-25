package view;

import javax.swing.*;
import controller.PaymentController;
import model.Payment;
import java.awt.*;

public class PaymentForm extends JFrame {

    public PaymentForm(int bookingId, double amount) {
        setTitle("Payment Form");
        setSize(400, 300);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // UI Components
        JLabel lblBooking = new JLabel("Booking ID:");
        lblBooking.setBounds(30, 30, 100, 25);
        JTextField txtBooking = new JTextField(String.valueOf(bookingId));
        txtBooking.setBounds(140, 30, 200, 25);
        txtBooking.setEditable(false);

        JLabel lblAmount = new JLabel("Amount (Rs.):");
        lblAmount.setBounds(30, 70, 100, 25);
        JTextField txtAmount = new JTextField(String.valueOf(amount));
        txtAmount.setBounds(140, 70, 200, 25);
        txtAmount.setEditable(false);

        JLabel lblMethod = new JLabel("Method:");
        lblMethod.setBounds(30, 110, 100, 25);
        String[] methods = {"Credit Card", "Debit Card", "Cash", "Online Transfer"};
        JComboBox<String> cmbMethod = new JComboBox<>(methods);
        cmbMethod.setBounds(140, 110, 200, 25);

        JButton btnPay = new JButton("Confirm Payment");
        btnPay.setBounds(140, 160, 200, 35);
        btnPay.setBackground(new Color(41, 128, 185));
        btnPay.setForeground(Color.WHITE);

        add(lblBooking); add(txtBooking);
        add(lblAmount); add(txtAmount);
        add(lblMethod); add(cmbMethod);
        add(btnPay);

        PaymentController paymentCtrl = new PaymentController();

        btnPay.addActionListener(e -> {
            Payment payment = new Payment();
            payment.setBookingId(bookingId);
            payment.setAmount(amount);
            payment.setMethod(cmbMethod.getSelectedItem().toString());
            payment.setStatus("Completed");

            // set SQL date
            payment.setPaymentDate(new java.sql.Date(System.currentTimeMillis()));
            payment.setTransactionId("TXN-" + System.currentTimeMillis());

            if (paymentCtrl.addPayment(payment)) {
                JOptionPane.showMessageDialog(this, "✅ Payment Successful!\nTransaction ID: " + payment.getTransactionId());
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Payment Failed! Check Database.");
            }
        });

        setVisible(true);
    }
}
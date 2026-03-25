package controller;

import database.DBConnection;
import model.Payment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentController {

    public boolean addPayment(Payment payment) {
        String query = "INSERT INTO payments (booking_id, amount, method, status, payment_date, transaction_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, payment.getBookingId());
            ps.setDouble(2, payment.getAmount());
            ps.setString(3, payment.getMethod());
            ps.setString(4, payment.getStatus());
            ps.setTimestamp(5, new Timestamp(payment.getPaymentDate().getTime()));
            ps.setString(6, payment.getTransactionId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Payment> getPaymentsByUser(int userId) {
        List<Payment> list = new ArrayList<>();
        String query = "SELECT p.* FROM payments p JOIN bookings b ON p.booking_id = b.id WHERE b.user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Payment p = new Payment();
                p.setId(rs.getInt("id"));
                p.setBookingId(rs.getInt("booking_id"));
                p.setAmount(rs.getDouble("amount"));
                p.setMethod(rs.getString("method"));
                p.setStatus(rs.getString("status"));
                p.setPaymentDate(rs.getTimestamp("payment_date"));
                p.setTransactionId(rs.getString("transaction_id"));
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // To view all payments to staff
    public List<Payment> getAllPayments() {
        List<Payment> list = new ArrayList<>();
        String query = "SELECT * FROM payments ORDER BY id DESC";
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Payment p = new Payment();
                p.setId(rs.getInt("id"));
                p.setBookingId(rs.getInt("booking_id"));
                p.setAmount(rs.getDouble("amount"));
                p.setMethod(rs.getString("method"));
                p.setStatus(rs.getString("status"));
                p.setPaymentDate(rs.getTimestamp("payment_date"));
                p.setTransactionId(rs.getString("transaction_id"));
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
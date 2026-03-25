package controller;

import database.DBConnection;
import model.Booking;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingController {


    public int createBooking(Booking booking) {

        String query = "INSERT INTO bookings (user_id, room_id, check_in, check_out, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, booking.getUserId());
            ps.setInt(2, booking.getRoomId());
            ps.setDate(3, booking.getCheckInDate());
            ps.setDate(4, booking.getCheckOutDate());
            ps.setString(5, booking.getStatus());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) return rs.getInt(1); // අලුතින් හැදුණු Booking ID එක ලබා දෙයි
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in createBooking: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    //Retrieve all bookings related to a particular customer
    public List<Booking> getBookingsByUser(int userId) {
        List<Booking> list = new ArrayList<>();
        // Room Number එක පෙන්වීමට JOIN එකක් භාවිතා කරයි
        String query = "SELECT b.*, r.room_number FROM bookings b " +
                "JOIN rooms r ON b.room_id = r.id " +
                "WHERE b.user_id = ? ORDER BY b.id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Booking b = new Booking();
                b.setId(rs.getInt("id"));
                b.setUserId(rs.getInt("user_id"));
                b.setRoomId(rs.getInt("room_id"));
                b.setRoomNumber(rs.getString("room_number"));
                b.setCheckInDate(rs.getDate("check_in"));
                b.setCheckOutDate(rs.getDate("check_out"));
                b.setStatus(rs.getString("status"));
                list.add(b);
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in getBookingsByUser: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // Receiving all customer bookings (for staff)
    public List<Booking> getAllBookings() {
        List<Booking> list = new ArrayList<>();
        String query = "SELECT b.*, r.room_number FROM bookings b " +
                "JOIN rooms r ON b.room_id = r.id " +
                "ORDER BY b.id DESC";

        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Booking b = new Booking();
                b.setId(rs.getInt("id"));
                b.setUserId(rs.getInt("user_id"));
                b.setRoomId(rs.getInt("room_id"));
                b.setRoomNumber(rs.getString("room_number"));
                b.setCheckInDate(rs.getDate("check_in"));
                b.setCheckOutDate(rs.getDate("check_out"));
                b.setStatus(rs.getString("status"));
                list.add(b);
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in getAllBookings: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // cancel booking
    public boolean cancelBooking(int bookingId) {
        String query = "DELETE FROM bookings WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, bookingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //  confirmBooking
    public boolean confirmBooking(int bookingId) {
        String query = "UPDATE bookings SET status = 'Confirmed' WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, bookingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
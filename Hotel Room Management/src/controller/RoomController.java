package controller;

import database.DBConnection;
import model.Room;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomController {

    // Add room (auto room number)
    public boolean addRoom(String type, double price) {
        String generatedRoomNo = "RM-" + (System.currentTimeMillis() % 10000);

        String query = "INSERT INTO rooms(room_number, type, price, status, description) VALUES(?, ?, ?, 'Available', 'Manual Entry')";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, generatedRoomNo);
            ps.setString(2, type);
            ps.setDouble(3, price);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Add room (object)
    public boolean addRoom(Room room) {
        String query = "INSERT INTO rooms(room_number, type, price, status, description) VALUES(?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, room.getRoomNumber());
            ps.setString(2, room.getType());
            ps.setDouble(3, room.getPrice());
            ps.setString(4, room.getStatus());
            ps.setString(5, room.getDescription());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Search rooms
    public List<Room> searchRooms(String type, String status, Double minPrice, Double maxPrice) {
        List<Room> rooms = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM rooms WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (type != null && !type.isEmpty() && !type.equals("All")) {
            query.append(" AND type = ?");
            params.add(type);
        }

        if (status != null && !status.isEmpty() && !status.equals("All")) {
            query.append(" AND status = ?");
            params.add(status);
        }

        if (minPrice != null) {
            query.append(" AND price >= ?");
            params.add(minPrice);
        }

        if (maxPrice != null) {
            query.append(" AND price <= ?");
            params.add(maxPrice);
        }

        query.append(" ORDER BY id DESC");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rooms;
    }

    //  Get room by ID
    public Room getRoomById(int roomId) {
        String query = "SELECT * FROM rooms WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapResultSetToRoom(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    //  Get all rooms
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT * FROM rooms ORDER BY id DESC";

        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rooms;
    }

    //  Update room - Real
    public boolean updateRoom(Room room) {
        String query = "UPDATE rooms SET room_number = ?, type = ?, price = ?, status = ?, description = ? WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, room.getRoomNumber());
            ps.setString(2, room.getType());
            ps.setDouble(3, room.getPrice());
            ps.setString(4, room.getStatus());
            ps.setString(5, room.getDescription());
            ps.setInt(6, room.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //  Update room details by Staff using Room Number

    public boolean updateRoomByStaff(String roomNo, String type, double price, String status) {
        String query = "UPDATE rooms SET type = ?, price = ?, status = ? WHERE room_number = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, type);
            ps.setDouble(2, price);
            ps.setString(3, status);
            ps.setString(4, roomNo);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //  Update status
    public boolean updateRoomStatus(int roomId, String status) {
        String query = "UPDATE rooms SET status = ? WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, status);
            ps.setInt(2, roomId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //  Delete room
    public boolean deleteRoom(int roomId) {
        String query = "DELETE FROM rooms WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, roomId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //  Map ResultSet - Room object
    private Room mapResultSetToRoom(ResultSet rs) throws SQLException {
        Room r = new Room();
        r.setId(rs.getInt("id"));
        r.setRoomNumber(rs.getString("room_number"));
        r.setType(rs.getString("type"));
        r.setPrice(rs.getDouble("price"));
        r.setStatus(rs.getString("status"));
        r.setDescription(rs.getString("description"));
        return r;
    }
}
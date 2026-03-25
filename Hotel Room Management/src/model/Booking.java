package model;

import java.sql.Date;

public class Booking {
    private int id;
    private int userId;
    private int roomId;
    private String roomNumber;
    private Date checkInDate;
    private Date checkOutDate;
    private int guests;
    private double totalAmount;
    private String status;

    // Default Constructor - Meka thama Dashboard eken use wenne
    public Booking() {
        long today = System.currentTimeMillis();
        this.checkInDate = new Date(today);
        this.checkOutDate = new Date(today + 86400000); // 1 day after
        this.status = "Pending";
        this.guests = 1;
        this.totalAmount = 0.0;
    }

    // Parametrized Constructor
    public Booking(int userId, int roomId, String checkInDate, String checkOutDate) {
        this.userId = userId;
        this.roomId = roomId;
        try {
            this.checkInDate = Date.valueOf(checkInDate);
            this.checkOutDate = Date.valueOf(checkOutDate);
        } catch (Exception e) {
            System.out.println("Date format error: Please use yyyy-mm-dd");
        }
        this.status = "Confirmed";
        this.guests = 1;
        this.totalAmount = 0.0;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public Date getCheckInDate() { return checkInDate; }
    public void setCheckInDate(Date checkInDate) { this.checkInDate = checkInDate; }
    public Date getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(Date checkOutDate) { this.checkOutDate = checkOutDate; }
    public int getGuests() { return guests; }
    public void setGuests(int guests) { this.guests = guests; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
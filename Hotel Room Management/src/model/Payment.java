package model;

import java.util.Date;

public class Payment {
    private int id;
    private int bookingId;
    private double amount;
    private String method;
    private String status;
    private String transactionId;
    private Date paymentDate;

    public Payment() {}

    public Payment(int bookingId, double amount, String method) {
        this.bookingId = bookingId;
        this.amount = amount;
        this.method = method;
        this.status = "Completed";
        this.paymentDate = new Date();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public Date getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Date paymentDate) { this.paymentDate = paymentDate; }
}
package model;

import java.time.LocalDate;

public class Booking {
    public int bookingId, customerId, roomId;
    public String customerName; // YEH NAYI FIELD ADD KI HAI
    public LocalDate checkIn, checkOut;
    public double totalBill;
    public String status;

    // Naya Constructor (Customer Name ke saath)
    public Booking(int bookingId, int customerId, String customerName, int roomId,
                   LocalDate checkIn, LocalDate checkOut, double totalBill, String status) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.roomId = roomId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalBill = totalBill;
        this.status = status;
    }
}
package model;

public class Room {
    protected int roomId;
    protected String roomNumber;
    protected double baseTariff;
    protected boolean available;

    public Room(int roomId, String roomNumber, double baseTariff, boolean available) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.baseTariff = baseTariff;
        this.available = available;
    }

    public double calculateTariff(int days) {
        return baseTariff * days; // overridden by subclasses
    }

    public int getRoomId() { return roomId; }
    public String getRoomNumber() { return roomNumber; }
    public double getBaseTariff() { return baseTariff; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean a) { this.available = a; }
    public String getRoomType() { return "Standard"; }
}
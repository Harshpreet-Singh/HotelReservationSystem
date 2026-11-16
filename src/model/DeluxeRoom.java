package model;

public class DeluxeRoom extends Room {
    public DeluxeRoom(int roomId, String roomNumber, double baseTariff, boolean available) {
        super(roomId, roomNumber, baseTariff, available);
    }
    @Override
    public double calculateTariff(int days) {
        return super.calculateTariff(days) + 500 * days; // deluxe surcharge
    }
    @Override
    public String getRoomType() { return "Deluxe"; }
}
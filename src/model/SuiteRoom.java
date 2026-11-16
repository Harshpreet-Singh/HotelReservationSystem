package model;

public class SuiteRoom extends Room {
    public SuiteRoom(int roomId, String roomNumber, double baseTariff, boolean available) {
        super(roomId, roomNumber, baseTariff, available);
    }
    @Override
    public double calculateTariff(int days) {
        return super.calculateTariff(days) + 1500 * days;
    }
    @Override
    public String getRoomType() { return "Suite"; }
}
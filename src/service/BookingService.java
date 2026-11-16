package service;

import db.DBConnection;
import exceptions.RoomOccupiedException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.Room;

public class BookingService {

    // 1. List available rooms
    public List<Room> getAvailableRooms() throws Exception {
        List<Room> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "SELECT * FROM rooms WHERE is_available=TRUE");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("room_id");
                String no = rs.getString("room_number");
                String type = rs.getString("room_type");
                double t = rs.getDouble("base_tariff");
                Room r;
                switch (type) {
                    case "Deluxe": r = new model.DeluxeRoom(id, no, t, true); break;
                    case "Suite":  r = new model.SuiteRoom(id, no, t, true);  break;
                    default:       r = new Room(id, no, t, true);
                }
                list.add(r);
            }
        }
        return list;
    }

    // 2. Register customer
    public int registerCustomer(String name, String phone, String email) throws Exception {
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement check = c.prepareStatement("SELECT customer_id FROM customers WHERE phone = ?");
            check.setString(1, phone);
            ResultSet rs = check.executeQuery();
            if (rs.next()) return rs.getInt("customer_id"); 

            PreparedStatement ps = c.prepareStatement(
                "INSERT INTO customers(name, phone, email) VALUES(?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setString(3, email);
            ps.executeUpdate();
            ResultSet k = ps.getGeneratedKeys();
            k.next();
            return k.getInt(1);
        }
    }

    // 3. Book room
    public int bookRoom(int roomId, int customerId, LocalDate in, LocalDate out)
            throws Exception, RoomOccupiedException {
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement chk = c.prepareStatement(
                "SELECT is_available FROM rooms WHERE room_id=?");
            chk.setInt(1, roomId);
            ResultSet rs = chk.executeQuery();
            if (!rs.next() || !rs.getBoolean("is_available"))
                throw new RoomOccupiedException("Room " + roomId + " is occupied!");

            PreparedStatement ps = c.prepareStatement(
                "INSERT INTO bookings(customer_id,room_id,check_in,check_out,status) " +
                "VALUES(?,?,?,?, 'BOOKED')", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, customerId);
            ps.setInt(2, roomId);
            ps.setDate(3, Date.valueOf(in));
            ps.setDate(4, Date.valueOf(out));
            ps.executeUpdate();

            ResultSet k = ps.getGeneratedKeys();
            k.next();
            int bid = k.getInt(1);

            PreparedStatement up = c.prepareStatement(
                "UPDATE rooms SET is_available=FALSE WHERE room_id=?");
            up.setInt(1, roomId);
            up.executeUpdate();

            return bid;
        }
    }

    // 4. Check-out
    public double checkOutAndGenerateBill(int bookingId) throws Exception {
        try (Connection c = DBConnection.getConnection();
             CallableStatement cs = c.prepareCall("{call generate_bill(?, ?)}")) {
            cs.setInt(1, bookingId);
            cs.registerOutParameter(2, Types.DOUBLE);
            cs.execute();
            double bill = cs.getDouble(2);

            PreparedStatement ps = c.prepareStatement(
                "UPDATE rooms r JOIN bookings b ON r.room_id=b.room_id " +
                "SET r.is_available=TRUE, b.status='COMPLETED' WHERE b.booking_id=?");
            ps.setInt(1, bookingId);
            ps.executeUpdate();
            return bill;
        }
    }

    // 5. Booking history (UPDATED: Name JOIN & Estimated Bill Logic)
    public List<model.Booking> getBookingHistory() throws Exception {
        List<model.Booking> list = new ArrayList<>();
        
        // Query mein customers ka JOIN aur rooms ka tariff add kiya hai
        String sql = "SELECT b.*, c.name as customer_name, r.base_tariff, r.room_type " +
                     "FROM bookings b " +
                     "JOIN customers c ON b.customer_id = c.customer_id " +
                     "JOIN rooms r ON b.room_id = r.room_id " +
                     "ORDER BY b.booking_id DESC";
                     
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int bid = rs.getInt("booking_id");
                int cid = rs.getInt("customer_id");
                String cName = rs.getString("customer_name");
                int rid = rs.getInt("room_id");
                LocalDate in = rs.getDate("check_in").toLocalDate();
                LocalDate out = rs.getDate("check_out").toLocalDate();
                double bill = rs.getDouble("total_bill");
                String status = rs.getString("status");
                
                // Agar bill 0 hai (yaani abhi BOOKED hai), toh estimated bill calculate karo
                if (bill == 0 && status.equals("BOOKED")) {
                    long days = java.time.temporal.ChronoUnit.DAYS.between(in, out);
                    if (days <= 0) days = 1; // minimum 1 day
                    
                    double tariff = rs.getDouble("base_tariff");
                    String type = rs.getString("room_type");
                    
                    // Polymorphism apply karke estimated bill nikalo
                    model.Room tempRoom;
                    if (type.equals("Deluxe")) tempRoom = new model.DeluxeRoom(rid, "", tariff, true);
                    else if (type.equals("Suite")) tempRoom = new model.SuiteRoom(rid, "", tariff, true);
                    else tempRoom = new model.Room(rid, "", tariff, true);
                    
                    double baseCost = tempRoom.calculateTariff((int) days);
                    bill = baseCost + (baseCost * 0.18); // 18% tax estimation
                }

                list.add(new model.Booking(bid, cid, cName, rid, in, out, bill, status));
            }
        }
        return list;
    }
        // 6. Admin: Update Room Price
    public void updateRoomPrice(int roomId, double newPrice) throws Exception {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "UPDATE rooms SET base_tariff=? WHERE room_id=?")) {
            ps.setDouble(1, newPrice);
            ps.setInt(2, roomId);
            ps.executeUpdate();
        }
    }

    // 7. Admin: Add New Room
    public void addRoom(String roomNumber, String roomType, double baseTariff) throws Exception {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "INSERT INTO rooms(room_number, room_type, base_tariff, is_available) VALUES(?,?,?,TRUE)")) {
            ps.setString(1, roomNumber);
            ps.setString(2, roomType);
            ps.setDouble(3, baseTariff);
            ps.executeUpdate();
        }
    }

    // 8. Admin: Delete Room (Optional)
    public void deleteRoom(int roomId) throws Exception {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM rooms WHERE room_id=?")) {
            ps.setInt(1, roomId);
            ps.executeUpdate();
        }
    }
}
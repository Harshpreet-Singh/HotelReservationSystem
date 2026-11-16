package service;

import db.DBConnection;
import java.sql.*;
import java.time.LocalDate;

public class AutoReleaseThread extends Thread {
    @Override
    public void run() {
        while (true) {
            try {
                Connection con = DBConnection.getConnection();
                String sql = "UPDATE bookings b JOIN rooms r ON b.room_id=r.room_id " +
                             "SET r.is_available=TRUE, b.status='COMPLETED' " +
                             "WHERE b.status='BOOKED' AND b.check_out < ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setDate(1, Date.valueOf(LocalDate.now()));
                int n = ps.executeUpdate();
                if (n > 0) System.out.println("[AutoRelease] Released " + n + " room(s).");
                con.close();
                Thread.sleep(60_000); // every minute
            } catch (Exception e) {
                System.out.println("AutoRelease error: " + e.getMessage());
            }
        }
    }
}
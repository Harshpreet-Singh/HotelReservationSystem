import service.AutoReleaseThread;

public class Main {
    public static void main(String[] args) {
        new AutoReleaseThread().start();   // Unit 3 — background thread
        javax.swing.SwingUtilities.invokeLater(() -> new ui.HotelGUI().setVisible(true));
    }
}
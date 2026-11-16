package ui;

import com.toedter.calendar.JDateChooser;
import model.Room;
import service.BookingService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class HotelGUI extends JFrame {

    private final BookingService service = new BookingService();

    // Theme colors
    private static final Color PRIMARY   = new Color(37, 99, 235);   // blue
    private static final Color SUCCESS   = new Color(22, 163, 74);   // green
    private static final Color DANGER    = new Color(220, 38, 38);   // red
    private static final Color BG        = new Color(245, 247, 250);
    private static final Color CARD_BG   = Color.WHITE;
    private static final Color TEXT_DARK = new Color(30, 41, 59);

    // Form widgets
    private JTextField nameF, phoneF, emailF;
    private JComboBox<String> roomDropdown;
    private JDateChooser checkInChooser, checkOutChooser;
    private JLabel statusBar;
    private JTextArea historyArea;

    // Map "ID | No | Type" -> roomId
    private java.util.Map<String, Integer> roomKeyToId = new java.util.HashMap<>();

    public HotelGUI() {
        setTitle("Hotel Room Reservation System");
        setSize(920, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(10, 10));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        refreshRoomDropdown();
    }

            // ---------- HEADER ----------
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY);
        header.setBorder(new EmptyBorder(18, 24, 18, 24));

        // --- Left Side ---
        JLabel title = new JLabel("Hotel Room Reservation System");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel subtitle = new JLabel("OOPJ Project using Java Swing & JDBC");
        subtitle.setForeground(new Color(219, 234, 254));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setOpaque(false);
        left.add(title);
        left.add(subtitle);

        // --- Right Side (Flat Admin Button) ---
        JButton adminBtn = new JButton("Admin Login");
        adminBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        adminBtn.setForeground(Color.WHITE);
        adminBtn.setBackground(new Color(37, 99, 235)); // Same as PRIMARY
        adminBtn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.WHITE, 1, true), // White outline
                new EmptyBorder(6, 12, 6, 12)
        ));
        adminBtn.setFocusPainted(false);
        adminBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover Effect
        adminBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                adminBtn.setBackground(new Color(29, 78, 216)); // Darker blue on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                adminBtn.setBackground(new Color(37, 99, 235));
            }
        });

        adminBtn.addActionListener(e -> showAdminLogin());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
        right.setOpaque(false);
        right.add(adminBtn);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        
        return header;
    }

    // ---------- CENTER (cards) ----------
    private JPanel buildCenter() {
        JPanel center = new JPanel(new GridLayout(1, 2, 16, 16));
        center.setBackground(BG);
        center.setBorder(new EmptyBorder(16, 16, 16, 16));
        center.add(buildBookingCard());
        center.add(buildHistoryCard());
        return center;
    }

    private JPanel buildBookingCard() {
        JPanel card = card("New Booking");

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        nameF  = styledField();
        phoneF = styledField();
        emailF = styledField();

        roomDropdown = new JComboBox<>();
        roomDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roomDropdown.setBackground(Color.WHITE);

        checkInChooser  = new JDateChooser();
        checkOutChooser = new JDateChooser();
        styleDateChooser(checkInChooser);
        styleDateChooser(checkOutChooser);

        // Row 1
        addRow(form, gbc, 0, "Name", nameF, "Phone", phoneF);
        // Row 2
        addRow(form, gbc, 1, "Email", emailF, "Room", roomDropdown);
        // Row 3
        addRow(form, gbc, 2, "Check-in", checkInChooser, "Check-out", checkOutChooser);

        card.add(form, BorderLayout.CENTER);

        // Buttons
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        JButton refreshBtn = pillButton("Refresh Rooms", new Color(100, 116, 139));
        JButton bookBtn    = pillButton("Book Room", SUCCESS);

        refreshBtn.addActionListener(e -> refreshRoomDropdown());
        bookBtn.addActionListener(e -> onBook());

        actions.add(refreshBtn);
        actions.add(bookBtn);
        card.add(actions, BorderLayout.SOUTH);

        return card;
    }

    private JPanel buildHistoryCard() {
        JPanel card = card("Booking History");

        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        historyArea.setBackground(new Color(248, 250, 252));
        JScrollPane scroll = new JScrollPane(historyArea);
        scroll.setBorder(new LineBorder(new Color(226, 232, 240)));

        card.add(scroll, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        JButton histBtn     = pillButton("Load History", PRIMARY);
        JButton checkoutBtn = pillButton("Check Out", DANGER);

        histBtn.addActionListener(e -> onLoadHistory());
        checkoutBtn.addActionListener(e -> onCheckout());

        actions.add(histBtn);
        actions.add(checkoutBtn);
        card.add(actions, BorderLayout.SOUTH);

        return card;
    }

    private JPanel card(String title) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(16, 16, 16, 16)));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(TEXT_DARK);
        card.add(lbl, BorderLayout.NORTH);

        return card;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(241, 245, 249));
        bar.setBorder(new EmptyBorder(6, 16, 6, 16));

        statusBar = new JLabel("Ready");
        statusBar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusBar.setForeground(new Color(71, 85, 105));

        bar.add(statusBar, BorderLayout.WEST);
        return bar;
    }

    // ---------- HELPERS ----------
    private JTextField styledField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(203, 213, 225), 1, true),
                new EmptyBorder(6, 8, 6, 8)));
        return f;
    }

    private void styleDateChooser(JDateChooser c) {
        c.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        c.setDateFormatString("yyyy-MM-dd");
        c.setPreferredSize(new Dimension(180, 34));
    }

    private JButton pillButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setBackground(bg);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10, 16, 10, 16));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void addRow(JPanel form, GridBagConstraints gbc, int row,
                        String l1, JComponent c1, String l2, JComponent c2) {
        gbc.gridy = row;

        gbc.gridx = 0; gbc.weightx = 0;
        form.add(label(l1), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        form.add(c1, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        form.add(label(l2), gbc);

        gbc.gridx = 3; gbc.weightx = 1;
        form.add(c2, gbc);
    }

    private JLabel label(String s) {
        JLabel l = new JLabel(s);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(new Color(51, 65, 85));
        return l;
    }

    private void setStatus(String msg, Color color) {
        statusBar.setText(msg);
        statusBar.setForeground(color);
    }

    // ---------- LOGIC ----------
    private void refreshRoomDropdown() {
        try {
            List<Room> rooms = service.getAvailableRooms();
            roomDropdown.removeAllItems();
            roomKeyToId.clear();
            for (Room r : rooms) {
                String key = "ID " + r.getRoomId() + " · " + r.getRoomNumber()
                        + " · " + r.getRoomType() + " · ₹" + (int) r.getBaseTariff();
                roomDropdown.addItem(key);
                roomKeyToId.put(key, r.getRoomId());
            }
            setStatus("Loaded " + rooms.size() + " available room(s).", SUCCESS);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            setStatus("Failed to load rooms.", DANGER);
        }
    }

    private void onBook() {
        if (roomDropdown.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "No room selected.");
            return;
        }
        String key = (String) roomDropdown.getSelectedItem();
        int roomId = roomKeyToId.get(key);

        Date inD  = checkInChooser.getDate();
        Date outD = checkOutChooser.getDate();
        if (inD == null || outD == null) {
            JOptionPane.showMessageDialog(this, "Please select both dates.");
            return;
        }

        LocalDate in  = inD.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate out = outD.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (!out.isAfter(in)) {
            JOptionPane.showMessageDialog(this, "Check-out must be after check-in.");
            return;
        }

        try {
            int cid = service.registerCustomer(nameF.getText(), phoneF.getText(), emailF.getText());
            int bid = service.bookRoom(roomId, cid, in, out);
            JOptionPane.showMessageDialog(this, "Booked!\nBooking ID = " + bid);
            setStatus("Booking #" + bid + " created.", SUCCESS);
            refreshRoomDropdown();
            onLoadHistory();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            setStatus("Booking failed: " + ex.getMessage(), DANGER);
        }
    }

    private void onLoadHistory() {
        try {
            List<model.Booking> list = service.getBookingHistory();
            StringBuilder sb = new StringBuilder();
            
            // Naya header format (Customer Name ke saath)
            sb.append(String.format("%-4s | %-15s | %-5s | %-12s | %-12s | %-10s | %-10s%n",
                    "ID", "Customer Name", "Room", "Check-In", "Check-Out", "Bill", "Status"));
            sb.append("─".repeat(88)).append("\n");
            
            for (var b : list) {
                String billDisplay;
                if (b.status.equals("BOOKED")) {
                    billDisplay = "₹" + Math.round(b.totalBill) + " (Est)";
                } else {
                    billDisplay = "₹" + Math.round(b.totalBill);
                }
                
                // Agar naam bahut lamba hai toh cut kar do taaki alignment kharab na ho
                String name = b.customerName;
                if (name.length() > 15) name = name.substring(0, 13) + "..";

                sb.append(String.format("%-4d | %-15s | %-5d | %-12s | %-12s | %-10s | %-10s%n",
                        b.bookingId, name, b.roomId,
                        b.checkIn, b.checkOut, billDisplay, b.status));
            }
            historyArea.setText(sb.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void onCheckout() {
        String idStr = JOptionPane.showInputDialog(this, "Enter Booking ID:");
        if (idStr == null || idStr.isBlank()) return;
        try {
            double bill = service.checkOutAndGenerateBill(Integer.parseInt(idStr));
            String billText = new StringBuffer()
                    .append("=========== HOTEL BILL ===========\n")
                    .append("Booking ID : ").append(idStr).append("\n")
                    .append("GST (18%)  included\n")
                    .append("Total Bill : ₹").append(Math.round(bill)).append("\n")
                    .append("==================================")
                    .toString();
            JOptionPane.showMessageDialog(this, billText);
            setStatus("Checked out booking #" + idStr + " · ₹" + Math.round(bill), SUCCESS);
            refreshRoomDropdown();
            onLoadHistory();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            setStatus("Checkout failed.", DANGER);
        }
    }
    private void showAdminLogin() {
        String pass = JOptionPane.showInputDialog(this, "Enter Admin Password:", "Admin Login", JOptionPane.QUESTION_MESSAGE);
        if ("admin123".equals(pass)) {
            showAdminPanel();
        } else if (pass != null) {
            JOptionPane.showMessageDialog(this, "Wrong password!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAdminPanel() {
        JFrame adminFrame = new JFrame("Admin Dashboard");
        adminFrame.setSize(500, 520);
        adminFrame.setLocationRelativeTo(this);
        adminFrame.getContentPane().setBackground(new Color(245, 247, 250));
        adminFrame.setLayout(new BorderLayout(10, 10));

        // --- Top Header ---
        JPanel topHeader = new JPanel();
        topHeader.setBackground(new Color(71, 85, 105)); // Grayish color for admin
        topHeader.setBorder(new EmptyBorder(15, 20, 15, 20));
        topHeader.setLayout(new BorderLayout());
        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        topHeader.add(title, BorderLayout.WEST);
        adminFrame.add(topHeader, BorderLayout.NORTH);

        // --- Main Content Panel ---
        JPanel content = new JPanel();
        content.setBackground(new Color(245, 247, 250));
        content.setBorder(new EmptyBorder(10, 20, 20, 20));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // Room Select Dropdown Data
        JComboBox<String> roomSelect = new JComboBox<>();
        java.util.Map<String, Integer> roomMap = new java.util.HashMap<>();
        try {
            // Saare rooms fetch kar rahe hain (available + occupied)
            java.sql.Connection c = db.DBConnection.getConnection();
            java.sql.PreparedStatement ps = c.prepareStatement("SELECT room_id, room_number FROM rooms ORDER BY room_id");
            java.sql.ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String key = "Room " + rs.getString("room_number") + " (ID: " + rs.getInt("room_id") + ")";
                roomSelect.addItem(key);
                roomMap.put(key, rs.getInt("room_id"));
            }
            c.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        roomSelect.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        roomSelect.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // ==========================================
        // SECTION 1: Update or Delete Existing Room
        // ==========================================
        JPanel updatePanel = createSectionPanel("Update / Delete Room");
        JPanel updateForm = new JPanel(new GridBagLayout());
        updateForm.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Select Room
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        updateForm.add(new JLabel("Select Room:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        updateForm.add(roomSelect, gbc);

        // Row 2: New Price
        JTextField newPriceF = new JTextField();
        newPriceF.setPreferredSize(new Dimension(200, 35));
        newPriceF.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        updateForm.add(new JLabel("New Price (₹):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        updateForm.add(newPriceF, gbc);

        // Row 3: Buttons
        JPanel btnPanel1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel1.setOpaque(false);
        
        JButton deleteBtn = pillButton("Delete Room", DANGER);
        deleteBtn.addActionListener(e -> {
            String selected = (String) roomSelect.getSelectedItem();
            if (selected == null) return;
            int rid = roomMap.get(selected);
            int confirm = JOptionPane.showConfirmDialog(adminFrame, 
                    "Are you sure you want to delete this room?\n(Bookings linked to this room might cause errors)", 
                    "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    service.deleteRoom(rid);
                    JOptionPane.showMessageDialog(adminFrame, "Room Deleted Successfully!");
                    adminFrame.dispose(); // Close and reopen to refresh
                    showAdminPanel();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(adminFrame, "Error: " + ex.getMessage());
                }
            }
        });

        JButton updateBtn = pillButton("Update Price", new Color(234, 179, 8)); // Yellowish
        updateBtn.setForeground(Color.BLACK);
        updateBtn.addActionListener(e -> {
            try {
                int rid = roomMap.get((String) roomSelect.getSelectedItem());
                double price = Double.parseDouble(newPriceF.getText());
                service.updateRoomPrice(rid, price);
                JOptionPane.showMessageDialog(adminFrame, "Price Updated Successfully!");
                newPriceF.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(adminFrame, "Please enter a valid number.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(adminFrame, "Error: " + ex.getMessage());
            }
        });

        btnPanel1.add(deleteBtn);
        btnPanel1.add(updateBtn);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        updateForm.add(btnPanel1, gbc);

        updatePanel.add(updateForm, BorderLayout.CENTER);
        content.add(updatePanel);
        content.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer

        // ==========================================
        // SECTION 2: Add New Room
        // ==========================================
        JPanel addPanel = createSectionPanel("Add New Room");
        JPanel addForm = new JPanel(new GridBagLayout());
        addForm.setOpaque(false);
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(5, 5, 5, 5);
        gbc2.fill = GridBagConstraints.HORIZONTAL;

        JTextField roomNoF = new JTextField();
        roomNoF.setPreferredSize(new Dimension(200, 35));
        JComboBox<String> roomTypeCombo = new JComboBox<>(new String[]{"Standard", "Deluxe", "Suite"});
        roomTypeCombo.setPreferredSize(new Dimension(200, 35));
        JTextField basePriceF = new JTextField();
        basePriceF.setPreferredSize(new Dimension(200, 35));

        // Room No
        gbc2.gridx = 0; gbc2.gridy = 0; gbc2.weightx = 0;
        addForm.add(new JLabel("Room Number:"), gbc2);
        gbc2.gridx = 1; gbc2.weightx = 1;
        addForm.add(roomNoF, gbc2);

        // Room Type
        gbc2.gridx = 0; gbc2.gridy = 1; gbc2.weightx = 0;
        addForm.add(new JLabel("Room Type:"), gbc2);
        gbc2.gridx = 1; gbc2.weightx = 1;
        addForm.add(roomTypeCombo, gbc2);

        // Base Price
        gbc2.gridx = 0; gbc2.gridy = 2; gbc2.weightx = 0;
        addForm.add(new JLabel("Base Price (₹):"), gbc2);
        gbc2.gridx = 1; gbc2.weightx = 1;
        addForm.add(basePriceF, gbc2);

        // Add Button
        JButton addBtn = pillButton("Add Room", SUCCESS);
        addBtn.addActionListener(e -> {
            try {
                String rNo = roomNoF.getText().trim();
                String rType = (String) roomTypeCombo.getSelectedItem();
                double rPrice = Double.parseDouble(basePriceF.getText());
                
                if (rNo.isEmpty()) {
                    JOptionPane.showMessageDialog(adminFrame, "Room number cannot be empty!");
                    return;
                }

                service.addRoom(rNo, rType, rPrice);
                JOptionPane.showMessageDialog(adminFrame, "Room Added Successfully!");
                roomNoF.setText(""); 
                basePriceF.setText("");
                
                adminFrame.dispose();
                showAdminPanel(); // Refresh panel to show new room in dropdown
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(adminFrame, "Please enter a valid price.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(adminFrame, "Error: " + ex.getMessage());
            }
        });

        JPanel btnPanel2 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnPanel2.setOpaque(false);
        btnPanel2.add(addBtn);

        gbc2.gridx = 0; gbc2.gridy = 3; gbc2.gridwidth = 2;
        addForm.add(btnPanel2, gbc2);

        addPanel.add(addForm, BorderLayout.CENTER);
        content.add(addPanel);

        adminFrame.add(content, BorderLayout.CENTER);
        adminFrame.setVisible(true);
    }

    // Helper method for creating nice section cards
    private JPanel createSectionPanel(String titleText) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(15, 15, 15, 15)));

        JLabel lbl = new JLabel(titleText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(new Color(51, 65, 85));
        panel.add(lbl, BorderLayout.NORTH);

        return panel;
    }
}
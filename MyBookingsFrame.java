import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class MyBookingsFrame extends JFrame {

    private int userId;
    private JTable table;
    private DefaultTableModel model;

    public MyBookingsFrame(int userId) {

        this.userId = userId;

        setTitle("My Bookings");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33, 150, 243));
        header.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        JButton backBtn = new JButton("← Back");
        styleHeaderButton(backBtn);

        JLabel title = new JLabel("My Bookings");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setHorizontalAlignment(JLabel.CENTER);

        header.add(backBtn, BorderLayout.WEST);
        header.add(title, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);

        // ===== TABLE =====
        String[] columns = {
                "Booking ID",
                "Source",
                "Destination",
                "Travel Date",
                "Seat Number",
                "Fare"
        };

        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(240, 240, 240));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(scrollPane, BorderLayout.CENTER);

        loadBookings();

        // ===== ACTIONS =====
        backBtn.addActionListener(e -> {
            dispose();
            new UserDashboard(userId);
        });

        setVisible(true);
    }

    private void loadBookings() {

        model.setRowCount(0);

        String query =
                "SELECT b.booking_id, r.source, r.destination, " +
                "s.travel_date, b.seat_number, s.fare " +
                "FROM bookings b " +
                "JOIN schedules s ON b.schedule_id = s.schedule_id " +
                "JOIN routes r ON s.route_id = r.route_id " +
                "WHERE b.user_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            boolean hasData = false;

            while (rs.next()) {

                hasData = true;

                model.addRow(new Object[]{
                        rs.getInt("booking_id"),
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getDate("travel_date"),
                        rs.getInt("seat_number"),
                        "₹ " + rs.getDouble("fare")
                });
            }

            if (!hasData) {
                JOptionPane.showMessageDialog(this,
                        "No bookings found.",
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading bookings.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // ===== STYLING =====
    private void styleHeaderButton(JButton button) {
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }
}
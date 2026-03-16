import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewAllBookingsFrame extends JFrame {

    private int userId;
    private JTable table;
    private DefaultTableModel model;

    public ViewAllBookingsFrame(int userId) {

        this.userId = userId;
        setTitle("All Bookings - Admin");
        setSize(950, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33, 150, 243));
        header.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        JButton backBtn = new JButton("← Back");
        backBtn.setFocusPainted(false);

        JLabel title = new JLabel("All Bookings (Admin Panel)", JLabel.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        header.add(backBtn, BorderLayout.WEST);
        header.add(title, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);

        // ===== TABLE =====
        String[] columns = {
                "Booking ID",
                "User ID",
                "Source",
                "Destination",
                "Travel Date",
                "Seat No",
                "Fare"
        };

        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(scrollPane, BorderLayout.CENTER);

        loadBookings();

        // ===== ACTION =====
        backBtn.addActionListener(e -> {
            dispose();
            new AdminDashboard(userId);   // Make sure this class exists
        });

        setVisible(true);
    }

    private void loadBookings() {

        model.setRowCount(0);

        String query = """
                SELECT b.booking_id, b.user_id,
                       r.source, r.destination,
                       s.travel_date, b.seat_number, s.fare
                FROM bookings b
                JOIN schedules s ON b.schedule_id = s.schedule_id
                JOIN routes r ON s.route_id = r.route_id
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            boolean hasData = false;

            while (rs.next()) {

                hasData = true;

                model.addRow(new Object[]{
                        rs.getInt("booking_id"),
                        rs.getInt("user_id"),
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getDate("travel_date"),
                        rs.getInt("seat_number"),
                        "₹ " + rs.getDouble("fare")
                });
            }

            if (!hasData) {
                JOptionPane.showMessageDialog(this,
                        "No bookings available.",
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
}
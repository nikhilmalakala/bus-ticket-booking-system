import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.DecimalFormat;

public class ViewSchedulesFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    private int userId;
    private String role;

    public ViewSchedulesFrame(int userId, String role) {

        this.userId = userId;
        this.role = role;

        setTitle("View Schedules");
        setSize(950, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33, 150, 243));
        header.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        JLabel title = new JLabel("Available Bus Schedules", JLabel.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        header.add(title, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);

        // ===== TABLE =====
        String[] columns = {
                "Schedule ID",
                "Source",
                "Destination",
                "Travel Date",
                "Departure Time",
                "Fare (₹)"
        };

        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setSelectionBackground(new Color(220, 240, 255));
        table.setGridColor(new Color(230, 230, 230));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(scrollPane, BorderLayout.CENTER);

        loadSchedules();

        // ===== BACK BUTTON PANEL =====
        JButton backBtn = new JButton("← Back");
        backBtn.setFocusPainted(false);
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        backBtn.addActionListener(e -> {
            dispose();
            if (role.equalsIgnoreCase("admin"))
                new AdminDashboard(userId);
            else
                new UserDashboard(userId);
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));
        bottomPanel.add(backBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // ===== LOAD SCHEDULES =====
    private void loadSchedules() {

        model.setRowCount(0);

        String query =
                "SELECT s.schedule_id, r.source, r.destination, " +
                "s.travel_date, s.departure_time, s.fare " +
                "FROM schedules s " +
                "JOIN routes r ON s.route_id = r.route_id";

        DecimalFormat df = new DecimalFormat("0.00");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            boolean hasData = false;

            while (rs.next()) {

                hasData = true;

                model.addRow(new Object[]{
                        rs.getInt("schedule_id"),
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getDate("travel_date"),
                        rs.getTime("departure_time"),
                        "₹ " + df.format(rs.getDouble("fare"))
                });
            }

            if (!hasData) {
                JOptionPane.showMessageDialog(this,
                        "No schedules available.",
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading schedules.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
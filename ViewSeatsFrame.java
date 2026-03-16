import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ViewSeatsFrame extends JFrame {

    private JTextField scheduleField;
    private JPanel seatPanel;
    private int userId;

    public ViewSeatsFrame(int userId) {

        this.userId = userId;

        setTitle("Seat Availability");
        setSize(650, 550);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33,150,243));
        header.setBorder(BorderFactory.createEmptyBorder(12,15,12,15));

        JLabel title = new JLabel("Seat Availability Viewer", JLabel.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        header.add(title, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // ===== TOP INPUT PANEL =====
        JPanel topPanel = new JPanel();
        topPanel.setBorder(BorderFactory.createEmptyBorder(15,10,10,10));

        scheduleField = new JTextField(10);
        JButton loadBtn = new JButton("Load Seats");
        loadBtn.setFocusPainted(false);

        topPanel.add(new JLabel("Schedule ID:"));
        topPanel.add(scheduleField);
        topPanel.add(loadBtn);

        add(topPanel, BorderLayout.BEFORE_FIRST_LINE);

        // ===== SEAT PANEL =====
        seatPanel = new JPanel(new GridLayout(0, 4, 12, 12)); // 4 seats per row
        seatPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JScrollPane scrollPane = new JScrollPane(seatPanel);
        add(scrollPane, BorderLayout.CENTER);

        // ===== LEGEND + BACK =====
        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel legendPanel = new JPanel();
        legendPanel.add(createLegend("Available", Color.GREEN));
        legendPanel.add(createLegend("Booked", Color.RED));

        JButton backBtn = new JButton("← Back");
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> {
            dispose();
            new UserDashboard(userId);
        });

        bottomPanel.add(legendPanel, BorderLayout.CENTER);
        bottomPanel.add(backBtn, BorderLayout.EAST);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10,10,15,15));

        add(bottomPanel, BorderLayout.SOUTH);

        // ===== ACTION =====
        loadBtn.addActionListener(e -> loadSeats());

        setVisible(true);
    }

    private void loadSeats() {

        seatPanel.removeAll();

        if (scheduleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Schedule ID");
            return;
        }

        int scheduleId;

        try {
            scheduleId = Integer.parseInt(scheduleField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Schedule ID");
            return;
        }

        String query = "SELECT seat_number, is_booked FROM seats WHERE schedule_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, scheduleId);
            ResultSet rs = ps.executeQuery();

            boolean hasSeats = false;

            while (rs.next()) {

                hasSeats = true;

                int seatNo = rs.getInt("seat_number");
                boolean booked = rs.getBoolean("is_booked");

                JButton seatBtn = new JButton(String.valueOf(seatNo));
                seatBtn.setEnabled(false);
                seatBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
                seatBtn.setOpaque(true);
                seatBtn.setBorderPainted(false);

                if (booked) {
                    seatBtn.setBackground(Color.RED);
                    seatBtn.setForeground(Color.WHITE);
                } else {
                    seatBtn.setBackground(Color.GREEN);
                    seatBtn.setForeground(Color.BLACK);
                }

                seatPanel.add(seatBtn);
            }

            if (!hasSeats) {
                JOptionPane.showMessageDialog(this,
                        "No seats found for this schedule.");
            }

            seatPanel.revalidate();
            seatPanel.repaint();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading seats.");
            e.printStackTrace();
        }
    }

    // ===== LEGEND CREATOR =====
    private JPanel createLegend(String text, Color color) {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT,5,0));

        JButton box = new JButton();
        box.setEnabled(false);
        box.setBackground(color);
        box.setPreferredSize(new Dimension(20,20));
        box.setOpaque(true);
        box.setBorderPainted(false);

        panel.add(box);
        panel.add(new JLabel(text));

        return panel;
    }
}
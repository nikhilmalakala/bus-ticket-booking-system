import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AddScheduleFrame extends JFrame {

    private JComboBox<String> busCombo;
    private JComboBox<String> routeCombo;
    private JTextField dateField, timeField, fareField;

    public AddScheduleFrame(int adminId) {

        setTitle("Add Schedule - Admin");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33, 150, 243));
        header.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        JButton backBtn = new JButton("← Back");
        styleHeaderButton(backBtn);

        JLabel title = new JLabel("Add New Schedule");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setHorizontalAlignment(JLabel.CENTER);

        header.add(backBtn, BorderLayout.WEST);
        header.add(title, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);

        // ===== FORM PANEL =====
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        busCombo = new JComboBox<>();
        routeCombo = new JComboBox<>();
        dateField = new JTextField(15);
        timeField = new JTextField(15);
        fareField = new JTextField(15);

        loadBuses();
        loadRoutes();

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Select Bus:"), gbc);

        gbc.gridx = 1;
        formPanel.add(busCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Select Route:"), gbc);

        gbc.gridx = 1;
        formPanel.add(routeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Travel Date (YYYY-MM-DD):"), gbc);

        gbc.gridx = 1;
        formPanel.add(dateField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Departure Time (HH:MM:SS):"), gbc);

        gbc.gridx = 1;
        formPanel.add(timeField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Fare:"), gbc);

        gbc.gridx = 1;
        formPanel.add(fareField, gbc);

        // ===== BUTTON PANEL =====
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));

        JButton addButton = new JButton("Add Schedule");
        JButton clearButton = new JButton("Clear");

        stylePrimaryButton(addButton);
        styleSecondaryButton(clearButton);

        buttonPanel.add(addButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // ===== ACTIONS =====
        addButton.addActionListener(e -> addSchedule());
        clearButton.addActionListener(e -> clearFields());

        backBtn.addActionListener(e -> {
            dispose();
            new AdminDashboard(adminId);
        });

        setVisible(true);
    }

    // ===== LOAD BUSES =====
    private void loadBuses() {
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT bus_id, bus_name FROM buses")) {

            while (rs.next()) {
                busCombo.addItem(
                        rs.getInt("bus_id") + " - " +
                                rs.getString("bus_name"));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading buses!");
        }
    }

    // ===== LOAD ROUTES =====
    private void loadRoutes() {
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT route_id, source, destination FROM routes")) {

            while (rs.next()) {
                routeCombo.addItem(
                        rs.getInt("route_id") + " - " +
                                rs.getString("source") + " to " +
                                rs.getString("destination"));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading routes!");
        }
    }

    // ===== ADD SCHEDULE =====
    private void addSchedule() {

        if (busCombo.getSelectedItem() == null ||
                routeCombo.getSelectedItem() == null) {

            showMessage("Please add Bus and Route first!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String date = dateField.getText().trim();
        String time = timeField.getText().trim();
        String fareText = fareField.getText().trim();

        if (date.isEmpty() || time.isEmpty() || fareText.isEmpty()) {
            showMessage("All fields are required!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double fare;

        try {
            fare = Double.parseDouble(fareText);
        } catch (NumberFormatException ex) {
            showMessage("Fare must be a valid number!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            int busId = Integer.parseInt(
                    busCombo.getSelectedItem().toString().split(" - ")[0]);

            int routeId = Integer.parseInt(
                    routeCombo.getSelectedItem().toString().split(" - ")[0]);

            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO schedules(bus_id,route_id,travel_date,departure_time,fare) VALUES(?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            ps.setInt(1, busId);
            ps.setInt(2, routeId);
            ps.setString(3, date);
            ps.setString(4, time);
            ps.setDouble(5, fare);

            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();

            if (keys.next()) {
                int scheduleId = keys.getInt(1);

                PreparedStatement seatQuery = con.prepareStatement(
                        "SELECT total_seats FROM buses WHERE bus_id=?");
                seatQuery.setInt(1, busId);

                ResultSet seatRs = seatQuery.executeQuery();

                if (seatRs.next()) {
                    int totalSeats = seatRs.getInt(1);

                    for (int i = 1; i <= totalSeats; i++) {
                        PreparedStatement insertSeat = con.prepareStatement(
                                "INSERT INTO seats(schedule_id,seat_number,is_booked) VALUES(?,?,FALSE)");
                        insertSeat.setInt(1, scheduleId);
                        insertSeat.setInt(2, i);
                        insertSeat.executeUpdate();
                    }
                }
            }

            showMessage("Schedule Added Successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            dispose();

        } catch (Exception e) {
            showMessage("Error adding schedule!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void clearFields() {
        dateField.setText("");
        timeField.setText("");
        fareField.setText("");
    }

    private void showMessage(String message, String title, int type) {
        JOptionPane.showMessageDialog(this, message, title, type);
    }

    // ===== STYLES =====
    private void stylePrimaryButton(JButton button) {
        button.setBackground(new Color(33, 150, 243));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setPreferredSize(new Dimension(150, 40));
    }

    private void styleSecondaryButton(JButton button) {
        button.setBackground(Color.LIGHT_GRAY);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setPreferredSize(new Dimension(120, 40));
    }

    private void styleHeaderButton(JButton button) {
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }
}
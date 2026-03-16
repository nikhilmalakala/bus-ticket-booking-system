import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class BookSeatFrame extends JFrame {

    private JTextField nameField, scheduleField, seatField;
    private int userId;

    public BookSeatFrame(int userId) {

        this.userId = userId;

        setTitle("Book Seat");
        setSize(550, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33, 150, 243));
        header.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        JButton backBtn = new JButton("← Back");
        styleHeaderButton(backBtn);

        JLabel title = new JLabel("Book a Seat");
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

        nameField = new JTextField(15);
        scheduleField = new JTextField(15);
        seatField = new JTextField(15);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Passenger Name:"), gbc);

        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Schedule ID:"), gbc);

        gbc.gridx = 1;
        formPanel.add(scheduleField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Seat Number:"), gbc);

        gbc.gridx = 1;
        formPanel.add(seatField, gbc);

        // ===== BUTTON PANEL =====
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));

        JButton bookBtn = new JButton("Confirm Booking");
        JButton clearBtn = new JButton("Clear");

        stylePrimaryButton(bookBtn);
        styleSecondaryButton(clearBtn);

        buttonPanel.add(bookBtn);
        buttonPanel.add(clearBtn);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // ===== ACTIONS =====
        bookBtn.addActionListener(e -> bookSeat());
        clearBtn.addActionListener(e -> clearFields());

        backBtn.addActionListener(e -> {
            dispose();
            new UserDashboard(userId);
        });

        setVisible(true);
    }

    private void bookSeat() {

        String passengerName = nameField.getText().trim();
        String scheduleText = scheduleField.getText().trim();
        String seatText = seatField.getText().trim();

        if (passengerName.isEmpty() || scheduleText.isEmpty() || seatText.isEmpty()) {
            showMessage("All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int scheduleId, seatNumber;

        try {
            scheduleId = Integer.parseInt(scheduleText);
            seatNumber = Integer.parseInt(seatText);
        } catch (NumberFormatException ex) {
            showMessage("Schedule ID and Seat must be numbers!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            PreparedStatement checkSeat = con.prepareStatement(
                    "SELECT is_booked FROM seats WHERE schedule_id=? AND seat_number=?"
            );

            checkSeat.setInt(1, scheduleId);
            checkSeat.setInt(2, seatNumber);

            ResultSet rs = checkSeat.executeQuery();

            if (!rs.next()) {
                showMessage("Invalid seat number!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                con.rollback();
                return;
            }

            if (rs.getBoolean("is_booked")) {
                showMessage("Seat already booked!",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                con.rollback();
                return;
            }

            PreparedStatement insertBooking = con.prepareStatement(
                    "INSERT INTO bookings(user_id,schedule_id,seat_number,passenger_name) VALUES(?,?,?,?)"
            );

            insertBooking.setInt(1, userId);
            insertBooking.setInt(2, scheduleId);
            insertBooking.setInt(3, seatNumber);
            insertBooking.setString(4, passengerName);
            insertBooking.executeUpdate();

            PreparedStatement updateSeat = con.prepareStatement(
                    "UPDATE seats SET is_booked=TRUE WHERE schedule_id=? AND seat_number=?"
            );

            updateSeat.setInt(1, scheduleId);
            updateSeat.setInt(2, seatNumber);
            updateSeat.executeUpdate();

            con.commit();

            showMessage("Seat Booked Successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            clearFields();

        } catch (Exception e) {
            showMessage("Error booking seat!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void clearFields() {
        nameField.setText("");
        scheduleField.setText("");
        seatField.setText("");
    }

    private void showMessage(String message, String title, int type) {
        JOptionPane.showMessageDialog(this, message, title, type);
    }

    // ===== STYLING =====
    private void stylePrimaryButton(JButton button) {
        button.setBackground(new Color(33, 150, 243));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setPreferredSize(new Dimension(160, 40));
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
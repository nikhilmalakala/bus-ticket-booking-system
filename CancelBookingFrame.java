import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CancelBookingFrame extends JFrame {

    private JTextField bookingField;
    private int userId;

    public CancelBookingFrame(int userId) {

        this.userId = userId;

        setTitle("Cancel Booking");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(244, 67, 54)); // Red theme for cancel
        header.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        JButton backBtn = new JButton("← Back");
        styleHeaderButton(backBtn);

        JLabel title = new JLabel("Cancel Booking");
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

        bookingField = new JTextField(15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Booking ID:"), gbc);

        gbc.gridx = 1;
        formPanel.add(bookingField, gbc);

        // ===== BUTTON PANEL =====
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));

        JButton cancelBtn = new JButton("Confirm Cancellation");
        JButton clearBtn = new JButton("Clear");

        styleDangerButton(cancelBtn);
        styleSecondaryButton(clearBtn);

        buttonPanel.add(cancelBtn);
        buttonPanel.add(clearBtn);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // ===== ACTIONS =====
        cancelBtn.addActionListener(e -> cancelBooking());
        clearBtn.addActionListener(e -> bookingField.setText(""));

        backBtn.addActionListener(e -> {
            dispose();
            new UserDashboard(userId);
        });

        setVisible(true);
    }

    private void cancelBooking() {

        String bookingText = bookingField.getText().trim();

        if (bookingText.isEmpty()) {
            showMessage("Booking ID is required!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int bookingId;

        try {
            bookingId = Integer.parseInt(bookingText);
        } catch (NumberFormatException ex) {
            showMessage("Booking ID must be a number!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel this booking?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            PreparedStatement select = con.prepareStatement(
                    "SELECT schedule_id, seat_number FROM bookings WHERE booking_id=? AND user_id=?"
            );

            select.setInt(1, bookingId);
            select.setInt(2, userId);
            ResultSet rs = select.executeQuery();

            if (!rs.next()) {
                showMessage("Invalid Booking ID or you do not own this booking!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                con.rollback();
                return;
            }

            int scheduleId = rs.getInt("schedule_id");
            int seatNumber = rs.getInt("seat_number");

            PreparedStatement delete = con.prepareStatement(
                    "DELETE FROM bookings WHERE booking_id=?"
            );
            delete.setInt(1, bookingId);
            delete.executeUpdate();

            PreparedStatement update = con.prepareStatement(
                    "UPDATE seats SET is_booked=FALSE WHERE schedule_id=? AND seat_number=?"
            );
            update.setInt(1, scheduleId);
            update.setInt(2, seatNumber);
            update.executeUpdate();

            con.commit();

            showMessage("Booking Cancelled Successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            bookingField.setText("");

        } catch (Exception e) {
            showMessage("Error cancelling booking!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showMessage(String message, String title, int type) {
        JOptionPane.showMessageDialog(this, message, title, type);
    }

    // ===== STYLES =====
    private void styleDangerButton(JButton button) {
        button.setBackground(new Color(244, 67, 54));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setPreferredSize(new Dimension(180, 40));
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
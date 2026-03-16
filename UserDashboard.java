import javax.swing.*;
import java.awt.*;

public class UserDashboard extends JFrame {

    private int userId;

    public UserDashboard(int userId) {

        this.userId = userId;

        setTitle("User Dashboard");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33,150,243));
        header.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        JLabel title = new JLabel("User Dashboard", JLabel.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        header.add(title, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // ===== BUTTON PANEL =====
        JPanel panel = new JPanel(new GridLayout(6,1,15,15));
        panel.setBorder(BorderFactory.createEmptyBorder(40,80,40,80));

        JButton viewSchedules = createButton("View Schedules");
        JButton viewSeats = createButton("View Seats");
        JButton bookSeat = createButton("Book Seat");
        JButton myBookings = createButton("My Bookings");
        JButton cancelBooking = createButton("Cancel Booking");
        JButton logout = createButton("Logout");

        panel.add(viewSchedules);
        panel.add(viewSeats);
        panel.add(bookSeat);
        panel.add(myBookings);
        panel.add(cancelBooking);
        panel.add(logout);

        add(panel, BorderLayout.CENTER);

        // ===== ACTIONS =====
        viewSchedules.addActionListener(e ->
                new ViewSchedulesFrame(userId, "user"));

        viewSeats.addActionListener(e ->
                new ViewSeatsFrame(userId));

        bookSeat.addActionListener(e ->
                new BookSeatFrame(userId));

        myBookings.addActionListener(e ->
                new MyBookingsFrame(userId));

        cancelBooking.addActionListener(e ->
                new CancelBookingFrame(userId));

        logout.addActionListener(e -> {

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new MainFrame();
            }
        });

        setVisible(true);
    }

    // ===== COMMON BUTTON STYLE =====
    private JButton createButton(String text) {

        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBackground(new Color(240,240,240));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }
}
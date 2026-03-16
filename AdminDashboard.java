import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    private int userId;

    public AdminDashboard(int userId) {

        this.userId = userId;

        setTitle("Admin Dashboard");
        setSize(550, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33,150,243));
        header.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        JLabel title = new JLabel("Admin Dashboard", JLabel.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        header.add(title, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // ===== BUTTON PANEL =====
        JPanel panel = new JPanel(new GridLayout(7, 1, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(40,100,40,100));

        JButton addBus = createButton("Add Bus");
        JButton addRoute = createButton("Add Route");
        JButton addSchedule = createButton("Add Schedule");
        JButton viewBookings = createButton("View All Bookings");
        JButton viewUsers = createButton("View All Users");
        JButton viewSchedules = createButton("View Schedules");
        JButton logout = createButton("Logout");

        panel.add(addBus);
        panel.add(addRoute);
        panel.add(addSchedule);
        panel.add(viewBookings);
        panel.add(viewUsers);
        panel.add(viewSchedules);
        panel.add(logout);

        add(panel, BorderLayout.CENTER);

        // ===== ACTIONS =====
        addBus.addActionListener(e -> new AddBusFrame());
        addRoute.addActionListener(e -> new AddRouteFrame(userId));
        addSchedule.addActionListener(e -> new AddScheduleFrame(userId));
        viewBookings.addActionListener(e -> new ViewAllBookingsFrame(userId));
        viewUsers.addActionListener(e -> new ViewAllUsersFrame(userId));
        viewSchedules.addActionListener(e -> new ViewSchedulesFrame(userId, "admin"));

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
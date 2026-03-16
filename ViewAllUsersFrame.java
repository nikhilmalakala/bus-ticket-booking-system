import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewAllUsersFrame extends JFrame {

    private int userId;
    private JTable table;
    private DefaultTableModel model;

    public ViewAllUsersFrame(int userId) {

         this.userId = userId;
        setTitle("All Users - Admin");
        setSize(850, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33, 150, 243));
        header.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        JButton backBtn = new JButton("← Back");
        backBtn.setFocusPainted(false);

        JLabel title = new JLabel("All Registered Users (Admin Panel)", JLabel.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        header.add(backBtn, BorderLayout.WEST);
        header.add(title, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);

        // ===== TABLE =====
        String[] columns = {
                "User ID",
                "Username",
                "Email",
                "Role"
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

        loadUsers();

        // ===== ACTION =====
        backBtn.addActionListener(e -> {
            dispose();
            new AdminDashboard(userId); // Make sure this exists
        });

        setVisible(true);
    }

    private void loadUsers() {

        model.setRowCount(0);

        String query = "SELECT user_id, username, email, role FROM users";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            boolean hasData = false;

            while (rs.next()) {

                hasData = true;

                model.addRow(new Object[]{
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("role")
                });
            }

            if (!hasData) {
                JOptionPane.showMessageDialog(this,
                        "No users found.",
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading users.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AddRouteFrame extends JFrame {

    private JTextField sourceField, destField;

    public AddRouteFrame(int adminId) {

        setTitle("Add Route - Admin");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33, 150, 243));
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JButton backBtn = new JButton("← Back");
        styleHeaderButton(backBtn);

        JLabel title = new JLabel("Add New Route");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setHorizontalAlignment(JLabel.CENTER);

        header.add(backBtn, BorderLayout.WEST);
        header.add(title, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);

        // ===== FORM PANEL =====
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        sourceField = new JTextField(15);
        destField = new JTextField(15);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Source:"), gbc);

        gbc.gridx = 1;
        formPanel.add(sourceField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Destination:"), gbc);

        gbc.gridx = 1;
        formPanel.add(destField, gbc);

        // ===== BUTTON PANEL =====
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton addBtn = new JButton("Add Route");
        JButton clearBtn = new JButton("Clear");

        stylePrimaryButton(addBtn);
        styleSecondaryButton(clearBtn);

        buttonPanel.add(addBtn);
        buttonPanel.add(clearBtn);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // ===== ACTIONS =====
        addBtn.addActionListener(e -> addRoute());
        clearBtn.addActionListener(e -> clearFields());

        backBtn.addActionListener(e -> {
            dispose();
            new AdminDashboard(adminId);
        });

        setVisible(true);
    }

    // ===== ADD ROUTE LOGIC =====
    private void addRoute() {

        String source = sourceField.getText().trim();
        String destination = destField.getText().trim();

        if (source.isEmpty() || destination.isEmpty()) {
            showMessage("All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (source.equalsIgnoreCase(destination)) {
            showMessage("Source and Destination cannot be the same!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO routes(source, destination) VALUES(?, ?)")) {

            ps.setString(1, source);
            ps.setString(2, destination);

            ps.executeUpdate();

            showMessage("Route Added Successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            clearFields();

        } catch (SQLIntegrityConstraintViolationException ex) {
            showMessage("This route already exists!",
                    "Duplicate Route",
                    JOptionPane.WARNING_MESSAGE);

        } catch (Exception e) {
            showMessage("Something went wrong!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void clearFields() {
        sourceField.setText("");
        destField.setText("");
    }

    private void showMessage(String message, String title, int type) {
        JOptionPane.showMessageDialog(this, message, title, type);
    }

    // ===== STYLING METHODS =====

    private void stylePrimaryButton(JButton button) {
        button.setBackground(new Color(33, 150, 243));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setPreferredSize(new Dimension(130, 35));
    }

    private void styleSecondaryButton(JButton button) {
        button.setBackground(Color.LIGHT_GRAY);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setPreferredSize(new Dimension(130, 35));
    }

    private void styleHeaderButton(JButton button) {
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }
}
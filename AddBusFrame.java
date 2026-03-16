import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AddBusFrame extends JFrame {

    private JTextField numberField, nameField, seatsField;

    public AddBusFrame() {

        setTitle("Add Bus - Admin");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== HEADER PANEL =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33, 150, 243));
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JButton backBtn = new JButton("← Back");
        styleHeaderButton(backBtn);

        JLabel title = new JLabel("Add New Bus");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setHorizontalAlignment(JLabel.CENTER);

        header.add(backBtn, BorderLayout.WEST);
        header.add(title, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);

        // ===== CENTER FORM PANEL =====
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        numberField = new JTextField(15);
        nameField = new JTextField(15);
        seatsField = new JTextField(15);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Bus Number:"), gbc);

        gbc.gridx = 1;
        formPanel.add(numberField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Bus Name:"), gbc);

        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Total Seats:"), gbc);

        gbc.gridx = 1;
        formPanel.add(seatsField, gbc);

        // ===== BUTTON PANEL =====
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton addBtn = new JButton("Add Bus");
        JButton clearBtn = new JButton("Clear");

        stylePrimaryButton(addBtn);
        styleSecondaryButton(clearBtn);

        buttonPanel.add(addBtn);
        buttonPanel.add(clearBtn);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // ===== ACTIONS =====
        addBtn.addActionListener(e -> addBus());
        clearBtn.addActionListener(e -> clearFields());

        backBtn.addActionListener(e -> {
            dispose();
            new AdminDashboard(0); // Replace 0 with adminId if required
        });

        setVisible(true);
    }

    // ===== ADD BUS LOGIC =====
    private void addBus() {

        String number = numberField.getText().trim();
        String name = nameField.getText().trim();
        String seatsText = seatsField.getText().trim();

        if (number.isEmpty() || name.isEmpty() || seatsText.isEmpty()) {
            showMessage("All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int seats;

        try {
            seats = Integer.parseInt(seatsText);
            if (seats <= 0) {
                showMessage("Seats must be greater than 0", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            showMessage("Seats must be a valid number", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO buses(bus_number,bus_name,total_seats) VALUES(?,?,?)")) {

            ps.setString(1, number);
            ps.setString(2, name);
            ps.setInt(3, seats);

            ps.executeUpdate();

            showMessage("Bus Added Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();

        } catch (SQLIntegrityConstraintViolationException ex) {
            showMessage("Bus number already exists!", "Duplicate Error", JOptionPane.WARNING_MESSAGE);

        } catch (Exception e) {
            showMessage("Something went wrong!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void clearFields() {
        numberField.setText("");
        nameField.setText("");
        seatsField.setText("");
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
        button.setPreferredSize(new Dimension(120, 35));
    }

    private void styleSecondaryButton(JButton button) {
        button.setBackground(Color.LIGHT_GRAY);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setPreferredSize(new Dimension(120, 35));
    }

    private void styleHeaderButton(JButton button) {
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }
}
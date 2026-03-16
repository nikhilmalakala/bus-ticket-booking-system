import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegisterFrame extends JFrame {

    JTextField usernameField,emailField;
    JPasswordField passwordField;

    public RegisterFrame(){

        setTitle("Bus Booking System - Register");
        setSize(450,320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        // TITLE
        JLabel title = new JLabel("BUS BOOKING SYSTEM - REGISTER", JLabel.CENTER);
        title.setFont(new Font("Arial",Font.BOLD,20));
        title.setBorder(BorderFactory.createEmptyBorder(15,10,10,10));
        add(title,BorderLayout.NORTH);

        // CENTER FORM
        JPanel form = new JPanel(new GridLayout(3,2,10,12));
        form.setBorder(BorderFactory.createEmptyBorder(10,50,10,50));

        form.add(new JLabel("Username:"));
        usernameField = new JTextField();
        form.add(usernameField);

        form.add(new JLabel("Email:"));
        emailField = new JTextField();
        form.add(emailField);

        form.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        form.add(passwordField);

        add(form,BorderLayout.CENTER);

        // BUTTON PANEL
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER,20,10));

        JButton registerBtn = new JButton("Register");
        JButton backBtn = new JButton("Back");

        buttons.add(registerBtn);
        buttons.add(backBtn);

        add(buttons,BorderLayout.SOUTH);

        registerBtn.addActionListener(e -> register());

        backBtn.addActionListener(e -> {
            new MainFrame();
            dispose();
        });

        setVisible(true);
    }

    private void register(){

        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if(username.isEmpty() || email.isEmpty() || password.isEmpty()){
            JOptionPane.showMessageDialog(this,"All fields required");
            return;
        }

        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO users(username,email,password,role) VALUES(?,?,?,?)")){

            ps.setString(1,username);
            ps.setString(2,email);
            ps.setString(3,password);
            ps.setString(4,"user");

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,"Registration Successful");

            new MainFrame();
            dispose();

        }catch(Exception ex){
            JOptionPane.showMessageDialog(this,"Error!");
        }
    }
}
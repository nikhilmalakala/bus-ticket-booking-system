import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MainFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;

    public MainFrame(){

        setTitle("Bus Booking System - Login");
        setSize(450,280);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // TITLE
        JLabel title = new JLabel("BUS BOOKING SYSTEM",JLabel.CENTER);
        title.setFont(new Font("Arial",Font.BOLD,22));
        title.setBorder(BorderFactory.createEmptyBorder(15,10,15,10));
        add(title,BorderLayout.NORTH);

        // FORM PANEL
        JPanel form = new JPanel(new GridLayout(2,2,10,10));
        form.setBorder(BorderFactory.createEmptyBorder(20,60,10,60));

        form.add(new JLabel("Email:"));
        emailField = new JTextField();
        form.add(emailField);

        form.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        form.add(passwordField);

        add(form,BorderLayout.CENTER);

        // BOTTOM PANEL
        // BOTTOM PANEL
JPanel bottom = new JPanel();
bottom.setLayout(new BoxLayout(bottom,BoxLayout.Y_AXIS));

JButton loginBtn = new JButton("Login");
loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

// PANEL FOR TEXT + LINK
JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,5,0));

JLabel text = new JLabel("Don't have an account?");
JLabel registerLink = new JLabel("<html><u>Register</u></html>");

registerLink.setForeground(Color.BLUE);
registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));

registerPanel.add(text);
registerPanel.add(registerLink);

bottom.add(loginBtn);
bottom.add(Box.createVerticalStrut(10));
bottom.add(registerPanel);
bottom.add(Box.createVerticalStrut(15));

add(bottom,BorderLayout.SOUTH);

        // ACTIONS
        loginBtn.addActionListener(e->login());

        registerLink.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
                new RegisterFrame();
                dispose();
            }
        });

        setVisible(true);
    }

    private void login(){

        String email=emailField.getText();
        String password=new String(passwordField.getPassword());

        try(Connection con=DBConnection.getConnection();
            PreparedStatement ps=con.prepareStatement(
                    "SELECT user_id,role FROM users WHERE email=? AND password=?")){

            ps.setString(1,email);
            ps.setString(2,password);

            ResultSet rs=ps.executeQuery();

            if(rs.next()){

                int userId=rs.getInt("user_id");
                String role=rs.getString("role");

                dispose();

                if(role.equalsIgnoreCase("admin"))
                    new AdminDashboard(userId);
                else
                    new UserDashboard(userId);

            }else{
                JOptionPane.showMessageDialog(this,"Invalid login details");
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static void main(String[] args){
        new MainFrame();
    }
}
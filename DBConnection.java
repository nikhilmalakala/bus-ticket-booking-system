import java.sql.*;

public class DBConnection {

    static final String URL = "jdbc:mysql://localhost:3306/busbooking";
    static final String USER = "root";
    static final String PASSWORD = "nikhil";

    public static Connection getConnection() {

        Connection con = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            con = DriverManager.getConnection(URL, USER, PASSWORD);

        } catch(Exception e) {
            System.out.println(e);
        }

        return con;
    }
}

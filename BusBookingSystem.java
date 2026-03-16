import java.sql.*;
import java.util.*;

public class BusBookingSystem {

    static Scanner sc = new Scanner(System.in);

    static int currentUserId;
    static String currentRole;
    static String currentUsername;

    // ================= MAIN =================

    public static void main(String[] args) {

        while(true) {

            System.out.println("\n========== BUS BOOKING SYSTEM ==========");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");

            System.out.print("Choose option: ");

            int choice = sc.nextInt();

            switch(choice) {

                case 1:
                    login();
                    break;

                case 2:
                    register();
                    break;

                case 3:
                    System.out.println("Thank you for using the system.");
                    System.exit(0);

                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    // ================= REGISTER =================

    public static void register() {

    sc.nextLine();

    System.out.println("\n=== USER REGISTRATION ===");

    System.out.print("Enter username: ");
    String username = sc.nextLine();

    System.out.print("Enter email: ");
    String email = sc.nextLine();

    System.out.print("Enter password: ");
    String password = sc.nextLine();

    try {

        Connection con = DBConnection.getConnection();

        PreparedStatement ps = con.prepareStatement(
            "INSERT INTO users(username,email,password,role) VALUES(?,?,?,?)"
        );

        ps.setString(1, username);
        ps.setString(2, email);
        ps.setString(3, password);
        ps.setString(4, "user");

        ps.executeUpdate();

        System.out.println("Registration successful.");

    }
    catch(SQLException e) {
        System.out.println("Username or Email already exists.");
    }
    catch(Exception e) {
        e.printStackTrace();
    }
}
public static void login() {

    sc.nextLine();

    System.out.println("\n=== LOGIN ===");

    System.out.print("Email: ");
    String email = sc.nextLine();

    System.out.print("Password: ");
    String password = sc.nextLine();

    try {

        Connection con = DBConnection.getConnection();

        PreparedStatement ps = con.prepareStatement(
            "SELECT user_id, username, role FROM users WHERE email=? AND password=?"
        );

        ps.setString(1, email);
        ps.setString(2, password);

        ResultSet rs = ps.executeQuery();

        if(rs.next()) {

            currentUserId = rs.getInt("user_id");
            currentUsername = rs.getString("username");
            currentRole = rs.getString("role");

            System.out.println("Login successful. Welcome " + currentUsername);

            if(currentRole.equals("admin"))
                adminMenu();
            else
                userMenu();
        }
        else {

            System.out.println("Invalid login credentials.");
        }

    }
    catch(Exception e) {
        e.printStackTrace();
    }
}

    // ================= USER MENU =================

    public static void userMenu() {

        while(true) {

            System.out.println("\n========== USER MENU ==========");
            System.out.println("1. View schedules");
            System.out.println("2. View seat availability");
            System.out.println("3. Book seat");
            System.out.println("4. Cancel booking");
            System.out.println("5. Logout");

            System.out.print("Choose option: ");

            int choice = sc.nextInt();

            switch(choice) {

                case 1:
                    viewSchedules();
                    break;

                case 2:
                    viewSeats();
                    break;

                case 3:
                    bookSeat();
                    break;

                case 4:
                    cancelBooking();
                    break;

                case 5:
                    return;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    // ================= ADMIN MENU =================

    public static void adminMenu() {

        while(true) {

            System.out.println("\n========== ADMIN MENU ==========");
            System.out.println("1. Add bus");
            System.out.println("2. Add route");
            System.out.println("3. Add schedule");
            System.out.println("4. View all bookings");
            System.out.println("5. View all users");
            System.out.println("6. View schedules");
            System.out.println("7. Logout");

            System.out.print("Choose option: ");

            int choice = sc.nextInt();

            switch(choice) {

                case 1:
                    addBus();
                    break;

                case 2:
                    addRoute();
                    break;

                case 3:
                    addSchedule();
                    break;

                case 4:
                    viewAllBookings();
                    break;

                case 5:
                    viewAllUsers();
                    break;

                case 6:
                    viewSchedules();
                    break;

                case 7:
                    return;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    // ================= VIEW SCHEDULES =================

    public static void viewSchedules() {

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                "SELECT schedule_id,source,destination,travel_date,departure_time,fare " +
                "FROM schedules JOIN routes ON schedules.route_id=routes.route_id")) {

            while(rs.next()) {

                System.out.println(
                    "Schedule ID: " + rs.getInt(1) +
                    " | " + rs.getString(2) +
                    " -> " + rs.getString(3) +
                    " | Date: " + rs.getDate(4) +
                    " | Time: " + rs.getTime(5) +
                    " | Fare: " + rs.getDouble(6)
                );
            }

        }
        catch(Exception e) {

            e.printStackTrace();
        }
    }

    // ================= VIEW SEATS =================

    public static void viewSeats() {

        System.out.print("Enter schedule ID: ");
        int scheduleId = sc.nextInt();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                "SELECT seat_number,is_booked FROM seats WHERE schedule_id=?")) {

            ps.setInt(1, scheduleId);

            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {

                    System.out.println(
                        "Seat " + rs.getInt(1) +
                        " : " +
                        (rs.getBoolean(2) ? "BOOKED" : "AVAILABLE")
                    );
                }
            }

        }
        catch(Exception e) {

            e.printStackTrace();
        }
    }

    // ================= BOOK SEAT =================

    public static void bookSeat() {

        sc.nextLine();

        System.out.print("Enter passenger name: ");
        String name = sc.nextLine();

        System.out.print("Enter schedule ID: ");
        int scheduleId = sc.nextInt();

        System.out.print("Enter seat number: ");
        int seatNumber = sc.nextInt();

        try {

            Connection con = DBConnection.getConnection();

            PreparedStatement check = con.prepareStatement(
                "SELECT is_booked FROM seats WHERE schedule_id=? AND seat_number=?"
            );

            check.setInt(1, scheduleId);
            check.setInt(2, seatNumber);

            ResultSet rs = check.executeQuery();

            if(rs.next() && rs.getBoolean(1)) {

                System.out.println("Seat already booked.");
                return;
            }

            PreparedStatement insert = con.prepareStatement(
                "INSERT INTO bookings(user_id,schedule_id,seat_number,passenger_name) VALUES(?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS
            );

            insert.setInt(1, currentUserId);
            insert.setInt(2, scheduleId);
            insert.setInt(3, seatNumber);
            insert.setString(4, name);

            insert.executeUpdate();

            ResultSet key = insert.getGeneratedKeys();

            if(key.next()) {

                int bookingId = key.getInt(1);

                PreparedStatement update = con.prepareStatement(
                    "UPDATE seats SET is_booked=TRUE WHERE schedule_id=? AND seat_number=?"
                );

                update.setInt(1, scheduleId);
                update.setInt(2, seatNumber);

                update.executeUpdate();

                System.out.println("Booking confirmed. Booking ID: " + bookingId);
            }

        }
        catch (SQLIntegrityConstraintViolationException e) {
    System.out.println("Seat already booked! Please choose another seat.");
}
catch (Exception e) {
    System.out.println("Something went wrong.");
    e.printStackTrace();
}
    }

    // ================= CANCEL BOOKING =================

    public static void cancelBooking() {

        System.out.print("Enter booking ID: ");
        int bookingId = sc.nextInt();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement select = con.prepareStatement(
                "SELECT schedule_id,seat_number FROM bookings WHERE booking_id=? AND user_id=?")) {

            select.setInt(1, bookingId);
            select.setInt(2, currentUserId); // Verify user owns the booking

            try (ResultSet rs = select.executeQuery()) {
                if(rs.next()) {

                    int scheduleId = rs.getInt(1);
                    int seatNumber = rs.getInt(2);

                    try(PreparedStatement delete = con.prepareStatement(
                        "DELETE FROM bookings WHERE booking_id=?"
                    )) {
                        delete.setInt(1, bookingId);
                        delete.executeUpdate();
                    }

                    try(PreparedStatement update = con.prepareStatement(
                        "UPDATE seats SET is_booked=FALSE WHERE schedule_id=? AND seat_number=?"
                    )) {
                        update.setInt(1, scheduleId);
                        update.setInt(2, seatNumber);
                        update.executeUpdate();
                    }

                    System.out.println("Booking cancelled successfully.");
                }
                else {

                    System.out.println("Invalid booking ID or you do not own this booking.");
                }
            }

        }
        catch(Exception e) {

            e.printStackTrace();
        }
    }

    // ================= ADMIN FUNCTIONS =================

    public static void addBus() {

        sc.nextLine();

        System.out.print("Bus number: ");
        String number = sc.nextLine();

        System.out.print("Bus name: ");
        String name = sc.nextLine();

        System.out.print("Total seats: ");
        int seats = sc.nextInt();

        try {

            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO buses(bus_number,bus_name,total_seats) VALUES(?,?,?)"
            );

            ps.setString(1, number);
            ps.setString(2, name);
            ps.setInt(3, seats);

            ps.executeUpdate();

            System.out.println("Bus added.");

        }
        catch(Exception e) {

            e.printStackTrace();
        }
    }

    public static void addRoute() {

        sc.nextLine();

        System.out.print("Source: ");
        String source = sc.nextLine();

        System.out.print("Destination: ");
        String dest = sc.nextLine();

        try {

            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO routes(source,destination) VALUES(?,?)"
            );

            ps.setString(1, source);
            ps.setString(2, dest);

            ps.executeUpdate();

            System.out.println("Route added.");

        }
        catch(Exception e) {

            e.printStackTrace();
        }
    }

   public static void addSchedule() {

    try {
        Connection con = DBConnection.getConnection();

        // Show buses
        System.out.println("\nAvailable Buses:");
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM buses");

        while (rs.next()) {
            System.out.println("ID: " + rs.getInt("bus_id") +
                    " | Bus Number: " + rs.getString("bus_number") +
                    " | Name: " + rs.getString("bus_name"));
        }

        // Show routes
        System.out.println("\nAvailable Routes:");
        rs = stmt.executeQuery("SELECT * FROM routes");

        while (rs.next()) {
            System.out.println("ID: " + rs.getInt("route_id") +
                    " | " + rs.getString("source") +
                    " -> " + rs.getString("destination"));
        }

        // Take input
        System.out.print("Bus ID: ");
        int busId = sc.nextInt();

        System.out.print("Route ID: ");
        int routeId = sc.nextInt();
        sc.nextLine();

        System.out.print("Date (YYYY-MM-DD): ");
        String date = sc.nextLine();

        System.out.print("Time (HH:MM:SS): ");
        String time = sc.nextLine();

        System.out.print("Fare: ");
        double fare = sc.nextDouble();

        // Insert schedule
        PreparedStatement ps = con.prepareStatement(
                "INSERT INTO schedules(bus_id,route_id,travel_date,departure_time,fare) VALUES(?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS
        );

        ps.setInt(1, busId);
        ps.setInt(2, routeId);
        ps.setString(3, date);
        ps.setString(4, time);
        ps.setDouble(5, fare);

        ps.executeUpdate();

        ResultSet generatedKeys = ps.getGeneratedKeys();

        if (generatedKeys.next()) {

            int scheduleId = generatedKeys.getInt(1);

            // Get total seats
            PreparedStatement seatQuery = con.prepareStatement(
                    "SELECT total_seats FROM buses WHERE bus_id=?"
            );

            seatQuery.setInt(1, busId);
            ResultSet seatRs = seatQuery.executeQuery();

            if (seatRs.next()) {

                int totalSeats = seatRs.getInt(1);

                // Insert seats
                for (int i = 1; i <= totalSeats; i++) {

                    PreparedStatement insertSeat = con.prepareStatement(
                            "INSERT INTO seats(schedule_id,seat_number,is_booked) VALUES(?,?,FALSE)"
                    );

                    insertSeat.setInt(1, scheduleId);
                    insertSeat.setInt(2, i);
                    insertSeat.executeUpdate();
                }
            }
        }

        System.out.println("Schedule added with seats created.");

    } catch (Exception e) {
        e.printStackTrace();
    }
   }
    public static void viewAllBookings() {

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM bookings")) {

            while(rs.next()) {

                System.out.println(
                    "Booking ID: " + rs.getInt(1) +
                    " User ID: " + rs.getInt(2) +
                     " | Schedule ID: " + rs.getInt(3) +
                    " Seat: " + rs.getInt(4)
                );
            }

        }
        catch(Exception e) {

            e.printStackTrace();
        }
    }
    public static void viewAllUsers() {

    try (Connection con = DBConnection.getConnection();
         Statement st = con.createStatement();
         ResultSet rs = st.executeQuery(
            "SELECT user_id, username, email, role FROM users"
         )) {

        System.out.println("\n===== ALL USERS =====");

        while (rs.next()) {

            System.out.println(
                "User ID: " + rs.getInt("user_id") +
                " | Username: " + rs.getString("username") +
                " | Email: " + rs.getString("email") +
                " | Role: " + rs.getString("role")
            );
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}
}

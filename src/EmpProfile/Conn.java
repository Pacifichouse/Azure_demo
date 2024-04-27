package EmpProfile;

import java.sql.*;

public class Conn {
    
    Connection c;
    Statement s;

    public Conn () {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            c = DriverManager.getConnection("jdbc:mysql:///employeemanagementsystem", "root", "Artee@123");
            s = c.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
 // Add this method to prepare statements
    public PreparedStatement prepareStatement(String query) {
        try {
            return c.prepareStatement(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}


package EmpProfile;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

import org.json.JSONException;
import org.json.JSONObject;

public class MarkAttendance {
    // Constants for predefined location
	
	// Mega pearl  19.298940738131694, 73.21341463861972
	//Durga Imperial    19.223516716329677, 73.12161538465493
	// Constants for predefined locations
    static final Map<String, double[]> PREDEFINED_LOCATIONS = new HashMap<>();

    static {
        // Add predefined locations with their names
        PREDEFINED_LOCATIONS.put("Mega Pearl", new double[]{19.298940738131694, 73.21341463861972});
        PREDEFINED_LOCATIONS.put("Durga Imperial", new double[]{19.223516716329677, 73.12161538465493});
        // Add more locations as needed
    }
    static final double officeRadius =100; // Maximum distance allowed in meters  19.114204145772725, 72.85488338737751

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/employeemanagementsystem";

    static final String USER = "root";
    static final String PASS = "Artee@123";

    static Connection conn = null;
    static Statement stmt = null;
    
    String username;
    
    

//    private static Map<String, Integer> getEmployees() throws SQLException {
//        Map<String, Integer> employeesMap = new HashMap<>();
//        String sql = "SELECT id, username FROM employees";
//        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//            ResultSet rs = stmt.executeQuery();
//            while (rs.next()) {
//                int id = rs.getInt("id");
//                String username = rs.getString("username");
//                employeesMap.put(username, id);
//            }
//        }
//        return employeesMap;
//    }

    private static boolean isWithinAnyRadius(double empLat, double empLong) {
        try {
            for (Map.Entry<String, double[]> entry : PREDEFINED_LOCATIONS.entrySet()) {
                double[] location = entry.getValue();
                double distance = calculateDistance(empLat, empLong, location[0], location[1]);
                if (distance <= officeRadius) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    
    
 // Radius of the Earth in meters
    private static final double EARTH_RADIUS = 637100; 

    // Method to calculate distance between two points using Haversine formula
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        System.out.println(c + "C Radious Value");
        System.out.println(a + "A Value");
        return EARTH_RADIUS * c;
    }





//    private static double calculateDistance(double empLat, double empLong, double destLat, double destLong) {
//        // Calculate distance between two points using Haversine formula
//        double earthRadius = 6371000; // Radius of the Earth in meters
//        double dLat = Math.toRadians(destLat - empLat);
//        double dLon = Math.toRadians(destLong - empLong);
//        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
//                   Math.cos(Math.toRadians(empLat)) * Math.cos(Math.toRadians(destLat)) *
//                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//        return earthRadius * c;
//    }

    MarkAttendance(String username) {
    	
    	// Set the username for the current instance
        this.username = username; // Assuming 'this' refers to your ActionListener
        try {
        	Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();

            JFrame frame = new JFrame("Attendance System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 200);
            frame.setLayout(new GridLayout(3, 2));

            JLabel usernameLabel = new JLabel("Username:");
            JTextField usernameField = new JTextField();
            JLabel actionLabel = new JLabel("Mark:");
            String[] actions = {"In", "Out", "Weekoff"};
            JComboBox<String> actionComboBox = new JComboBox<>(actions);

            JButton submitButton = new JButton("Submit");
            JButton backButton = new JButton("Back");

            backButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Code to navigate back to the home page
                    // For example, if Home class is your home page, you can do:
                    frame.dispose(); // Close the current frame
                    new Home(username); // Open the home page
                }
            });

         // Add a new case to handle "Weekoff" action in submitButton ActionListener
            submitButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String username = usernameField.getText();
                    // Check if the username is not empty or null
                    if (username == null || username.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Please enter a valid username.");
                        return; // Exit the method without marking attendance
                    }
                    String action = (String) actionComboBox.getSelectedItem();
                    String currentTime = getCurrentTime();

                    // Check if the action is "Weekoff"
                    if (action.equals("Weekoff")) {
                        markWeekoff(username, currentTime);
                        JOptionPane.showMessageDialog(null, "Weekoff marked successfully.");
                        return; // Exit the method after marking weekoff
                    }

                    // For "In" and "Out" actions, continue with the existing logic
                    double[] empLocation = MarkAttendance.this.getEmployeeLocation();
                    double empLatitude = empLocation[0];
                    double empLongitude = empLocation[1];
                    
                    try {
                        // Fetch employee location
                        JSONObject locationData = fetchEmployeeLocation();

                        // Extract latitude and longitude from response
                        JSONObject location = locationData.getJSONObject("location");
                        double latitude = location.getDouble("lat");
                        double longitude = location.getDouble("lng");

                        MarkAttendance geofence = new MarkAttendance(username);
                        if (MarkAttendance.isWithinAnyRadius(latitude, longitude)) {
                            markAttendance(username, action, currentTime, empLatitude, empLongitude);
                            JOptionPane.showMessageDialog(null, "Attendance marked successfully.");
                        } else {
                            JOptionPane.showMessageDialog(null, "Please go to the correct location to mark attendance.");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            

      frame.add(usernameLabel);
      frame.add(usernameField);
      frame.add(actionLabel);
      frame.add(actionComboBox);
//      frame.add(new JLabel()); // Empty label for spacing
      frame.add(backButton); // Add the back button
      frame.add(submitButton);
     
      frame.setLocationRelativeTo(null); // Center the frame on the screen
      frame.setVisible(true);

  } catch (Exception e) {
      e.printStackTrace();
  }
}

    private double[] getEmployeeLocation() {
        // This method should retrieve the employee's current location
        // For the sake of example, let's assume we're fetching it from a GPS-based service
        // In a real application, you would use appropriate methods/APIs to get the location

        // Example location within the predefined radius (40.7128, -74.0060 ± 0.1 degree)
        double empLatitude = fetchLatitudeFromGPS(); // Method to fetch latitude from GPS
        double empLongitude = fetchLongitudeFromGPS(); // Method to fetch longitude from GPS

        System.out.println(empLatitude + "EMP_Lat");
        System.out.println(empLongitude + "EMP_Log");

        return new double[]{empLatitude, empLongitude};
    }
    
    private double fetchLatitudeFromGPS() {
        // Method to fetch latitude from GPS service or location provider
        // You should implement the logic to retrieve the latitude here
        // For example, you might use a library or API to interact with GPS hardware
        // or retrieve location data from a service
        // For the sake of example, let's return a placeholder value
        return 0.0; // Placeholder value, replace with actual implementation
    }

    private double fetchLongitudeFromGPS() {
        // Method to fetch longitude from GPS service or location provider
        // You should implement the logic to retrieve the longitude here
        // For example, you might use a library or API to interact with GPS hardware
        // or retrieve location data from a service
        // For the sake of example, let's return a placeholder value
        return 0.0; // Placeholder value, replace with actual implementation
    }

    
 // Method to mark weekoff and update status column accordingly
    private static void markWeekoff(String username, String time) {
        try {
            int employeeId = getEmployeeId(username);
            String checkIfExistsSQL = "SELECT * FROM attendance WHERE employee_id = ? AND attendance_date = CURDATE()";
            try (PreparedStatement checkIfExistsStmt = conn.prepareStatement(checkIfExistsSQL)) {
                checkIfExistsStmt.setInt(1, employeeId);
                ResultSet rs = checkIfExistsStmt.executeQuery();
                if (rs.next()) {
                    String updateSQL = "UPDATE attendance SET status = 'Weekoff' WHERE employee_id = ? AND attendance_date = CURDATE()";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
                        updateStmt.setInt(1, employeeId);
                        updateStmt.executeUpdate();
                    }
                } else {
                    String insertSQL = "INSERT INTO attendance (employee_id, username, attendance_date, status) VALUES (?, ?, CURDATE(), 'Weekoff')";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                        insertStmt.setInt(1, employeeId);
                        insertStmt.setString(2, username);
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    
    
    // NEW CODE
    
    public static JSONObject fetchEmployeeLocation() throws IOException, JSONException {
        // Replace "YOUR_API_KEY" with your actual Google Maps API key
        String apiKey = "AIzaSyCYWctM7RGH12FlSazNS1lnr03-OmPlFog";
        System.out.println(apiKey+"api key");

        // URL for Google Maps Geolocation API
        String apiUrl = "https://www.googleapis.com/geolocation/v1/geolocate?key=" + apiKey;
        System.out.println(apiUrl+" apiUrl");

        // Create HTTP connection
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Send POST request
        OutputStream os = conn.getOutputStream();
        os.write("{}".getBytes());
        os.flush();
        os.close();

        // Read response
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        StringBuilder response = new StringBuilder();
        System.out.println(response+" response");
        
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
            System.out.println(response+" response");
            
            
        }

        // Close connection
        conn.disconnect();

        // Parse JSON response
        return new JSONObject(response.toString());
        
    }

    
//Retrieve employee ID based on username
private static int getEmployeeId(String username) throws SQLException {
    String sql = "SELECT empId FROM employee WHERE username = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("empId");
        } else {
            throw new SQLException("Employee not found for username: " + username);
        }
    }
}

// Mark attendance with proper employee ID
private static void markAttendance(String username, String action, String time, double latitude, double longitude) {
    try {
        // Retrieve employee ID based on the username
        int employeeId = getEmployeeId(username);

        // Check if an attendance record already exists for the current date and employee
        String checkIfExistsSQL = "SELECT * FROM attendance WHERE employee_id = ? AND attendance_date = CURDATE()";
        try (PreparedStatement checkIfExistsStmt = conn.prepareStatement(checkIfExistsSQL)) {
            checkIfExistsStmt.setInt(1, employeeId);
            ResultSet rs = checkIfExistsStmt.executeQuery();
            if (rs.next()) {
                // If an attendance record already exists for the current date and employee...
                if (action.equals("In") && rs.getString("in_time") != null) {
                    JOptionPane.showMessageDialog(null, "You have already marked attendance for 'In' today.");
                } else if (action.equals("Out") && rs.getString("out_time") != null) {
                    JOptionPane.showMessageDialog(null, "You have already marked attendance for 'Out' today.");
                } else {
                    // Update the existing record with the out time if action is "Out"
                    if (action.equals("Out")) {
                        String updateSQL = "UPDATE attendance SET out_time = ?, location = ? WHERE employee_id = ? AND attendance_date = CURDATE()";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
                            updateStmt.setString(1, time);
                            updateStmt.setString(2, getLocationName(latitude, longitude));
                            updateStmt.setInt(3, employeeId);
                            updateStmt.executeUpdate();
                            JOptionPane.showMessageDialog(null, "Attendance marked successfully for 'Out'.");
                        }
                    }
                }
            } else {
                // If no attendance record exists for the current date and employee...
                if (action.equals("In")) {
                    String insertSQL = "INSERT INTO attendance (employee_id, username, attendance_date, in_time, location) VALUES (?, ?, CURDATE(), ?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                        insertStmt.setInt(1, employeeId);
                        insertStmt.setString(2, username);
                        insertStmt.setString(3, time);
                        insertStmt.setString(4, getLocationName(latitude, longitude));
                        insertStmt.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Attendance marked successfully for 'In'.");
                    }
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        // Handle any database errors
    }
}

private static String getLocationName(double latitude, double longitude) {
    String closestLocation = "Unknown";
    double closestDistance = Double.MAX_VALUE;

    for (Map.Entry<String, double[]> entry : PREDEFINED_LOCATIONS.entrySet()) {
        String locationName = entry.getKey();
        double[] location = entry.getValue();
        double distance = calculateDistance(latitude, longitude, location[0], location[1]);
        
        if (distance < closestDistance) {
            closestDistance = distance;
            closestLocation = locationName;
        }
    }
    return closestLocation;
}


private static String getCurrentTime() {
    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    return now.format(formatter);
}

public static void main(String[] args) throws NoSuchMethodException, SecurityException {
	String username = "default";
    new MarkAttendance(username);
    
    
}
}
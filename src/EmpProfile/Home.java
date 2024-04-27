package EmpProfile;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Home extends JFrame implements ActionListener {
	

    JButton view, add, add1, remove, update, update1, apply, view1, open, view2, notificationTab, applyLeaveTab;
    private JButton logoutButton;
    private String username;
    private ArrayList<String> leaveRequests; // Store leave requests

    public Home(String username) {
        this.username = username;
        leaveRequests = new ArrayList<>();

        setTitle("Home Page");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null); // Using absolute layout for simplicity

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/home.jpg"));
        Image i2 = i1.getImage().getScaledInstance(1120, 630, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel image = new JLabel(i3);
        image.setBounds(0, 0, 1120, 630);
        add(image);

        JLabel heading = new JLabel("Employee Management System");
        heading.setBounds(620, 20, 400, 40);
        heading.setFont(new Font("Raleway", Font.BOLD, 25));
        image.add(heading);

        add1 = new JButton("Mark Attendance");
        add1.setBounds(650, 80, 150, 40);
        add1.addActionListener(this);
        image.add(add1);

        view = new JButton("View Employee");
        view.setBounds(820, 80, 150, 40);
        view.addActionListener(this);
        image.add(view);

        view1 = new JButton("View Attendance");
        view1.setBounds(650, 140, 150, 40);
        view1.addActionListener(this);
        image.add(view1);

        apply = new JButton("Apply Leave");
        apply.setBounds(820, 140, 150, 40);
        apply.addActionListener(this);
        image.add(apply);

        if (username.equals("pacifichouse")) {
            // If the logged-in user is "pasifichouse", show all buttons
            add = new JButton("Add Employee");
            add.setBounds(650, 200, 150, 40);
            add.addActionListener(this);
            image.add(add);

            remove = new JButton("Remove Employee");
            remove.setBounds(820, 200, 150, 40);
            remove.addActionListener(this);
            image.add(remove);

            update1 = new JButton("Update Employee");
            update1.setBounds(650, 260, 150, 40);
            update1.addActionListener(this);
            image.add(update1);

            open = new JButton("Salary Sheet");
            open.setBounds(820, 260, 150, 40);
            open.addActionListener(this);
            image.add(open);

            JButton notificationButton = new JButton("Notifications");
            notificationButton.setBounds(650, 320, 150, 40);
            notificationButton.addActionListener(this);
            add(notificationButton);
        }
        
     // Create logout button
        logoutButton = new JButton("Logout");
        logoutButton.setBounds(950, 500, 100, 40);
        logoutButton.addActionListener(this);
        image.add(logoutButton);

        setSize(1120, 630);
        setLocation(100, 50);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
    	if (ae.getSource() == add) {
            setVisible(false);
            new AddEmployee();
    	}else if (ae.getActionCommand().equals("Apply Leave")) {
            // Open the apply leave form
            new ApplyLeave(username);
        } else if (ae.getActionCommand().equals("Notifications")) {
            // Show notifications
            showNotifications();
        } else   if (ae.getSource() == add1) {
          setVisible(false);
          new MarkAttendance(username);
      } else 
      	if (ae.getSource() == view) {
          setVisible(false);
          ViewEmployee ViewEmployee = new ViewEmployee(username);
      } else if (ae.getSource() == update) {
          setVisible(false);
          new ViewEmployee(username);
      } else if (ae.getSource() == update1) {
          setVisible(false);
          new UpdateEmployee(username);
      } else if (ae.getSource() == view1) {
          setVisible(false);
          ViewAttendance viewAttendance = new ViewAttendance(username);
      } else if (ae.getSource() == open) {
          setVisible(false);
          new SalarySheetGenerator(username);
          // Logout action listener
      } else if (ae.getSource() == logoutButton) {
              // Perform logout actions
              performLogout();
          
      }  else {
              setVisible(false);
              new RemoveEmployee(username);
          
      }
    }
    
    private void performLogout() {
        // Add any necessary logout logic here
        // For example, clearing session data, closing connections, etc.
        // Then close the current window
        dispose();
        // Optionally, redirect to the login page if applicable
        // Example:
         new Login(); // Assuming Login is your login page class
    }

    public void addLeaveRequest(String request) {
        // Add leave request to the list
        leaveRequests.add(request);
    }

    private void showNotifications() {
        // Create a new frame for displaying notifications
        JFrame notificationFrame = new JFrame("Leave Applications");
        notificationFrame.setLayout(new BorderLayout());

        // Create a panel to hold the notifications
        JPanel notificationPanel = new JPanel();
        notificationPanel.setLayout(new BoxLayout(notificationPanel, BoxLayout.Y_AXIS));

        // Add each leave request to the panel
        for (String request : leaveRequests) {
            JPanel requestPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            
            // Split the request string to retrieve the individual pieces of information
            String[] requestInfo = request.split(";");
            
            // Ensure that the requestInfo array has at least 4 elements
            if (requestInfo.length >= 4) {
                // Extract information from the request string
                String employeeName = requestInfo[0];
                String startDate = requestInfo[1];
                String endDate = requestInfo[2];
                String reason = requestInfo[3];

                // Create labels for the information
                JLabel nameLabel = new JLabel("Employee: " + employeeName);
                JLabel startDateLabel = new JLabel("Start Date: " + startDate);
                JLabel endDateLabel = new JLabel("End Date: " + endDate);
                JLabel reasonLabel = new JLabel("Reason: " + reason);
                JButton approveButton = new JButton("Approve");
                JButton rejectButton = new JButton("Reject");

             // Add action listener for approve button
                approveButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    	 updateLeaveRequestStatus(employeeName, startDate, endDate, reason, "Approved");
                    	// Update attendance table if necessary
                         updateAttendanceTable(employeeName, startDate, endDate, "Leave"); // Mark the dates as leave days
                        // Display "Approved" label
                        JLabel statusLabel = new JLabel("Approved");
                        statusLabel.setBackground(new Color(144, 238, 144)); // Light green color
                        statusLabel.setOpaque(true); // Set to opaque to make background color visible
                        requestPanel.remove(approveButton);
                        requestPanel.remove(rejectButton);
                        requestPanel.add(statusLabel);
                        requestPanel.revalidate();
                        requestPanel.repaint();
                    }
                });

                // Add action listener for reject button
                rejectButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    	 updateLeaveRequestStatus(employeeName, startDate, endDate, reason, "Rejected");
                        // Display "Rejected" label
                        JLabel statusLabel = new JLabel("Rejected");
                        statusLabel.setBackground(Color.RED); // Red color
                        statusLabel.setOpaque(true); // Set to opaque to make background color visible
                        requestPanel.remove(approveButton);
                        requestPanel.remove(rejectButton);
                        requestPanel.add(statusLabel);
                        requestPanel.revalidate();
                        requestPanel.repaint();
                    }
                });


                // Add components to the request panel
                requestPanel.add(nameLabel);
                requestPanel.add(startDateLabel);
                requestPanel.add(endDateLabel);
                requestPanel.add(reasonLabel);
                requestPanel.add(approveButton);
                requestPanel.add(rejectButton);
                notificationPanel.add(requestPanel);
            } else {
                // Log an error if the request format is incorrect
                System.err.println("Invalid leave request format: " + request);
            }
        }

        // Add the notification panel to a scrollable pane
        JScrollPane scrollPane = new JScrollPane(notificationPanel);
        notificationFrame.add(scrollPane, BorderLayout.CENTER);

        notificationFrame.setSize(600, 400);
        notificationFrame.setLocationRelativeTo(null); // Center the window
        notificationFrame.setVisible(true);
    }
    
    
    private void updateLeaveRequestStatus(String employeeName, String startDate, String endDate, String reason, String status ) {
        // Code to update leave request status in the database
        
        // Database connection parameters
        String url = "jdbc:mysql://localhost:3306/employeemanagementsystem";
        String username = "root";
        String password = "Artee@123";
        
        // SQL query to update leave request status
        String sql = "INSERT INTO LeaveRequests (employeeName, startDate, endDate, reason, status) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = conn.prepareStatement(sql)) {
            
        	// Set parameters
            statement.setString(1, employeeName);
            statement.setString(2, startDate); // This should be a date value, not "Artee"
            statement.setString(3, endDate);
            statement.setString(4, reason);
            statement.setString(5, status);
            
            // Execute the update
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Leave request status updated successfully.");
            } else {
                System.out.println("No leave request found for the given details.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateAttendanceTable(String employeeName, String startDate, String endDate, String status) {
        // Code to update attendance table in the database
        
        // Database connection parameters
        String url = "jdbc:mysql://localhost:3306/employeemanagementsystem";
        String dbusername = "root";
        String password = "Artee@123";
        
        // SQL query to update attendance table
        String sql = "INSERT INTO Attendance (status, username, attendance_date) VALUES (?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(url, dbusername, password);
             PreparedStatement statement = conn.prepareStatement(sql)) {
            
            // Get the date range between startDate and endDate
            java.time.LocalDate start = java.time.LocalDate.parse(startDate);
            java.time.LocalDate end = java.time.LocalDate.parse(endDate);
            
            // Iterate over each date in the range
            for (java.time.LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                // Check if the employee is on leave for the current date
                if (isOnLeave(employeeName, date)) {
                    System.out.println("Employee is on leave for date: " + date);
                    // If employee is on leave, update only the status for that date
                    statement.setString(1, status);
                    statement.setString(2, username);
                    statement.setString(3, date.toString());
                    
                    // Execute the update
                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Attendance updated for date: " + date);
                    } else {
                        System.out.println("No attendance record found for date: " + date);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    
    private boolean isOnLeave(String employeeName, java.time.LocalDate date) {
        // Database connection parameters
        String url = "jdbc:mysql://localhost:3306/employeemanagementsystem";
        String username = "root";
        String password = "Artee@123";
        
        // SQL query to check if the employee is on leave for the given date
        String sql = "SELECT * FROM LeaveRequests WHERE employeeName = ? AND ? BETWEEN startDate AND endDate";
        
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = conn.prepareStatement(sql)) {
            
            // Set parameters
            statement.setString(1, employeeName);
            statement.setString(2, date.toString());
            
            // Execute the query
            ResultSet resultSet = statement.executeQuery();
            
            // If there's any result, the employee is on leave for the given date
            return resultSet.next();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false; // Return false in case of an error
        }
    }




    public static void main(String[] args) {
        String username = "default"; // or get username from user input
        new Home(username);
    }
}






//package EmpProfile;
//
//import javax.swing.*;
//
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//
//public class Home extends JFrame implements ActionListener {
//
//	JButton view, add, add1, remove, update, update1, apply, view1, open, view2, notificationTab, applyLeaveTab;
//    private String username;
//    private ArrayList<String> leaveRequests; // Store leave requests
//
//    public Home(String username) {
//        this.username = username;
//        leaveRequests = new ArrayList<>();
//
//        setTitle("Home Page");
//        setDefaultCloseOperation(EXIT_ON_CLOSE);
//        setLayout(null); // Using absolute layout for simplicity
//        
//      ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/home.jpg"));
//      Image i2 = i1.getImage().getScaledInstance(1120, 630, Image.SCALE_DEFAULT);
//      ImageIcon i3 = new ImageIcon(i2);
//      JLabel image = new JLabel(i3);
//      image.setBounds(0, 0, 1120, 630);
//      add(image);
//      
//      JLabel heading = new JLabel("Employee Management System");
//      heading.setBounds(620, 20, 400, 40);
//      heading.setFont(new Font("Raleway", Font.BOLD, 25));
//      image.add(heading);
//      
//      add1 = new JButton("Mark Attendance");
//      add1.setBounds(650, 80, 150, 40);
//      add1.addActionListener(this);
//      image.add(add1);
//      
//      
//      view = new JButton("View Employee");
//      view.setBounds(820, 80, 150, 40);
//      view.addActionListener(this);
//      image.add(view);
//      
//      view1 = new JButton("View Attendance");
//      view1.setBounds(650, 140, 150, 40);
//      view1.addActionListener(this);
//      image.add(view1);
//      
//     
//      apply = new JButton("Apply Leave");
//      apply.setBounds(820, 140, 150, 40);
//      apply.addActionListener(this);
//      image.add(apply);
//      
//      
//      add = new JButton("Add Employee");
//      add.setBounds(650, 200, 150, 40);
//      add.addActionListener(this);
//      image.add(add);
//      
//
//      remove = new JButton("Remove Employee");
//      remove.setBounds(820, 200, 150, 40);
//      remove.addActionListener(this);
//      image.add(remove);
//    
//    
//      update1 = new JButton("Update Employee");
//      update1.setBounds(650, 260, 150, 40);
//      update1.addActionListener(this);
//      image.add(update1);
//    
//      open = new JButton("Salary Sheet");
//      open.setBounds(820, 260, 150, 40);
//      open.addActionListener(this);
//      image.add(open);
//    
//    
//    
//      JButton notificationButton = new JButton("Notifications");
//      notificationButton.setBounds(650, 320, 150, 40);
//      notificationButton.addActionListener(this);
//      add(notificationButton);
//      
//
//
//    setSize(1120, 630);
//  setLocation(100, 50);
//  setVisible(true);
//    }
//
//    public void actionPerformed(ActionEvent ae) {
//    	if (ae.getSource() == add) {
//            setVisible(false);
//            new AddEmployee();
//    	}else if (ae.getActionCommand().equals("Apply Leave")) {
//            // Open the apply leave form
//            new ApplyLeave(username);
//        } else if (ae.getActionCommand().equals("Notifications")) {
//            // Show notifications
//            showNotifications();
//        } else   if (ae.getSource() == add1) {
//          setVisible(false);
//          new MarkAttendance();
//      } else 
//      	if (ae.getSource() == view) {
//          setVisible(false);
//          ViewEmployee ViewEmployee = new ViewEmployee(username);
//      } else if (ae.getSource() == update) {
//          setVisible(false);
//          new ViewEmployee(username);
//      } else if (ae.getSource() == update1) {
//          setVisible(false);
//          new UpdateEmployee(username);
//      } else if (ae.getSource() == view1) {
//          setVisible(false);
//          ViewAttendance viewAttendance = new ViewAttendance(username);
//      } else if (ae.getSource() == open) {
//          setVisible(false);
//          new SalarySheetGenerator();
//      }  else {
//              setVisible(false);
//              new RemoveEmployee();
//          
//      }
//    }
//
//    public void addLeaveRequest(String request) {
//        // Add leave request to the list
//        leaveRequests.add(request);
//    }
//
//    private void showNotifications() {
//        // Create a new frame for displaying notifications
//        JFrame notificationFrame = new JFrame("Leave Applications");
//        notificationFrame.setLayout(new BorderLayout());
//
//        // Create a panel to hold the notifications
//        JPanel notificationPanel = new JPanel();
//        notificationPanel.setLayout(new BoxLayout(notificationPanel, BoxLayout.Y_AXIS));
//
//        // Add each leave request to the panel
//        for (String request : leaveRequests) {
//            JPanel requestPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//            
//            // Split the request string to retrieve the individual pieces of information
//            String[] requestInfo = request.split(";");
//            
//            // Ensure that the requestInfo array has at least 4 elements
//            if (requestInfo.length >= 4) {
//                // Extract information from the request string
//                String employeeName = requestInfo[0];
//                String startDate = requestInfo[1];
//                String endDate = requestInfo[2];
//                String reason = requestInfo[3];
//
//                // Create labels for the information
//                JLabel nameLabel = new JLabel("Employee: " + employeeName);
//                JLabel startDateLabel = new JLabel("Start Date: " + startDate);
//                JLabel endDateLabel = new JLabel("End Date: " + endDate);
//                JLabel reasonLabel = new JLabel("Reason: " + reason);
//                JButton approveButton = new JButton("Approve");
//                JButton rejectButton = new JButton("Reject");
//
//             // Add action listener for approve button
//                approveButton.addActionListener(new ActionListener() {
//                    public void actionPerformed(ActionEvent e) {
//                    	 updateLeaveRequestStatus(employeeName, startDate, endDate, reason, "Approved");
//                    	// Update attendance table if necessary
//                         updateAttendanceTable(employeeName, startDate, endDate, "Leave"); // Mark the dates as leave days
//                        // Display "Approved" label
//                        JLabel statusLabel = new JLabel("Approved");
//                        statusLabel.setBackground(new Color(144, 238, 144)); // Light green color
//                        statusLabel.setOpaque(true); // Set to opaque to make background color visible
//                        requestPanel.remove(approveButton);
//                        requestPanel.remove(rejectButton);
//                        requestPanel.add(statusLabel);
//                        requestPanel.revalidate();
//                        requestPanel.repaint();
//                    }
//                });
//
//                // Add action listener for reject button
//                rejectButton.addActionListener(new ActionListener() {
//                    public void actionPerformed(ActionEvent e) {
//                    	 updateLeaveRequestStatus(employeeName, startDate, endDate, reason, "Rejected");
//                        // Display "Rejected" label
//                        JLabel statusLabel = new JLabel("Rejected");
//                        statusLabel.setBackground(Color.RED); // Red color
//                        statusLabel.setOpaque(true); // Set to opaque to make background color visible
//                        requestPanel.remove(approveButton);
//                        requestPanel.remove(rejectButton);
//                        requestPanel.add(statusLabel);
//                        requestPanel.revalidate();
//                        requestPanel.repaint();
//                    }
//                });
//
//
//                // Add components to the request panel
//                requestPanel.add(nameLabel);
//                requestPanel.add(startDateLabel);
//                requestPanel.add(endDateLabel);
//                requestPanel.add(reasonLabel);
//                requestPanel.add(approveButton);
//                requestPanel.add(rejectButton);
//                notificationPanel.add(requestPanel);
//            } else {
//                // Log an error if the request format is incorrect
//                System.err.println("Invalid leave request format: " + request);
//            }
//        }
//
//        // Add the notification panel to a scrollable pane
//        JScrollPane scrollPane = new JScrollPane(notificationPanel);
//        notificationFrame.add(scrollPane, BorderLayout.CENTER);
//
//        notificationFrame.setSize(600, 400);
//        notificationFrame.setLocationRelativeTo(null); // Center the window
//        notificationFrame.setVisible(true);
//    }
//    
//    
//    private void updateLeaveRequestStatus(String employeeName, String startDate, String endDate, String reason, String status ) {
//        // Code to update leave request status in the database
//        
//        // Database connection parameters
//        String url = "jdbc:mysql://localhost:3306/employeemanagementsystem";
//        String username = "root";
//        String password = "Artee@123";
//        
//        // SQL query to update leave request status
//        String sql = "INSERT INTO LeaveRequests (employeeName, startDate, endDate, reason, status) VALUES (?, ?, ?, ?, ?)";
//        
//        try (Connection conn = DriverManager.getConnection(url, username, password);
//             PreparedStatement statement = conn.prepareStatement(sql)) {
//            
//        	// Set parameters
//            statement.setString(1, employeeName);
//            statement.setString(2, startDate); // This should be a date value, not "Artee"
//            statement.setString(3, endDate);
//            statement.setString(4, reason);
//            statement.setString(5, status);
//            
//            // Execute the update
//            int rowsAffected = statement.executeUpdate();
//            if (rowsAffected > 0) {
//                System.out.println("Leave request status updated successfully.");
//            } else {
//                System.out.println("No leave request found for the given details.");
//            }
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    private void updateAttendanceTable(String employeeName, String startDate, String endDate, String status) {
//        // Code to update attendance table in the database
//        
//        // Database connection parameters
//        String url = "jdbc:mysql://localhost:3306/employeemanagementsystem";
//        String dbusername = "root";
//        String password = "Artee@123";
//        
//        // SQL query to update attendance table
//        String sql = "INSERT INTO Attendance (status, username, attendance_date) VALUES (?, ?, ?)";
//        
//        try (Connection conn = DriverManager.getConnection(url, dbusername, password);
//             PreparedStatement statement = conn.prepareStatement(sql)) {
//            
//            // Get the date range between startDate and endDate
//            java.time.LocalDate start = java.time.LocalDate.parse(startDate);
//            java.time.LocalDate end = java.time.LocalDate.parse(endDate);
//            
//            // Iterate over each date in the range
//            for (java.time.LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
//                // Check if the employee is on leave for the current date
//                if (isOnLeave(employeeName, date)) {
//                    System.out.println("Employee is on leave for date: " + date);
//                    // If employee is on leave, update only the status for that date
//                    statement.setString(1, status);
//                    statement.setString(2, username);
//                    statement.setString(3, date.toString());
//                    
//                    // Execute the update
//                    int rowsAffected = statement.executeUpdate();
//                    if (rowsAffected > 0) {
//                        System.out.println("Attendance updated for date: " + date);
//                    } else {
//                        System.out.println("No attendance record found for date: " + date);
//                    }
//                }
//            }
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    
//    private boolean isOnLeave(String employeeName, java.time.LocalDate date) {
//        // Database connection parameters
//        String url = "jdbc:mysql://localhost:3306/employeemanagementsystem";
//        String username = "root";
//        String password = "Artee@123";
//        
//        // SQL query to check if the employee is on leave for the given date
//        String sql = "SELECT * FROM LeaveRequests WHERE employeeName = ? AND ? BETWEEN startDate AND endDate";
//        
//        try (Connection conn = DriverManager.getConnection(url, username, password);
//             PreparedStatement statement = conn.prepareStatement(sql)) {
//            
//            // Set parameters
//            statement.setString(1, employeeName);
//            statement.setString(2, date.toString());
//            
//            // Execute the query
//            ResultSet resultSet = statement.executeQuery();
//            
//            // If there's any result, the employee is on leave for the given date
//            return resultSet.next();
//            
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//            return false; // Return false in case of an error
//        }
//    }
//
//
//
//
//    public static void main(String[] args) {
//        String username = "default"; // or get username from user input
//        new Home(username);
//    }
//}
//



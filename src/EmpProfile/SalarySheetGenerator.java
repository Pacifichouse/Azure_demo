package EmpProfile;

import java.sql.*;
import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

public class SalarySheetGenerator extends JFrame implements ActionListener {

    JTable table;
    JDatePickerImpl startDatePicker;
    JDatePickerImpl endDatePicker;
    String username;

    SalarySheetGenerator(String username) {
    	this.username = username;
        setTitle("Salary Sheet");
        setSize(900, 600);
        setLocation(200, 50);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        UtilDateModel startModel = new UtilDateModel();
        UtilDateModel endModel = new UtilDateModel();
        Properties properties = new Properties();
        properties.put("text.today", "Today");
        properties.put("text.month", "Month");
        properties.put("text.year", "Year");
        JDatePanelImpl startDatePanel = new JDatePanelImpl(startModel, properties);
        JDatePanelImpl endDatePanel = new JDatePanelImpl(endModel, properties);
        startDatePicker = new JDatePickerImpl(startDatePanel, new DateLabelFormatter());
        endDatePicker = new JDatePickerImpl(endDatePanel, new DateLabelFormatter());

        JPanel datePanel = new JPanel(new GridLayout(1, 4));
        datePanel.add(new JLabel("Start Date:"));
        datePanel.add(startDatePicker);
        datePanel.add(new JLabel("End Date:"));
        datePanel.add(endDatePicker);
        add(datePanel, BorderLayout.NORTH);

        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JButton generateButton = new JButton("Generate Salary Sheet");
        generateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generateSalarySheet();
            }
        });
        add(generateButton, BorderLayout.SOUTH);

        JButton printButton = new JButton("Print");
        printButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                printTable();
            }
        });
        add(printButton, BorderLayout.EAST);

        setVisible(true);
        
        
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Navigate back to the home page
                dispose(); // Close the current window
                // Create an instance of the Home class
//                Home homePage = new Home(username);
//                homePage.setVisible(true); // Show the home page
                
                new Home(username);
            }
        });
        add(backButton, BorderLayout.WEST); // Add the back button to the west side of the layout
    }

    private void generateSalarySheet() {
        java.util.Date startDate = (java.util.Date) startDatePicker.getModel().getValue();
        java.util.Date endDate = (java.util.Date) endDatePicker.getModel().getValue();

        // Validate that both start date and end date are selected
        if (startDate == null || endDate == null) {
            JOptionPane.showMessageDialog(this, "Please select both start date and end date.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "SELECT empId, username, Salary FROM Employee";
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/employeemanagementsystem", "root", "Artee@123");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Employee ID");
            model.addColumn("Employee Name");
            model.addColumn("Monthly Salary");
            model.addColumn("Total Working Days (Monthly)");
            model.addColumn("Total Present Days (Selected Period)");
            model.addColumn("Total Leave Days (Selected Period)"); // Added for clarity
            model.addColumn("Total Week Off Days (Selected Period)"); // Added for clarity
            model.addColumn("Total Late Marks (Selected Period)");
            model.addColumn("Total Salary");

            while (rs.next()) {
                String empId = rs.getString("empId");
                String empName = rs.getString("username");
                double monthlySalary = rs.getDouble("Salary");

                // Calculate salary for the selected period
                int totalWorkingDaysInPeriod = getTotalWorkingDaysInPeriod(startDate, endDate);
                int totalPresentDaysInPeriod = getTotalPresentDaysInPeriod(empId, startDate, endDate);
                int totalLeaveDaysInPeriod = getTotalLeaveDaysInPeriod(empId, startDate, endDate);
                int totalWeekOffDaysInPeriod = getTotalWeekOffDaysInPeriod(startDate, endDate);
                int totalLateMarksInPeriod = getTotalLateMarksInPeriod(empId, startDate, endDate); // New method call

             // Calculate per-day salary
                double perDaySalary = monthlySalary / totalWorkingDaysInPeriod;

                // Calculate salary for attended days
                double attendedDaysSalary = perDaySalary * totalPresentDaysInPeriod;

                // Calculate total salary including leave days
                double totalSalary = attendedDaysSalary + (totalLeaveDaysInPeriod + totalWeekOffDaysInPeriod) * perDaySalary;

                // Deduct salary for late marks
                int extraLateMarks = Math.max(totalLateMarksInPeriod - 4, 0);
                double lateMarkDeduction = (0.5 * perDaySalary) * extraLateMarks;

                // Deduct late mark deduction from total salary
                double finalSalary = totalSalary - lateMarkDeduction;

                // Add the final salary to the table model
                model.addRow(new Object[]{empId, empName, monthlySalary, totalWorkingDaysInPeriod, totalPresentDaysInPeriod, totalLeaveDaysInPeriod, totalWeekOffDaysInPeriod, totalLateMarksInPeriod, finalSalary});


            }

            table.setModel(model);
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    private int getTotalLateMarksInPeriod(String empId, java.util.Date startDate, java.util.Date endDate) {
        int totalLateMarks = 0;
        LocalDate startLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        for (LocalDate date = startLocalDate; date.isBefore(endLocalDate.plusDays(1)); date = date.plusDays(1)) {
            if (isLateMark(empId, date)) {
                totalLateMarks++;
            }
        }
        return totalLateMarks;
    }
    
    private boolean isLateMark(String empId, LocalDate date) {
        // Logic to determine if it's a late mark day based on your business rules or database information
        // For demonstration purposes, let's assume late mark days are stored in a table named "LateMarks"
        // and employees marked as late if they come after 10:16 AM.
        LocalTime lateMarkTime = LocalTime.of(10, 16); // 10:16 AM
        LocalDateTime dateTime = LocalDateTime.of(date, lateMarkTime);
        String query = "SELECT COUNT(*) AS count FROM Attendance WHERE employee_id = ? AND attendance_date = ? AND in_time > ?";
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/employeemanagementsystem", "root", "Artee@123");
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, empId);
            pstmt.setDate(2, java.sql.Date.valueOf(date));
            pstmt.setObject(3, dateTime);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("count");
                return count > 0;
            }
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    private int getTotalWorkingDaysInPeriod(java.util.Date startDate, java.util.Date endDate) {
        // Convert Date objects to LocalDate
        LocalDate startLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        
        // Get the current month and year from the end date
        int currentYear = endLocalDate.getYear();
        int currentMonth = endLocalDate.getMonthValue();
        
        // Calculate the start date of the period (21st of the previous month)
        LocalDate periodStartDate = LocalDate.of(currentYear, currentMonth - 1, 21);
        if (currentMonth == 1) {
            periodStartDate = LocalDate.of(currentYear - 1, 12, 21); // Wrap to December of the previous year if current month is January
        }
        
        // Calculate the end date of the period (20th of the current month)
        LocalDate periodEndDate = LocalDate.of(currentYear, currentMonth, 20);
        
        int totalWorkingDays = 0;
        
        // Iterate over each day from the start date to the end date
        for (LocalDate date = periodStartDate; !date.isAfter(periodEndDate); date = date.plusDays(1)) {
            if (!isWeekOff(date)) {
                totalWorkingDays++;
            }
        }
        
        return totalWorkingDays;
    }
    

    private boolean isWeekOff(LocalDate date) {
        // Logic to determine if it's a week off day based on the status column in the Attendance table
        String query = "SELECT COUNT(*) AS count FROM Attendance WHERE attendance_date = ? AND (status = 'Week Off' OR status = 'week off')";
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/employeemanagementsystem", "root", "Artee@123");
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setDate(1, java.sql.Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("count");
                return count > 0;
            }
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    

    private int getTotalPresentDaysInPeriod(String empId, java.util.Date startDate, java.util.Date endDate) {
        int totalPresentDays = 0;
        LocalDate startLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        for (LocalDate date = startLocalDate; date.isBefore(endLocalDate.plusDays(1)); date = date.plusDays(1)) {
            if (isPresent(empId, date)) {
                if (!isLeaveDay(empId, date) && !isWeekOff(date)) {
                    totalPresentDays++;
                }
            }
        }
        return totalPresentDays;
    }

    private boolean isLeaveDay(String empId, LocalDate date) {
        String query = "SELECT COUNT(*) AS leave_count FROM Attendance WHERE employee_id = ? AND attendance_date = ? AND (status = 'Leave' OR status = 'leave')";
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/employeemanagementsystem", "root", "Artee@123");
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, empId);
            pstmt.setDate(2, java.sql.Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("leave_count");
                return count > 0;
            }
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }


	private boolean isPresent(String empId, LocalDate date) {
        // Here you can implement logic to check if the employee is present on a given date.
        // You may need to query the database or use some other method to determine if the employee has attendance records (in or out) on the given date.
        // For demonstration purposes, let's assume there is a method called 'hasAttendanceRecord' that returns true if the employee has attendance records (either in or out) on the given date.
        return hasAttendanceRecord(empId, date);
    }
    
    
    private int getTotalLeaveDaysInPeriod(String empId, java.util.Date startDate, java.util.Date endDate) {
        int totalLeaveDays = 0;
        LocalDate startLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        for (LocalDate date = startLocalDate; date.isBefore(endLocalDate.plusDays(1)); date = date.plusDays(1)) {
            if (isLeaveDay(empId, date)) {
                totalLeaveDays++;
            }
        }
        return totalLeaveDays;
    }

    private boolean hasAttendanceRecord(String empId, LocalDate date) {
        String query = "SELECT COUNT(*) AS attendance_count FROM Attendance WHERE employee_id = ? AND attendance_date = ?";
        try {
            // Establish connection to the database
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/employeemanagementsystem", "root", "Artee@123");
            
            // Prepare the SQL statement
            PreparedStatement pstmt = conn.prepareStatement(query);
            
            // Set the parameters (employee ID and attendance date)
            pstmt.setString(1, empId);
            pstmt.setDate(2, java.sql.Date.valueOf(date));
            
            // Execute the query
            ResultSet rs = pstmt.executeQuery();
            
            // Check if there are any attendance records for the employee on the specified date
            if (rs.next()) {
                return rs.getInt("attendance_count") > 0; // Return true if there are attendance records, false otherwise
            }
            
            // Close the database connection
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace(); // Print any SQL exception that occurs
        }
        return false; // Return false if an error occurs or if there are no attendance records
    }

    private int getTotalWeekOffDaysInPeriod(java.util.Date startDate, java.util.Date endDate) {
        int totalWeekOffDays = 0;
        LocalDate startLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        for (LocalDate date = startLocalDate; date.isBefore(endLocalDate.plusDays(1)); date = date.plusDays(1)) {
            if (isWeekOff(date)) {
                totalWeekOffDays++;
            }
        }
        return totalWeekOffDays;
    }
 
 
 
 private void printTable() {
     try {
         // Show print dialog
         boolean complete = table.print();

         // Check if printing is successful
         if (complete) {
             JOptionPane.showMessageDialog(this, "Printing Completed!", "Print", JOptionPane.INFORMATION_MESSAGE);
         } else {
             JOptionPane.showMessageDialog(this, "Printing Cancelled!", "Print", JOptionPane.WARNING_MESSAGE);
         }
     } catch (PrinterException pe) {
         JOptionPane.showMessageDialog(this, "Printing Failed: " + pe.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
     }
 }

 public static void main(String[] args) {
     SwingUtilities.invokeLater(new Runnable() {
         public void run() {
        	 String username = "default";
             new SalarySheetGenerator(username);
         }
     });
 }

 @Override
 public void actionPerformed(ActionEvent e) {
     // TODO Auto-generated method stub
 }
}








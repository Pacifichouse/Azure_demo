package EmpProfile;


import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

import net.proteanit.sql.DbUtils;
import java.awt.event.*;
import com.toedter.calendar.JDateChooser;

public class ViewAttendance extends JFrame implements ActionListener {

    JTable table;
    JComboBox<String> searchDropdown; // Dropdown for selecting search criteria
    JComboBox<String> searchValueDropdown; // Dropdown for selecting search value
    JButton back, search; // Added search button
    JDateChooser fromDateChooser, toDateChooser; // Date pickers for from and to dates
    String loggedInUsername;

    ViewAttendance(String username) {
        this.loggedInUsername = username;

        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        JLabel searchlbl = new JLabel("Search by:");
        searchlbl.setBounds(20, 20, 100, 20);
        add(searchlbl);

        // Dropdown for selecting search criteria
        searchDropdown = new JComboBox<>(new String[]{"Username", "Employee ID"});
        searchDropdown.setBounds(120, 20, 120, 20);
        searchDropdown.addActionListener(this);
        add(searchDropdown);

        // Date picker for from date
        JLabel fromLbl = new JLabel("From:");
        fromLbl.setBounds(260, 20, 50, 20);
        add(fromLbl);
        fromDateChooser = new JDateChooser();
        fromDateChooser.setBounds(300, 20, 150, 20);
        add(fromDateChooser);

        // Date picker for to date
        JLabel toLbl = new JLabel("To:");
        toLbl.setBounds(470, 20, 30, 20);
        add(toLbl);
        toDateChooser = new JDateChooser();
        toDateChooser.setBounds(500, 20, 150, 20);
        add(toDateChooser);

        // Dropdown for selecting search value
        searchValueDropdown = new JComboBox<>();
        searchValueDropdown.setBounds(670, 20, 150, 20);
        add(searchValueDropdown);

        table = new JTable();

        // Constructing query with parameterized username
        String query = (loggedInUsername.equals("pacifichouse")) ?
                "SELECT * FROM Attendance" :
                "SELECT * FROM Attendance WHERE username = ?";

        try {
            Conn c = new Conn();
            PreparedStatement pstmt = c.prepareStatement(query);
            if (!loggedInUsername.equals("pacifichouse")) {
                pstmt.setString(1, loggedInUsername); // Setting the username parameter
            }
            ResultSet rs = pstmt.executeQuery();
            table.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            e.printStackTrace();
        }

        JScrollPane jsp = new JScrollPane(table);
        jsp.setBounds(0, 100, 900, 600);
        add(jsp);

        back = new JButton("Back");
        back.setBounds(220, 70, 80, 20);
        back.addActionListener(this);
        add(back);

        // Show search button only for pacifichouse user
        if (loggedInUsername.equals("pacifichouse")) {
            search = new JButton("Search");
            search.setBounds(830, 20, 80, 20);
            search.addActionListener(this);
            add(search);
        }

        setSize(900, 700);
        setLocation(200, 20);
        setVisible(true);
    }

    private void populateUsernames() {
        if (loggedInUsername.equals("pacifichouse")) {
            try {
                Conn c = new Conn();
                ResultSet rs = c.s.executeQuery("SELECT DISTINCT username FROM Attendance");
                while (rs.next()) {
                    searchValueDropdown.addItem(rs.getString("username"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void populateEmpIds() {
        if (loggedInUsername.equals("pacifichouse")) {
            try {
                Conn c = new Conn();
                ResultSet rs = c.s.executeQuery("SELECT DISTINCT employee_id FROM Attendance");
                while (rs.next()) {
                    searchValueDropdown.addItem(rs.getString("employee_id"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == back) {
            setVisible(false);
            new Home(loggedInUsername);
        } else if (ae.getSource() == search) {
            String searchValue = searchValueDropdown.getSelectedItem().toString();
            String searchCriteria = searchDropdown.getSelectedItem().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fromDate = sdf.format(fromDateChooser.getDate());
            String toDate = sdf.format(toDateChooser.getDate());
            
            try {
                Conn c = new Conn();
                String query = "";
                if (searchCriteria.equals("Username")) {
                    query = "SELECT * FROM Attendance WHERE username = ?";
                } else if (searchCriteria.equals("Employee ID")) {
                    query = "SELECT * FROM Attendance WHERE employee_id = ?";
                }
                if (fromDateChooser.getDate() != null && toDateChooser.getDate() != null) {
                    query += " AND attendance_date BETWEEN ? AND ?";
                }
                PreparedStatement pstmt = c.prepareStatement(query);
                pstmt.setString(1, searchValue);
                if (fromDateChooser.getDate() != null && toDateChooser.getDate() != null) {
                    pstmt.setString(2, fromDate);
                    pstmt.setString(3, toDate);
                }
                ResultSet rs = pstmt.executeQuery();
                table.setModel(DbUtils.resultSetToTableModel(rs));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (ae.getSource() == searchDropdown) {
            String selectedCriteria = searchDropdown.getSelectedItem().toString();
            searchValueDropdown.removeAllItems(); // Clear previous items
            if (selectedCriteria.equals("Username")) {
                populateUsernames();
            } else if (selectedCriteria.equals("Employee ID")) {
                populateEmpIds();
            }
        }
    }

    public static void main(String[] args) {
        String username = "default"; // or get username from user input
        new ViewAttendance(username);
    }
}





//package EmpProfile;
//
//import javax.swing.*;
//import java.awt.*;
//import java.sql.*;
//import net.proteanit.sql.DbUtils;
//import java.awt.event.*;
//
//public class ViewAttendance extends JFrame implements ActionListener {
//
//    JTable table;
//    Choice cemployeeId;
//    JButton back, search; // Added search button
//    String username;
//
//    ViewAttendance(String username) {
//        this.username = username;
//
//        getContentPane().setBackground(Color.WHITE);
//        setLayout(null);
//
//        JLabel searchlbl = new JLabel("Search by Employee Id");
//        searchlbl.setBounds(20, 20, 150, 20);
//        add(searchlbl);
//
//        cemployeeId = new Choice();
//        cemployeeId.setBounds(180, 20, 150, 20);
//        add(cemployeeId);
//
//        // Show search button and search by ID option only for pacifichouse user
//        if (username.equals("pacifichouse")) {
//            search = new JButton("Search");
//            search.setBounds(120, 70, 80, 20);
//            search.addActionListener(this);
//            add(search);
//        }
//
//        try {
//            Conn c = new Conn();
//            ResultSet rs = c.s.executeQuery("select * from employee");
//            while (rs.next()) {
//                cemployeeId.add(rs.getString("empId"));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        table = new JTable();
//
//        // Constructing query with parameterized username
//        String query = (username.equals("pacifichouse")) ?
//                "SELECT * FROM Attendance" :
//                "SELECT * FROM Attendance WHERE username IN (SELECT username FROM login WHERE username = ?)";
//
//        try {
//            Conn c = new Conn();
//            PreparedStatement pstmt = c.prepareStatement(query);
//            if (!username.equals("pacifichouse")) {
//                pstmt.setString(1, username); // Setting the username parameter
//            }
//            ResultSet rs = pstmt.executeQuery();
//            table.setModel(DbUtils.resultSetToTableModel(rs));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        JScrollPane jsp = new JScrollPane(table);
//        jsp.setBounds(0, 100, 900, 600);
//        add(jsp);
//
//        back = new JButton("Back");
//        back.setBounds(220, 70, 80, 20);
//        back.addActionListener(this);
//        add(back);
//
//        setSize(900, 700);
//        setLocation(200, 20);
//        setVisible(true);
//    }
//
//    public void actionPerformed(ActionEvent ae) {
//        if (ae.getSource() == back) {
//            setVisible(false);
//            new Home(username);
//        } else if (ae.getSource() == search) {
//            String selectedEmpId = cemployeeId.getSelectedItem();
//            if (selectedEmpId != null && !selectedEmpId.isEmpty()) {
//                try {
//                    Conn c = new Conn();
//                    String query = "SELECT * FROM Attendance WHERE employee_id = ?";
//                    PreparedStatement pstmt = c.prepareStatement(query);
//                    pstmt.setString(1, selectedEmpId);
//                    ResultSet rs = pstmt.executeQuery();
//                    table.setModel(DbUtils.resultSetToTableModel(rs));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            } else {
//                JOptionPane.showMessageDialog(this, "Please select an employee ID.", "No Employee ID Selected", JOptionPane.WARNING_MESSAGE);
//            }
//        }
//    }
//
//    public static void main(String[] args) {
//        String username = "default"; // or get username from user input
//        new ViewAttendance(username);
//    }
//}














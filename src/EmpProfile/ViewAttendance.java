package EmpProfile;


import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

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
            table.setModel(DbUtilsHelper.resultSetToTableModel(rs));
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

    public class DbUtilsHelper {
    public static DefaultTableModel resultSetToTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();

        // Get column names
        int columnCount = metaData.getColumnCount();
        Vector<String> columnNames = new Vector<>();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        // Get data rows
        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                row.add(rs.getObject(columnIndex));
            }
            data.add(row);
        }

        return new DefaultTableModel(data, columnNames);
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
                table.setModel(DbUtilsHelper.resultSetToTableModel(rs));
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





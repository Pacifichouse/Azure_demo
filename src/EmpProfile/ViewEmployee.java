package EmpProfile;

import javax.swing.*;

import net.proteanit.sql.DbUtils;

import java.awt.*;
import java.sql.*;
import java.awt.event.*;

public class ViewEmployee extends JFrame implements ActionListener {

    JButton back;
    JPanel detailsPanel;
    JTable table;
    Choice cemployeeId;
    JButton search, print, update;
    String username;
    Connection connection;

    ViewEmployee(String username) {
        this.username = username;

        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        if (username.equals("pacifichouse")) {
            initializeTableBasedUI();
        } else {
            initializeDetailsPanelBasedUI();
        }

        setSize(800, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeTableBasedUI() {
        table = new JTable();

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new FlowLayout());

        JLabel searchlbl = new JLabel("Search by Employee Id");
        panel.add(searchlbl);

        cemployeeId = new Choice();
        panel.add(cemployeeId);

        try {
            Conn c = new Conn();
            ResultSet rs = c.s.executeQuery("select * from employee");
            while (rs.next()) {
                cemployeeId.add(rs.getString("empId"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        search = new JButton("Search");
        search.addActionListener(this);
        panel.add(search);

        print = new JButton("Print");
        print.addActionListener(this);
        panel.add(print);

        update = new JButton("Update");
        update.addActionListener(this);
        panel.add(update);

        back = new JButton("Back");
        back.addActionListener(this);
        panel.add(back);

        add(panel, BorderLayout.NORTH);
        
//        add(panel, BorderLayout.NORTH);

       

        JScrollPane jsp = new JScrollPane(table);
        add(jsp, BorderLayout.CENTER);
        
        // Fetch all data and populate table
        populateTable();
    }
    
    private void populateTable() {
        try {
            Conn c = new Conn();
            ResultSet rs = c.s.executeQuery("select * from employee");
            table = new JTable(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            e.printStackTrace();
        }

        JScrollPane jsp = new JScrollPane(table);
        add(jsp, BorderLayout.CENTER);
    }

    private void initializeDetailsPanelBasedUI() {
        detailsPanel = new JPanel();
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setLayout(new GridLayout(0, 2, 10, 10));

        JScrollPane detailsScrollPane = new JScrollPane(detailsPanel);
        add(detailsScrollPane, BorderLayout.CENTER);

        back = new JButton("Back");
        back.addActionListener(this);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(back);

        add(bottomPanel, BorderLayout.SOUTH);

        fetchEmployeeDetails();
    }

    private void fetchEmployeeDetails() {
        detailsPanel.removeAll();

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/employeemanagementsystem", "root", "Artee@123");

            String query = "SELECT * FROM employee WHERE username = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    String value = rs.getString(i);
                    JLabel label = new JLabel(columnName + ": " + value);
                    detailsPanel.add(label);
                }
            }

            rs.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        detailsPanel.revalidate();
        detailsPanel.repaint();
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == back) {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            setVisible(false);
            new Home(username);
        } else if (ae.getSource() == search) {
            String query = "select * from employee where empId = '" + cemployeeId.getSelectedItem() + "'";
            try {
                Conn c = new Conn();
                ResultSet rs = c.s.executeQuery(query);
                table.setModel(DbUtils.resultSetToTableModel(rs));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (ae.getSource() == print) {
            try {
                table.print();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (ae.getSource() == update) {
            setVisible(false);
            new UpdateEmployee(cemployeeId.getSelectedItem());
        }
    }

    public static void main(String[] args) {
        String username = "default"; // or get username from user input
        new ViewEmployee(username);
    }
}

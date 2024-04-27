package EmpProfile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.ArrayList;

public class ApplyLeave extends JFrame implements ActionListener {

    private JTextField leaveTypeField;
    private JTextField startDateField;
    private JTextField endDateField;
    private JTextField reasonField;
    private String username;
    private Connection connection;
    private JButton back;

    public ApplyLeave(String username) {
        this.username = username;

        setTitle("Leave Application Form");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 400);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2));

        JLabel leaveTypeLabel = new JLabel("Leave Type:");
        leaveTypeField = new JTextField();
        JLabel startDateLabel = new JLabel("Start Date:");
        startDateField = new JTextField();
        JLabel endDateLabel = new JLabel("End Date:");
        endDateField = new JTextField();
        JLabel reasonLabel = new JLabel("Reason:");
        reasonField = new JTextField();

        back = new JButton("Back");
        back.addActionListener(this);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitLeaveRequest();
            }
        });

        panel.add(leaveTypeLabel);
        panel.add(leaveTypeField);
        panel.add(startDateLabel);
        panel.add(startDateField);
        panel.add(endDateLabel);
        panel.add(endDateField);
        panel.add(reasonLabel);
        panel.add(reasonField);
        panel.add(new JLabel()); // Placeholder for spacing
        panel.add(back);
        panel.add(new JLabel()); // Placeholder for spacing
        panel.add(submitButton);

        add(panel);
        setVisible(true);
    }

    private void submitLeaveRequest() {
        // Retrieve leave request details from input fields
        String leaveType = leaveTypeField.getText();
        String startDate = startDateField.getText();
        String endDate = endDateField.getText();
        String reason = reasonField.getText();

        // Simulated backend operation: Print leave request details to console
        System.out.println("Leave Type: " + leaveType);
        System.out.println("Start Date: " + startDate);
        System.out.println("End Date: " + endDate);
        System.out.println("Reason: " + reason);

        // Placeholder for sending notification to HR panel
        System.out.println("Sending leave request notification to HR panel...");

        // Construct the leave request string in the expected format
        String leaveRequest = username + ";" + startDate + ";" + endDate + ";" + reason;

        // Add the leave request to the list
        Home home = new Home(username);
        home.addLeaveRequest(leaveRequest);
        
        

        // Display a notification message to the user
        JOptionPane.showMessageDialog(this, "Leave request submitted successfully!");

        // Further processing or actions can be added here if needed
    }



    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == back) {
            // Close the frame properly
            dispose();
            // Assuming you need to navigate to the Home frame
            new Home(username);
        }
    }

    public static void main(String[] args) {
        String username = "default"; // or get username from user input
        new ApplyLeave(username);
    }
}

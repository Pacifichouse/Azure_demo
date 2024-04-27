package EmpProfile;


import java.awt.*;
import javax.swing.*;
import com.toedter.calendar.JDateChooser;
import java.util.*;
import java.awt.event.*;

public class AddEmployee extends JFrame implements ActionListener{
    
    Random ran = new Random();
    int number = ran.nextInt(999999);
    
    JTextField tfname, tffname, tfaddress, tfphone, tfaadhar, tfemail, tfsalary, tfdesignation, tfid, tfBAcc, tfBName, tfifsc;
    JDateChooser dcdob, dcdoj;
    JComboBox cbeducation;
//    JLabel lblempId;
    JButton add, back;
    String username;
    
    AddEmployee() {
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);
        
        JLabel heading = new JLabel("Add Employee Detail");
        heading.setBounds(320, 10, 500, 50);
        heading.setFont(new Font("SAN_SERIF", Font.BOLD, 25));
        add(heading);
        
        JLabel labelname = new JLabel("First Name");
        labelname.setBounds(50, 100, 150, 30);
        labelname.setFont(new Font("serif", Font.PLAIN, 20));
        add(labelname);
        
        tfname = new JTextField();
        tfname.setBounds(200, 100, 150, 30);
        add(tfname);
        
        JLabel labelfname = new JLabel("Last Name");
        labelfname.setBounds(400, 100, 150, 30);
        labelfname.setFont(new Font("serif", Font.PLAIN, 20));
        add(labelfname);
        
        tffname = new JTextField();
        tffname.setBounds(600, 100, 150, 30);
        add(tffname);
        
        JLabel labeldob = new JLabel("Date of Birth");
        labeldob.setBounds(50, 150, 150, 30);
        labeldob.setFont(new Font("serif", Font.PLAIN, 20));
        add(labeldob);
        
        dcdob = new JDateChooser();
        dcdob.setBounds(200, 150, 150, 30);
        add(dcdob);
        
        JLabel labelsalary = new JLabel("Salary");
        labelsalary.setBounds(400, 150, 150, 30);
        labelsalary.setFont(new Font("serif", Font.PLAIN, 20));
        add(labelsalary);
        
        tfsalary = new JTextField();
        tfsalary.setBounds(600, 150, 150, 30);
        add(tfsalary);
        
        JLabel labeladdress = new JLabel("Address");
        labeladdress.setBounds(50, 200, 150, 30);
        labeladdress.setFont(new Font("serif", Font.PLAIN, 20));
        add(labeladdress);
        
        tfaddress = new JTextField();
        tfaddress.setBounds(200, 200, 150, 30);
        add(tfaddress);
        
        JLabel labelphone = new JLabel("Phone");
        labelphone.setBounds(400, 200, 150, 30);
        labelphone.setFont(new Font("serif", Font.PLAIN, 20));
        add(labelphone);
        
        tfphone = new JTextField();
        tfphone.setBounds(600, 200, 150, 30);
        add(tfphone);
        
        JLabel labelemail = new JLabel("Email");
        labelemail.setBounds(50, 250, 150, 30);
        labelemail.setFont(new Font("serif", Font.PLAIN, 20));
        add(labelemail);
        
        tfemail = new JTextField();
        tfemail.setBounds(200, 250, 150, 30);
        add(tfemail);
        
        JLabel labeleducation = new JLabel("Higest Education");
        labeleducation.setBounds(400, 250, 150, 30);
        labeleducation.setFont(new Font("serif", Font.PLAIN, 20));
        add(labeleducation);
        
        String courses[] = {"BBA", "BCA", "BA", "BSC", "B.COM", "BTech", "MBA", "MCA", "MA", "MTech", "MSC", "PHD", "Other"};
        cbeducation = new JComboBox(courses);
        cbeducation.setBackground(Color.WHITE);
        cbeducation.setBounds(600, 250, 150, 30);
        add(cbeducation);
        
        JLabel labeldesignation = new JLabel("Designation");
        labeldesignation.setBounds(50, 300, 150, 30);
        labeldesignation.setFont(new Font("serif", Font.PLAIN, 20));
        add(labeldesignation);
        
        tfdesignation = new JTextField();
        tfdesignation.setBounds(200, 300, 150, 30);
        add(tfdesignation);
        
        JLabel labelaadhar = new JLabel("Aadhar Number");
        labelaadhar.setBounds(400, 300, 150, 30);
        labelaadhar.setFont(new Font("serif", Font.PLAIN, 20));
        add(labelaadhar);
        
        tfaadhar = new JTextField();
        tfaadhar.setBounds(600, 300, 150, 30);
        add(tfaadhar);
        
        JLabel labelempId = new JLabel("Employee id");
        labelempId.setBounds(50, 350, 150, 30);
        labelempId.setFont(new Font("serif", Font.PLAIN, 20));
        add(labelempId);
        
        tfid = new JTextField();
        tfid.setBounds(200, 350, 150, 30);
        add(tfid);
        
        JLabel labelBankAcc = new JLabel("Bank Account No.");
        labelBankAcc.setBounds(400, 350, 150, 30);
        labelBankAcc.setFont(new Font("serif", Font.PLAIN, 20));
        add(labelBankAcc);
        
        tfBAcc = new JTextField();
        tfBAcc.setBounds(600, 350, 150, 30);
        add(tfBAcc);
        
        JLabel labelBankName = new JLabel("Bank Name");
        labelBankName.setBounds(50, 400, 150, 30);
        labelBankName.setFont(new Font("serif", Font.PLAIN, 20));
        add(labelBankName);
        
        tfBName = new JTextField();
        tfBName.setBounds(200, 400, 150, 30);
        add(tfBName);
        
        JLabel labelIfsc = new JLabel("IFSC Code");
        labelIfsc.setBounds(400, 400, 150, 30);
        labelIfsc.setFont(new Font("serif", Font.PLAIN, 20));
        add(labelIfsc);
        
        tfifsc = new JTextField();
        tfifsc.setBounds(600, 400, 150, 30);
        add(tfifsc);
        
        JLabel labeldoj = new JLabel("Date of Join");
        labeldoj.setBounds(50, 450, 150, 30);
        labeldoj.setFont(new Font("serif", Font.PLAIN, 20));
        add(labeldoj);
        
        dcdoj = new JDateChooser();
        dcdoj.setBounds(200, 450, 150, 30);
        add(dcdoj);
        
//        lblempId = new JLabel("" + number);
//        lblempId.setBounds(200, 400, 150, 30);
//        lblempId.setFont(new Font("serif", Font.PLAIN, 20));
//        add(lblempId);
        
        add = new JButton("Add Details");
        add.setBounds(250, 550, 150, 40);
        add.addActionListener(this);
        add.setBackground(Color.BLACK);
        add.setForeground(Color.WHITE);
        add(add);
        
        back = new JButton("Back");
        back.setBounds(450, 550, 150, 40);
        back.addActionListener(this);
        back.setBackground(Color.BLACK);
        back.setForeground(Color.WHITE);
        add(back);
        
        setSize(900, 700);
        setLocation(200, 20);
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == add) {
            String name = tfname.getText();
            String fname = tffname.getText();
            String dob = ((JTextField) dcdob.getDateEditor().getUiComponent()).getText();
            String salary = tfsalary.getText();
            String address = tfaddress.getText();
            String phone = tfphone.getText();
            String email = tfemail.getText();
            String education = (String) cbeducation.getSelectedItem();
            String designation = tfdesignation.getText();
            String aadhar = tfaadhar.getText();
            String empId = tfid.getText();
            String BankAcc = tfBAcc.getText();
            String BankName = tfBName.getText();
            String Ifsc = tfifsc.getText();
            String doj =((JTextField) dcdoj.getDateEditor().getUiComponent()).getText();
            
            try {
                Conn conn = new Conn();
                String query = "insert into employee values('"+name+"', '"+fname+"', '"+dob+"', '"+salary+"', '"+address+"', '"+phone+"', '"+email+"', '"+education+"', '"+designation+"', '"+aadhar+"', '"+empId+"','"+BankAcc+"' ,'"+BankName+"', '"+Ifsc+"', '"+doj+"')";
                conn.s.executeUpdate(query);
                JOptionPane.showMessageDialog(null, "Details added successfully");
                setVisible(false);
                new Home(username);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            setVisible(false);
            new Home(username);
        }
    }

    public static void main(String[] args) {
        new AddEmployee();
    }
}


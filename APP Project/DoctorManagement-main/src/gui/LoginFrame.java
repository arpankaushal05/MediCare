package gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    
    public LoginFrame() {
        setTitle("Login");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);
        
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(10, 20, 80, 25);
        add(lblUsername);
        
        JTextField txtUsername = new JTextField(20);
        txtUsername.setBounds(100, 20, 165, 25);
        add(txtUsername);
        
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(10, 50, 80, 25);
        add(lblPassword);
        
        JPasswordField txtPassword = new JPasswordField(20);
        txtPassword.setBounds(100, 50, 165, 25);
        add(txtPassword);
        
        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(10, 80, 255, 25);
        add(btnLogin);

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = txtUsername.getText();
                String password = new String(txtPassword.getPassword());
                // Authentication logic goes here
                JOptionPane.showMessageDialog(null, "Logged in as " + username);
                dispose(); // Close login frame
            }
        });
        
        setVisible(true);
    }
}

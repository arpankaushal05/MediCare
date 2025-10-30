package client.admin;

import javax.swing.SwingUtilities;

public class AdminApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminLoginFrame().setVisible(true));
    }
}

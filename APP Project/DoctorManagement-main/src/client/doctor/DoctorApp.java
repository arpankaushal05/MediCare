package client.doctor;

import javax.swing.SwingUtilities;

public class DoctorApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DoctorLoginFrame().setVisible(true));
    }
}

package client.patient;

import javax.swing.SwingUtilities;

public class PatientApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PatientLoginFrame loginFrame = new PatientLoginFrame();
            loginFrame.setVisible(true);
        });
    }
}

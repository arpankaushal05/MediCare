package client;

import client.admin.AdminLoginFrame;
import client.doctor.DoctorLoginFrame;
import client.patient.PatientLoginFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class HomeLauncher extends JFrame {
    private BufferedImage backgroundImage;

    public HomeLauncher() {
        setTitle("Doctor Management Portal Launcher");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loadBackgroundImage();

        if (backgroundImage != null) {
            setSize(new Dimension(backgroundImage.getWidth(), backgroundImage.getHeight()));
        } else {
            setSize(520, 340);
        }

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        backgroundPanel.setLayout(new BorderLayout(20, 20));

        JLabel heading = new JLabel("Choose a portal to open", SwingConstants.CENTER);
        heading.setFont(new Font("SansSerif", Font.BOLD, 22));
        heading.setForeground(Color.WHITE);
        JPanel headingWrapper = new JPanel(new BorderLayout());
        headingWrapper.setOpaque(false);
        headingWrapper.add(heading, BorderLayout.CENTER);
        headingWrapper.setBorder(BorderFactory.createEmptyBorder(25, 25, 10, 25));
        backgroundPanel.add(headingWrapper, BorderLayout.NORTH);

        JPanel buttonsPanel = new JPanel(new GridLayout(3, 1, 12, 12));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(0, 60, 10, 60));

        JButton adminButton = new JButton("Administrator Portal");
        adminButton.addActionListener(event -> openAdminPortal());
        buttonsPanel.add(stylePrimaryButton(adminButton));

        JButton doctorButton = new JButton("Doctor Portal");
        doctorButton.addActionListener(event -> openDoctorPortal());
        buttonsPanel.add(stylePrimaryButton(doctorButton));

        JButton patientButton = new JButton("Patient Portal");
        patientButton.addActionListener(event -> openPatientPortal());
        buttonsPanel.add(stylePrimaryButton(patientButton));

        backgroundPanel.add(buttonsPanel, BorderLayout.CENTER);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(event -> System.exit(0));
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 25));
        footer.add(styleSecondaryButton(exitButton));
        backgroundPanel.add(footer, BorderLayout.SOUTH);

        setContentPane(backgroundPanel);
        setLocationRelativeTo(null);
    }

    private JButton stylePrimaryButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(new Color(0, 102, 204));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        return button;
    }

    private JButton styleSecondaryButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(new Color(255, 255, 255, 180));
        button.setForeground(Color.DARK_GRAY);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        return button;
    }

    private void loadBackgroundImage() {
        try {
            backgroundImage = ImageIO.read(new File("images/home_page.png"));
        } catch (IOException e) {
            System.err.println("[DoctorManagement] Unable to load home page background: " + e.getMessage());
            backgroundImage = null;
        }
    }

    private void openAdminPortal() {
        SwingUtilities.invokeLater(() -> new AdminLoginFrame().setVisible(true));
    }

    private void openDoctorPortal() {
        SwingUtilities.invokeLater(() -> new DoctorLoginFrame().setVisible(true));
    }

    private void openPatientPortal() {
        SwingUtilities.invokeLater(() -> new PatientLoginFrame().setVisible(true));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HomeLauncher().setVisible(true));
    }
}

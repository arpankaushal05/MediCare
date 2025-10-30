package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class MainFrame extends JFrame {
    private BufferedImage backgroundImage;
    private ViewDoctorsFrame viewDoctorsFrame;
    private ViewPatientsFrame viewPatientsFrame;

    public MainFrame() {
        loadBackground();

        setTitle("Doctor Management System");
        if (backgroundImage != null) {
            setSize(new Dimension(backgroundImage.getWidth(), backgroundImage.getHeight()));
        } else {
            setSize(960, 640);
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(getBackground());
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        backgroundPanel.setLayout(new BorderLayout());

        JLabel heading = new JLabel("Administrator Control Center");
        heading.setFont(new Font("SansSerif", Font.BOLD, 28));
        heading.setForeground(java.awt.Color.WHITE);
        JPanel headingPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 25));
        headingPanel.setOpaque(false);
        headingPanel.add(heading);
        backgroundPanel.add(headingPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (g instanceof java.awt.Graphics2D g2) {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                }
            }
        };
        buttonPanel.setOpaque(false);

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));
        topRow.setOpaque(false);
        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));
        bottomRow.setOpaque(false);

        JButton btnViewDoctors = createStyledButton("View Doctors");
        JButton btnViewPatients = createStyledButton("View Patients");
        JButton btnManageDoctors = createStyledButton("Manage Doctors");
        JButton btnManagePatients = createStyledButton("Manage Patients");
        JButton btnManageAppointments = createStyledButton("Manage Appointments");


        btnViewDoctors.addActionListener(e -> showViewDoctorsFrame());
        btnViewPatients.addActionListener(e -> showViewPatientsFrame());
        btnManageDoctors.addActionListener(e -> showManageDoctorsFrame());
        btnManagePatients.addActionListener(e -> new ManagePatientsFrame());
        btnManageAppointments.addActionListener(e -> new ManageAppointmentsFrame());

        topRow.add(btnViewDoctors);
        topRow.add(btnViewPatients);
        topRow.add(btnManageDoctors);

        bottomRow.add(btnManagePatients);
        bottomRow.add(btnManageAppointments);

        buttonPanel.add(topRow, BorderLayout.NORTH);
        buttonPanel.add(bottomRow, BorderLayout.SOUTH);

        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(backgroundPanel);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadBackground() {
        try {
            backgroundImage = ImageIO.read(new File("images/admin_page.png"));
        } catch (IOException e) {
            System.err.println("[DoctorManagement] Unable to load admin background: " + e.getMessage());
            backgroundImage = null;
        }
    }

    private void showViewDoctorsFrame() {
        if (viewDoctorsFrame == null) {
            viewDoctorsFrame = new ViewDoctorsFrame();
        }
        viewDoctorsFrame.setVisible(true);
    }
    private void showViewPatientsFrame() {
        if (viewPatientsFrame == null) {
            viewPatientsFrame = new ViewPatientsFrame();
        }
        viewPatientsFrame.setVisible(true);
    }
    private void showManageDoctorsFrame() {
        new ManageDoctorsFrame().setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(180, 48));
        button.setFocusPainted(false);
        button.setBackground(new java.awt.Color(0, 51, 102, 220));
        button.setForeground(java.awt.Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 16, 10, 16));

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame();
        });
    }
}
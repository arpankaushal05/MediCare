package client.patient;

import dao.AppointmentDAO;
import dao.ChatDAO;
import dao.DoctorDAO;
import models.Appointment;
import models.ChatConversation;
import models.ChatMessage;
import models.Doctor;
import models.Patient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

class PatientDashboardFrame extends JFrame {
    private final Patient patient;
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final ChatDAO chatDAO = new ChatDAO();
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final DefaultTableModel appointmentTableModel = new DefaultTableModel(new Object[]{"Date", "Time", "Doctor", "Notes"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final DefaultTableModel doctorsTableModel = new DefaultTableModel(new Object[]{"Doctor ID", "Full Name", "Username", "Specialization", "Email", "Phone"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final DefaultComboBoxModel<Doctor> doctorComboModel = new DefaultComboBoxModel<>();
    private final DefaultListModel<ChatConversation> conversationListModel = new DefaultListModel<>();
    private final JTextArea chatTranscriptArea = new JTextArea();
    private final JTextArea messageInputArea = new JTextArea(4, 50);
    private final JButton sendButton = new JButton("Send");
    private final JComboBox<Doctor> doctorComboBox = new JComboBox<>(doctorComboModel);
    private final JTextField appointmentDateField = new JTextField(12);
    private final JTextField appointmentTimeField = new JTextField(8);
    private final JTextArea appointmentNotesArea = new JTextArea(4, 20);

    PatientDashboardFrame(Patient patient) {
        this.patient = patient;
        setTitle("Patient Portal - Welcome " + patient.getFullName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Tabs for Appointments and Chat
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Appointments", buildAppointmentsPanel());
        tabbedPane.addTab("Book Appointment", buildBookAppointmentPanel());
        tabbedPane.addTab("Medical Profile", buildMedicalProfilePanel());
        tabbedPane.addTab("Chat", buildChatPanel());
        tabbedPane.addTab("Doctors", buildDoctorsPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // Load initial data
        loadAppointments();
        loadConversations();
        loadDoctors();
    }

    private JPanel buildAppointmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JTable appointmentTable = new JTable(appointmentTableModel);
        JScrollPane scrollPane = new JScrollPane(appointmentTable);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildMedicalProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int row = 0;
        addMedicalInfoRow(content, gbc, row++, "Full Name", patient.getFullName());
        addMedicalInfoRow(content, gbc, row++, "Username", patient.getUsername());
        addMedicalInfoRow(content, gbc, row++, "Email", patient.getEmail());
        addMedicalInfoRow(content, gbc, row++, "Phone", patient.getPhone());
        addMedicalInfoRow(content, gbc, row++, "Gender", defaultValue(patient.getGender()));
        addMedicalInfoRow(content, gbc, row++, "Blood Group", defaultValue(patient.getBloodGroup()));

        String heightValue = patient.getHeightMeters() != null ? String.format("%.2f m", patient.getHeightMeters()) : "Not provided";
        addMedicalInfoRow(content, gbc, row++, "Height", heightValue);

        addMedicalInfoRow(content, gbc, row++, "Allergies", defaultValue(patient.getAllergies()));
        addMedicalInfoRow(content, gbc, row++, "Disease", defaultValue(patient.getDisease()));

        panel.add(content, BorderLayout.NORTH);
        return panel;
    }

    private void addMedicalInfoRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(label + ":"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(new JLabel(value), gbc);
    }

    private String defaultValue(String value) {
        return (value == null || value.isBlank()) ? "Not provided" : value;
    }

    private JPanel buildBookAppointmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        doctorComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Doctor doctor) {
                    setText(doctor.getName() + " - " + doctor.getSpecialization());
                }
                return this;
            }
        });

        addFormRow(form, gbc, 0, "Doctor:", doctorComboBox);

        appointmentDateField.setToolTipText("Format: YYYY-MM-DD");
        addFormRow(form, gbc, 1, "Appointment Date:", appointmentDateField);

        appointmentTimeField.setToolTipText("Format: HH:MM (24h)");
        addFormRow(form, gbc, 2, "Appointment Time:", appointmentTimeField);

        appointmentNotesArea.setLineWrap(true);
        appointmentNotesArea.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(appointmentNotesArea);
        notesScroll.setPreferredSize(new Dimension(250, 90));
        addFormRow(form, gbc, 3, "Notes:", notesScroll);

        panel.add(form, BorderLayout.CENTER);

        JButton bookButton = new JButton("Book Appointment");
        bookButton.addActionListener(event -> bookAppointment());

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.add(bookButton);

        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void addFormRow(JPanel form, GridBagConstraints gbc, int row, String label, Component component) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        form.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(component, gbc);
    }

    private JPanel buildChatPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JList<ChatConversation> conversationList = new JList<>(conversationListModel);
        conversationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        conversationList.setCellRenderer(new ConversationRenderer());
        conversationList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                ChatConversation selected = conversationList.getSelectedValue();
                if (selected != null) {
                    loadMessages(selected);
                }
            }
        });

        JPanel conversationPanel = new JPanel(new BorderLayout());
        conversationPanel.setBorder(BorderFactory.createTitledBorder("Conversations"));
        conversationPanel.add(new JScrollPane(conversationList), BorderLayout.CENTER);

        chatTranscriptArea.setEditable(false);
        chatTranscriptArea.setLineWrap(true);
        chatTranscriptArea.setWrapStyleWord(true);

        messageInputArea.setLineWrap(true);
        messageInputArea.setWrapStyleWord(true);

        sendButton.addActionListener(e -> sendMessage(conversationList.getSelectedValue()));

        JPanel messagePanel = new JPanel(new BorderLayout(5, 5));
        messagePanel.setBorder(BorderFactory.createTitledBorder("New Message"));
        messagePanel.add(new JScrollPane(messageInputArea), BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);

        JPanel chatPanel = new JPanel(new BorderLayout(5, 5));
        chatPanel.setBorder(BorderFactory.createTitledBorder("Messages"));
        chatPanel.add(new JScrollPane(chatTranscriptArea), BorderLayout.CENTER);
        chatPanel.add(messagePanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, conversationPanel, chatPanel);
        splitPane.setResizeWeight(0.3);

        panel.add(splitPane, BorderLayout.CENTER);
        panel.add(buildChatToolbar(conversationList), BorderLayout.NORTH);
        return panel;
    }

    private JPanel buildDoctorsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JTable doctorsTable = new JTable(doctorsTableModel);
        doctorsTable.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(doctorsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh Doctors");
        refreshButton.addActionListener(event -> loadDoctors());

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        toolbar.add(refreshButton);
        panel.add(toolbar, BorderLayout.SOUTH);

        return panel;
    }

    private JToolBar buildChatToolbar(JList<ChatConversation> conversationList) {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton newConversationButton = new JButton("Start New Conversation");
        newConversationButton.addActionListener(e -> startNewConversation(conversationList));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            loadConversations();
            ChatConversation selected = conversationList.getSelectedValue();
            if (selected != null) {
                loadMessages(selected);
            }
        });

        toolBar.add(newConversationButton);
        toolBar.add(refreshButton);
        return toolBar;
    }

    private void loadAppointments() {
        List<Appointment> appointments = appointmentDAO.getAppointmentsForPatient(patient.getPatientId());
        appointmentTableModel.setRowCount(0);

        for (Appointment appointment : appointments) {
            Doctor doctor = new DoctorDAO().getDoctorById(appointment.getDoctorId());
            appointmentTableModel.addRow(new Object[]{
                    appointment.getAppointmentDate(),
                    appointment.getAppointmentTime(),
                    doctor != null ? doctor.getName() + " (" + doctor.getSpecialization() + ")" : "Doctor " + appointment.getDoctorId(),
                    appointment.getNotes()
            });
        }
    }

    private void loadConversations() {
        conversationListModel.clear();
        List<ChatConversation> conversations = chatDAO.getConversationsForPatient(patient.getPatientId());
        for (ChatConversation conversation : conversations) {
            conversationListModel.addElement(conversation);
        }
    }

    private void loadMessages(ChatConversation conversation) {
        chatTranscriptArea.setText("");
        List<ChatMessage> messages = chatDAO.getMessagesForConversation(conversation.getConversationId());
        for (ChatMessage message : messages) {
            chatTranscriptArea.append(String.format("[%s | %s]%n%s%n%n",
                    message.getSenderType(),
                    message.getSentAt() != null ? message.getSentAt() : "Pending",
                    message.getMessageText()));
        }
        chatTranscriptArea.setCaretPosition(chatTranscriptArea.getDocument().getLength());
    }

    private void sendMessage(ChatConversation conversation) {
        if (conversation == null) {
            JOptionPane.showMessageDialog(this, "Please select a conversation first.", "No Conversation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String text = messageInputArea.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Type a message before sending.", "Empty Message", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ChatMessage message = new ChatMessage(conversation.getConversationId(), "PATIENT", patient.getPatientId(), text);
        ChatMessage savedMessage = chatDAO.addMessage(message);
        if (savedMessage != null) {
            messageInputArea.setText("");
            loadMessages(conversation);
        } else {
            JOptionPane.showMessageDialog(this, "Unable to send message. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void startNewConversation(JList<ChatConversation> conversationList) {
        String doctorIdText = JOptionPane.showInputDialog(this, "Enter Doctor ID to chat with:", "Start Conversation", JOptionPane.PLAIN_MESSAGE);
        if (doctorIdText == null || doctorIdText.trim().isEmpty()) {
            return;
        }

        try {
            int doctorId = Integer.parseInt(doctorIdText.trim());
            ChatConversation conversation = chatDAO.createConversation(patient.getPatientId(), doctorId);
            if (conversation != null) {
                conversationListModel.addElement(conversation);
                conversationList.setSelectedValue(conversation, true);
            } else {
                JOptionPane.showMessageDialog(this, "Unable to create conversation. Verify the doctor ID.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid doctor ID. Please enter a numeric value.", "Validation", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Provide human-readable text in JList
    private static class ConversationRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof ChatConversation conversation) {
                setText(String.format("Conversation #%d (Doctor %d) - %s",
                        conversation.getConversationId(),
                        conversation.getDoctorId(),
                        conversation.getStatus()));
            }
            return this;
        }
    }

    private void loadDoctors() {
        doctorsTableModel.setRowCount(0);
        List<Doctor> doctors = doctorDAO.getAllDoctors();
        doctorComboModel.removeAllElements();
        for (Doctor doctor : doctors) {
            doctorsTableModel.addRow(new Object[]{
                    doctor.getDoctorId(),
                    doctor.getName(),
                    doctor.getUsername(),
                    doctor.getSpecialization(),
                    doctor.getEmail(),
                    doctor.getPhone()
            });
            doctorComboModel.addElement(doctor);
        }
    }

    private void bookAppointment() {
        Doctor selectedDoctor = (Doctor) doctorComboBox.getSelectedItem();
        if (selectedDoctor == null) {
            JOptionPane.showMessageDialog(this, "Please select a doctor.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String dateText = appointmentDateField.getText().trim();
        String timeText = appointmentTimeField.getText().trim();
        String notes = appointmentNotesArea.getText().trim();

        if (dateText.isEmpty() || timeText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Date and time are required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate date;
        LocalTime time;
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        try {
            date = LocalDate.parse(dateText);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            time = LocalTime.parse(timeText, timeFormatter);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid time format. Use HH:MM (24h).", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Appointment appointment = new Appointment(
                selectedDoctor.getDoctorId(),
                patient.getPatientId(),
                date,
                time,
                notes
        );

        int generatedId = appointmentDAO.addAppointment(appointment);
        if (generatedId > 0) {
            JOptionPane.showMessageDialog(this, "Appointment booked successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            appointmentDateField.setText("");
            appointmentTimeField.setText("");
            appointmentNotesArea.setText("");
            loadAppointments();
        } else {
            JOptionPane.showMessageDialog(this, "Unable to book appointment. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

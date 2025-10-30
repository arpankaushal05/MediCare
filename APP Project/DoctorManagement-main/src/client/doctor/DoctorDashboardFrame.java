package client.doctor;

import dao.AppointmentDAO;
import dao.ChatDAO;
import dao.PatientDAO;
import models.Appointment;
import models.ChatConversation;
import models.ChatMessage;
import models.Doctor;
import models.Patient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

class DoctorDashboardFrame extends JFrame {
    private final Doctor doctor;
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final ChatDAO chatDAO = new ChatDAO();
    private final PatientDAO patientDAO = new PatientDAO();

    private final DefaultTableModel appointmentsTableModel = new DefaultTableModel(new Object[]{"Date", "Time", "Patient", "Notes"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final DefaultTableModel appointmentInfoTableModel = new DefaultTableModel(
            new Object[]{"Date", "Time", "Patient Username", "Full Name", "Email", "Phone", "Gender", "Blood Group", "Height (m)", "Allergies", "Disease", "Notes"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final DefaultListModel<ChatConversation> conversationListModel = new DefaultListModel<>();
    private final JTextArea chatTranscriptArea = new JTextArea();
    private final JTextArea messageInputArea = new JTextArea(4, 50);
    private final JButton sendButton = new JButton("Send");
    private final JButton closeConversationButton = new JButton("Mark Closed");
    private final JButton reopenConversationButton = new JButton("Reopen");

    DoctorDashboardFrame(Doctor doctor) {
        this.doctor = doctor;
        setTitle("Doctor Portal - Welcome " + doctor.getName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(950, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Appointments", buildAppointmentsPanel());
        tabbedPane.addTab("Appointment Information", buildAppointmentInfoPanel());
        tabbedPane.addTab("Chats", buildChatPanel());

        add(tabbedPane, BorderLayout.CENTER);

        loadAppointments();
        loadConversations();
    }

    private JPanel buildAppointmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JTable appointmentsTable = new JTable(appointmentsTableModel);
        JScrollPane scrollPane = new JScrollPane(appointmentsTable);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildAppointmentInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JTable infoTable = new JTable(appointmentInfoTableModel);
        infoTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        infoTable.getColumnModel().getColumn(0).setPreferredWidth(85);
        infoTable.getColumnModel().getColumn(1).setPreferredWidth(70);
        infoTable.getColumnModel().getColumn(2).setPreferredWidth(130);
        infoTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        infoTable.getColumnModel().getColumn(4).setPreferredWidth(170);
        infoTable.getColumnModel().getColumn(5).setPreferredWidth(120);
        infoTable.getColumnModel().getColumn(6).setPreferredWidth(90);
        infoTable.getColumnModel().getColumn(7).setPreferredWidth(110);
        infoTable.getColumnModel().getColumn(8).setPreferredWidth(90);
        infoTable.getColumnModel().getColumn(9).setPreferredWidth(140);
        infoTable.getColumnModel().getColumn(10).setPreferredWidth(140);
        infoTable.getColumnModel().getColumn(11).setPreferredWidth(150);
        infoTable.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(infoTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(event -> loadAppointmentInfo());

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        toolbar.add(refreshButton);
        panel.add(toolbar, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildChatPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JList<ChatConversation> conversationList = new JList<>(conversationListModel);
        conversationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        conversationList.setCellRenderer(new ConversationRenderer());
        conversationList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
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

        sendButton.addActionListener(event -> sendMessage(conversationList.getSelectedValue()));
        closeConversationButton.addActionListener(event -> updateConversationStatus("CLOSED", conversationList.getSelectedValue()));
        reopenConversationButton.addActionListener(event -> updateConversationStatus("OPEN", conversationList.getSelectedValue()));

        JPanel messagePanel = new JPanel(new BorderLayout(5, 5));
        messagePanel.setBorder(BorderFactory.createTitledBorder("Reply"));
        messagePanel.add(new JScrollPane(messageInputArea), BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new GridLayout(1, 3, 8, 0));
        actionPanel.add(sendButton);
        actionPanel.add(closeConversationButton);
        actionPanel.add(reopenConversationButton);
        messagePanel.add(actionPanel, BorderLayout.SOUTH);

        JPanel transcriptPanel = new JPanel(new BorderLayout(5, 5));
        transcriptPanel.setBorder(BorderFactory.createTitledBorder("Messages"));
        transcriptPanel.add(new JScrollPane(chatTranscriptArea), BorderLayout.CENTER);
        transcriptPanel.add(messagePanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, conversationPanel, transcriptPanel);
        splitPane.setResizeWeight(0.35);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(event -> refreshConversations(conversationList));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.add(refreshButton);

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER);

        updateActionState(null);
        return panel;
    }

    private void loadAppointments() {
        appointmentsTableModel.setRowCount(0);
        List<Appointment> appointments = appointmentDAO.getAppointmentsForDoctor(doctor.getDoctorId());
        for (Appointment appointment : appointments) {
            Patient patient = patientDAO.getPatientById(appointment.getPatientId());
            String patientLabel = patient != null ? patient.getFullName() + " (" + patient.getUsername() + ")" : "Patient " + appointment.getPatientId();
            appointmentsTableModel.addRow(new Object[]{
                    appointment.getAppointmentDate(),
                    appointment.getAppointmentTime(),
                    patientLabel,
                    appointment.getNotes()
            });
        }
        loadAppointmentInfo();
    }

    private void loadAppointmentInfo() {
        appointmentInfoTableModel.setRowCount(0);
        List<Appointment> appointments = appointmentDAO.getAppointmentsForDoctor(doctor.getDoctorId());
        for (Appointment appointment : appointments) {
            Patient patient = patientDAO.getPatientById(appointment.getPatientId());
            appointmentInfoTableModel.addRow(new Object[]{
                    appointment.getAppointmentDate(),
                    appointment.getAppointmentTime(),
                    patient != null ? patient.getUsername() : "-",
                    patient != null ? patient.getFullName() : "-",
                    patient != null ? safeValue(patient.getEmail()) : "-",
                    patient != null ? safeValue(patient.getPhone()) : "-",
                    patient != null ? safeValue(patient.getGender()) : "-",
                    patient != null ? safeValue(patient.getBloodGroup()) : "-",
                    patient != null && patient.getHeightMeters() != null ? String.format("%.2f", patient.getHeightMeters()) : "-",
                    patient != null ? safeValue(patient.getAllergies()) : "-",
                    patient != null ? safeValue(patient.getDisease()) : "-",
                    safeValue(appointment.getNotes())
            });
        }
    }

    private String safeValue(String value) {
        return (value == null || value.isBlank()) ? "-" : value;
    }

    private void loadConversations() {
        conversationListModel.clear();
        List<ChatConversation> conversations = chatDAO.getConversationsForDoctor(doctor.getDoctorId());
        for (ChatConversation conversation : conversations) {
            conversationListModel.addElement(conversation);
        }
        if (!conversationListModel.isEmpty()) {
            conversationListModel.getElementAt(0);
        }
    }

    private void refreshConversations(JList<ChatConversation> conversationList) {
        ChatConversation selected = conversationList.getSelectedValue();
        Integer previousId = selected != null ? selected.getConversationId() : null;

        loadConversations();

        if (previousId != null) {
            for (int i = 0; i < conversationListModel.size(); i++) {
                if (conversationListModel.get(i).getConversationId() == previousId) {
                    conversationList.setSelectedIndex(i);
                    break;
                }
            }
        }
        updateActionState(conversationList.getSelectedValue());
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
        updateActionState(conversation);
    }

    private void sendMessage(ChatConversation conversation) {
        if (conversation == null) {
            JOptionPane.showMessageDialog(this, "Select a conversation first.", "No Conversation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String text = messageInputArea.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Type a message before sending.", "Empty Message", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if ("CLOSED".equalsIgnoreCase(conversation.getStatus())) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "This conversation is marked CLOSED. Reopen it before replying?",
                    "Conversation Closed",
                    JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                chatDAO.updateConversationStatus(conversation.getConversationId(), "OPEN");
            } else {
                return;
            }
        }

        ChatMessage message = new ChatMessage(conversation.getConversationId(), "DOCTOR", doctor.getDoctorId(), text);
        ChatMessage savedMessage = chatDAO.addMessage(message);
        if (savedMessage != null) {
            messageInputArea.setText("");
            loadMessages(conversation);
            loadConversations();
        } else {
            JOptionPane.showMessageDialog(this, "Unable to send message. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateConversationStatus(String newStatus, ChatConversation conversation) {
        if (conversation == null) {
            JOptionPane.showMessageDialog(this, "Select a conversation first.", "No Conversation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (newStatus.equalsIgnoreCase(conversation.getStatus())) {
            return;
        }

        chatDAO.updateConversationStatus(conversation.getConversationId(), newStatus);
        loadConversations();
        loadMessages(conversation);
    }

    private void updateActionState(ChatConversation conversation) {
        boolean hasSelection = conversation != null;
        sendButton.setEnabled(hasSelection);
        closeConversationButton.setEnabled(hasSelection && !"CLOSED".equalsIgnoreCase(conversation != null ? conversation.getStatus() : ""));
        reopenConversationButton.setEnabled(hasSelection && "CLOSED".equalsIgnoreCase(conversation != null ? conversation.getStatus() : ""));
    }

    private class ConversationRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof ChatConversation conversation) {
                Patient patient = patientDAO.getPatientById(conversation.getPatientId());
                String patientLabel = patient != null ? patient.getFullName() : "Patient " + conversation.getPatientId();
                setText(String.format("#%d - %s [%s]",
                        conversation.getConversationId(),
                        patientLabel,
                        conversation.getStatus()));
            }
            return this;
        }
    }
}

package gui;

import dao.ChatDAO;
import dao.DoctorDAO;
import dao.PatientDAO;
import models.ChatConversation;
import models.ChatMessage;
import models.Doctor;
import models.Patient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageChatsFrame extends JFrame {
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final PatientDAO patientDAO = new PatientDAO();
    private final ChatDAO chatDAO = new ChatDAO();

    private final JComboBox<DoctorSelection> doctorComboBox;
    private final DefaultListModel<ChatConversation> conversationListModel = new DefaultListModel<>();
    private final JList<ChatConversation> conversationList = new JList<>(conversationListModel);
    private final JTextArea chatTranscriptArea = new JTextArea();
    private final JTextArea messageInputArea = new JTextArea(4, 50);
    private final JButton sendButton = new JButton("Send");
    private final JButton closeConversationButton = new JButton("Mark Closed");
    private final JButton reopenConversationButton = new JButton("Reopen");

    private final Map<Integer, String> patientNameCache = new HashMap<>();

    public ManageChatsFrame() {
        setTitle("Manage Patient Chats");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel rootPanel = new JPanel(new BorderLayout(10, 10));
        rootPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        doctorComboBox = new JComboBox<>();
        JPanel topPanel = buildTopPanel();
        rootPanel.add(topPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildConversationPanel(), buildChatPanel());
        splitPane.setResizeWeight(0.35);
        rootPanel.add(splitPane, BorderLayout.CENTER);

        add(rootPanel, BorderLayout.CENTER);

        loadDoctors();
        loadConversations();
    }

    private JPanel buildTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel selectorPanel = new JPanel();
        selectorPanel.add(new JLabel("Doctor:"));
        doctorComboBox.setPreferredSize(new Dimension(220, 25));
        doctorComboBox.addActionListener(e -> loadConversations());
        selectorPanel.add(doctorComboBox);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshCurrentConversation());
        selectorPanel.add(refreshButton);

        panel.add(selectorPanel, BorderLayout.WEST);
        return panel;
    }

    private JPanel buildConversationPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Conversations"));

        conversationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        conversationList.setCellRenderer(new ConversationRenderer());
        conversationList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedConversation();
            }
        });

        panel.add(new JScrollPane(conversationList), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildChatPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Messages"));

        chatTranscriptArea.setEditable(false);
        chatTranscriptArea.setLineWrap(true);
        chatTranscriptArea.setWrapStyleWord(true);

        JScrollPane transcriptScrollPane = new JScrollPane(chatTranscriptArea);
        panel.add(transcriptScrollPane, BorderLayout.CENTER);

        messageInputArea.setLineWrap(true);
        messageInputArea.setWrapStyleWord(true);

        sendButton.addActionListener(e -> sendMessage());
        closeConversationButton.addActionListener(e -> updateConversationStatus("CLOSED"));
        reopenConversationButton.addActionListener(e -> updateConversationStatus("OPEN"));

        JPanel actionsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        actionsPanel.add(sendButton);
        actionsPanel.add(closeConversationButton);
        actionsPanel.add(reopenConversationButton);

        JPanel composePanel = new JPanel(new BorderLayout(5, 5));
        composePanel.setBorder(BorderFactory.createTitledBorder("Reply"));
        composePanel.add(new JScrollPane(messageInputArea), BorderLayout.CENTER);
        composePanel.add(actionsPanel, BorderLayout.SOUTH);

        panel.add(composePanel, BorderLayout.SOUTH);
        updateActionState();

        return panel;
    }

    private void loadDoctors() {
        doctorComboBox.removeAllItems();
        doctorComboBox.addItem(new DoctorSelection(null, "All Doctors"));
        List<Doctor> doctors = doctorDAO.getAllDoctors();
        for (Doctor doctor : doctors) {
            doctorComboBox.addItem(new DoctorSelection(doctor, doctor.getName() + " (" + doctor.getSpecialization() + ")"));
        }
        doctorComboBox.setSelectedIndex(0);
    }

    private void loadConversations() {
        conversationListModel.clear();
        ChatConversation previouslySelected = conversationList.getSelectedValue();
        Integer previousId = previouslySelected != null ? previouslySelected.getConversationId() : null;

        DoctorSelection selection = (DoctorSelection) doctorComboBox.getSelectedItem();
        List<ChatConversation> conversations;
        if (selection != null && selection.getDoctorId() != null) {
            conversations = chatDAO.getConversationsForDoctor(selection.getDoctorId());
        } else {
            conversations = chatDAO.getAllConversations();
        }

        for (ChatConversation conversation : conversations) {
            conversationListModel.addElement(conversation);
        }

        if (previousId != null) {
            for (int i = 0; i < conversationListModel.size(); i++) {
                if (conversationListModel.get(i).getConversationId() == previousId) {
                    conversationList.setSelectedIndex(i);
                    break;
                }
            }
        } else if (!conversationListModel.isEmpty()) {
            conversationList.setSelectedIndex(0);
        }

        updateActionState();
    }

    private void loadSelectedConversation() {
        ChatConversation selected = conversationList.getSelectedValue();
        chatTranscriptArea.setText("");

        if (selected == null) {
            updateActionState();
            return;
        }

        List<ChatMessage> messages = chatDAO.getMessagesForConversation(selected.getConversationId());
        for (ChatMessage message : messages) {
            chatTranscriptArea.append(String.format("[%s | %s]%n%s%n%n",
                    message.getSenderType(),
                    message.getSentAt() != null ? message.getSentAt() : "Pending",
                    message.getMessageText()));
        }
        chatTranscriptArea.setCaretPosition(chatTranscriptArea.getDocument().getLength());
        updateActionState();
    }

    private void sendMessage() {
        ChatConversation conversation = conversationList.getSelectedValue();
        DoctorSelection doctorSelection = (DoctorSelection) doctorComboBox.getSelectedItem();

        if (conversation == null) {
            JOptionPane.showMessageDialog(this, "Select a conversation first.", "No Conversation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (doctorSelection == null || doctorSelection.getDoctorId() == null) {
            JOptionPane.showMessageDialog(this, "Choose a doctor before sending a reply.", "Doctor Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String text = messageInputArea.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Type a message before sending.", "Empty Message", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if ("CLOSED".equalsIgnoreCase(conversation.getStatus())) {
            int result = JOptionPane.showConfirmDialog(this,
                    "This conversation is marked as CLOSED. Reopen it before sending?",
                    "Conversation Closed",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                chatDAO.updateConversationStatus(conversation.getConversationId(), "OPEN");
                loadConversations();
            } else {
                return;
            }
        }

        ChatMessage message = new ChatMessage(conversation.getConversationId(), "DOCTOR", doctorSelection.getDoctorId(), text);
        ChatMessage savedMessage = chatDAO.addMessage(message);
        if (savedMessage != null) {
            messageInputArea.setText("");
            loadConversations();
            loadSelectedConversation();
        } else {
            JOptionPane.showMessageDialog(this, "Unable to send message. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateConversationStatus(String status) {
        ChatConversation conversation = conversationList.getSelectedValue();
        if (conversation == null) {
            JOptionPane.showMessageDialog(this, "Select a conversation first.", "No Conversation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (status.equalsIgnoreCase(conversation.getStatus())) {
            return;
        }

        chatDAO.updateConversationStatus(conversation.getConversationId(), status);
        loadConversations();
        loadSelectedConversation();
    }

    private void refreshCurrentConversation() {
        loadConversations();
        loadSelectedConversation();
    }

    private void updateActionState() {
        ChatConversation selected = conversationList.getSelectedValue();
        boolean hasSelection = selected != null;
        sendButton.setEnabled(hasSelection);
        closeConversationButton.setEnabled(hasSelection && !"CLOSED".equalsIgnoreCase(selected.getStatus()));
        reopenConversationButton.setEnabled(hasSelection && "CLOSED".equalsIgnoreCase(selected.getStatus()));
    }

    private String getPatientName(int patientId) {
        if (!patientNameCache.containsKey(patientId)) {
            Patient patient = patientDAO.getPatientById(patientId);
            String name = patient != null ? patient.getFullName() : "Patient " + patientId;
            patientNameCache.put(patientId, name);
        }
        return patientNameCache.get(patientId);
    }

    private static class DoctorSelection {
        private final Doctor doctor;
        private final String label;

        DoctorSelection(Doctor doctor, String label) {
            this.doctor = doctor;
            this.label = label;
        }

        Integer getDoctorId() {
            return doctor != null ? doctor.getDoctorId() : null;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private class ConversationRenderer extends DefaultListCellRenderer {
        @Override
        public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof ChatConversation conversation) {
                String patientName = getPatientName(conversation.getPatientId());
                setText(String.format("#%d - %s (Doctor %d) [%s]",
                        conversation.getConversationId(),
                        patientName,
                        conversation.getDoctorId(),
                        conversation.getStatus()));
            }
            return this;
        }
    }
}

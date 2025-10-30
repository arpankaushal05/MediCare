package models;

import java.time.LocalDateTime;

public class ChatConversation {
    private int conversationId;
    private int patientId;
    private int doctorId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ChatConversation(int conversationId, int patientId, int doctorId, String status,
                             LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.conversationId = conversationId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public ChatConversation(int patientId, int doctorId) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.status = "OPEN";
    }

    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

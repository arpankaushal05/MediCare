package models;

import java.time.LocalDateTime;

public class ChatMessage {
    private int messageId;
    private int conversationId;
    private String senderType;
    private int senderId;
    private String messageText;
    private LocalDateTime sentAt;

    public ChatMessage(int messageId, int conversationId, String senderType, int senderId,
                       String messageText, LocalDateTime sentAt) {
        this.messageId = messageId;
        this.conversationId = conversationId;
        this.senderType = senderType;
        this.senderId = senderId;
        this.messageText = messageText;
        this.sentAt = sentAt;
    }

    public ChatMessage(int conversationId, String senderType, int senderId, String messageText) {
        this.conversationId = conversationId;
        this.senderType = senderType;
        this.senderId = senderId;
        this.messageText = messageText;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    public String getSenderType() {
        return senderType;
    }

    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}

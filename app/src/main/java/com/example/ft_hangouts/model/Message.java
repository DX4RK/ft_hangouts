package com.example.ft_hangouts.model;

public class Message {
    private int id;
    private int contactId;
    private String body;
    private long timestamp;
    private boolean isSent;

    public Message(int id, int contactId, String body, long timestamp, boolean isSent) {
        this.id = id;
        this.contactId = contactId;
        this.body = body;
        this.timestamp = timestamp;
        this.isSent = isSent;
    }

    public Message(int contactId, String body, long timestamp, boolean isSent) {
        this(-1, contactId, body, timestamp, isSent);
    }

    public int getId() { return id; }
    public int getContactId() { return contactId; }
    public String getBody() { return body; }
    public long getTimestamp() { return timestamp; }
    public boolean isSent() { return isSent; }
}
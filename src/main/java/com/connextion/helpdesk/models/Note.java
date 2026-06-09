package com.connextion.helpdesk.models;

import java.sql.Timestamp;

public class Note {
    private int id;
    private String description;
    private Timestamp noteTimestamp;
    private int issueId;
    private int supportUserId;
    
    // Helper field for UI display
    private String supportUserName;

    public Note() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Timestamp getNoteTimestamp() { return noteTimestamp; }
    public void setNoteTimestamp(Timestamp noteTimestamp) { this.noteTimestamp = noteTimestamp; }

    public int getIssueId() { return issueId; }
    public void setIssueId(int issueId) { this.issueId = issueId; }

    public int getSupportUserId() { return supportUserId; }
    public void setSupportUserId(int supportUserId) { this.supportUserId = supportUserId; }

    public String getSupportUserName() { return supportUserName; }
    public void setSupportUserName(String supportUserName) { this.supportUserName = supportUserName; }
}

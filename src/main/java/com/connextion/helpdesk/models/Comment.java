package com.connextion.helpdesk.models;

import java.sql.Timestamp;

public class Comment {
    private int id;
    private String description;
    private Timestamp commentTimestamp;
    private int issueId;
    private String userType;
    private int userId;
    
    // Helper field for UI display
    private String userName;

    public Comment() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Timestamp getCommentTimestamp() { return commentTimestamp; }
    public void setCommentTimestamp(Timestamp commentTimestamp) { this.commentTimestamp = commentTimestamp; }

    public int getIssueId() { return issueId; }
    public void setIssueId(int issueId) { this.issueId = issueId; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}

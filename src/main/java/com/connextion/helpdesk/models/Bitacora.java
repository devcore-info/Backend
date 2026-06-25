package com.connextion.helpdesk.models;

import java.sql.Timestamp;

public class Bitacora {
    private int id;
    private int issueId;
    private String changedBy;
    private String actionType;
    private String description;
    private Timestamp changeTimestamp;

    public Bitacora() {}

    public Bitacora(int issueId, String changedBy, String actionType, String description) {
        this.issueId = issueId;
        this.changedBy = changedBy;
        this.actionType = actionType;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIssueId() { return issueId; }
    public void setIssueId(int issueId) { this.issueId = issueId; }

    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Timestamp getChangeTimestamp() { return changeTimestamp; }
    public void setChangeTimestamp(Timestamp changeTimestamp) { this.changeTimestamp = changeTimestamp; }
}

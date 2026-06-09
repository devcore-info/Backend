package com.connextion.helpdesk.models;

import java.sql.Timestamp;

public class Issue {
    private int id;
    private String description;
    private String contactPhone;
    private String contactEmail;
    private String address;
    private String status;
    private String classification;
    private int clientId;
    private int serviceId;
    private Integer supportUserAssignedId;
    private String resolutionComment;
    private Timestamp registerTimestamp;

    // Helper fields for details
    private String clientName;
    private String serviceName;
    private String supportUserAssignedName;

    public Issue() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getClassification() { return classification; }
    public void setClassification(String classification) { this.classification = classification; }

    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }

    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }

    public Integer getSupportUserAssignedId() { return supportUserAssignedId; }
    public void setSupportUserAssignedId(Integer supportUserAssignedId) { this.supportUserAssignedId = supportUserAssignedId; }

    public String getResolutionComment() { return resolutionComment; }
    public void setResolutionComment(String resolutionComment) { this.resolutionComment = resolutionComment; }

    public Timestamp getRegisterTimestamp() { return registerTimestamp; }
    public void setRegisterTimestamp(Timestamp registerTimestamp) { this.registerTimestamp = registerTimestamp; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getSupportUserAssignedName() { return supportUserAssignedName; }
    public void setSupportUserAssignedName(String supportUserAssignedName) { this.supportUserAssignedName = supportUserAssignedName; }
}

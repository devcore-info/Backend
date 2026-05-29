package com.connextion.helpdesk.models;

import java.util.List;

public class SupportUser {
    private int id;
    private String name;
    private String firstSurname;
    private String secondSurname;
    private String email;
    private String password;
    private boolean isSupervisor;
    private List<Integer> services;

    // Default Constructor
    public SupportUser() {}

    // Getters and Setters (Encapsulation Requirement)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getFirstSurname() { return firstSurname; }
    public void setFirstSurname(String firstSurname) { this.firstSurname = firstSurname; }

    public String getSecondSurname() { return secondSurname; }
    public void setSecondSurname(String secondSurname) { this.secondSurname = secondSurname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean getIsSupervisor() { return isSupervisor; }
    public void setIsSupervisor(boolean isSupervisor) { this.isSupervisor = isSupervisor; }

    public List<Integer> getServices() { return services; }
    public void setServices(List<Integer> services) { this.services = services; }
}

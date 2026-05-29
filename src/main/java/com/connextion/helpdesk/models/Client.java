package com.connextion.helpdesk.models;

import java.util.List;

public class Client {
    private int id;
    private String name;
    private String firstSurname;
    private String secondSurname;
    private String email;
    private String password;
    private String address;
    private String phone;
    private String secondContact;
    private List<Integer> services;

    // Default Constructor
    public Client() {}

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

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getSecondContact() { return secondContact; }
    public void setSecondContact(String secondContact) { this.secondContact = secondContact; }

    public List<Integer> getServices() { return services; }
    public void setServices(List<Integer> services) { this.services = services; }
}

package com.connextion.helpdesk.services;

import com.connextion.helpdesk.models.Client;
import com.connextion.helpdesk.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.SQLException;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public boolean registerClient(Client client) throws SQLException, IllegalArgumentException {
        if (client == null || client.getName() == null || client.getFirstSurname() == null || 
            client.getSecondSurname() == null || client.getEmail() == null || 
            client.getPassword() == null || client.getServices() == null || client.getServices().isEmpty()) {
            throw new IllegalArgumentException("Missing required fields or services");
        }
        return clientRepository.register(client);
    }

    public Client loginClient(String email, String password) throws SQLException, IllegalArgumentException {
        if (email == null || password == null) {
            throw new IllegalArgumentException("Email and password are required");
        }
        return clientRepository.login(email, password);
    }
}

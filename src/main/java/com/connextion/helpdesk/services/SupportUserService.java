package com.connextion.helpdesk.services;

import com.connextion.helpdesk.models.SupportUser;
import com.connextion.helpdesk.repositories.SupportUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.SQLException;

@Service
public class SupportUserService {

    @Autowired
    private SupportUserRepository supportUserRepository;

    public boolean registerSupportUser(SupportUser user) throws SQLException, IllegalArgumentException {
        if (user == null || user.getName() == null || user.getFirstSurname() == null || 
            user.getSecondSurname() == null || user.getEmail() == null || 
            user.getPassword() == null || user.getServices() == null || user.getServices().isEmpty()) {
            throw new IllegalArgumentException("Missing required fields or services");
        }
        return supportUserRepository.register(user);
    }

    public SupportUser loginSupportUser(String email, String password) throws SQLException, IllegalArgumentException {
        if (email == null || password == null) {
            throw new IllegalArgumentException("Email and password are required");
        }
        return supportUserRepository.login(email, password);
    }
}

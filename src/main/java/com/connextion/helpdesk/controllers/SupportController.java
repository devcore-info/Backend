package com.connextion.helpdesk.controllers;

import com.connextion.helpdesk.models.SupportUser;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/api/v1/support")
public class SupportController {

    // CU7: Register Support User (Supporter or Supervisor)
    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerSupportUser(SupportUser user) {
        Map<String, String> response = new HashMap<>();

        // Business Rule Validations
        if (user == null || user.getName() == null || user.getFirstSurname() == null || 
            user.getSecondSurname() == null || user.getEmail() == null || 
            user.getPassword() == null || user.getServices() == null || user.getServices().isEmpty()) {
            
            response.put("error", "Missing required fields or services");
            return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
        }

        // TODO: Call Service layer to insert into Support_Users and Support_User_Services
        
        response.put("message", "Support user registered successfully");
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    // CU8: Support / Supervisor Login
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginSupportUser(Map<String, String> credentials) {
        Map<String, Object> response = new HashMap<>();
        
        if (credentials == null) {
            response.put("error", "Email and password are required");
            return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
        }
        
        String email = credentials.get("email");
        String password = credentials.get("password");

        if (email == null || password == null) {
            response.put("error", "Email and password are required");
            return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
        }

        // TODO: Database verification checking that the user has at least one assigned service
        // Mock validation for testing roles
        if ("supervisor@connection.com".equals(email) && "admin123".equals(password)) {
            response.put("message", "Authentication successful");
            response.put("email", email);
            response.put("isSupervisor", true); // Tells frontend to show Supervisor dashboard
            return Response.ok(response).build();
        }

        response.put("error", "Invalid email or password");
        return Response.status(Response.Status.UNAUTHORIZED).entity(response).build();
    }
}

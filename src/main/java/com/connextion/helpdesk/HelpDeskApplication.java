package com.connextion.helpdesk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class HelpDeskApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        createDatabaseIfNotExist();
        return application.sources(HelpDeskApplication.class);
    }

    public static void main(String[] args) {
        createDatabaseIfNotExist();
        SpringApplication.run(HelpDeskApplication.class, args);
    }

    private static void createDatabaseIfNotExist() {
        String dbHost = System.getenv("DB_HOST");
        if (dbHost == null || dbHost.trim().isEmpty()) {
            dbHost = "localhost";
        }
        String password = System.getenv("MSSQL_SA_PASSWORD");
        if (password == null || password.trim().isEmpty()) {
            password = "16200122Wqkj!";
        }

        System.out.println("[DB-INIT] Ensuring ConnextionDB exists on " + dbHost + "...");
        
        int maxRetries = 15;
        int delaySeconds = 10;
        
        for (int i = 1; i <= maxRetries; i++) {
            java.sql.Connection conn = null;
            java.sql.Statement stmt = null;
            try {
                com.microsoft.sqlserver.jdbc.SQLServerDataSource ds = new com.microsoft.sqlserver.jdbc.SQLServerDataSource();
                ds.setServerName(dbHost);
                ds.setPortNumber(1433);
                ds.setDatabaseName("master");
                ds.setUser("sa");
                ds.setPassword(password);
                ds.setTrustServerCertificate(true);
                ds.setLoginTimeout(10);
                
                conn = ds.getConnection();
                stmt = conn.createStatement();
                
                java.sql.ResultSet rs = stmt.executeQuery("SELECT database_id FROM sys.databases WHERE name = 'ConnextionDB'");
                boolean exists = rs.next();
                rs.close();
                
                if (!exists) {
                    System.out.println("[DB-INIT] ConnextionDB does not exist. Creating database...");
                    stmt.executeUpdate("CREATE DATABASE ConnextionDB");
                    System.out.println("[DB-INIT] ConnextionDB created successfully.");
                } else {
                    System.out.println("[DB-INIT] ConnextionDB already exists.");
                }
                break;
            } catch (Exception e) {
                System.err.println("[DB-INIT] Attempt " + i + " failed to connect/create database: " + e.getMessage());
                if (i < maxRetries) {
                    System.out.println("[DB-INIT] Waiting " + delaySeconds + " seconds before retrying...");
                    try {
                        Thread.sleep(delaySeconds * 1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else {
                    System.err.println("[DB-INIT] Max retries reached. Database might not be created.");
                    e.printStackTrace();
                }
            } finally {
                try { if (stmt != null) stmt.close(); } catch (Exception e) {}
                try { if (conn != null) conn.close(); } catch (Exception e) {}
            }
        }
    }
}

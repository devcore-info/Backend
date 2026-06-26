-- Insert company services
INSERT INTO Services (id, name) VALUES (1, 'Telefonía móvil');
INSERT INTO Services (id, name) VALUES (2, 'Cable');
INSERT INTO Services (id, name) VALUES (3, 'Internet');
INSERT INTO Services (id, name) VALUES (4, 'Telefonía fija');

-- Insert default Support Users (Supervisor and Technician)
INSERT INTO Support_Users (name, first_surname, second_surname, email, password, is_supervisor)
VALUES ('Supervisor', 'General', 'Connextion', 'supervisor@connextion.com', 'password123', 1);

INSERT INTO Support_Users (name, first_surname, second_surname, email, password, is_supervisor)
VALUES ('Soportista', 'Tecnico', 'Connextion', 'soporte@connextion.com', 'password123', 0);

-- Map Supervisor (ID 1) to all services
INSERT INTO Support_User_Services (support_user_id, service_id) VALUES (1, 1);
INSERT INTO Support_User_Services (support_user_id, service_id) VALUES (1, 2);
INSERT INTO Support_User_Services (support_user_id, service_id) VALUES (1, 3);
INSERT INTO Support_User_Services (support_user_id, service_id) VALUES (1, 4);

-- Map Soportista (ID 2) to services 1, 2, 3
INSERT INTO Support_User_Services (support_user_id, service_id) VALUES (2, 1);
INSERT INTO Support_User_Services (support_user_id, service_id) VALUES (2, 2);
INSERT INTO Support_User_Services (support_user_id, service_id) VALUES (2, 3);

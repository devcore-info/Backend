-- Insert company services
SET IDENTITY_INSERT Services ON;

INSERT INTO Services (id, name) VALUES (1, 'Telefonía móvil');
INSERT INTO Services (id, name) VALUES (2, 'Cable');
INSERT INTO Services (id, name) VALUES (3, 'Internet');
INSERT INTO Services (id, name) VALUES (4, 'Telefonía fija');

SET IDENTITY_INSERT Services OFF;

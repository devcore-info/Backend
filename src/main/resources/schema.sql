-- Drop foreign keys and tables if they exist to allow clean initialization
IF OBJECT_ID('dbo.Notes', 'U') IS NOT NULL DROP TABLE dbo.Notes;
IF OBJECT_ID('dbo.Comments', 'U') IS NOT NULL DROP TABLE dbo.Comments;
IF OBJECT_ID('dbo.Issues', 'U') IS NOT NULL DROP TABLE dbo.Issues;
IF OBJECT_ID('dbo.Client_Services', 'U') IS NOT NULL DROP TABLE dbo.Client_Services;
IF OBJECT_ID('dbo.Support_User_Services', 'U') IS NOT NULL DROP TABLE dbo.Support_User_Services;
IF OBJECT_ID('dbo.Clients', 'U') IS NOT NULL DROP TABLE dbo.Clients;
IF OBJECT_ID('dbo.Support_Users', 'U') IS NOT NULL DROP TABLE dbo.Support_Users;
IF OBJECT_ID('dbo.Services', 'U') IS NOT NULL DROP TABLE dbo.Services;

-- Create Services table
CREATE TABLE Services (
    id INT PRIMARY KEY, -- We insert static IDs for Services (1, 2, 3, 4)
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Create Clients table
CREATE TABLE Clients (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    first_surname VARCHAR(100) NOT NULL,
    second_surname VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    address VARCHAR(255) NULL,
    phone VARCHAR(50) NULL,
    second_contact VARCHAR(50) NULL
);

-- Create Client_Services junction table
CREATE TABLE Client_Services (
    client_id INT NOT NULL,
    service_id INT NOT NULL,
    PRIMARY KEY (client_id, service_id),
    FOREIGN KEY (client_id) REFERENCES Clients(id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES Services(id) ON DELETE CASCADE
);

-- Create Support_Users table
CREATE TABLE Support_Users (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    first_surname VARCHAR(100) NOT NULL,
    second_surname VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_supervisor BIT NOT NULL DEFAULT 0
);

-- Create Support_User_Services junction table
CREATE TABLE Support_User_Services (
    support_user_id INT NOT NULL,
    service_id INT NOT NULL,
    PRIMARY KEY (support_user_id, service_id),
    FOREIGN KEY (support_user_id) REFERENCES Support_Users(id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES Services(id) ON DELETE CASCADE
);

-- Create Issues table (CU4, CU5, CU10)
CREATE TABLE Issues (
    id INT IDENTITY(1,1) PRIMARY KEY,
    description VARCHAR(MAX) NOT NULL,
    contact_phone VARCHAR(50) NULL,
    contact_email VARCHAR(150) NULL,
    address VARCHAR(255) NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'Ingresado', -- 'Ingresado', 'Asignado', 'En Progreso', 'Resuelto'
    classification VARCHAR(50) NOT NULL DEFAULT 'Media', -- 'Baja', 'Media', 'Alta'
    client_id INT NOT NULL,
    service_id INT NOT NULL,
    support_user_assigned_id INT NULL,
    resolution_comment VARCHAR(MAX) NULL,
    register_timestamp DATETIME NOT NULL DEFAULT GETDATE(),
    FOREIGN KEY (client_id) REFERENCES Clients(id),
    FOREIGN KEY (service_id) REFERENCES Services(id),
    FOREIGN KEY (support_user_assigned_id) REFERENCES Support_Users(id)
);

-- Create Comments table (CU6, CU14)
CREATE TABLE Comments (
    id INT IDENTITY(1,1) PRIMARY KEY,
    description VARCHAR(MAX) NOT NULL,
    comment_timestamp DATETIME NOT NULL DEFAULT GETDATE(),
    issue_id INT NOT NULL,
    user_type VARCHAR(50) NOT NULL, -- 'CLIENT' or 'SUPPORT'
    user_id INT NOT NULL, -- ID of the Client or SupportUser
    FOREIGN KEY (issue_id) REFERENCES Issues(id) ON DELETE CASCADE
);

-- Create Notes table (CU13)
CREATE TABLE Notes (
    id INT IDENTITY(1,1) PRIMARY KEY,
    description VARCHAR(MAX) NOT NULL,
    note_timestamp DATETIME NOT NULL DEFAULT GETDATE(),
    issue_id INT NOT NULL,
    support_user_id INT NOT NULL,
    FOREIGN KEY (issue_id) REFERENCES Issues(id) ON DELETE CASCADE,
    FOREIGN KEY (support_user_id) REFERENCES Support_Users(id) ON DELETE CASCADE
);

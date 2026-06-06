-- Drop foreign keys and tables if they exist to allow clean initialization
IF OBJECT_ID('dbo.Client_Services', 'U') IS NOT NULL DROP TABLE dbo.Client_Services;
IF OBJECT_ID('dbo.Support_User_Services', 'U') IS NOT NULL DROP TABLE dbo.Support_User_Services;
IF OBJECT_ID('dbo.Clients', 'U') IS NOT NULL DROP TABLE dbo.Clients;
IF OBJECT_ID('dbo.Support_Users', 'U') IS NOT NULL DROP TABLE dbo.Support_Users;
IF OBJECT_ID('dbo.Services', 'U') IS NOT NULL DROP TABLE dbo.Services;

-- Create Services table
CREATE TABLE Services (
    id INT IDENTITY(1,1) PRIMARY KEY,
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

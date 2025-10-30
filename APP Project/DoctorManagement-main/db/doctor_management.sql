-- Database schema and seed data for Doctor Management application
-- Run these commands in your MySQL instance.

DROP DATABASE IF EXISTS doctor_management;
CREATE DATABASE doctor_management;
USE doctor_management;

CREATE TABLE doctors (
    doctor_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    phone VARCHAR(25) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE patients (
    patient_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL,
    phone VARCHAR(25) NOT NULL,
    gender VARCHAR(50),
    blood_group VARCHAR(10),
    height_meters DECIMAL(4,2),
    allergies VARCHAR(255),
    disease VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE appointments (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    doctor_id INT NOT NULL,
    patient_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_appointments_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (doctor_id) ON DELETE CASCADE,
    CONSTRAINT fk_appointments_patient FOREIGN KEY (patient_id) REFERENCES patients (patient_id) ON DELETE CASCADE
);

CREATE TABLE patient_history (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    visit_date DATE NOT NULL,
    diagnosis TEXT,
    treatment TEXT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_patient_history_patient FOREIGN KEY (patient_id) REFERENCES patients (patient_id) ON DELETE CASCADE,
    CONSTRAINT fk_patient_history_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (doctor_id) ON DELETE CASCADE
);

CREATE TABLE chat_conversations (
    conversation_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    status ENUM('OPEN', 'CLOSED') DEFAULT 'OPEN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_chat_patient FOREIGN KEY (patient_id) REFERENCES patients (patient_id) ON DELETE CASCADE,
    CONSTRAINT fk_chat_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (doctor_id) ON DELETE CASCADE
);

CREATE TABLE chat_messages (
    message_id INT AUTO_INCREMENT PRIMARY KEY,
    conversation_id INT NOT NULL,
    sender_type ENUM('PATIENT', 'DOCTOR') NOT NULL,
    sender_id INT NOT NULL,
    message_text TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_chat_conversation FOREIGN KEY (conversation_id) REFERENCES chat_conversations (conversation_id) ON DELETE CASCADE
);

CREATE TABLE admin_users (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Seed data for quick testing
INSERT INTO doctors (username, password, name, specialization, email, phone) VALUES
('aisha.k', 'aisha@123', 'Dr. Aisha Kapoor', 'Cardiologist', 'aisha.kapoor@example.com', '+91-9876543210'),
('rohan.m', 'rohan@123', 'Dr. Rohan Mehta', 'Dermatologist', 'rohan.mehta@example.com', '+91-9123456780'),
('neha.s', 'neha@123', 'Dr. Neha Singh', 'Pediatrician', 'neha.singh@example.com', '+91-9988776655'),
('sameer.j', 'sameer@123', 'Dr. Sameer Joshi', 'Neurologist', 'sameer.joshi@example.com', '+91-9011223344'),
('kavita.r', 'kavita@123', 'Dr. Kavita Rao', 'Orthopedic Surgeon', 'kavita.rao@example.com', '+91-9345612780'),
('farhan.a', 'farhan@123', 'Dr. Farhan Ali', 'Psychiatrist', 'farhan.ali@example.com', '+91-9487654321');

INSERT INTO patients (username, password, full_name, email, phone, gender, blood_group, height_meters, allergies, disease) VALUES
('rahul.p', 'rahul@123', 'Rahul Patel', 'rahul.patel@example.com', '+91-9001002003', 'Male', 'B+', 1.75, 'None', 'Hypertension'),
('priya.s', 'priya@123', 'Priya Sharma', 'priya.sharma@example.com', '+91-9556677889', 'Female', 'O+', 1.62, 'Pollen', 'Asthma');

INSERT INTO appointments (doctor_id, patient_id, appointment_date, appointment_time, notes) VALUES
(1, 1, '2024-10-01', '10:00:00', 'Routine heart check-up'),
(2, 2, '2024-10-05', '15:30:00', 'Skin allergy consultation');

INSERT INTO patient_history (patient_id, doctor_id, visit_date, diagnosis, treatment, notes) VALUES
(1, 1, '2024-07-15', 'Hypertension', 'Lifestyle changes, medication prescribed', 'Patient advised to follow up in three months'),
(2, 2, '2024-08-20', 'Contact dermatitis', 'Topical ointment prescribed', 'Follow-up scheduled after two weeks');

INSERT INTO chat_conversations (patient_id, doctor_id, status) VALUES
(1, 1, 'OPEN'),
(2, 2, 'OPEN');

INSERT INTO chat_messages (conversation_id, sender_type, sender_id, message_text) VALUES
(1, 'PATIENT', 1, 'Hello doctor, I have a question about my medication.'),
(1, 'DOCTOR', 1, 'Sure Rahul, please let me know your concern.'),
(2, 'PATIENT', 2, 'My rash is still itching, what should I do?'),
(2, 'DOCTOR', 2, 'Continue the ointment for another week and keep the area dry.');

INSERT INTO admin_users (username, password, full_name) VALUES
('admin', 'admin@123', 'System Administrator');

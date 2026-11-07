# Student Management System
This was my first project!

A desktop application built with **Java Swing** and **MySQL** (JDBC) for managing student records, with separate Admin and Student roles.

## Features

**Admin**
- Add, update and delete students
- Deleting a student also removes their login account
- View all students, sorted by roll number

**Student**
- Sign up and log in with roll number and password
- View their own record only (read-only)

**General**
- Input validation: roll numbers and marks accept digits only
- Duplicate roll numbers are rejected
- Consistent UI with working Back and Close buttons on every window

## Tech Stack
Java (JDK 8+), Java Swing, MySQL, MySQL Connector/J 8.1.0

## Setup

### 1. Database
```sql
CREATE DATABASE sms_db;
USE sms_db;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) UNIQUE,
    password VARCHAR(100),
    role VARCHAR(20)
);

CREATE TABLE students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    roll_no VARCHAR(50) UNIQUE,
    name VARCHAR(200),
    marks INT
);

CREATE USER 'sms_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON sms_db.* TO 'sms_user'@'localhost';
FLUSH PRIVILEGES;

INSERT INTO users (username, password, role)
VALUES ('admin', 'choose_a_password', 'admin');
```
Use the same `your_password` in the database connection code.

### 2. Compile
```
javac -cp "mysql-connector-j-8.1.0.jar:." *.java
```

### 3. Run
```
java -cp "mysql-connector-j-8.1.0.jar:." sms
```
On Windows, use `;` instead of `:` in the classpath.

## Usage
- **Admin:** log in with the admin account you created above.
- **Student:** sign up first, then log in with roll number and password.

## Project Files
`sms.java`, `WelcomeFrame.java`, `LoginFrame.java`, `SignUpFrame.java`, `ManagementFrame.java`, `AddFrame.java`, `UpdateFrame.java`, `DeleteFrame.java`, `ViewFrame.java`

## Author
Tanmay Shinde, MCA, VJTI

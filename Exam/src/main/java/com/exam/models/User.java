
package com.exam.models;

public class User {
    private int id;
    private String firstName, lastName, studentId, email, password, department, role;

    // Constructor for creating new user (registration)
    public User(String firstName, String lastName, String studentId,
                String email, String password, String department, String role) {
        this.firstName  = firstName;
        this.lastName   = lastName;
        this.studentId  = studentId;
        this.email      = email;
        this.password   = password;
        this.department = department;
        this.role       = role;
    }

    // Constructor for loading from database (includes id)
    public User(int id, String firstName, String lastName, String studentId,
                String email, String password, String department, String role) {
        this(firstName, lastName, studentId, email, password, department, role);
        this.id = id;
    }

    // Getters
    public int    getId()         { return id; }
    public String getFirstName()  { return firstName; }
    public String getLastName()   { return lastName; }
    public String getStudentId()  { return studentId; }
    public String getEmail()      { return email; }
    public String getPassword()   { return password; }
    public String getDepartment() { return department; }
    public String getRole()       { return role; }

    // Setters for mutable fields
    public void setPassword(String password)   { this.password   = password; }
    public void setDepartment(String dept)     { this.department = dept; }
    public void setRole(String role)           { this.role       = role; }
}
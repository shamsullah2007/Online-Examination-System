package com.exam.dao;

import com.exam.database.DBConnection;
import com.exam.models.User;
import com.exam.utils.PasswordUtils;

import java.sql.*;

public class UserDAO {

    // Registers a new student — hashes password before saving
    public boolean register(User user) {
        String sql = "INSERT INTO users (first_name, last_name, student_id, " +
                "email, password, department, role) VALUES (?,?,?,?,?,?,?)";

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {

            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getStudentId());
            ps.setString(4, user.getEmail());
            ps.setString(5, PasswordUtils.hash(user.getPassword())); // hash before saving
            ps.setString(6, user.getDepartment());
            ps.setString(7, user.getRole());

            ps.executeUpdate();
            return true; // registration successful

        } catch (SQLIntegrityConstraintViolationException e) {
            // Duplicate email or student_id
            System.err.println("Duplicate entry: " + e.getMessage());
            return false;

        } catch (SQLException e) {
            System.err.println("Register error: " + e.getMessage());
            return false;
        }
    }

    // Checks login — returns User object if credentials match, null otherwise
    public User login(String email, String rawPassword) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");

                    // Verify entered password against stored BCrypt hash
                    if (PasswordUtils.verify(rawPassword, storedHash)) {
                        return new User(
                                rs.getInt("id"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getString("student_id"),
                                rs.getString("email"),
                                storedHash,
                                rs.getString("department"),
                                rs.getString("role")
                        );
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
        }

        return null; // email not found or wrong password
    }

    // Returns true if email is already registered
    public boolean emailExists(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // true if any row found
            }

        } catch (SQLException e) {
            System.err.println("Email check error: " + e.getMessage());
        }

        return false;
    }
}
package com.exam.dao;

import com.exam.database.DBConnection;
import com.exam.models.Exam;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExamDAO {

    // Returns only active exams — shown on student dashboard
    public List<Exam> getActiveExams() {
        List<Exam> list = new ArrayList<>();
        String sql = "SELECT * FROM exams WHERE is_active = 1";

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapExam(rs));
            }

        } catch (SQLException e) {
            System.err.println("Get active exams error: " + e.getMessage());
        }

        return list;
    }

    // Returns all exams — shown on admin screen
    public List<Exam> getAllExams() {
        List<Exam> list = new ArrayList<>();
        String sql = "SELECT * FROM exams ORDER BY scheduled_at DESC";

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapExam(rs));
            }

        } catch (SQLException e) {
            System.err.println("Get all exams error: " + e.getMessage());
        }

        return list;
    }

    // Inserts a new exam created by admin
    public boolean createExam(Exam exam) {
        String sql = "INSERT INTO exams (title, description, duration_min, total_marks, " +
                "pass_marks, scheduled_at, created_by, is_active) " +
                "VALUES (?,?,?,?,?,?,?,?)";

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {

            ps.setString(1, exam.getTitle());
            ps.setString(2, exam.getDescription());
            ps.setInt   (3, exam.getDurationMinutes());
            ps.setInt   (4, exam.getTotalMarks());
            ps.setInt   (5, exam.getPassMarks());
            ps.setString(6, exam.getScheduledAt());
            ps.setInt   (7, exam.getCreatedBy());
            ps.setInt   (8, exam.isActive() ? 1 : 0);

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Create exam error: " + e.getMessage());
            return false;
        }
    }

    // Private helper — builds an Exam object from a ResultSet row
    private Exam mapExam(ResultSet rs) throws SQLException {
        return new Exam(
                rs.getInt   ("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getInt   ("duration_min"),
                rs.getInt   ("total_marks"),
                rs.getInt   ("pass_marks"),
                rs.getString("scheduled_at"),
                rs.getInt   ("created_by"),
                rs.getInt   ("is_active") == 1
        );
    }
}
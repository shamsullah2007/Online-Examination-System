package com.exam.dao;

import com.exam.database.DBConnection;
import com.exam.models.Attempt;
import com.exam.models.Option;
import com.exam.models.Question;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttemptDAO {

    // Creates a new attempt record when student starts an exam
    // Returns the generated attempt id (needed to save answers later)
    public int startAttempt(int userId, int examId) {
        String sql = "INSERT INTO attempts (user_id, exam_id) VALUES (?, ?)";

        try (PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, userId);
            ps.setInt(2, examId);
            ps.executeUpdate();

            // Get the auto-generated attempt id
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("Start attempt error: " + e.getMessage());
        }

        return -1; // failed
    }

    // Saves all answers and calculates score in one database transaction
    public int submitAttempt(int attemptId, List<Question> questions, int passMarks) {
        Connection conn = DBConnection.getConnection();
        int totalScore = 0;

        String insertAnswer = "INSERT INTO answers " +
                "(attempt_id, question_id, selected_opt, is_correct) " +
                "VALUES (?, ?, ?, ?)";

        String updateAttempt = "UPDATE attempts SET score=?, is_passed=?, " +
                "submitted_at=NOW() WHERE id=?";

        try {
            conn.setAutoCommit(false); // start transaction

            try (PreparedStatement ansPs = conn.prepareStatement(insertAnswer)) {

                for (Question q : questions) {

                    // Find if the selected option is correct
                    boolean correct = false;
                    if (q.isAnswered()) {
                        for (Option opt : q.getOptions()) {
                            if (opt.getId() == q.getSelectedOptionId() && opt.isCorrect()) {
                                correct = true;
                                totalScore += q.getMarks();
                                break;
                            }
                        }
                    }

                    // Add this answer to the batch
                    ansPs.setInt(1, attemptId);
                    ansPs.setInt(2, q.getId());
                    ansPs.setInt(3, q.getSelectedOptionId()); // -1 if unanswered
                    ansPs.setInt(4, correct ? 1 : 0);
                    ansPs.addBatch();
                }

                ansPs.executeBatch(); // insert all answers at once
            }

            // Update the attempt with final score and pass/fail status
            try (PreparedStatement upPs = conn.prepareStatement(updateAttempt)) {
                upPs.setInt(1, totalScore);
                upPs.setInt(2, totalScore >= passMarks ? 1 : 0);
                upPs.setInt(3, attemptId);
                upPs.executeUpdate();
            }

            conn.commit(); // save everything
            conn.setAutoCommit(true);

        } catch (SQLException e) {
            System.err.println("Submit attempt error: " + e.getMessage());
            try {
                conn.rollback(); // undo everything if anything failed
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                System.err.println("Rollback error: " + ex.getMessage());
            }
        }

        return totalScore;
    }

    // Returns all past attempts for a student — used on dashboard
    public List<Attempt> getAttemptsByUser(int userId) {
        List<Attempt> list = new ArrayList<>();
        String sql = "SELECT * FROM attempts WHERE user_id = ? ORDER BY started_at DESC";

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Attempt(
                            rs.getInt   ("id"),
                            rs.getInt   ("user_id"),
                            rs.getInt   ("exam_id"),
                            rs.getString("started_at"),
                            rs.getString("submitted_at"),
                            rs.getInt   ("score"),
                            rs.getInt   ("is_passed") == 1
                    ));
                }
            }

        } catch (SQLException e) {
            System.err.println("Get attempts error: " + e.getMessage());
        }

        return list;
    }
}
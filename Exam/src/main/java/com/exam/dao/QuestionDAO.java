package com.exam.dao;

import com.exam.database.DBConnection;
import com.exam.models.Option;
import com.exam.models.Question;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO {

    // Loads all questions for an exam, with their options attached
    public List<Question> getQuestionsWithOptions(int examId) {
        List<Question> questions = new ArrayList<>();
        String qSql = "SELECT * FROM questions WHERE exam_id = ?";

        try (PreparedStatement qPs = DBConnection.getConnection().prepareStatement(qSql)) {

            qPs.setInt(1, examId);

            try (ResultSet qRs = qPs.executeQuery()) {

                while (qRs.next()) {
                    // Build the Question object from this row
                    Question q = new Question(
                            qRs.getInt   ("id"),
                            qRs.getInt   ("exam_id"),
                            qRs.getString("text"),
                            qRs.getInt   ("marks"),
                            qRs.getString("topic")
                    );

                    // Load all options for this question
                    q.setOptions(loadOptions(q.getId()));
                    questions.add(q);
                }
            }

        } catch (SQLException e) {
            System.err.println("Get questions error: " + e.getMessage());
        }

        return questions;
    }

    // Loads all answer options for a single question
    private List<Option> loadOptions(int questionId) throws SQLException {
        List<Option> options = new ArrayList<>();
        String oSql = "SELECT * FROM options WHERE question_id = ?";

        try (PreparedStatement oPs = DBConnection.getConnection().prepareStatement(oSql)) {

            oPs.setInt(1, questionId);

            try (ResultSet oRs = oPs.executeQuery()) {
                while (oRs.next()) {
                    options.add(new Option(
                            oRs.getInt   ("id"),
                            oRs.getInt   ("question_id"),
                            oRs.getString("option_text"),
                            oRs.getInt   ("is_correct") == 1
                    ));
                }
            }
        }

        return options;
    }
}
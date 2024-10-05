package dataBase.questionsDAOs;

import dataBase.ConnectionPool;
import objects.questions.GradedQuestion;
import objects.questions.Question;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GradedQuestionDAO implements QuestionDAOType {
    private ConnectionPool pool;
    private Connection conn;

    public GradedQuestionDAO(ConnectionPool pool) throws SQLException {
        this.pool = pool;
        this.conn = pool.getConnection();
    }

    @Override
    public int addQuestion(Question question, int quizId) {
        if (question instanceof GradedQuestion) {
            GradedQuestion gradedQuestion = (GradedQuestion) question;
            String query = "INSERT INTO QUESTIONS (quiz_id, question_text, question_type, timer) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, quizId);
                statement.setString(2, gradedQuestion.getQuestion());
                statement.setString(3, gradedQuestion.getQuestionType());
                statement.setInt(4, gradedQuestion.getTimer());

                int affectedRows = statement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating question failed, no rows affected.");
                }

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int questionId = generatedKeys.getInt(1);
                        gradedQuestion.setQuestionId(questionId);
                        return questionId;
                    } else {
                        throw new SQLException("Creating question failed, no ID obtained.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return -1; //failure
            }
        } else {
            throw new IllegalArgumentException("Question type not supported");
        }
    }

    @Override
    public Question getQuestionByQuestionId(int questionId) {
        String query = "SELECT question_text, question_type, timer FROM QUESTIONS WHERE question_id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, questionId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String questionText = resultSet.getString("question_text");
                    String questionType = resultSet.getString("question_type");
                    int timer = resultSet.getInt("timer");

                    GradedQuestion gradedQuestion = new GradedQuestion(questionText);
                    gradedQuestion.setQuestionId(questionId);
                    gradedQuestion.setTimer(timer);

                    return gradedQuestion;
                } else {
                    return null; // No question found with the given ID
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // Return null in case of an error
        }
    }
}

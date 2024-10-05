package dataBase.questionsDAOs;

import dataBase.ConnectionPool;
import objects.questions.*;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

class PictureResponseDAO implements QuestionDAOType {
    private final ConnectionPool pool;

    public PictureResponseDAO(ConnectionPool pool) {this.pool = pool;}

    @Override
    public int addQuestion(Question question, int quizId) {
        int questionId = -1;
        String insertQuestionQuery = "INSERT INTO QUESTIONS (quiz_id, question_text, question_type, timer) VALUES (?, ?, ?, ?)";
        String insertOptionsQuery = "INSERT INTO PICTURE_RESPONSE (question_id, question_URL, response_answer) VALUES (?, ?, ?)";
        Connection connection = pool.getConnection();

        try (
             PreparedStatement insertQuestionStatement = connection.prepareStatement(insertQuestionQuery, PreparedStatement.RETURN_GENERATED_KEYS);
             PreparedStatement insertOptionsStatement = connection.prepareStatement(insertOptionsQuery)) {

            // Insert into QUESTIONS table
            insertQuestionStatement.setInt(1, quizId);
            insertQuestionStatement.setString(2, question.getQuestion());
            insertQuestionStatement.setString(3, question.getQuestionType());
            insertQuestionStatement.setInt(4, question.getTimer());
            insertQuestionStatement.executeUpdate();

            // Get the generated question_id for the newly inserted Question
            try (ResultSet generatedKeys = insertQuestionStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    questionId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Inserting Question failed, no ID obtained.");
                }
            }

            // Insert into PICTURE_RESPONSE table
            insertOptionsStatement.setInt(1, questionId);
            insertOptionsStatement.setString(2, question.getOptions()[0]);
            insertOptionsStatement.setString(3, question.getCorrectAnswers()[0]);
            insertOptionsStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            pool.releaseConnection(connection);
        }
        return questionId;
    }

    @Override
    public Question getQuestionByQuestionId(int questionId) {
        String selectQuestionQuery = "SELECT * FROM QUESTIONS WHERE question_id = ?";
        String selectOptionsQuery = "SELECT * FROM PICTURE_RESPONSE WHERE question_id = ?";
        Connection connection = pool.getConnection();

        try (
             PreparedStatement selectQuestionStatement = connection.prepareStatement(selectQuestionQuery);
             PreparedStatement selectOptionsStatement = connection.prepareStatement(selectOptionsQuery)) {

            // Get the question details from the QUESTIONS table
            selectQuestionStatement.setInt(1, questionId);
            try (ResultSet questionResultSet = selectQuestionStatement.executeQuery()) {
                if (questionResultSet.next()) {
                    String questionText = questionResultSet.getString("question_text");
                    int timer = questionResultSet.getInt("timer");

                    // Get the options from the FILL_IN_THE_BLANK table
                    selectOptionsStatement.setInt(1, questionId);
                    try (ResultSet optionsResultSet = selectOptionsStatement.executeQuery()) {
                        if (optionsResultSet.next()) {
                            String URL = optionsResultSet.getString("question_URL");
                            String answer = optionsResultSet.getString("response_answer");

                            Question newQuestion = new PictureResponse(questionText, URL, answer);
                            newQuestion.setTimer(timer);
                            newQuestion.setQuestionId(questionId);

                            return newQuestion;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            pool.releaseConnection(connection);
        }
        return null;
    }
}
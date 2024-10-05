package dataBase.questionsDAOs;

import dataBase.ConnectionPool;
import objects.questions.FillInTheBlank;
import objects.questions.PictureResponse;
import objects.questions.Question;
import objects.questions.QuestionResponse;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class QuestionResponseDAO implements QuestionDAOType {
    private final ConnectionPool pool;

    public QuestionResponseDAO(ConnectionPool pool) {this.pool = pool;}

    @Override
    public int addQuestion(Question question, int quizId) {
        int questionId = -1;
        String insertQuestionQuery = "INSERT INTO QUESTIONS (quiz_id, question_text, question_type, timer) VALUES (?, ?, ?, ?);";
        String insertResponseQuery = "INSERT INTO QUESTION_RESPONSE (question_id, response_answer) VALUES (?, ?);";
        Connection connection = pool.getConnection();

        try (
            PreparedStatement insertQuestionStatement = connection.prepareStatement(insertQuestionQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            PreparedStatement insertResponseStmt = connection.prepareStatement(insertResponseQuery)) {
            insertQuestionStatement.setInt(1, quizId);
            insertQuestionStatement.setInt(4, question.getTimer());
            insertQuestionStatement.setString(2, question.getQuestion());
            insertQuestionStatement.setString(3, question.getQuestionType());

            // Execute the insert statement for Question
            int affectedRows = insertQuestionStatement.executeUpdate();
            if (affectedRows == 0) { throw new SQLException("Inserting Question to QUESTIONS failed"); }

            // Get the generated question_id for the newly inserted Question
            try (ResultSet generatedKeys = insertQuestionStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    questionId = generatedKeys.getInt(1);

                    // inserts the into QUESTION_RESPONSE
                    insertResponseStmt.setInt(1, questionId);
                    insertResponseStmt.setString(2, question.getCorrectAnswers()[0]);
                    insertResponseStmt.executeUpdate();
                } else { throw new SQLException("Inserting Question to QUESTION_RESPONSE failed"); }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        finally{
            pool.releaseConnection(connection);
        }
        return questionId;
    }

    @Override
    public Question getQuestionByQuestionId(int questionId) {
        String selectQuery = "SELECT * FROM QUESTIONS " +
                "JOIN QUESTION_RESPONSE " +
                "ON QUESTIONS.question_id = QUESTION_RESPONSE.question_id " +
                "WHERE QUESTIONS.question_id = ?;";
        Connection connection = pool.getConnection();

        try (
             PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            selectStatement.setInt(1, questionId);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    String questionText = resultSet.getString("question_text");
                    String responseAnswer = resultSet.getString("response_answer");
                    int timer = resultSet.getInt("timer");
                    Question newQuestion = null;
                    switch (resultSet.getString("question_type")) {
                        case "QuestionResponse":
                            newQuestion = new QuestionResponse(questionText, responseAnswer);
                            break;
                        case "FillInTheBlank":
                            newQuestion = new FillInTheBlank(questionText, responseAnswer);
                            break;
                    }
                    newQuestion.setQuestionId(questionId);
                    newQuestion.setTimer(timer);
                    return newQuestion;
                }
            }
        } catch (SQLException e) {e.printStackTrace(); }
        finally{
            pool.releaseConnection(connection);
        }
        return null;
    }
}

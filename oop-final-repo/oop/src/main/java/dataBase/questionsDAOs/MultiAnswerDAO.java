package dataBase.questionsDAOs;

import dataBase.ConnectionPool;
import objects.questions.MultiAnswer;
import objects.questions.Question;
import org.apache.commons.dbcp2.BasicDataSource;

import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class MultiAnswerDAO implements QuestionDAOType {
    private final ConnectionPool pool;

    public MultiAnswerDAO(ConnectionPool pool) {this.pool = pool;
    }

    @Override
    public int addQuestion(Question question, int quizId) {
        int questionId = -1;
        String insertQuestionQuery = "INSERT INTO QUESTIONS (quiz_id, question_text, question_type, timer) VALUES (?, ?, ?, ?)";
        String insertMultiAnswerQuery = "INSERT INTO MULTI_ANSWER (question_id, is_ordered, num_fields) VALUES (?, ?, ?)";
        String insertOptionQuery = "INSERT INTO MULTI_ANSWER_ANSWERS (question_id, answer_text, answer_order) VALUES (?, ?, ?)";
        Connection connection = pool.getConnection();
        try (
             PreparedStatement insertQuestionStatement = connection.prepareStatement(insertQuestionQuery, PreparedStatement.RETURN_GENERATED_KEYS);
             PreparedStatement insertMULTI_ANSWERStmt = connection.prepareStatement(insertMultiAnswerQuery);
             PreparedStatement insertOptionStmt = connection.prepareStatement(insertOptionQuery)) {

            // Insert the question into the QUESTIONS table
            insertQuestionStatement.setInt(1, quizId);
            insertQuestionStatement.setString(2, question.getQuestion());
            insertQuestionStatement.setString(3, question.getQuestionType());
            insertQuestionStatement.setInt(4, question.getTimer());

            int affectedRows = insertQuestionStatement.executeUpdate();
            if (affectedRows == 0) { throw new SQLException("Inserting Question to QUESTIONS failed");}

            // Get the generated question_id for the newly inserted Question
            try (ResultSet generatedKeys = insertQuestionStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    questionId = generatedKeys.getInt(1);

                    // Insert the MULTI_ANSWER details into the MULTI_ANSWER table
                    insertMULTI_ANSWERStmt.setInt(1, questionId);
                    insertMULTI_ANSWERStmt.setBoolean(2, question.isOrdered());
                    insertMULTI_ANSWERStmt.setInt(3, question.getNumFields());
                    insertMULTI_ANSWERStmt.executeUpdate();
                } else { throw new SQLException("Inserting Question to MULTI_ANSWER failed");}
            }

            // Insert the options into the MULTI_ANSWER_ANSWERS table
            String[] answers = question.getCorrectAnswers();
            for (int i = 0; i < answers.length; i++) {
                insertOptionStmt.setInt(1, questionId);
                insertOptionStmt.setString(2, answers[i]);
                insertOptionStmt.setInt(3, i);
                insertOptionStmt.executeUpdate();
            }
        }  catch (SQLException e) {
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
        String selectMultiAnswerQuery = "SELECT * FROM MULTI_ANSWER WHERE question_id = ?";
        String selectAnswersQuery = "SELECT * FROM MULTI_ANSWER_ANSWERS WHERE question_id = ? ORDER BY answer_order";
        Connection connection = pool.getConnection();

        try (
             PreparedStatement selectQuestionStatement = connection.prepareStatement(selectQuestionQuery);
             PreparedStatement selectMultiAnswerStatement = connection.prepareStatement(selectMultiAnswerQuery);
             PreparedStatement selectAnswersStatement = connection.prepareStatement(selectAnswersQuery)) {

            selectQuestionStatement.setInt(1, questionId);
            try (ResultSet questionResultSet = selectQuestionStatement.executeQuery()) {
                if (questionResultSet.next()) {
                    String questionText = questionResultSet.getString("question_text");
                    int timer = questionResultSet.getInt("timer");
                    boolean isOrdered = false;
                    int numFields = 0;

                    selectMultiAnswerStatement.setInt(1, questionId);
                    try (ResultSet multiAnswerResultSet = selectMultiAnswerStatement.executeQuery()) {
                        if (multiAnswerResultSet.next()) {
                            isOrdered = multiAnswerResultSet.getBoolean("is_ordered");
                            numFields = multiAnswerResultSet.getInt("num_fields");
                        }
                    }

                    selectAnswersStatement.setInt(1, questionId);
                    try (ResultSet answersResultSet = selectAnswersStatement.executeQuery()) {
                        List<String> answersList = new ArrayList<>();
                        while (answersResultSet.next()) {
                            String optionText = answersResultSet.getString("answer_text");
                            answersList.add(optionText);
                        }

                        String[] answers = answersList.toArray(new String[0]);

                        // Create and return the MULTI_ANSWER object
                        Question multiAnswer = new MultiAnswer(questionText, answers, isOrdered);
                        multiAnswer.setQuestionId(questionId);
                        multiAnswer.setTimer(timer);
                        return multiAnswer;
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

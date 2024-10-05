package dataBase.questionsDAOs;

import dataBase.ConnectionPool;
import objects.questions.Matching;
import objects.questions.Question;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class MatchingDAO implements QuestionDAOType {
    private final ConnectionPool pool;

    public MatchingDAO(ConnectionPool pool) {this.pool = pool;}

    @Override
    public int addQuestion(Question question, int quizId) {
        int questionId = 1;
        String insertQuestionQuery = "INSERT INTO QUESTIONS (quiz_id, question_text, question_type, timer) VALUES (?, ?, ?, ?)";
        String insertMatchingQuery = "INSERT INTO MATCHING (question_id, source, target) VALUES (?, ?, ?)";
        Connection connection = pool.getConnection();

        try (
             PreparedStatement insertQuestionStatement = connection.prepareStatement(insertQuestionQuery, PreparedStatement.RETURN_GENERATED_KEYS);
             PreparedStatement insertMatchingStatement = connection.prepareStatement(insertMatchingQuery)) {

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

            // Insert into MATCHING table
            String[] source = question.getOptions().clone();
            String[] target = question.getCorrectAnswers().clone();

            for (int i = 0; i < source.length; i++) {
                insertMatchingStatement.setInt(1, questionId);
                insertMatchingStatement.setString(2, source[i]);
                insertMatchingStatement.setString(3, target[i]);
                insertMatchingStatement.executeUpdate();
            }

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
        String selectMatchingQuery = "SELECT * FROM MATCHING WHERE question_id = ?";
        Connection connection = pool.getConnection();

        try (
             PreparedStatement selectQuestionStatement = connection.prepareStatement(selectQuestionQuery);
             PreparedStatement selectMatchingStatement = connection.prepareStatement(selectMatchingQuery)) {

            // Get the questionText from the QUESTIONS table
            selectQuestionStatement.setInt(1, questionId);
            try (ResultSet questionResultSet = selectQuestionStatement.executeQuery()) {
                if (questionResultSet.next()) {
                    String questionText = questionResultSet.getString("question_text");
                    int timer = questionResultSet.getInt("timer");

                    // Get the matching details from the MATCHING table
                    selectMatchingStatement.setInt(1, questionId);
                    try (ResultSet matchingResultSet = selectMatchingStatement.executeQuery()) {
                        List<String> sourceList = new ArrayList<>();
                        List<String> targetList = new ArrayList<>();
                        while (matchingResultSet.next()) {
                            String source = matchingResultSet.getString("source");
                            String target = matchingResultSet.getString("target");
                            sourceList.add(source);
                            targetList.add(target);
                        }

                        // Convert lists to arrays
                        String[] sourceArray = sourceList.toArray(new String[0]);
                        String[] targetArray = targetList.toArray(new String[0]);

                        // Create and return the Matching object
                        Question matching = new Matching(questionText, sourceArray, targetArray);
                        matching.setTimer(timer);
                        matching.setQuestionId(questionId);
                        return matching;
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

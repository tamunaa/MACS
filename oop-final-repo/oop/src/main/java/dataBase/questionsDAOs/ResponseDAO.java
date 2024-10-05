package dataBase.questionsDAOs;

import dataBase.ConnectionPool;
import objects.QuestionResponsePair;
import objects.Response;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResponseDAO {
    private final ConnectionPool pool;
    public ResponseDAO(ConnectionPool pool){this.pool = pool;}

    public List<QuestionResponsePair> getUngradedResponsesByAuthorID(int authorId) throws SQLException {
        List<QuestionResponsePair> questionResponsePairs = new ArrayList<>();

        String query = "SELECT R.*, Q.question_text FROM RESPONSES R " +
                "JOIN QUESTIONS Q ON R.Question_ID = Q.question_id " +
                "JOIN QUIZZES Z ON Q.quiz_id = Z.ID " +
                "WHERE Z.Author = ? AND R.Is_graded = FALSE";
        Connection connection = pool.getConnection();

        try  {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, authorId);

            ResultSet resultSet = statement.executeQuery();
            System.out.println("aqamde movidaaaaaaaaaaaaa");
            while (resultSet.next()) {
                System.out.println("wailshi shevidaa");
                String questionText = resultSet.getString("question_text");

                int id = resultSet.getInt("ID");
                int questionId = resultSet.getInt("Question_ID");
                int historyId = resultSet.getInt("History_ID");
                int grade = resultSet.getInt("grade");
                boolean isGraded = resultSet.getBoolean("Is_graded");
                String responseText = resultSet.getString("Response");

                Response response = new Response(id, questionId, historyId, grade, isGraded, responseText);

                QuestionResponsePair pair = new QuestionResponsePair(questionText, response);
                questionResponsePairs.add(pair);
                System.out.println("size daoShi "+ questionResponsePairs.size());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // Rethrow the exception for handling at a higher level
        }
        finally{
            pool.releaseConnection(connection);
        }

        return questionResponsePairs;
    }



    public void addResponse(int questionId, int historyId, int grade, boolean isGraded, String response) {
        String query = "INSERT INTO RESPONSES (Question_ID, History_ID, grade, Is_graded, Response) VALUES (?, ?, ?, ?, ?)";
        Connection connection = pool.getConnection();

        try (
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, questionId);
            statement.setInt(2, historyId);
            statement.setInt(3, grade);
            statement.setBoolean(4, isGraded);
            statement.setString(5, response);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            pool.releaseConnection(connection);
        }
    }

    //get responses which are not graded yet
    public Response getResponseByHistory(int historyId) {
        String query = "SELECT * FROM RESPONSES WHERE History_ID = ? AND Is_graded = ? LIMIT 1";
        Connection connection = pool.getConnection();

        try (
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, historyId);
            statement.setBoolean(2, false);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("ID");
                    int questionId = resultSet.getInt("Question_ID");
                    int grade = resultSet.getInt("grade");
                    boolean isGraded = resultSet.getBoolean("Is_graded");
                    String responseText = resultSet.getString("Response");

                    return new Response(id, questionId, historyId, grade, isGraded, responseText);
                } else {
                    // Execute GradeDAO logic when no ungraded responses are found
                    int score = calculateTotalScoreForHistory(historyId);
                    GradeDAO gradeDAO = new GradeDAO(pool);
                    gradeDAO.updateScore(historyId);
                    return null;
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

    public int calculateTotalScoreForHistory(int historyId) {
        String query = "SELECT SUM(grade) AS total_score FROM RESPONSES WHERE History_ID = ?";
        Connection connection = pool.getConnection();

        try (
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, historyId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("total_score");
                } else {
                    return 0; // No responses found for the given historyId
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
             // Return -1 in case of an error
        }
        finally{
            pool.releaseConnection(connection);
        }
        return -1;
    }


    public boolean addScoreAndMarkAsGraded(int responseId, int grade, boolean isGraded) {
        if(isGraded) {
            System.out.println("here");
            String query = "UPDATE RESPONSES SET grade = ?, Is_graded = ? WHERE ID = ?";
            Connection connection = pool.getConnection();

            try (
                 PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, grade);
                System.out.println(grade);
                statement.setBoolean(2, true);

                statement.setInt(3, responseId);
                System.out.println(responseId);

                int rowsAffected = statement.executeUpdate();
                return rowsAffected > 0; // Return true if at least one row was affected
            } catch (SQLException e) {
                e.printStackTrace();
                return false; // Return false in case of an error
            }
            finally{
                pool.releaseConnection(connection);
            }
        }
        return true;
    }



    //for tests
    public String getResponseByQuestionAndHistory(int questionId, int historyId) {
        String query = "SELECT * FROM RESPONSES WHERE Question_ID = ? AND History_ID = ?";
        Connection connection = pool.getConnection();

        try (
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, questionId);
            statement.setInt(2, historyId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String response = resultSet.getString("Response");

                    return response;
                } else {
                    return null; // No response found with the given IDs
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // Return null in case of an error
        }
        finally{
            pool.releaseConnection(connection);
        }

    }


}

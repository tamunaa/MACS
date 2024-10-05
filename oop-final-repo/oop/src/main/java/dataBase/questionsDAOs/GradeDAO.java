package dataBase.questionsDAOs;

import dataBase.ConnectionPool;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GradeDAO {
    private final ConnectionPool pool;
    private Connection con;

    public GradeDAO(ConnectionPool pool) throws SQLException {
        this.pool = pool;
        this.con = pool.getConnection();
    }

    public boolean updateScore(int historyId) {
        String query = "UPDATE HISTORY SET score = (SELECT SUM(grade) FROM RESPONSES WHERE History_ID = ?) WHERE ID = ?";
        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, historyId);
            statement.setInt(2, historyId);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getScoreByHistoryId(int historyId) {
        String query = "SELECT score FROM HISTORY WHERE ID = ?";
        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, historyId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("score");
                } else {
                    return -1; // No history found with the given ID
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Return -1 in case of an error
        }
    }

    public boolean gradeSingleResponse(int questionId, int score) {
        String query = "UPDATE RESPONSES SET grade = ? WHERE Question_ID = ?";
        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, score);
            statement.setInt(2, questionId);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public int calculateTotalScoreForHistory(int historyId) {
        String query = "SELECT SUM(grade) AS total_score FROM RESPONSES WHERE History_ID = ?";
        try (PreparedStatement statement = con.prepareStatement(query)) {
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
            return -1; // Return -1 in case of an error
        }
    }


    public boolean addScoreAndMarkAsGraded(int responseId, int grade) {
        String query = "UPDATE RESPONSES SET grade = ?, Is_graded = ? WHERE ID = ?";
        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, grade);
            statement.setBoolean(2, true);
            statement.setInt(3, responseId);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0; // Return true if at least one row was affected
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false in case of an error
        }
    }
}

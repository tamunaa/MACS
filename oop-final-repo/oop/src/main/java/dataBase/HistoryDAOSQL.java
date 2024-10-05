package dataBase;

import objects.History;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static dataBase.HistoryDAO.ERROR;
import static dataBase.HistoryDAO.SUCCESS;

public class HistoryDAOSQL implements HistoryDAO{
    private ConnectionPool pool;

    public HistoryDAOSQL(ConnectionPool pool) {
        this.pool = pool;
    }

    @Override
    public int insertHistory(History history) {
        Connection connection = pool.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO HISTORY (Quiz_ID, User_ID, score, Time_relapsed, Date_taken) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            preparedStatement.setInt(1, history.getQuizId());
            preparedStatement.setInt(2, history.getUserId());
            preparedStatement.setInt(3, history.getScore());
            preparedStatement.setDouble(4, history.getTimeRelapsed());
            preparedStatement.setTimestamp(5, history.getDateTaken());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    history.setId(generatedId);
                    return SUCCESS;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            pool.releaseConnection(connection);
        }

        return ERROR;
    }

    @Override
    public History getHistoryById(int id) {
        Connection connection = pool.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM HISTORY WHERE ID = ?"
            );

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int quizId = resultSet.getInt("Quiz_ID");
                int userId = resultSet.getInt("User_ID");
                int score = resultSet.getInt("score");
                int timeElapsed = resultSet.getInt("Time_relapsed");
                Timestamp dateTaken = resultSet.getTimestamp("Date_taken");

                History history = new History(quizId, userId, score, timeElapsed, dateTaken);
                history.setId(id);
                return history;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            pool.releaseConnection(connection);
        }
        return null;
    }

    @Override
    public List<History> getHistoryByUserId(int userId) {
        List<History> historyList = new ArrayList<>();
        Connection connection = pool.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM HISTORY WHERE User_ID = ?"
            );

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                int quizId = resultSet.getInt("Quiz_ID");
                int score = resultSet.getInt("score");
                int timeElapsed = resultSet.getInt("Time_relapsed");
                Timestamp dateTaken = resultSet.getTimestamp("Date_taken");

                History history = new History(quizId, userId, score, timeElapsed, dateTaken);
                history.setId(id);
                historyList.add(history);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            pool.releaseConnection(connection);
        }
        return historyList;
    }

    @Override
    public List<History> getHistoryByQuizId(int quizId) {
        List<History> historyList = new ArrayList<>();
        Connection connection = pool.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM HISTORY WHERE Quiz_ID = ?"
            );

            preparedStatement.setInt(1, quizId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                int userId = resultSet.getInt("User_ID");
                int score = resultSet.getInt("score");
                int timeElapsed = resultSet.getInt("Time_relapsed");
                Timestamp dateTaken = resultSet.getTimestamp("Date_taken");

                History history = new History(quizId, userId, score, timeElapsed, dateTaken);
                history.setId(id);
                historyList.add(history);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            pool.releaseConnection(connection);
        }
        return historyList;
    }
    @Override
    public List<History> UserRecentHistory(int quizId, int userId, int limit) {
        List<History> historyList = new ArrayList<>();
        Connection connection = pool.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM HISTORY WHERE Quiz_ID = ? and User_ID = ? ORDER BY Date_taken DESC LIMIT ?; "
            );

            preparedStatement.setInt(1, quizId);
            preparedStatement.setInt(2, userId);
            preparedStatement.setInt(3, limit);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                int score = resultSet.getInt("score");
                int timeElapsed = resultSet.getInt("Time_relapsed");
                Timestamp dateTaken = resultSet.getTimestamp("Date_taken");

                History history = new History(quizId, userId, score, timeElapsed, dateTaken);
                history.setId(id);
                historyList.add(history);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            pool.releaseConnection(connection);
        }
        return historyList;
    }
    @Override
    public List<History> sortedHistory(int quizId, String orderType,int limit) {
        List<History> historyList = new ArrayList<>();
        Connection connection = pool.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM HISTORY WHERE Quiz_ID = ? ORDER BY ? DESC LIMIT ?;"
            );

            preparedStatement.setInt(1, quizId);
            preparedStatement.setString(2, orderType);
            preparedStatement.setInt(3, limit);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                int userId = resultSet.getInt("User_ID");
                int score = resultSet.getInt("score");
                int timeElapsed = resultSet.getInt("Time_relapsed");
                Timestamp dateTaken = resultSet.getTimestamp("Date_taken");

                History history = new History(quizId, userId, score, timeElapsed, dateTaken);
                history.setId(id);
                historyList.add(history);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            pool.releaseConnection(connection);
        }
        return historyList;
    }
}

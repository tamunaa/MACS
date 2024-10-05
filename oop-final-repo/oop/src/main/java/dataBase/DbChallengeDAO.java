package dataBase;

import objects.Challenge;
import objects.Quiz;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbChallengeDAO implements ChallengeDAO{
    private final ConnectionPool pool;
    public DbChallengeDAO(ConnectionPool pool){this.pool = pool;}

    @Override
    public int addChallenge(Challenge challenge) {
        int challenge_id = -1;
        Connection connection = pool.getConnection();
        try {

            PreparedStatement statement = connection.prepareStatement("INSERT INTO CHALLENGES(Challenger_ID, Challenged_ID, Quiz_ID, Date_sent) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, challenge.getChallengerID());
            statement.setInt(2,challenge.getChallengedID());
            statement.setInt(3, challenge.getQuizID());
            statement.setTimestamp(4, challenge.getDateSent());
            int rowsChanged = statement.executeUpdate();
            if(rowsChanged == 0){
                statement.close();
                connection.close();
                throw new SQLException("Inserting Challenge into Challenges failed");
            }
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                challenge_id = rs.getInt(1);
            } else {
                throw new SQLException("Inserting Quiz failed, no ID obtained.");
            }
            challenge.setChallengeId(challenge_id);
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            //throw new RuntimeException(e);
        }
        finally{
            pool.releaseConnection(connection);
        }
        return challenge_id;
    }

    @Override
    public boolean removeChallenge(int id) {
        Connection connection = pool.getConnection();

        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM CHALLENGES WHERE ID = ?");
            statement.setInt(1, id);
            statement.executeUpdate();
            statement.close();
            connection.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();

        }
        finally{
            pool.releaseConnection(connection);
        }
        return false;
    }

    @Override
    public List<Challenge> getChallengeByChallengerID(int challengerID) {
        ArrayList<Challenge> challenges = new ArrayList<>();
        Connection connection = pool.getConnection();
        try {

            PreparedStatement statement = connection.prepareStatement("select * from CHALLENGES where Challenger_ID = ?");
            statement.setInt(1, challengerID);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                Challenge challenge = new Challenge(resultSet.getInt("Challenger_ID"),
                        resultSet.getInt("Challenged_ID"),  resultSet.getInt("Quiz_ID"),
                        resultSet.getTimestamp("Date_sent"));
                challenge.setChallengeId(resultSet.getInt("ID"));
                challenges.add(challenge);
            }
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        finally{
            pool.releaseConnection(connection);
        }
        return challenges;
    }

    @Override
    public List<Challenge> getChallengeByChallengedID(int challengedID) {
        ArrayList<Challenge> challenges = new ArrayList<>();
        Connection connection = pool.getConnection();

        try {
            PreparedStatement statement = connection.prepareStatement("select * from CHALLENGES where Challenged_ID = ?");
            statement.setInt(1, challengedID);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                Challenge challenge = new Challenge(resultSet.getInt("Challenger_ID"),
                        resultSet.getInt("Challenged_ID"),  resultSet.getInt("Quiz_ID"),
                        resultSet.getTimestamp("Date_sent"));
                challenge.setChallengeId(resultSet.getInt("ID"));
                challenges.add(challenge);
            }
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        finally{
            pool.releaseConnection(connection);
        }
        return challenges;
    }

    @Override
    public List<Challenge> getChallengeByQuizID(int quizID) {
        ArrayList<Challenge> challenges = new ArrayList<>();
        Connection connection = pool.getConnection();

        try {
            PreparedStatement statement = connection.prepareStatement("select * from CHALLENGES where Quiz_ID = ?");
            statement.setInt(1, quizID);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                Challenge challenge = new Challenge(resultSet.getInt("Challenger_ID"),
                        resultSet.getInt("Challenged_ID"),  resultSet.getInt("Quiz_ID"),
                        resultSet.getTimestamp("Date_sent"));
                challenge.setChallengeId(resultSet.getInt("ID"));
                challenges.add(challenge);
            }
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        finally{
            pool.releaseConnection(connection);
        }
        return challenges;
    }

    @Override
    public Challenge getChallengeByID(int id) {
        Challenge challenge = null;
        Connection connection = pool.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("select * from CHALLENGES where ID = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                challenge = new Challenge(resultSet.getInt("Challenger_ID"),
                        resultSet.getInt("Challenged_ID"),  resultSet.getInt("Quiz_ID"),
                        resultSet.getTimestamp("Date_sent"));
                challenge.setChallengeId(resultSet.getInt("ID"));
            }
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        finally{
            pool.releaseConnection(connection);
        }
        return challenge;
    }

}

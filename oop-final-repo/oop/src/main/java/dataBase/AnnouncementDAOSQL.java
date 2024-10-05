package dataBase;

import objects.Announcement;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementDAOSQL implements AnnouncementDAO {
    private final ConnectionPool pool;
    public AnnouncementDAOSQL(ConnectionPool pool){this.pool = pool;}

    @Override
    public int insertAnnouncement(Announcement announcement) {
        Connection connection = pool.getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO ANNOUNCEMENTS (User_ID, Creation_date, Announcement_title, Announcement_text) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            preparedStatement.setInt(1, announcement.getCreatorId());
            preparedStatement.setTimestamp(2, announcement.getCreationDate());
            preparedStatement.setString(3, announcement.getTitle());
            preparedStatement.setString(4, announcement.getText());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    announcement.setId(generatedId);
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
    public Announcement getAnnouncementById(int id) {
        Connection connection = pool.getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM ANNOUNCEMENTS WHERE ID = ?"
            );

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int creatorId = resultSet.getInt("User_ID");
                Timestamp creationDate = resultSet.getTimestamp("Creation_date");
                String title = resultSet.getString("Announcement_title");
                String text = resultSet.getString("Announcement_text");

                Announcement announcement = new Announcement(creatorId, creationDate, title, text);
                announcement.setId(id);
                return announcement;
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
    public int deleteAnnouncementById(int id) {
        Connection connection = pool.getConnection();

        try  {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM ANNOUNCEMENTS WHERE ID = ?"
            );

            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                return SUCCESS;
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
    public List<Announcement> getAllAnnouncements() {
        List<Announcement> announcements = new ArrayList<>();
        Connection connection = pool.getConnection();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM ANNOUNCEMENTS ORDER BY CREATION_DATE DESC");

            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                int creatorId = resultSet.getInt("User_ID");
                Timestamp creationDate = resultSet.getTimestamp("Creation_date");
                String title = resultSet.getString("Announcement_title");
                String text = resultSet.getString("Announcement_text");

                Announcement announcement = new Announcement(creatorId, creationDate, title, text);
                announcement.setId(id);
                announcements.add(announcement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            pool.releaseConnection(connection);
        }

        return announcements;
    }

    @Override
    public List<Announcement> searchAnnouncements(String searchInput) {
        List<Announcement> searchResults = new ArrayList<>();
        Connection connection = pool.getConnection();

        try  {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM ANNOUNCEMENTS WHERE Announcement_title LIKE ? OR Announcement_text LIKE ?"
            );

            preparedStatement.setString(1, "%" + searchInput + "%");
            preparedStatement.setString(2, "%" + searchInput + "%");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                int creatorId = resultSet.getInt("User_ID");
                Timestamp creationDate = resultSet.getTimestamp("Creation_date");
                String title = resultSet.getString("Announcement_title");
                String text = resultSet.getString("Announcement_text");

                Announcement announcement = new Announcement(creatorId, creationDate, title, text);
                announcement.setId(id);
                searchResults.add(announcement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            pool.releaseConnection(connection);
        }

        return searchResults;
    }
}

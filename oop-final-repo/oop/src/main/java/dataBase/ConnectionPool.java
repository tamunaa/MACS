package dataBase;

import java.sql.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import static dataBase.DataBaseInfo.*;

import org.apache.commons.dbcp2.BasicDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {
    private static BlockingQueue<Connection> connectionQueue;

    public ConnectionPool(int numConnections) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        connectionQueue = new ArrayBlockingQueue<>(numConnections);

        try {
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setUrl("jdbc:mysql://localhost:" + PORT);
            dataSource.setUsername(USERNAME);
            dataSource.setPassword(PASSWORD);
            dataSource.setMaxTotal(-1);
            for (int i = 0; i < numConnections; i++) {
                Connection con = dataSource.getConnection();
                connectionQueue.offer(con);
                Statement stm = con.createStatement();
                String db = DATABASE_NAME;
                stm.execute("USE " + db + ";");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error initializing the connection pool.");
        }
    }
    public ConnectionPool(int numConnections,String db, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        connectionQueue = new ArrayBlockingQueue<>(numConnections);

        try {
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setUrl("jdbc:mysql://localhost:" + PORT);
            dataSource.setUsername(USERNAME);
            dataSource.setPassword(password);
            dataSource.setMaxTotal(-1);
            for (int i = 0; i < numConnections; i++) {
                Connection con = dataSource.getConnection();
                connectionQueue.offer(con);
                Statement stm = con.createStatement();
                stm.execute("USE " + db + ";");
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Get a database connection from the pool
    public static Connection getConnection() {
        try {
            Connection connection = connectionQueue.poll(); // Try to get a connection
            if (connection == null || connection.isClosed()) {
                // If no available connection, create a new one
                connection = createNewConnection();
            }
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Connection createNewConnection() throws SQLException {
        try {
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setUrl("jdbc:mysql://localhost:" + PORT);
            dataSource.setUsername(USERNAME);
            dataSource.setPassword(PASSWORD);
            Connection con = dataSource.getConnection();
            Statement stm = con.createStatement();
            String db = DATABASE_NAME;
            stm.execute("USE " + db + ";");
            return con;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating a new database connection.", e);
        }
    }


    public static void releaseConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connectionQueue.add(connection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void close() throws SQLException {
        for (Connection connection : connectionQueue) {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        connectionQueue.clear();
    }
}

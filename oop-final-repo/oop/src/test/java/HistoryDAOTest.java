import dataBase.ConnectionPool;
import dataBase.HistoryDAO;
import dataBase.HistoryDAOSQL;
import objects.History;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.sql.*;
public class HistoryDAOTest {
    private static ConnectionPool pool = new ConnectionPool(5,"test_history","");
    private Connection conn;

    private HistoryDAO historyDAO;

    @BeforeEach
    public void setup() throws SQLException {
        conn = pool.getConnection();
        historyDAO = new HistoryDAOSQL(pool);
    }
    @AfterEach
    public void release() throws SQLException {
        pool.releaseConnection(conn);
    }
    @AfterAll
    public static void closeUp() throws SQLException {
        pool.close();
    }

    @Test
    public void testInsertAndGetHistory() {
        History history = new History(1, 1, 85, 120, null);
        int result = historyDAO.insertHistory(history);
        assertEquals(HistoryDAO.SUCCESS, result);

        History retrievedHistory = historyDAO.getHistoryById(history.getId());
        assertNotNull(retrievedHistory);
        assertEquals(history.getQuizId(), retrievedHistory.getQuizId());
        assertEquals(history.getUserId(), retrievedHistory.getUserId());
        assertEquals(history.getScore(), retrievedHistory.getScore());
        assertEquals(history.getTimeRelapsed(), retrievedHistory.getTimeRelapsed());
    }

    @Test
    public void testGetHistoryByUserId() {
        List<History> historyList = historyDAO.getHistoryByUserId(1);
        assertNotNull(historyList);
        assertEquals(2, historyList.size()); // Assuming there are 2 history records for user ID 1
    }

    @Test
    public void testGetHistoryByQuizId() {
        List<History> historyList = historyDAO.getHistoryByQuizId(2);
        assertNotNull(historyList);
        assertEquals(2, historyList.size());
    }

}

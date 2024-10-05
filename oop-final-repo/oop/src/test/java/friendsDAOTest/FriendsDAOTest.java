package friendsDAOTest;

import dataBase.ConnectionPool;
import dataBase.DbQuizDAO;
import dataBase.FriendsDAO;
import dataBase.UserDAO;
import objects.User;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FriendsDAOTest {
    private Connection connection;
    private UserDAO userDAO;
    private FriendsDAO friendsDAO;
    private static ConnectionPool pool;
    private static User tako;
    private static User shako;
    private static User mako;

    @BeforeAll
    public static void emptyTables() throws SQLException {
        pool = new ConnectionPool(5, "test_friend","root");
        Connection con = pool.getConnection();
        PreparedStatement stm;
        stm = con.prepareStatement("DELETE FROM FRIENDS ;");
        stm.executeUpdate();
        stm = con.prepareStatement("DELETE FROM FRIEND_REQS;");
        stm.executeUpdate();
        stm.close();
        pool.releaseConnection(con);

    }
    @BeforeEach
    public void setUp() {
        connection = pool.getConnection();
        userDAO = new UserDAO(pool);
        friendsDAO = new FriendsDAO(pool);
    }

@AfterAll
public static void closeUp() throws SQLException {
        pool.close();
}

    @AfterEach
    public void tearDown() {
        pool.releaseConnection(connection);
    }

    @Test
    @Order(1)
    void addRequestTest(){
        tako = userDAO.getUserByUserId(1);
        shako = userDAO.getUserByUserId(2);
        mako = userDAO.getUserByUserId(3);
        assertTrue(friendsDAO.addFriendshipRequest(tako,shako));
        assertFalse(friendsDAO.addFriendshipRequest(tako,shako));
        assertTrue(friendsDAO.isInFriendshipRequests(tako, shako));
        assertTrue(friendsDAO.addFriendshipRequest(tako, mako));

    }

    @Test
    @Order(2)
    void addRequestTestPart2(){
        List<User> reqs = friendsDAO.getAllFriendshipRequests(tako);
        assertEquals(2,reqs.size());
        for (int i = 0; i < reqs.size(); i++){
            User curr = reqs.get(i);
            assertTrue(curr.equals(shako) || curr.equals(mako));
        }
    }
    @Test
    @Order(3)
    void removeRequestTest() {
        assertFalse(friendsDAO.addFriendshipRequest(shako, shako));

        assertFalse(friendsDAO.isInFriendshipRequests(shako, tako));
        assertFalse(friendsDAO.isInFriendshipRequests(shako, mako));

    }
    @Test
    @Order(4)
    void removeRequestTestPart2(){
        assertTrue(friendsDAO.removeFriendshipRequest(tako, shako));
        assertFalse(friendsDAO.removeFriendshipRequest(tako, shako));
        assertFalse(friendsDAO.isInFriendshipRequests(tako, shako));

    }
    @Test
    @Order(5)
    void removeRequestTestPart3(){
        List<User> reqs1 = friendsDAO.getAllFriendshipRequests(tako);
        List<User> reqs2 = friendsDAO.getAllFriendshipRequests(shako);
        assertEquals(1,reqs1.size());
        assertEquals(0,reqs2.size());
        assertTrue(mako.equals(reqs1.get(0)));
    }

    @Test
    @Order(6)
    void addFriendshipTest(){
        assertTrue(friendsDAO.addFriendshipRequest(mako,tako));
       assertTrue(friendsDAO.addFriendship(tako,mako));
         assertTrue(friendsDAO.isFriendship(tako,mako));
    }
    @Test
    @Order(7)
    void addFriendshipTest2(){
        List<User> fr1 = friendsDAO.getAllFriends(tako);
        List<User> fr2 = friendsDAO.getAllFriends(mako);
        assertTrue(fr1.size() == 1 && fr2.size() == 1);
        assertTrue(mako.equals(fr1.get(0)));
        assertTrue(tako.equals(fr2.get(0)));

        assertFalse(friendsDAO.isFriendship(shako,tako));
    }

    @Test
    @Order(8)
    void removeFriendshipTest() {
        assertTrue(friendsDAO.removeFriendship(tako, mako));
        assertFalse(friendsDAO.isFriendship(tako, mako));
        assertFalse(friendsDAO.removeFriendship(mako, tako));
    }

}


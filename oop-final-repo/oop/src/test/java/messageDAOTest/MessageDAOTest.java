package messageDAOTest;

import dataBase.ConnectionPool;
import dataBase.MessageDAO;
import dataBase.UserDAO;
import objects.User;
import objects.messages.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.sql.*;

import org.apache.commons.dbcp2.BasicDataSource;
import at.favre.lib.crypto.bcrypt.BCrypt;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class MessageDAOTest {
    private Statement statement;
    private UserDAO userDAO;
    private MessageDAO messageDAO;
    private static ConnectionPool pool;
    private Connection conn;
    private User user1;
    private User user2;
    private Message note1;
    private Message note2;
    private Message note3;
    private Message req;
    private Message ch;
    private static int id1;
    private static int id2;


    @BeforeAll
    protected static void emptyTables() throws SQLException {
        pool = new ConnectionPool(5, "test_message","root");
        Connection con = pool.getConnection();
        PreparedStatement stm;
        stm = con.prepareStatement("DELETE FROM USERS ;");
        stm.executeUpdate();
        stm = con.prepareStatement("DELETE FROM MESSAGE ;");
        stm.executeUpdate();
        stm.close();
        pool.releaseConnection(con);
    }
    @BeforeEach
    public void setUp() {
        conn = pool.getConnection();
        userDAO = new UserDAO(pool);
        messageDAO = new MessageDAO(pool);
    }
    @AfterAll
    public static void closeUp() throws SQLException {
        pool.close();
    }
    @AfterEach
    public void tearDown() {
        pool.releaseConnection(conn);
    }
/*
    @Test
    void boo() {
        user1 = new User("girl","girl@gmail.com", "password");
        user2 = new User("guy", "guy@gmail.com", "pass");
        id1 = userDAO.addUser(user1);
        id2 = userDAO.addUser(user2);
        List<Message> l = messageDAO.getChat(id1,id2,true);
        assertTrue(l.isEmpty());
        note1 = new NoteMessage(id1,id2,"hey baby,how are you feeling");
        messageDAO.addMessage(note1);
        List<Message> ls = messageDAO.getChat(id1,id2,true);
        assertEquals(1,ls.size());
        assertTrue(messageDAO.deleteMessage(note1.getId()));
        List<Message> ls2 = messageDAO.getChat(id1,id2,true);
        assertTrue(ls2.isEmpty());
    }
 */
    @Test
    @Order(1)
    void createChatAddAndRemoveTest(){
        user1 = new User("girl","girl@gmail.com", "password");
        user2 = new User("guy", "guy@gmail.com", "pass");
         id1 = userDAO.addUser(user1);
         id2 = userDAO.addUser(user2);

         note1 = new NoteMessage(id1,id2,"hey baby,how are you feeling");
         note2 = new NoteMessage(id2,id1,"nothing much, dear");
         note3 = new NoteMessage(id1,id2,"nothing much here either...");

         assertTrue(messageDAO.addMessage(note1));
         assertTrue(messageDAO.addMessage(note2));
         assertTrue(messageDAO.addMessage(note3));
        List<Message> mes = messageDAO.getChat(id1,id2,true);
        List<String> strs = mes.stream().map(x-> (x).getContent()).collect(Collectors.toList());
        assertEquals(3,mes.size());
        assertTrue(strs.contains("hey baby,how are you feeling"));
        assertTrue(strs.contains("nothing much, dear"));
        assertTrue(strs.contains("nothing much here either..."));
    }
    @Test
    @Order(2)
    void deleteConvoTest(){
        assertEquals(1,messageDAO.getInteractions(id1).size());
        assertEquals(3,messageDAO.deleteAllMessages(id1,id2));
        assertTrue(id1 != id2);
        List<Message> mes = messageDAO.getChat(id1,id2,true);
         assertEquals(0, mes.size());
    }

    @Test
    @Order(3)
    void removeAndGetMessagesTest(){
        assertNotEquals(id1, id2);
        assertTrue(id1 != id2);
        ch = new ChallengeMessage(id1,id2,"challengeLink");
        messageDAO.addMessage(ch);
        Message rec = messageDAO.getMessage(ch.getId());
        assertEquals(ch.getSenderID(),rec.getSenderID());
        assertEquals(ch.getRecieverID(),rec.getRecieverID());
        assertEquals(ch.getType(), rec.getType());
        assertTrue(messageDAO.deleteMessage(ch.getId()));
        assertNull(messageDAO.getMessage(ch.getId()));
    }


    @Test
    @Order(4)
    void manageNotificationsTest(){
        User user3 = new User("enemy1","enemy1@gmail.com", "death");
        User user4 = new User("enemy2","enemy2@gmail.com", "death");
        int id3 = userDAO.addUser(user3);
        int id4 = userDAO.addUser(user4);
        ch = new ChallengeMessage(id3,id4,"challengeLink");
        req = new RequestMessage(id3,id4);
        assertTrue(messageDAO.addMessage(ch));
        assertTrue(messageDAO.addMessage(req));
        List<Message> notifs = messageDAO.getUsersRecentIncomingNotifications(id4);
        assertEquals(2,notifs.size());
        for(Message mes : notifs){
            assertTrue(mes.getId() == ch.getId() || mes.getId() == req.getId());
        }
        assertEquals(2,messageDAO.deleteNotifications(id4));
        notifs = messageDAO.getUsersRecentIncomingNotifications(id4);
        assertEquals(0,notifs.size());

    }

}

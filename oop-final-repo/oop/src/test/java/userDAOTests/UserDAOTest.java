package userDAOTests;

import dataBase.ConnectionPool;
import dataBase.UserDAO;
import objects.Review;
import objects.User;
import objects.Quiz;
import dataBase.DbQuizDAO;
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
public class UserDAOTest {
    private Connection connection;
    private Statement statement;
    private UserDAO userDAO;
    private DbQuizDAO quizDAO;
    private static ConnectionPool pool;
    private Connection conn;
    private User user1;
    private User user2;
    private User admin;

    @BeforeAll
    public static void emptyTables() throws SQLException {
        pool = new ConnectionPool(5, "test_user","root");
        Connection con = pool.getConnection();
        PreparedStatement stm;
        stm = con.prepareStatement("DELETE FROM USERS ;");
        stm.executeUpdate();
        stm = con.prepareStatement("DELETE FROM QUIZZES ;");
        stm.executeUpdate();
        stm = con.prepareStatement("DELETE FROM REVIEW ;");
        stm.executeUpdate();
        stm = con.prepareStatement("DELETE FROM RATING ;");
        stm.executeUpdate();
        stm.close();
        pool.releaseConnection(con);
    }
    @BeforeEach
    public void setUp() {
         conn = pool.getConnection();
        userDAO = new UserDAO(pool);
        quizDAO = new DbQuizDAO(pool);
    }

    @AfterEach
    public void tearDown() {
        pool.releaseConnection(conn);
    }

    @AfterAll
    public static void closeUp() throws SQLException {
        pool.close();
    }
    @Test
    @Order(1)
    void addUserTest(){
        user1 = new User("tako","tako@test.com",BCrypt.withDefaults().hashToString(10, "123".toCharArray()));
        admin = new User("admin","admin@test.com",BCrypt.withDefaults().hashToString(10, "admin".toCharArray()),true);

        int user1Id = userDAO.addUser(user1);
        int adminId = userDAO.addUser(admin);

        assertNotSame(-1,user1Id);
        assertTrue(admin.getId() >= 0);

        assertEquals(-1, userDAO.addUser(user1));
        assertEquals(-1, userDAO.addUser(admin));

        user1.setUsername("new");
        int newId = userDAO.addUser(user1);
        assertSame(-1,newId);
    }
    @Test
    @Order(2)
    void addUserNullTest(){
        user1 = new User("tako","@gmail.com",BCrypt.withDefaults().hashToString(10, "123".toCharArray()));
        user1.setUsername(null);
        assertEquals(-1, userDAO.addUser(user1));

    }
    @Test
    @Order(3)
    void getUserByEmailTest(){
        user2 = new User("user","user@test.com",BCrypt.withDefaults().hashToString(10, "user".toCharArray()));
        assertNotEquals (-1,userDAO.addUser(user2));
        User user = userDAO.getUserByEmail(user2.getEmail());
        assertEquals(user2.getEmail(), user.getEmail());
        assertEquals(user2.getPasswordHash(), user.getPasswordHash());
        assertEquals(user2.getUsername(), user.getUsername());
        User user3 = new User("user3","user3@test.com",BCrypt.withDefaults().hashToString(10, "user".toCharArray()));
        assertNull(userDAO.getUserByEmail(user3.getEmail()));
    }
    @Test
    @Order(4)
    void getUserByUsernameTest(){
        user2 = new User("qz","qz@test.com",BCrypt.withDefaults().hashToString(10, "qz".toCharArray()));
        assertNotEquals (-1,userDAO.addUser(user2));
        User user = userDAO.getUserByUsername(user2.getUsername());
        assertEquals(user2.getEmail(), user.getEmail());
        assertEquals(user2.getPasswordHash(), user.getPasswordHash());
        assertEquals(user2.getUsername(), user.getUsername());
        User user3 = new User("user3","user3@test.com",BCrypt.withDefaults().hashToString(10, "user".toCharArray()));
        assertNull(userDAO.getUserByUsername(user3.getUsername()));
    }
    @Test
    @Order(5)
    void getUserByUserIdTest(){
        user2 = new User("forId","dd@test.com",BCrypt.withDefaults().hashToString(10, "qz".toCharArray()));
        userDAO.addUser(user2);
        User user = userDAO.getUserByUserId(user2.getId());
        assertEquals(user2.getEmail(), user.getEmail());
        assertEquals(user2.getPasswordHash(), user.getPasswordHash());
        assertEquals(user2.getUsername(), user.getUsername());
        User user3 = new User("user","user3@test.com",BCrypt.withDefaults().hashToString(10, "user".toCharArray()));
        assertNull(userDAO.getUserByUserId(user3.getId()));
    }

    @Test
    @Order(6)
    void removeUserTest() {
        User newUser = new User("bro","bro@test.com",BCrypt.withDefaults().hashToString(10, "bro".toCharArray()) );
        assertNotEquals(-1,userDAO.addUser(newUser));
        assertTrue(userDAO.removeUser(newUser.getId()));
        assertFalse(userDAO.removeUser(newUser.getId()));
        assertNull(userDAO.getUserByUsername("bro"));

    }
    @Test
    @Order(7)
    void isValidUserTest() {
        user1 = new User("sis","sis@test.com",BCrypt.withDefaults().hashToString(10, "sis".toCharArray()) );
        assertNotEquals(-1,userDAO.addUser(user1));
        assertEquals(true, userDAO.isValidUser(user1.getUsername(), "sis"));
        assertEquals(false, userDAO.isValidUser(user1.getUsername(), "sista"));
        assertEquals(false, userDAO.isValidUser("wrongUsername", "sis"));
        assertTrue(userDAO.removeUser(user1.getId()));
        assertEquals(false,userDAO.isValidUser(user1.getUsername(),"sis"));
    }
    @Test
    @Order(8)
    void getIDbyUsernameTest() {
        assertEquals(-1,userDAO.getIDByUsername("test1"));
        user1 = new User("test1","test1@test.com",BCrypt.withDefaults().hashToString(10, "123".toCharArray()));
        int id = userDAO.addUser(user1);
        int checkid = userDAO.getIDByUsername(user1.getUsername());
        assertEquals(id,checkid);
        userDAO.removeUser(user1.getId());
        assertEquals(-1,userDAO.getIDByUsername(user1.getUsername()));

    }
    @Test
    @Order(9)
    void getUsernameByIDTest() {
        user2 = new User("test2","test2@test.com",BCrypt.withDefaults().hashToString(10, "123".toCharArray()));
        int id = userDAO.addUser(user2);
        assertEquals(id,user2.getId());
        assertEquals(user2.getUsername(),userDAO.getUsernameByID(id));
        userDAO.removeUser(user2.getId());
        assertNull(userDAO.getUsernameByID(id));
    }
    @Test
    @Order(10)
    void changeUsernameTest() {
        user1 = new User("username","email",BCrypt.withDefaults().hashToString(10, "123".toCharArray()));
        int id = userDAO.addUser(user1);
        assertTrue(userDAO.changeUsername(user1,"newUsername"));
        assertEquals("newUsername",user1.getUsername());
        assertEquals(id,user1.getId());
        assertEquals(id,userDAO.getIDByUsername("newUsername"));
        assertEquals(user1.getPasswordHash(),(userDAO.getUserByUsername("newUsername")).getPasswordHash());
        assertEquals(user1.getEmail(),(userDAO.getUserByUsername("newUsername")).getEmail());
        userDAO.removeUser(user1.getId());
        assertNull(userDAO.getUserByUsername(user1.getUsername()));
    }
    @Test
    @Order(11)
    void changePasswordTest() {
        user1 = new User("password","email2",BCrypt.withDefaults().hashToString(10, "pass".toCharArray()));
        userDAO.addUser(user1);
        assertTrue(userDAO.changePassword(user1.getUsername(),"newPassword"));
        User changedUser = userDAO.getUserByUsername(user1.getUsername());
        BCrypt.Verifyer verifier = BCrypt.verifyer();
        BCrypt.Result res = verifier.verify("newPassword".toCharArray(), changedUser.getPasswordHash().toCharArray());
        assertTrue(res.verified);
        assertFalse(verifier.verify("pass".toCharArray(), changedUser.getPasswordHash().toCharArray()).verified);
        assertTrue(userDAO.isValidUser(user1.getUsername(),"newPassword"));
        userDAO.removeUser(changedUser.getId());
        assertFalse(userDAO.isValidUser(user1.getUsername(),"newPassword"));
    }

    @Test
    @Order(12)
    void getAllUsersTest(){
        user1 = new User("tz","tz.com",BCrypt.withDefaults().hashToString(10,"tz".toCharArray()));
        admin = new User("admin1","admin1@test.com",BCrypt.withDefaults().hashToString(10, "admin1".toCharArray()),true);
        List<User> users = userDAO.getAllUsers();
        userDAO.addUser(user1);
        userDAO.addUser(admin);
        List<User> increasedUsers = userDAO.getAllUsers();
        assertEquals(users.size() + 2, increasedUsers.size());
        admin.equals(increasedUsers.get(increasedUsers.size() - 1));
        userDAO.removeUser(admin.getId());
        user1.equals(increasedUsers.get(increasedUsers.size() - 2));
        userDAO.removeUser(user1.getId());
        assertEquals(users.size() , userDAO.getAllUsers().size());
    }

    @Test
    @Order(13)
    void makeAndRemoveAdminTest() {
        admin = new User("coolguy","cool@test.com",BCrypt.withDefaults().hashToString(10, "cool".toCharArray()));

        userDAO.addUser(admin);
        assertFalse(userDAO.removeAdmin(admin));
        assertFalse(admin.isAdmin());
        userDAO.makeAdmin(admin);
        assertTrue(admin.isAdmin());
        userDAO.removeAdmin(admin);
        assertFalse(admin.isAdmin());

    } /*
    @Test
    @Order(14)
    void addReviewTest() {
        user1 = new User("reviewer1","rev1@test.com,",BCrypt.withDefaults().hashToString(10, "rev".toCharArray()));
        user2 = new User("reviewer2","rev2@test.com,",BCrypt.withDefaults().hashToString(10, "rev".toCharArray()));
        int id1 = userDAO.addUser(user1);
        int id2 = userDAO.addUser(user2);
        Quiz quiz = new Quiz(id1,"quizzie","easy quiz",10);
        quiz.setDateCreated(Timestamp.valueOf("2023-08-15 20:20:43"));
        int quizid = quizDAO.addQuiz(quiz);
        Review review1 = new Review(id1, quizid,"good quiz");
        Review review2 = new Review(id2, quizid,"bad quiz");
        userDAO.addReview(review1);
        userDAO.addReview(review2);
        assertNotEquals(-1,review1.getReview_id());
        assertNotEquals(-1,review2.getReview_id());

        List<Review> reviews = quizDAO.getReviews(quiz);
        assertEquals(2,reviews.size());
        assertEquals(id1, reviews.get(0).getUser_id());
        assertEquals(id2, reviews.get(1).getUser_id());
        assertEquals(quizid, reviews.get(0).getQuiz_id());
        assertEquals(quizid, reviews.get(1).getQuiz_id());
        assertEquals("good quiz", reviews.get(0).getContent());
        assertEquals("bad quiz", reviews.get(1).getContent());
    }
    @Test
    @Order(15)
    void setRatingTest(){
        user1 = new User("rater1","rate1@test.com,",BCrypt.withDefaults().hashToString(10, "rate".toCharArray()));
        user2 = new User("rater2","rate2@test.com,",BCrypt.withDefaults().hashToString(10, "rate".toCharArray()));
        int id1 = userDAO.addUser(user1);
        int id2 = userDAO.addUser(user2);
        Quiz quiz = new Quiz(id2,"quizlet","funny quiz",30);
        quiz.setDateCreated(Timestamp.valueOf("2023-08-15 20:20:43"));
        int quizid = quizDAO.addQuiz(quiz);
        assertTrue(userDAO.setRating(id1,quizid,1.0));
        assertTrue(userDAO.setRating(id2,quizid,2.5));
        assertEquals(1.75,quizDAO.getRating(quiz));
        userDAO.setRating(id1,quizid,4.0);
        assertEquals(3.25,quizDAO.getRating(quiz));
    }
*/
    @Test
    @Order(14)
    void addCategory(){
        User user = new User("imagine", "imagine@test,com", BCrypt.withDefaults().hashToString(10, "image".toCharArray()), true);
        userDAO.addUser(user);
        assertTrue(userDAO.addCategory(user.getId(), "Math"));
        assertTrue(userDAO.addCategory(user.getId(), "History"));
        assertTrue(userDAO.addCategory(user.getId(), "Science"));
    }

    @Test
    @Order(15)
    void cannotAddCategory(){
        User user2 = new User("imag", "imag@test,com", BCrypt.withDefaults().hashToString(10, "imag".toCharArray()), false);
        userDAO.addUser(user2);
        assertFalse(userDAO.addCategory(user2.getId(), "French"));
    }

    @Test
    @Order(16)
    void getCategories(){
        User user = new User("cat", "cat@test,com", BCrypt.withDefaults().hashToString(10, "cat".toCharArray()), true);
        userDAO.addUser(user);
        int id = user.getId();
        assertFalse(userDAO.addCategory(id, "Math"));
        assertFalse(userDAO.addCategory(id, "History"));
//        assertTrue(userDAO.addCategory(id, "English"));
//        assertTrue(userDAO.addCategory(id, "Biology"));
//        assertTrue(userDAO.addCategory(id,  "Science"));

        List<String> categories = userDAO.getCategories();
        assertEquals(3, categories.size());
        assertTrue(categories.contains("Math"));
        assertTrue(categories.contains("History"));
////        assertTrue(categories.contains("Science"));
    }

}

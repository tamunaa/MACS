import objects.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
public class UserTest {
    private User user;

    @BeforeEach
    void setUp() {
        user = new User("tako","tako@gmail.com","123" );
    }
    @Test
    void getIDTest() {
        assertEquals(-1,user.getId());
    }
    @Test
    void setIDTest() {
        user.setId(1);
        assertNotEquals(-1,user.getId());
        assertEquals(1,user.getId());
    }

    @Test
    void getUsernameTest() {
        assertEquals("tako",user.getUsername());
    }

    @Test
    void setUsernameTest() {
        user.setUsername("takuna");
        assertNotEquals("tako",user.getUsername());
        assertEquals("takuna", user.getUsername());
    }
    @Test
    void getEmailTest() {
        assertEquals("tako@gmail.com",user.getEmail());
    }
    @Test
    void getPasswordTest() {
        assertEquals("123",user.getPasswordHash());
    }
    @Test
    void setPasswordTest() {
        user.setPassword("1234");
        assertNotEquals("123",user.getPasswordHash());
        assertEquals("1234",user.getPasswordHash());
    }

    @Test
    void isAdminTest(){
        assertEquals(false,user.isAdmin());
    }

    @Test
    void setAndGetDateTest(){
        Timestamp newTime = Timestamp.valueOf("2023-08-12 12:42:18.700");
        assertNotEquals(newTime,user.getDate());
        user.setDate(newTime);
        assertEquals(newTime, user.getDate());
    }
    @Test
    void setAdminTest(){
        assertEquals(false,user.isAdmin());
        user.setAdmin(true);
        assertEquals(true,user.isAdmin());
    }
    @Test
    void EqualsTest(){
        User user2 = new User("tako","tako@gmail.com","123", false);
        user2.setId(1);
        assertFalse(user2.equals(user));
        user.setId(1);
        assertTrue(user.equals(user2));
        assertTrue(user2.equals(user));
    }
}

import dataBase.AnnouncementDAOSQL;
import dataBase.ConnectionPool;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import objects.Announcement;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AnnouncementDAOTest {
    private ConnectionPool pool = new ConnectionPool(5,"test_announcement","root");
    private Connection conn;

    private AnnouncementDAOSQL announcementDAO;

    @BeforeEach
    public void setup() {
        conn = pool.getConnection();
        announcementDAO = new AnnouncementDAOSQL(pool);
    }

    @AfterEach
    public void tearDown() {
        pool.releaseConnection(conn);
    }


    @Test
    public void testInsertAndRetrieveAnnouncement() {
        Announcement announcement = new Announcement(1, new Timestamp(System.currentTimeMillis()), "Test Title", "Test Text");

        int insertResult = announcementDAO.insertAnnouncement(announcement);
        assertEquals(AnnouncementDAOSQL.SUCCESS, insertResult);

        Announcement retrievedAnnouncement = announcementDAO.getAnnouncementById(announcement.getId());
        assertNotNull(retrievedAnnouncement);
        assertEquals("Test Title", retrievedAnnouncement.getTitle());
        assertEquals("Test Text", retrievedAnnouncement.getText());
        assertEquals(1, retrievedAnnouncement.getCreatorId());
    }

    @Test
    public void testDeleteAnnouncement() {
        Announcement announcement = new Announcement(1, new Timestamp(System.currentTimeMillis()), "Delete Test", "This announcement will be deleted");
        announcementDAO.insertAnnouncement(announcement);

        int deleteResult = announcementDAO.deleteAnnouncementById(announcement.getId());
        assertEquals(AnnouncementDAOSQL.SUCCESS, deleteResult);

        Announcement deletedAnnouncement = announcementDAO.getAnnouncementById(announcement.getId());
        assertNull(deletedAnnouncement);
    }

    @Test
    public void testSearchAnnouncements() {
        List<Announcement> searchResults = announcementDAO.searchAnnouncements("Quiz Contest");
        assertNotNull(searchResults);
        assertEquals("Quiz Contest", searchResults.get(0).getTitle());
    }

    @Test
    public void testGetAnnouncementById() {
        Announcement announcement = new Announcement(1, new Timestamp(System.currentTimeMillis()), "Test Title", "Test Text");
        announcementDAO.insertAnnouncement(announcement);

        Announcement retrievedAnnouncement = announcementDAO.getAnnouncementById(announcement.getId());
        assertNotNull(retrievedAnnouncement);
        assertEquals(announcement.getId(), retrievedAnnouncement.getId());
        assertEquals(announcement.getCreatorId(), retrievedAnnouncement.getCreatorId());
        assertEquals(announcement.getTitle(), retrievedAnnouncement.getTitle());
        assertEquals(announcement.getText(), retrievedAnnouncement.getText());
    }

    @Test
    public void testSearchAnnouncementsInDB() {
        List<Announcement> searchResults = announcementDAO.searchAnnouncements("Quiz Contest");
        assertNotNull(searchResults);
        assertEquals(1, searchResults.size());

        Announcement firstResult = searchResults.get(0);
        assertEquals("Quiz Contest", firstResult.getTitle());
        assertEquals("Participate in our latest quiz contest and stand a chance to win exciting prizes!", firstResult.getText());
    }

    @Test
    public void testSearchAnnouncementsNoResults() {
        List<Announcement> searchResults = announcementDAO.searchAnnouncements("Nonexistent Keyword");
        assertNotNull(searchResults);
        assertEquals(0, searchResults.size());
    }

    @Test
    public void testGetAllAnnouncements() {
        List<Announcement> initialAnnouncements = announcementDAO.getAllAnnouncements();

        Announcement announcement1 = new Announcement(1, null, "Important Update", "We have released a new feature that allows users to track their quiz history.");
        Announcement announcement2 = new Announcement(2, null, "Upcoming Maintenance", "Please be aware that there will be a scheduled maintenance on August 25th.");
        Announcement announcement3 = new Announcement(3, null, "New Achievements", "We're excited to announce a set of new achievements for our users.");
        Announcement announcement4 = new Announcement(4, null, "Quiz Contest", "Participate in our latest quiz contest and stand a chance to win exciting prizes!");

        announcementDAO.insertAnnouncement(announcement1);
        announcementDAO.insertAnnouncement(announcement2);
        announcementDAO.insertAnnouncement(announcement3);
        announcementDAO.insertAnnouncement(announcement4);

        List<Announcement> retrievedAnnouncements = announcementDAO.getAllAnnouncements();
        assertNotNull(retrievedAnnouncements);
        assertEquals(initialAnnouncements.size() + 4, retrievedAnnouncements.size());
    }
}

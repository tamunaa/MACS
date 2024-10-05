import objects.Announcement;
import org.junit.Test;
import java.sql.Timestamp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class AnnouncementTest {
    @Test
    public void testAnnouncementConstructorAndGetters() {
        int creatorId = 1;
        Timestamp creationDate = new Timestamp(System.currentTimeMillis());
        String title = "Test Title";
        String text = "Test Text";

        Announcement announcement = new Announcement(creatorId, creationDate, title, text);

        assertEquals(creatorId, announcement.getCreatorId());
        assertEquals(creationDate, announcement.getCreationDate());
        assertEquals(title, announcement.getTitle());
        assertEquals(text, announcement.getText());
    }

    @Test
    public void testAnnouncementIdSetterAndGetter() {
        Announcement announcement = new Announcement(1, new Timestamp(System.currentTimeMillis()), "Test", "Text");

        int newId = 100;
        announcement.setId(newId);

        assertEquals(newId, announcement.getId());
    }

    @Test
    public void testAnnouncementNotEquality() {
        Announcement announcement1 = new Announcement(1, new Timestamp(System.currentTimeMillis()), "Title", "Text");
        Announcement announcement2 = new Announcement(1, new Timestamp(System.currentTimeMillis()), "Title", "Text2");

        assertNotEquals(announcement1, announcement2);
        assertEquals(announcement1.getCreatorId(), announcement2.getCreatorId());
        assertEquals(announcement1.getTitle(), announcement2.getTitle());
        assertNotEquals(announcement1.getText(), announcement2.getText());
        assertEquals(announcement1.getCreationDate(), announcement2.getCreationDate());
    }

    @Test
    public void testGetCreatorId() {
        int creatorId = 5;
        Timestamp creationDate = new Timestamp(System.currentTimeMillis());
        String title = "Test Title";
        String text = "Test Text";

        Announcement announcement = new Announcement(creatorId, creationDate, title, text);

        assertEquals(creatorId, announcement.getCreatorId());
    }

    @Test
    public void testGetCreationDate() {
        int creatorId = 5;
        Timestamp creationDate = new Timestamp(System.currentTimeMillis());
        String title = "Test Title";
        String text = "Test Text";

        Announcement announcement = new Announcement(creatorId, creationDate, title, text);

        assertEquals(creationDate, announcement.getCreationDate());
    }

    @Test
    public void testGetTitle() {
        Announcement announcement = new Announcement(1, null, "Test Title", "Test Text");
        assertEquals("Test Title", announcement.getTitle());
    }

    @Test
    public void testGetText() {
        Announcement announcement = new Announcement(1, null, "Test Title", "Test Text");
        assertEquals("Test Text", announcement.getText());
    }

    @Test
    public void testSetText() {
        Announcement announcement = new Announcement(1, null, "Test Title", "Test Text");

        assertEquals("Test Text", announcement.getText());

    }
}

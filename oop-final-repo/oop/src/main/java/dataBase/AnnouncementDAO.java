package dataBase;

import objects.Announcement;

import java.util.List;

public interface AnnouncementDAO {
    int ERROR = -1;
    int SUCCESS = 0;
    String ATTR_NAME = "AnnouncementDAO";

    /**
     * Inserts announcement into the database
     * returns ERROR or SUCCESS
     */
    int insertAnnouncement(Announcement announcement);

    /**
     * Returns announcement from database with given id
     */
    Announcement getAnnouncementById(int id);

    /**
     * Deletes announcement from database with given id
     * returns ERROR or SUCCESS
     */
    int deleteAnnouncementById(int id);

    /**
     * Returns all announcements contained in the database
     */
    List<Announcement> getAllAnnouncements();

    /**
     * Returns List of announcements which correspond to search input
     */
    List<Announcement> searchAnnouncements(String searchInput);

}
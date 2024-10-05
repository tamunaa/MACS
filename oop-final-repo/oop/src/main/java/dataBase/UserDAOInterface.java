package dataBase;
import objects.User;
import objects.Review;
import java.util.List;
import java.sql.Date;
import java.sql.SQLException;

public interface UserDAOInterface {
    int addUser(User user);
    boolean removeUser(int userID);
    User getUserByEmail(String email);
    User getUserByUserId(int ID);
    User getUserByUsername(String username);
    int getIDByUsername(String username);
    String getUsernameByID(int  ID);
    boolean isValidUser(String username, String password);
    boolean changePassword(String username, String newPassword);
    boolean changeUsername(User user,String newUsername);
    List<User> getAllUsers();
    boolean makeAdmin(User user);
    boolean removeAdmin(User user);
    int addReview(Review review);
    boolean setRating(int userId, int quizId,double rating);
    List<String> getCategories();
    boolean addCategory(int id, String category);
}

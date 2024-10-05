package dataBase;
import objects.User;
import java.sql.SQLException;
import java.util.*;
public interface FriendsDAOInterface {
    boolean addFriendship(User user1, User user2);

    boolean removeFriendship(User user1, User user2);

    boolean isFriendship(User user1, User user2);

    List<User> getAllFriends(User user);

    boolean addFriendshipRequest(User target, User sender);

    boolean removeFriendshipRequest(User target, User sender);

    List<User> getAllFriendshipRequests(User target);

    boolean isInFriendshipRequests(User target, User sender);

}

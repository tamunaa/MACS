package dataBase;
import objects.User;
import java.sql.SQLException;

import static dataBase.FriendshipService.RequestResult.*;
public class FriendshipService {
    public enum RequestResult{
        REQUEST_SUCCESS,
        REQUEST_INVALID_USER,
        REQUEST_ALREADY_EXISTS,
        REQUEST_BECAME_FRIENDS,
        REQUEST_ARE_FRIENDS,
        REQUEST_SAME_USER
    }
    public boolean acceptRequest(User target, User sender, FriendsDAO dao){
        if(!dao.isInFriendshipRequests(target, sender)) return false;
        dao.removeFriendshipRequest(target, sender);
        dao.addFriendship(target, sender);
        return true;
    }
    public boolean rejectRequest(User target, User sender, FriendsDAO dao){
        if(!dao.isInFriendshipRequests(target, sender)) return false;
        dao.removeFriendshipRequest(target, sender);
        return true;
    }

    public boolean removeFriend(User user, String friendUsername, FriendsDAO friendsDAO, UserDAO userDAO){
        User friend = userDAO.getUserByUsername(friendUsername);
        if(friend == null) return false;
        if(!friendsDAO.isFriendship(user, friend)) return false;
        return friendsDAO.removeFriendship(user, friend);
    }
    public RequestResult sendRequest(User sender, String targetUsername, FriendsDAO friendsDAO, UserDAO userDAO){
        User target = userDAO.getUserByUsername(targetUsername);
        if(target == null) return REQUEST_INVALID_USER;
        if(target.getUsername().equals(sender.getUsername())) return REQUEST_SAME_USER;
        if(friendsDAO.isInFriendshipRequests(target, sender)){
            return REQUEST_ALREADY_EXISTS;
        }else if(friendsDAO.isInFriendshipRequests(sender, target)){
            friendsDAO.removeFriendshipRequest(sender, target);
            friendsDAO.addFriendship(sender, target);
            return REQUEST_BECAME_FRIENDS;
        }else if(friendsDAO.isFriendship(target, sender)){
            return REQUEST_ARE_FRIENDS;
        }else if(friendsDAO.addFriendshipRequest(target, sender)){
            return REQUEST_SUCCESS;
        }else{
            return REQUEST_INVALID_USER;
        }
    }

}

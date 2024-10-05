package dataBase;
import objects.messages.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public interface MessageDAOInterface {

    boolean addMessage(Message message);
    boolean deleteMessage(int id);
    int deleteNotifications(int reciever_id);
    int deleteAllMessages(int sender_id, int reciever_id);
    Message getMessage(int id);
    List<Message> getChat(int sender_id, int reciever_id, boolean asc);
    List<Message> getUsersRecentIncomingNotifications(int userId);
    List<Integer> getInteractions(int user_id);
}
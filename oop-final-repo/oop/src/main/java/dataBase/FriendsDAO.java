package dataBase;
import objects.User;
import java.util.List;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.ArrayList;

public class FriendsDAO implements FriendsDAOInterface{
    private ConnectionPool pool;


    public FriendsDAO(ConnectionPool pool) {
        this.pool = pool;
    }

    @Override
    public boolean addFriendship(User user1, User user2){
        if(isFriendship(user1,user2)) return false;
        Connection conn = pool.getConnection();
        try {

            PreparedStatement stm;

            stm = conn.prepareStatement("INSERT IGNORE INTO FRIENDS (User_ID, Friend_ID) VALUES (? , ?); ");
            stm.setInt(1, user1.getId());
            stm.setInt(2, user2.getId());
            int res = stm.executeUpdate();
            if (res == 1) return true;
        }catch(SQLException e){
            e.printStackTrace();
        }
        finally{
            pool.releaseConnection(conn);
        }
            return false;

    }

    @Override
    public boolean removeFriendship(User user1, User user2){
        if(!isFriendship(user1,user2)) return false;
        Connection conn = pool.getConnection();
        try{
            PreparedStatement stm;
            stm = conn.prepareStatement("DELETE FROM FRIENDS WHERE (User_ID = ? AND Friend_ID = ? ) OR (Friend_ID = ? AND User_ID = ?) ;");
            stm.setInt(1, user1.getId());
            stm.setInt(2, user2.getId());
            stm.setInt(3, user1.getId());
            stm.setInt(4, user2.getId());
            int res = stm.executeUpdate();
            if(res == 1) return true;
        }catch (SQLException e){
            e.printStackTrace();
        }
        finally{
            pool.releaseConnection(conn);
        }
        return false;
    }

    @Override
    public boolean isFriendship(User user1, User user2){
        return isFriendshipHelper(user1, user2) || isFriendshipHelper(user2, user1);
    }

    private boolean isFriendshipHelper(User user1, User user2){
        Connection conn = pool.getConnection();
        try {
            PreparedStatement stm;

            stm = conn.prepareStatement("SELECT * FROM FRIENDS WHERE (User_ID = ? AND Friend_ID = ? ) ;");
            stm.setInt(1, user1.getId());
            stm.setInt(2, user2.getId());
            ResultSet res = stm.executeQuery();

            if (res.next()) return true;
        }catch (SQLException e){
            e.printStackTrace();
        }
        finally{
            pool.releaseConnection(conn);
        }
            return false;
    }

    @Override
    public List<User> getAllFriends(User user){
        List<User> res = new ArrayList<>();
        Connection conn = pool.getConnection();
        try {
            PreparedStatement stm;

            String query = "SELECT U.ID, U.Username, U.Email, U.Password_hash, U.Is_administrator, U.Date_added  FROM USERS U JOIN FRIENDS F ON U.ID = F.Friend_ID WHERE F.User_ID = ? ;";
            stm = conn.prepareStatement(query);
            stm.setInt(1, user.getId());
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {

                User newUser = new User(rs.getString(2), rs.getString(3), rs.getString(4), rs.getBoolean(5), rs.getTimestamp(6));
                newUser.setId(rs.getInt(1));
                boolean yes = true;
                for(User us : res) {
                    if (us.getId() == newUser.getId()) yes = false;
                }
                if(yes) res.add(newUser);
            }

            String query2 = "SELECT U.ID, U.Username, U.Email, U.Password_hash, U.Is_administrator, U.Date_added  FROM USERS U JOIN FRIENDS F ON U.ID = F.User_ID WHERE F.Friend_ID = ? ;";
            PreparedStatement stm2 = conn.prepareStatement(query2);
            stm2.setInt(1, user.getId());
            ResultSet rs2 = stm2.executeQuery();
            while (rs2.next()) {
                User newUser = new User(rs2.getString(2), rs2.getString(3), rs2.getString(4), rs2.getBoolean(5), rs2.getTimestamp(6));
                newUser.setId(rs2.getInt(1));
                boolean yes = true;
                for(User us : res) {
                    if (us.getId() == newUser.getId()) yes = false;
                }
                if(yes) res.add(newUser);
            }
        }catch (SQLException e){
            e.printStackTrace();
            }
        finally{
            pool.releaseConnection(conn);
        }
            return res;
    }
    @Override
    public boolean addFriendshipRequest(User target, User sender){
        Connection conn = pool.getConnection();
        try {
            PreparedStatement stm;

            stm = conn.prepareStatement("INSERT IGNORE INTO FRIEND_REQS (Reciever_ID, Sender_ID) VALUES (? , ?) ;");
            stm.setInt(1, target.getId());
            stm.setInt(2, sender.getId());
            int res = stm.executeUpdate();

            if (res == 1) return true;
        }catch (SQLException e){
            e.printStackTrace();
        }
        finally{
            pool.releaseConnection(conn);
        }
            return false;
    }

    @Override
    public boolean removeFriendshipRequest(User target, User sender){
        Connection conn = pool.getConnection();
        try {
            PreparedStatement stm;

            stm = conn.prepareStatement("DELETE FROM FRIEND_REQS WHERE (Reciever_ID = ? AND Sender_ID = ? );");
            stm.setInt(1, target.getId());
            stm.setInt(2, sender.getId());
            int res = stm.executeUpdate();
            if (res == 1) return true;
        }catch (SQLException e){
            e.printStackTrace();
        }
        finally{
            pool.releaseConnection(conn);
        }
            return false;

    }

    @Override
    public List<User> getAllFriendshipRequests(User target){
        List<User> res = new ArrayList<>();
        Connection conn = pool.getConnection();
        try {
            PreparedStatement stm;

            String query = "SELECT U.ID, U.Username, U.Email, U.Password_hash, U.Is_administrator, U.Date_added  FROM USERS U JOIN FRIEND_REQS F ON U.ID = F.Sender_ID WHERE F.Reciever_ID = ? ;";
            stm = conn.prepareStatement(query);
            stm.setInt(1, target.getId());
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                User newUser = new User(rs.getString(2), rs.getString(3), rs.getString(4), rs.getBoolean(5), rs.getTimestamp(6));
                newUser.setId(rs.getInt(1));
                res.add(newUser);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        finally{
            pool.releaseConnection(conn);
        }
            return res;
    }

    @Override
    public boolean isInFriendshipRequests(User target, User sender){
        Connection conn = pool.getConnection();
        try {
            PreparedStatement stm;

            stm = conn.prepareStatement("SELECT * FROM FRIEND_REQS WHERE (Reciever_ID = ? AND Sender_ID = ? ) ;");
            stm.setInt(1, target.getId());
            stm.setInt(2, sender.getId());
            ResultSet res = stm.executeQuery();

            if (res.next()) return true;
        }catch(SQLException e){
                e.printStackTrace();
            }
        finally{
            pool.releaseConnection(conn);
        }
            return false;
    }
}

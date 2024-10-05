package dataBase;
import objects.User;
import objects.Review;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import at.favre.lib.crypto.bcrypt.BCrypt;

public class UserDAO implements UserDAOInterface{
    private final ConnectionPool pool;
    public UserDAO(ConnectionPool pool){this.pool = pool;}

    @Override
    public int addUser(User user){
        Connection conn = pool.getConnection();
        int userID = -1;
        try {
            PreparedStatement stm = conn.prepareStatement("INSERT INTO USERS (Username, Email, Password_hash, Is_administrator, Date_added) VALUES (?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            stm.setString(1, user.getUsername());
            stm.setString(2, user.getEmail());
            stm.setString(3, user.getPasswordHash());
            stm.setBoolean(4, user.isAdmin());
            stm.setTimestamp(5, user.getDate());
            int added = stm.executeUpdate();
            if (added != 1){
                throw new SQLException("Inserting user into Users has failed");
            }
            ResultSet rs = stm.getGeneratedKeys();
            if (rs.next()) {
                userID = rs.getInt(1);
                user.setId(userID);
            } else {
                throw new SQLException("Inserting user failed, no ID obtained.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
    }
        finally{
            pool.releaseConnection(conn);
        }
        return userID;
    }


    @Override
    public boolean removeUser(int userID){
        Connection conn = pool.getConnection();
        try {
            PreparedStatement stm = conn.prepareStatement("DELETE FROM USERS WHERE ID = ?;");
            stm.setInt(1, userID);
            int added = stm.executeUpdate();
            if (added == 1) return true;
        }catch(SQLException e){
                e.printStackTrace();
            }
        finally{
            pool.releaseConnection(conn);
        }
        return false;
    }

    @Override
    public User getUserByEmail(String email){
        Connection conn = pool.getConnection();
        try {

            PreparedStatement stm = conn.prepareStatement("SELECT * FROM USERS WHERE Email = ?;");
            stm.setString(1, email);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                String username = rs.getString(2);
                String password_hash = rs.getString(4);
                Boolean is_admin = rs.getBoolean(5);
                Timestamp date_added = rs.getTimestamp(6);
                User currUser = new User(username, email, password_hash, is_admin, date_added);
                currUser.setId(id);
                return currUser;
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        finally{
            pool.releaseConnection(conn);
        }
        return null;
    }

    @Override
    public User getUserByUserId(int ID){
        Connection conn = pool.getConnection();
        try {

            PreparedStatement stm = conn.prepareStatement("SELECT * FROM USERS WHERE ID = ?;");
            stm.setInt(1, ID);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                String username = rs.getString(2);
                String email = rs.getString(3);
                String password_hash = rs.getString(4);
                Boolean is_admin = rs.getBoolean(5);
                Timestamp date_added = rs.getTimestamp(6);

                User currUser = new User(username, email, password_hash, is_admin, date_added);
                currUser.setId(ID);
                return currUser;
            }
        }catch(SQLException e) {
            e.printStackTrace();
        }
        finally{
            pool.releaseConnection(conn);
        }
        return null;
    }
    @Override
    public User getUserByUsername(String username){
        Connection conn = pool.getConnection();
        try {

            PreparedStatement stm = conn.prepareStatement("SELECT * FROM USERS WHERE Username = ?;");
            stm.setString(1, username);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                String email = rs.getString(3);
                String password_hash = rs.getString(4);
                Boolean is_admin = rs.getBoolean(5);
                Timestamp date_added = rs.getTimestamp(6);

                User currUser = new User(username, email, password_hash, is_admin, date_added);
                currUser.setId(rs.getInt(1));
                return currUser;
            }
        }catch(SQLException e) {
            e.printStackTrace();
        }
        finally{
            pool.releaseConnection(conn);
        }
        return null;
    }

    @Override
    public int getIDByUsername(String username){
        Connection conn = pool.getConnection();
        try {
            PreparedStatement stm = conn.prepareStatement("SELECT ID FROM USERS WHERE Username = ?;");
            stm.setString(1, username);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        finally{
            pool.releaseConnection(conn);
        }
        return -1;
    }

    @Override
    public String getUsernameByID(int ID){
        Connection conn = pool.getConnection();
        try {
            PreparedStatement stm = conn.prepareStatement("SELECT Username FROM USERS WHERE ID = ?;");
            stm.setInt(1, ID);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getString("Username");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        finally{
            pool.releaseConnection(conn);
        }
        return null;
    }

    @Override
    public boolean isValidUser(String username, String password) {
        User usr = getUserByUsername(username);
        if(usr == null) {
            return false;
        }

        BCrypt.Verifyer verifier = BCrypt.verifyer();
        BCrypt.Result res = verifier.verify(password.toCharArray(), usr.getPasswordHash().toCharArray());
        return res.verified;
    }

    @Override
    public boolean changePassword(String username, String newPassword) {
        if(getUserByUsername(username) == null) return false;
        BCrypt.Hasher hasher = BCrypt.withDefaults();
        char[] charArray = new char[newPassword.length()];
        for(int i = 0; i < newPassword.length(); i++ ){
            charArray[i] = newPassword.charAt(i);
        }
        String passwordHash = hasher.hashToString(10,charArray);
        Connection conn = pool.getConnection();
        try {

            PreparedStatement stm = conn.prepareStatement("UPDATE USERS SET Password_hash = ? WHERE Username = ? ;");
            stm.setString(1, passwordHash);
            stm.setString(2,username);
            int changed = stm.executeUpdate();
            if(changed == 1) return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            pool.releaseConnection(conn);
        }
        return false;
    }

    @Override
    public boolean changeUsername(User user, String newUsername) {
        Connection conn = pool.getConnection();
        try {

            PreparedStatement stm = conn.prepareStatement("UPDATE USERS SET Username = ? WHERE ID = ? ;");
            stm.setString(1, newUsername);
            stm.setInt(2,user.getId());
            int changed = stm.executeUpdate();
            if(changed == 1) {
                user.setUsername(newUsername);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            pool.releaseConnection(conn);
        }
        return false;
    }
    
    @Override
    public List<User> getAllUsers() {
        List<User> res = new ArrayList<>();
        Connection conn = pool.getConnection();
        try {

            PreparedStatement stm = conn.prepareStatement("SELECT * FROM USERS;");
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
    public boolean makeAdmin(User user){
        if(user.isAdmin()) return false;
        Connection conn = pool.getConnection();
        try {

            PreparedStatement stm;

            stm = conn.prepareStatement("UPDATE USERS SET Is_administrator = TRUE WHERE ID = ?;");
            stm.setInt(1, user.getId());
            int changed = stm.executeUpdate();
            if(changed == 1) {
                user.setAdmin(true);
                return true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        finally{
            pool.releaseConnection(conn);
        }
        return false;
    }

    @Override
    public boolean removeAdmin(User user){
        if(!user.isAdmin()) return false;
        Connection conn = pool.getConnection();
        try {
            PreparedStatement stm;

            stm = conn.prepareStatement("UPDATE USERS SET Is_administrator = FALSE WHERE ID = ?;");
            stm.setInt(1, user.getId());
            int changed = stm.executeUpdate();
            if (changed == 1) {
                user.setAdmin(false);
                return true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        finally{
            pool.releaseConnection(conn);
        }
        return false;
    }

    @Override
    public int addReview(Review review) {
        int reviewID = -1;
        Connection conn = pool.getConnection();
        try {

            PreparedStatement stm = conn.prepareStatement("INSERT INTO REVIEW (User_ID, Quiz_ID, Content, Time_reviewed) VALUES (?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            stm.setInt(1, review.getUser_id());
            stm.setInt(2, review.getQuiz_id());
            stm.setString(3, review.getContent());
            stm.setTimestamp(4, review.getTime());
            int added = stm.executeUpdate();
            if (added != 1){
                throw new SQLException("Inserting review into Review has failed");
            }
            ResultSet rs = stm.getGeneratedKeys();
            if (rs.next()) {
                reviewID = rs.getInt(1);
                review.setId(reviewID);
            } else {
                throw new SQLException("Inserting review failed, no ID obtained.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            pool.releaseConnection(conn);
        }
        return reviewID;
    }

    @Override
    public boolean setRating(int userId, int quizId, double rating) {
        Connection conn = pool.getConnection();
        if(isRated(userId,quizId)) {

            try {
                PreparedStatement stm = conn.prepareStatement("UPDATE RATING SET Rating = ? WHERE User_ID = ? AND Quiz_ID = ? ;");
                stm.setDouble(1,rating);
                stm.setInt(2,userId);
                stm.setInt(3,quizId);
                int changed = stm.executeUpdate();
                if(changed == 1) return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            finally{
                pool.releaseConnection(conn);
            }
        } else {
            try{
                PreparedStatement stm = conn.prepareStatement("INSERT INTO RATING (User_ID, Quiz_ID, Rating) VALUES (?,?,?);", PreparedStatement.RETURN_GENERATED_KEYS);
                stm.setInt(1,userId);
                stm.setInt(2,quizId);
                stm.setDouble(3,rating);
                int added = stm.executeUpdate();
                if(added == 1) return true;
            }catch (SQLException e){
                e.printStackTrace();
            }
            finally{
                pool.releaseConnection(conn);
            }
        }

        return false;
    }

    @Override
    public List<String> getCategories() {
        ArrayList<String> categories = new ArrayList<>();
        Connection conn = pool.getConnection();
        try{

            PreparedStatement stm = conn.prepareStatement("SELECT Category FROM CATEGORY");
            ResultSet rs =  stm.executeQuery();
            while (rs.next()){
                categories.add(rs.getString(1));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        finally{
            pool.releaseConnection(conn);
        }
        return categories;
    }

    @Override
    public boolean addCategory(int id, String category) {
        Connection conn = pool.getConnection();
        try{
            if(getUserByUserId(id).isAdmin()) {
                PreparedStatement stm = conn.prepareStatement("INSERT INTO CATEGORY(User_ID, Category) VALUES (?,?);");
                stm.setInt(1, id);
                stm.setString(2, category);
                int added = stm.executeUpdate();
                if (added == 1) return true;
            }else{
                return false;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        finally{
            pool.releaseConnection(conn);
        }
        return false;
    }


    private boolean isRated(int userId, int quizId) {
        Connection conn = pool.getConnection();
        try{
            PreparedStatement stm = conn.prepareStatement("SELECT Rating FROM RATING WHERE User_ID = ? AND Quiz_ID = ? ;");
            stm.setInt(1,userId);
            stm.setInt(2,quizId);
            ResultSet rs =  stm.executeQuery();
            if(rs.next()) return true;
        }catch (SQLException e){
            e.printStackTrace();
        }
        finally{
            pool.releaseConnection(conn);
        }
        return false;
    }
}

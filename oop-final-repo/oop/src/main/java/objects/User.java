package objects;

import java.sql.Timestamp;

public class User {
    private int id = -1;
    private String username;
    private String passwordHash;
    private String email;
    private boolean isAdmin;
    private Timestamp dateAdded;

    /*   public User(int userId,String username ,String email, String passwordHash,  boolean isAdmin, Timestamp dateAdded) {
           this.id = userId;
           this.username = username;
           this.email = email;
           this.passwordHash = passwordHash;
           this.isAdmin = isAdmin;
           this.dateAdded = dateAdded;
       } */
    public User(String username ,String email, String passwordHash,  boolean isAdmin, Timestamp dateAdded) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.isAdmin = isAdmin;
        this.dateAdded = dateAdded;
    }
    public User(String username ,String email, String passwordHash) {
        this(username,email,passwordHash,false,new Timestamp(new java.util.Date().getTime()));
    }
    public User(String username ,String email, String passwordHash, Boolean isAdmin) {
        this(username,email,passwordHash,isAdmin,new Timestamp(new java.util.Date().getTime()));

    }
    public int getId() {
        return id;
    }
    public void setId(int newID){
        this.id = newID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String newUsername) {
        username = newUsername;
    }

    public String getEmail(){
        return email;
    }
    public String getPasswordHash(){
        return passwordHash;
    }

    public void setPassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean bool) {
        isAdmin = bool;
    }

    public Timestamp getDate(){
        return this.dateAdded;
    }
    public void setDate(Timestamp newDate){
        this.dateAdded = newDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return user.getId() == this.getId();
        // return user.getEmail().equals(this.email) && user.getPasswordHash().equals(this.passwordHash);
    }
}

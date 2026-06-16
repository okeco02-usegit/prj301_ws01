package lehuuhoanganh.utils;

/**
 * Model class representing a Registration record.
 * @author LeHuuHoangAnh
 */
public class User {

    private String userName;
    private String password;
    private String lastName;
    private boolean isAdmin;

    public User() {
        this.userName = null;
        this.password = null;
        this.lastName = null;
        this.isAdmin  = false;
    }

    public User(String userName, String password, String lastName, boolean isAdmin) {
        this.userName = userName;
        this.password = password;
        this.lastName = lastName;
        this.isAdmin  = isAdmin;
    }

    public String getUserName()              { return userName; }
    public void   setUserName(String u)      { this.userName = u; }

    public String getPassword()              { return password; }
    public void   setPassword(String p)      { this.password = p; }

    public String getLastName()              { return lastName; }
    public void   setLastName(String l)      { this.lastName = l; }

    public boolean isAdmin()                 { return isAdmin; }
    public void    setAdmin(boolean a)       { this.isAdmin = a; }

    @Override
    public String toString() {
        return String.format("UserName:%s | Password:%s | LastName:%s | IsAdmin:%b",
                userName, password, lastName, isAdmin);
    }
}

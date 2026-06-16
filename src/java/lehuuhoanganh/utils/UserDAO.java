package lehuuhoanganh.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;


/**
 * Data Access Object for Registration table.
 * Uses JNDI DataSource configured in context.xml.
 * @author LeHuuHoangAnh
 */
public class UserDAO {

    // ----------------------------------------------------------------
    // Get Connection via DataSource (JNDI)
    // ----------------------------------------------------------------
     public static Connection getConnection() throws Exception {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String connectionString = "jdbc:sqlserver://localhost:1433;database=LeHuuHoangAnh_SU26";
            //SQL Server Authentication
            Connection cnn = DriverManager.getConnection(connectionString, "sa", "1602321");
            return cnn;
        } catch (ClassNotFoundException | SQLException ex) {
            throw ex;
        }
    }

    // ----------------------------------------------------------------
    // LOGIN
    // ----------------------------------------------------------------
    /**
     * Returns a User if credentials match, null otherwise.
     */
    public User login(String userName, String password) throws Exception {
        User user = null;
        Connection cnn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cnn = getConnection();
            String sql = "SELECT LastName, IsAdmin FROM Registration "
                       + "WHERE UserName = ? AND Password = ?";
            ps = cnn.prepareStatement(sql);
            ps.setString(1, userName);
            ps.setString(2, password);
            rs = ps.executeQuery();
            if (rs.next()) {
                String lastName = rs.getString("LastName");
                boolean isAdmin = rs.getBoolean("IsAdmin");
                user = new User(userName, password, lastName, isAdmin);
            }
        } finally {
            close(rs, ps, cnn);
        }
        return user;
    }

    // ----------------------------------------------------------------
    // GET ALL USERS
    // ----------------------------------------------------------------
    public List<User> getAllUsers() throws Exception {
        List<User> list = new ArrayList<>();
        Connection cnn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cnn = getConnection();
            String sql = "SELECT UserName, Password, LastName, IsAdmin "
                       + "FROM Registration ORDER BY UserName";
            ps = cnn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new User(
                        rs.getString("UserName"),
                        rs.getString("Password"),
                        rs.getString("LastName"),
                        rs.getBoolean("IsAdmin")
                ));
            }
        } finally {
            close(rs, ps, cnn);
        }
        return list;
    }

    // ----------------------------------------------------------------
    // SEARCH BY LAST NAME (partial, case-insensitive)
    // ----------------------------------------------------------------
    public List<User> searchByLastName(String keyword) throws Exception {
        List<User> list = new ArrayList<>();
        Connection cnn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cnn = getConnection();
            String sql = "SELECT UserName, Password, LastName, IsAdmin "
                       + "FROM Registration "
                       + "WHERE LastName LIKE ? ORDER BY UserName";
            ps = cnn.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new User(
                        rs.getString("UserName"),
                        rs.getString("Password"),
                        rs.getString("LastName"),
                        rs.getBoolean("IsAdmin")
                ));
            }
        } finally {
            close(rs, ps, cnn);
        }
        return list;
    }

    // ----------------------------------------------------------------
    // GET SINGLE USER BY USERNAME
    // ----------------------------------------------------------------
    public User getUserByUserName(String userName) throws Exception {
        User user = null;
        Connection cnn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cnn = getConnection();
            String sql = "SELECT UserName, Password, LastName, IsAdmin "
                       + "FROM Registration WHERE UserName = ?";
            ps = cnn.prepareStatement(sql);
            ps.setString(1, userName);
            rs = ps.executeQuery();
            if (rs.next()) {
                user = new User(
                        rs.getString("UserName"),
                        rs.getString("Password"),
                        rs.getString("LastName"),
                        rs.getBoolean("IsAdmin")
                );
            }
        } finally {
            close(rs, ps, cnn);
        }
        return user;
    }

    // ----------------------------------------------------------------
    // DELETE
    // ----------------------------------------------------------------
    public boolean deleteUser(String userName) throws Exception {
        Connection cnn = null;
        PreparedStatement ps = null;
        try {
            cnn = getConnection();
            String sql = "DELETE FROM Registration WHERE UserName = ?";
            ps = cnn.prepareStatement(sql);
            ps.setString(1, userName);
            return ps.executeUpdate() > 0;
        } finally {
            close(null, ps, cnn);
        }
    }

    // ----------------------------------------------------------------
    // UPDATE
    // ----------------------------------------------------------------
    public boolean updateUser(User user) throws Exception {
        Connection cnn = null;
        PreparedStatement ps = null;
        try {
            cnn = getConnection();
            String sql = "UPDATE Registration "
                       + "SET Password = ?, LastName = ?, IsAdmin = ? "
                       + "WHERE UserName = ?";
            ps = cnn.prepareStatement(sql);
            ps.setString(1, user.getPassword());
            ps.setString(2, user.getLastName());
            ps.setBoolean(3, user.isAdmin());
            ps.setString(4, user.getUserName());
            return ps.executeUpdate() > 0;
        } finally {
            close(null, ps, cnn);
        }
    }

    // ----------------------------------------------------------------
    // Helper: close resources
    // ----------------------------------------------------------------
    private void close(ResultSet rs, PreparedStatement ps, Connection cnn) {
        try { if (rs  != null) rs.close();  } catch (SQLException ignored) {}
        try { if (ps  != null) ps.close();  } catch (SQLException ignored) {}
        try { if (cnn != null) cnn.close(); } catch (SQLException ignored) {}
    }
}

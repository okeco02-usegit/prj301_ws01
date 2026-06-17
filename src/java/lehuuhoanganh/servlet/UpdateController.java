package lehuuhoanganh.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import lehuuhoanganh.utils.UserDAO;
import lehuuhoanganh.utils.User;

/**
 * POST from UpdateUser.html -> validate + save + print result.
 *
 * @author LeHuuHoangAnh
 */
@WebServlet(name = "UpdateController", urlPatterns = {"/UpdateController"})
public class UpdateController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!checkAdmin(request, response)) {
            return;
        }

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String userName = request.getParameter("txtUserName");
        String password = request.getParameter("txtPassword");
        String lastName = request.getParameter("txtLastName");
        String isAdminStr = request.getParameter("chkIsAdmin");

        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Users Management</title></head><body>");
        out.println("<h2>Users Management - Update user</h2>");

        // --- Validation ---
        String errorMsg = null;
        if (userName == null || userName.trim().isEmpty()) {
            errorMsg = "Username is required.";
        } else if (password == null || password.trim().isEmpty()) {
            errorMsg = "Password is required.";
        } else if (password.trim().length() < 3) {
            errorMsg = "Password must be at least 3 characters.";
        } else if (lastName == null || lastName.trim().isEmpty()) {
            errorMsg = "Last name is required.";
        }

        if (errorMsg != null) {
            out.println("<p style='color:red'>" + errorMsg + "</p>");
            out.println("<a href='UpdateUser.html'>Back</a>");
            out.println("</body></html>");
            return;
        }

        try {
            boolean isAdmin = (isAdminStr != null);
            User user = new User(userName.trim(), password.trim(), lastName.trim(), isAdmin);
            UserDAO dao = new UserDAO();

            if (dao.updateUser(user)) {
                // Refresh session if user updated their own record
                HttpSession session = request.getSession(false);
                User loggedUser = (User) session.getAttribute("loggedUser");
                if (loggedUser.getUserName().equalsIgnoreCase(userName.trim())) {
                    session.setAttribute("loggedUser", user);
                }
                out.println("<p>User has been updated successfully</p>");
            } else {
                out.println("<h3>Something went wrong!</h3>");
            }
        } catch (Exception ex) {
            out.println("<p style='color:red'>Error: " + ex.getMessage() + "</p>");
        }

        out.println("<a href='UpdateUser.html'>Update another</a><br/>");
        out.println("<a href='ListController'>View user list</a><br/>");
        out.println("<a href='LoginController'>Back to Menu</a>");
        out.println("</body></html>");
        out.close();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("UpdateUser.html");
    }

    private boolean checkAdmin(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedUser") == null) {
            response.sendRedirect("Login.html");
            return false;
        }
        User u = (User) session.getAttribute("loggedUser");
        if (!u.isAdmin()) {
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<!DOCTYPE html><html><body>");
            out.println("<p style='color:red'>Access denied. Admin role required.</p>");
            out.println("<a href='LoginController'>Back to Menu</a>");
            out.println("</body></html>");
            return false;
        }
        return true;
    }
}

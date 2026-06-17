package lehuuhoanganh.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import lehuuhoanganh.utils.UserDAO;
import lehuuhoanganh.utils.User;

/**
 * POST from DeleteUser.html -> delete user by userName, print result. GET ->
 * called from search result Delete link, delete directly, print result.
 *
 * @author LeHuuHoangAnh
 */
@WebServlet(name = "DeleteController", urlPatterns = {"/DeleteController"})
public class DeleteController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Called from search result table "Delete" link
        if (!checkAdmin(request, response)) {
            return;
        }

        String userName = request.getParameter("userName");
        processDelete(userName, request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Called from DeleteUser.html form
        if (!checkAdmin(request, response)) {
            return;
        }

        String userName = request.getParameter("txtUserName");
        processDelete(userName, request, response);
    }

    private void processDelete(String userName, HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Users Management</title></head><body>");
        out.println("<h2>Users Management - Delete user</h2>");

        // --- Validation ---
        if (userName == null || userName.trim().isEmpty()) {
            out.println("<p style='color:red'>Username is required.</p>");
            out.println("<a href='DeleteUser.html'>Back</a>");
            out.println("</body></html>");
            return;
        }

        // Prevent deleting own account
        HttpSession session = request.getSession(false);
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (userName.trim().equalsIgnoreCase(loggedUser.getUserName())) {
            out.println("<p style='color:red'>You cannot delete your own account.</p>");
            out.println("<a href='DeleteUser.html'>Back</a>");
            out.println("</body></html>");
            return;
        }

        try {
            UserDAO dao = new UserDAO();
            if (dao.deleteUser(userName.trim())) {
                out.println("<p>User has been deleted successfully</p>");
            } else {
                out.println("<h3>Something went wrong!</h3>");
            }
        } catch (Exception ex) {
            out.println("<p style='color:red'>Error: " + ex.getMessage() + "</p>");
        }

        out.println("<a href='DeleteUser.html'>Delete another</a><br/>");
        out.println("<a href='ListController'>View user list</a><br/>");
        out.println("<a href='LoginController'>Back to Menu</a>");
        out.println("</body></html>");
        out.close();
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

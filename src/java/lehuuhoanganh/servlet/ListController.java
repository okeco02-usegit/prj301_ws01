package lehuuhoanganh.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import lehuuhoanganh.utils.UserDAO;
import lehuuhoanganh.utils.User;

/**
 * Shows full user list (admin only).
 * @author LeHuuHoangAnh
 */
@WebServlet(name = "ListController", urlPatterns = {"/ListController"})
public class ListController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!checkAdmin(request, response)) return;

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>User List</title></head><body>");
        out.println("<h2>View User List</h2>");

        // Success message from delete/update redirect
        String successMsg = request.getParameter("successMsg");
        if (successMsg != null && !successMsg.isEmpty()) {
            out.println("<p style='color:green'>" + successMsg + "</p>");
        }

        try {
            UserDAO dao = new UserDAO();
            List<User> userList = dao.getAllUsers();

            for (User u : userList) {
                out.println(u + "<br/>");
            }
        } catch (Exception ex) {
            out.println("<p style='color:red'>Error: " + ex.getMessage() + "</p>");
        }

        out.println("<br/><a href='UpdateUser.html'>Update user</a><br/>");
        out.println("<a href='DeleteUser.html'>Delete user</a><br/>");
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
            out.println("<a href='LoginController'>Back</a>");
            out.println("</body></html>");
            return false;
        }
        return true;
    }
}

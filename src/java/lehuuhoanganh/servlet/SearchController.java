package lehuuhoanganh.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import lehuuhoanganh.utils.UserDAO;
import lehuuhoanganh.utils.User;

@WebServlet(name = "SearchController", urlPatterns = {"/SearchController"})
public class SearchController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!checkLogin(request, response)) {
            return;
        }
        response.sendRedirect("SearchUser.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!checkLogin(request, response)) {
            return;
        }

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String keyword = request.getParameter("txtKeyword");

        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Search Result</title></head><body>");
        out.println("<h2>Search Result</h2>");

        // --- Validation ---
        if (keyword == null || keyword.trim().isEmpty()) {
            out.println("<p style='color:red'>Please enter a search keyword.</p>");
            out.println("<a href='SearchUser.html'>Back</a>");
            out.println("</body></html>");
            return;
        }

        out.println("<p>Your search value is: <b>" + keyword.trim() + "</b></p>");

        try {
            UserDAO dao = new UserDAO();
            List<User> results = dao.searchByLastName(keyword.trim());

            HttpSession session = request.getSession(false);
            User loggedUser = (User) session.getAttribute("loggedUser");

            if (results == null || results.isEmpty()) {
                out.println("<p>No users found.</p>");
            } else {
                out.println("<table border='1'>");
                out.println("<thead><tr>");
                out.println("<th>No.</th><th>Username</th><th>Password</th><th>Lastname</th><th>Role</th>");
                if (loggedUser.isAdmin()) {
                    out.println("<th></th>");
                }
                out.println("</tr></thead><tbody>");

                int count = 0;
                for (User u : results) {
                    out.println("<tr>");
                    out.println("<td>" + (++count) + ".</td>");
                    out.println("<td>" + u.getUserName() + "</td>");
                    out.println("<td>" + u.getPassword() + "</td>");
                    out.println("<td>" + u.getLastName() + "</td>");
                    out.println("<td>" + u.isAdmin() + "</td>");
                    if (loggedUser.isAdmin()) {
                        out.println("<td><a href='DeleteController?userName=" + u.getUserName()
                                + "'>Delete</a></td>");
                    }
                    out.println("</tr>");
                }
                out.println("</tbody></table>");
            }
        } catch (Exception ex) {
            out.println("<p style='color:red'>Error: " + ex.getMessage() + "</p>");
        }

        out.println("<br/><a href='SearchUser.html'>Back</a>");
        out.println("</body></html>");

        out.close();
    }

    private boolean checkLogin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedUser") == null) {
            response.sendRedirect("Login.html");
            return false;
        }
        return true;
    }
}

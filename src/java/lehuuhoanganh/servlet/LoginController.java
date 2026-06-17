package lehuuhoanganh.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import lehuuhoanganh.utils.UserDAO;
import lehuuhoanganh.utils.User;

/*
 * @author LeHuuHoangAnh
 */
@WebServlet(name = "LoginController", urlPatterns = {"/LoginController"})
public class LoginController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String userName = request.getParameter("txtUserName");
        String password = request.getParameter("txtPassword");

        // ── Case 1: server-side blank check (backup for JS disabled) ─
        if (userName == null || userName.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            sendError(response, "Username and password cannot be blank.");
            return;
        }

        try {
            UserDAO dao = new UserDAO();
            User user = dao.login(userName.trim(), password.trim());
            if (user == null) {
                sendError(response, "Invalid username or password.");
                return;
            }

            // ── Case 3: correct but isAdmin = 0 ─────────────────────
            if (!user.isAdmin()) {
                sendError(response, "You do not have permission to access this system.");
                return;
            }

            // ── Case 4: correct + admin → save session, go welcome ───
            HttpSession session = request.getSession();
            session.setAttribute("loggedUser", user);

            // Cookie: remember username 7 days
            Cookie userCookie = new Cookie("rememberedUser", user.getUserName());
            userCookie.setMaxAge(7 * 24 * 60 * 60);
            userCookie.setPath(request.getContextPath());
            response.addCookie(userCookie);

            // Print welcome page
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<!DOCTYPE html>");
            out.println("<html><head><title>Login Servlet</title></head><body>");
            out.println("<p>Welcome to " + user.getLastName() + " !!!</p>");
            out.println("<p>You are logged successfully in with administrator role.</p>");
            out.println("<a href='SearchController'>Search user</a><br/>");
            out.println("<a href='ListController'>View user list</a><br/>");
            out.println("<a href='UpdateUser.html'>Update user</a><br/>");
            out.println("<a href='DeleteUser.html'>Delete user</a><br/>");
            out.println("<br/><a href='LogoutController'>Logout</a>");
            out.println("</body></html>");
            out.close();

        } catch (Exception ex) {
            sendError(response, "System error: " + ex.getMessage());
        }
    }

   @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Kiểm tra xem người dùng đã đăng nhập chưa (đã có session chưa)
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loggedUser") != null) {
            User user = (User) session.getAttribute("loggedUser");
            
            // Nếu đã đăng nhập, in lại giao diện Menu chính (Welcome Page)
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<!DOCTYPE html>");
            out.println("<html><head><title>Main Menu</title></head><body>");
            out.println("<p>Welcome back, " + user.getLastName() + " !!!</p>");
            out.println("<p>You are logged successfully in with administrator role.</p>");
            out.println("<a href='SearchController'>Search user</a><br/>");
            out.println("<a href='ListController'>View user list</a><br/>");
            out.println("<a href='UpdateUser.html'>Update user</a><br/>");
            out.println("<a href='DeleteUser.html'>Delete user</a><br/>");
            out.println("<br/><a href='LogoutController'>Logout</a>");
            out.println("</body></html>");
            out.close();
        } else {
            // Nếu chưa đăng nhập hoặc session đã hết hạn, chuyển về Login.html
            response.sendRedirect("Login.html");
        }
    }

    // ── Helper: redirect back to Login.html with error message ───────
    private void sendError(HttpServletResponse response, String msg) throws IOException {
        String encoded = URLEncoder.encode(msg, "UTF-8");
        response.sendRedirect("Login.html?error=" + encoded);
    }
}

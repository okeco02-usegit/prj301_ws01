package lehuuhoanganh.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/**
 * Invalidates session, clears cookie, redirects to Login.html.
 * @author LeHuuHoangAnh
 */
@WebServlet(name = "LogoutController", urlPatterns = {"/LogoutController"})
public class LogoutController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Invalidate session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Clear cookie
        Cookie userCookie = new Cookie("rememberedUser", "");
        userCookie.setMaxAge(0);
        userCookie.setPath(request.getContextPath());
        response.addCookie(userCookie);

        response.sendRedirect("Login.html");
    }
}

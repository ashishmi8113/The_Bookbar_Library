package com.library.servlet;

import com.library.dao.UserDAO;
import com.library.model.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/admin/login")
public class AdminLoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        try {
            User u = new UserDAO().findByEmailAndPassword(email, password);
            if (u != null && "ADMIN".equalsIgnoreCase(u.getRole())) {
                HttpSession s = req.getSession();
                s.setAttribute("uid", u.getId());
                s.setAttribute("uname", u.getName());
                s.setAttribute("role", "ADMIN");
                resp.sendRedirect("../admin-home.html");
            } else {
                resp.setContentType("text/html");
                resp.getWriter().println("<h3>Not an admin. <a href='../admin-login.html'>Back</a></h3>");
            }
        } catch (Exception e) {
            resp.setContentType("text/html");
            resp.getWriter().println(e.getMessage());
        }
    }
}

package com.library.servlet;

import com.library.dao.UserDAO;
import com.library.model.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        resp.setContentType("text/html");
        try (PrintWriter out = resp.getWriter()) {
            User u = new UserDAO().findByEmailAndPassword(email, password);
            if (u != null) {
                HttpSession s = req.getSession();
                s.setAttribute("uid", u.getId());
                s.setAttribute("uname", u.getName());
                s.setAttribute("role", u.getRole());
                if ("ADMIN".equalsIgnoreCase(u.getRole())) resp.sendRedirect("admin-home.html");
                else resp.sendRedirect("user-home.html");
            } else {
                out.println("<h3>Invalid credentials. <a href='login.html'>Try again</a></h3>");
            }
        } catch (Exception e) {
            resp.getWriter().println(e.getMessage());
        }
    }
}

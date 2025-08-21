package com.library.servlet;

import com.library.dao.UserDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        System.out.println("Inside RegisterServlet");
        resp.setContentType("text/html");
        try (PrintWriter out = resp.getWriter()) {
            UserDAO userdao = new UserDAO();
            System.out.println("before boolean");
            boolean ok=userdao.create(name, email, password, "USER");
            System.out.println("after boolean");
            if (ok) {
                out.println("<h3>Registered. <a href='login.html'>Login</a></h3>");
            }

            else {
                out.println("<h3>Failed to register.</h3>");
            }
        } catch (Exception e) {
            resp.getWriter().println(e.getMessage());
        }
        System.out.println("End RegisterServlet");
    }
}

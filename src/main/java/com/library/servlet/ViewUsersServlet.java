package com.library.servlet;

import com.library.dao.BorrowDAO;
import com.library.dao.UserDAO;
import com.library.model.BorrowRecord;
import com.library.model.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/admin/users")
public class ViewUsersServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession s = req.getSession(false);
        if (s == null || !"ADMIN".equals(s.getAttribute("role"))) { resp.sendRedirect("../admin-login.html"); return; }

        resp.setContentType("text/html");
        try (PrintWriter out = resp.getWriter()) {
            List<User> users = new UserDAO().findAll();
            List<BorrowRecord> records = new BorrowDAO().listAll();

            out.println("<!doctype html><html><head><meta charset='utf-8'><title>Users</title>" +
                    "</head><body>");
            out.println("<a href='dashboard'>Dashboard</a> | <a href='../logout'>Logout</a>");
            out.println("<h2>Users</h2><table border='1'><tr><th>ID</th><th>Name</th><th>Email</th><th>Role</th></tr>");
            for (User u : users) {
                out.println("<tr><td>"+u.getId()+"</td><td>"+u.getName()+"</td><td>"+u.getEmail()+"</td><td>"+u.getRole()+"</td></tr>");
            }
            out.println("</table>");

            out.println("<h2>Borrowed Books</h2><table border='1'><tr><th>ID</th><th>User</th><th>Book</th><th>Borrowed</th><th>Returned</th></tr>");
            for (BorrowRecord br : records) {
                out.println("<tr><td>"+br.getId()+"</td><td>"+br.getUserName()+"</td><td>"+br.getBookTitle()+"</td><td>"+br.getBorrowDate()+"</td><td>"+(br.getReturnDate()==null?"":br.getReturnDate())+"</td></tr>");
            }
            out.println("</table></body></html>");
        } catch (Exception e) {
            resp.getWriter().println(e.getMessage());
        }
    }
}

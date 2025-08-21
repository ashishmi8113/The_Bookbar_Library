package com.library.servlet;

import com.library.dao.BookDAO;
import com.library.model.Book;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/admin/dashboard")
public class ManageBooksServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession s = req.getSession(false);
        if (s == null || !"ADMIN".equals(s.getAttribute("role"))) { resp.sendRedirect("../admin-login.html"); return; }

        resp.setContentType("text/html");
        try (PrintWriter out = resp.getWriter()) {
            List<Book> books = new BookDAO().findAll();
            out.println("<!doctype html><html><head><meta charset='utf-8'><title>Admin</title>" +
                    "</head><body>");
            out.println("<a href='../admin-home.html'>Home</a> | <a href='../logout'>Logout</a>");
            out.println("<h2>Manage Books</h2>");

            out.println("<form method='post' action='dashboard'>");
            out.println("Title:<input name='title' required/> ");
            out.println("Author:<input name='author'/> ");
            out.println("Category:<input name='category'/> ");
            out.println("Available:<input type='checkbox' name='available' checked/> ");
            out.println("<input type='hidden' name='action' value='add'/>");
            out.println("<button type='submit'>Add</button></form>");

            out.println("<table border='1'><tr><th>ID</th><th>Title</th><th>Author</th><th>Category</th><th>Available</th><th>Actions</th></tr>");
            for (Book b : books) {
                out.println("<tr>");
                out.println("<td>" + b.getId() + "</td>");
                out.println("<td>" + esc(b.getTitle()) + "</td>");
                out.println("<td>" + esc(b.getAuthor()) + "</td>");
                out.println("<td>" + esc(b.getCategory()) + "</td>");
                out.println("<td>" + (b.isAvailable() ? "Yes" : "No") + "</td>");
                out.println("<td>");
                out.println("<form style='display:inline' method='post' action='dashboard'>");
                out.println("<input type='hidden' name='action' value='delete'/>");
                out.println("<input type='hidden' name='id' value='" + b.getId() + "'/>");
                out.println("<button type='submit'>Delete</button></form> ");
                out.println("<form style='display:inline' method='post' action='dashboard'>");
                out.println("<input type='hidden' name='action' value='update'/>");
                out.println("<input type='hidden' name='id' value='" + b.getId() + "'/>");
                out.println("Title:<input name='title' value='" + esc(b.getTitle()) + "'/> ");
                out.println("Author:<input name='author' value='" + esc(b.getAuthor()) + "'/> ");
                out.println("Category:<input name='category' value='" + esc(b.getCategory()) + "'/> ");
                out.println("Avl:<input type='checkbox' name='available' " + (b.isAvailable() ? "checked" : "") + "/> ");
                out.println("<button type='submit'>Update</button></form>");
                out.println("</td></tr>");
            }
            out.println("</table></body></html>");
        } catch (Exception e) {
            resp.getWriter().println(e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession s = req.getSession(false);
        if (s == null || !"ADMIN".equals(s.getAttribute("role"))) { resp.sendRedirect("../admin-login.html"); return; }

        String action = req.getParameter("action");
        try {
            BookDAO dao = new BookDAO();
            if ("add".equals(action)) {
                String title = req.getParameter("title");
                String author = req.getParameter("author");
                String category = req.getParameter("category");
                boolean av = req.getParameter("available") != null;
                dao.add(title, author, category, av);
            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                dao.delete(id);
            } else if ("update".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                String title = req.getParameter("title");
                String author = req.getParameter("author");
                String category = req.getParameter("category");
                boolean av = req.getParameter("available") != null;
                dao.update(id, title, author, category, av);
            }
            resp.sendRedirect("dashboard");
        } catch (Exception e) {
            resp.setContentType("text/html");
            resp.getWriter().println(e.getMessage());
        }
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;");
    }
}

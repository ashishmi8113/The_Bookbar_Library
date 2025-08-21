package com.library.servlet;

import com.library.dao.BookDAO;
import com.library.dao.BorrowDAO;
import com.library.model.Book;
import com.library.model.BorrowRecord;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@WebServlet("/books/search")
public class BookSearchServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession s = req.getSession(false);
        if (s == null || s.getAttribute("uid") == null) { 
            resp.sendRedirect("../login.html"); 
            return; 
        }
        int uid = (int) s.getAttribute("uid");

        String q = req.getParameter("q");
        resp.setContentType("text/html");

        try (PrintWriter out = resp.getWriter()) {
            List<Book> books = (q == null || q.isBlank())
                    ? new BookDAO().findAll()
                    : new BookDAO().search(q);

            List<BorrowRecord> myRecords = new BorrowDAO().listByUser(uid);
            Set<Integer> borrowedIds = new HashSet<>();
            for (BorrowRecord br : myRecords) 
                if (br.getReturnDate() == null) borrowedIds.add(br.getBookId());

            out.println("<!doctype html><html><head><meta charset='utf-8'><title>Search Books</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; background:#f7f9fc; margin:0; padding:0; }");
            out.println("header { background:#2c3e50; color:#fff; padding:15px; text-align:center; }");
            out.println("header a { color:#f1c40f; margin:0 15px; text-decoration:none; font-weight:bold; }");
            out.println(".container { width:90%; max-width:1000px; margin:20px auto; }");
            out.println("form.search-bar { text-align:center; margin-bottom:20px; }");
            out.println("form.search-bar input { padding:10px; width:250px; border-radius:6px; border:1px solid #ccc; }");
            out.println("form.search-bar button { padding:10px 15px; background:#3498db; color:#fff; border:none; border-radius:6px; cursor:pointer; }");
            out.println("form.search-bar button:hover { background:#2980b9; }");
            out.println(".grid { display:grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap:20px; }");
            out.println(".card { background:#fff; padding:15px; border-radius:10px; box-shadow:0 2px 8px rgba(0,0,0,0.1); text-align:center; }");
            out.println(".card img { width:90px; height:120px; object-fit:cover; margin-bottom:10px; }");
            out.println(".card h3 { margin:8px 0; font-size:16px; color:#2c3e50; }");
            out.println(".card p { margin:4px 0; font-size:14px; color:#555; }");
            out.println(".card button { margin-top:8px; padding:6px 12px; background:#27ae60; color:#fff; border:none; border-radius:6px; cursor:pointer; font-size:14px; }");
            out.println(".card button:hover { background:#1e8449; }");
            out.println(".card span { color:#e74c3c; font-weight:bold; }");
            out.println("</style>");
            out.println("<script src='../js/covers.js' defer></script>");
            out.println("</head><body>");

            out.println("<header><h1>📚 Library Search</h1>");
            out.println("<a href='../user-home.html'>🏠 Home</a> | <a href='../logout'>🚪 Logout</a></header>");

            out.println("<div class='container'>");
            out.println("<form class='search-bar' method='get' action='search'>");
            out.println("<input name='q' placeholder='Search by title or author' value='" + esc(q) + "'/>");
            out.println("<button type='submit'>🔍 Search</button>");
            out.println("</form>");

            out.println("<div class='grid'>");
            for (Book b : books) {
                boolean iBorrowed = borrowedIds.contains(b.getId());
                out.println("<div class='card' data-title='" + esc(b.getTitle()) + "'>");
                out.println("<img class='cover' src='../images/default-cover.png' alt='cover'/>");
                out.println("<h3>" + esc(b.getTitle()) + "</h3>");
                out.println("<p>👨 " + esc(b.getAuthor()) + "</p>");
                out.println("<p>📂 " + esc(b.getCategory()) + "</p>");
                if (iBorrowed) {
                    out.println("<form method='post' action='../books/return'>" +
                            "<input type='hidden' name='bookId' value='" + b.getId() + "'/>" +
                            "<button type='submit'>Return</button></form>");
                } else if (b.isAvailable()) {
                    out.println("<form method='post' action='../books/borrow'>" +
                            "<input type='hidden' name='bookId' value='" + b.getId() + "'/>" +
                            "<button type='submit'>Borrow</button></form>");
                } else {
                    out.println("<span>❌ Not available</span>");
                }
                out.println("</div>");
            }
            out.println("</div></div></body></html>");
        } catch (Exception e) {
            resp.getWriter().println(e.getMessage());
        }
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;");
    }
}

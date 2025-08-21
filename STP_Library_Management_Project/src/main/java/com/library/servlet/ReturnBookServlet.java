package com.library.servlet;

import com.library.dao.BorrowDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/books/return")
public class ReturnBookServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession s = req.getSession(false);
        if (s == null || s.getAttribute("uid") == null) { resp.sendRedirect("../login.html"); return; }
        int uid = (int) s.getAttribute("uid");
        int bookId = Integer.parseInt(req.getParameter("bookId"));
        try {
            new BorrowDAO().returnBook(uid, bookId);
            resp.sendRedirect("search");
        } catch (Exception e) {
            resp.setContentType("text/html");
            resp.getWriter().println(e.getMessage());
        }
    }
}

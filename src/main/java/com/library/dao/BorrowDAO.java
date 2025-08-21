package com.library.dao;

import com.library.config.DBUtil;
import com.library.model.BorrowRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowDAO {

    public boolean borrow(int userId, int bookId) throws SQLException {
        String check = "SELECT available FROM books WHERE id=?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps1 = con.prepareStatement(check)) {
            ps1.setInt(1, bookId);
            try (ResultSet rs = ps1.executeQuery()) {
                if (rs.next() && rs.getBoolean(1)) {
                    try (PreparedStatement ps2 = con.prepareStatement(
                            "INSERT INTO borrowed_books(user_id,book_id,borrow_date) VALUES(?,?,CURRENT_DATE())");
                         PreparedStatement ps3 = con.prepareStatement(
                                 "UPDATE books SET available=false WHERE id=?")) {
                        ps2.setInt(1, userId);
                        ps2.setInt(2, bookId);
                        ps2.executeUpdate();
                        ps3.setInt(1, bookId);
                        ps3.executeUpdate();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean returnBook(int userId, int bookId) throws SQLException {
        String find = "SELECT id FROM borrowed_books WHERE user_id=? AND book_id=? AND return_date IS NULL ORDER BY borrow_date DESC LIMIT 1";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps1 = con.prepareStatement(find)) {
            ps1.setInt(1, userId);
            ps1.setInt(2, bookId);
            try (ResultSet rs = ps1.executeQuery()) {
                if (rs.next()) {
                    int borrowId = rs.getInt(1);
                    try (PreparedStatement ps2 = con.prepareStatement("UPDATE borrowed_books SET return_date=CURRENT_DATE() WHERE id=?");
                         PreparedStatement ps3 = con.prepareStatement("UPDATE books SET available=true WHERE id=?")) {
                        ps2.setInt(1, borrowId);
                        ps2.executeUpdate();
                        ps3.setInt(1, bookId);
                        ps3.executeUpdate();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public List<BorrowRecord> listByUser(int userId) throws SQLException {
        List<BorrowRecord> list = new ArrayList<>();
        String sql = """
            SELECT b.id,u.name,k.title,b.borrow_date,b.return_date,k.id AS book_id
            FROM borrowed_books b
            JOIN users u ON b.user_id=u.id
            JOIN books k ON b.book_id=k.id
            WHERE u.id=? ORDER BY b.borrow_date DESC
        """;
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BorrowRecord br = new BorrowRecord();
                    br.setId(rs.getInt("id"));
                    br.setUserId(userId);
                    br.setBookId(rs.getInt("book_id"));
                    br.setUserName(rs.getString("name"));
                    br.setBookTitle(rs.getString("title"));
                    br.setBorrowDate(rs.getDate("borrow_date"));
                    br.setReturnDate(rs.getDate("return_date"));
                    list.add(br);
                }
            }
        }
        return list;
    }

    public List<BorrowRecord> listAll() throws SQLException {
        List<BorrowRecord> list = new ArrayList<>();
        String sql = """
            SELECT b.id,u.name,k.title,b.borrow_date,b.return_date,u.id AS user_id,k.id AS book_id
            FROM borrowed_books b
            JOIN users u ON b.user_id=u.id
            JOIN books k ON b.book_id=k.id
            ORDER BY b.borrow_date DESC
        """;
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                BorrowRecord br = new BorrowRecord();
                br.setId(rs.getInt("id"));
                br.setUserId(rs.getInt("user_id"));
                br.setBookId(rs.getInt("book_id"));
                br.setUserName(rs.getString("name"));
                br.setBookTitle(rs.getString("title"));
                br.setBorrowDate(rs.getDate("borrow_date"));
                br.setReturnDate(rs.getDate("return_date"));
                list.add(br);
            }
        }
        return list;
    }
}

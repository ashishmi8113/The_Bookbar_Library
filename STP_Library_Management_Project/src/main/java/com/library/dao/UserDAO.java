package com.library.dao;

import com.library.config.DBUtil;
import com.library.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public boolean create(String name, String email, String password, String role) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO users(name,email,password,role) VALUES(?,?,?,?)";

        System.out.println("before connection");
        Connection con = DBUtil.getConnection();
        System.out.println("after connection");
        PreparedStatement ps = con.prepareStatement(sql);
        try {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, role);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return ps.executeUpdate() > 0;
    }

    public User findByEmailAndPassword(String email, String password) throws SQLException {
        String sql = "SELECT id,name,email,role FROM users WHERE email=? AND password=?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("role")
                    );
                }
            }
        }
        return null;
    }

    public List<User> findAll() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id,name,email,role FROM users";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("role")
                ));
            }
        }
        return list;
    }
}

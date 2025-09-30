package com.employee.app;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

  
    public List<Employee> listAll() throws SQLException { 

     
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT id, name, email, department FROM employees ORDER BY id DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Employee(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("department")
                ));
            }
        }
        return list;
    }

    public int add(Employee e) throws SQLException {
        String sql = "INSERT INTO employees (name, email, department) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getName());
            ps.setString(2, e.getEmail());
            ps.setString(3, e.getDepartment());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Creating employee failed, no rows affected.");
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Creating employee failed, no ID obtained.");
    }

    public boolean deleteById(int id) throws SQLException {
        String sql = "DELETE FROM employees WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}

package com.example.demo.repository;

import org.springframework.stereotype.Repository;
import com.example.demo.infrastructure.DatabaseConnector;
// Repository chịu trách nhiệm tương tác với bảng "customers" trong cơ sở dữ liệu MySQL.

@Repository
public class CustomerRepo {
    private final DatabaseConnector databaseConnector = new DatabaseConnector();

    // Thêm các phương thức tương tác với bảng "customers" ở đây
    public Customer findById(int id) {
       try (var conn = databaseConnector.getConnection()) {
            String sql = "SELECT * FROM customers WHERE id = ?";
            try (var preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.setInt(1, id);
                try (var resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        Customer customer = new Customer();
                        customer.setId(resultSet.getInt("id"));
                        customer.setName(resultSet.getString("name"));
                        // Thiết lập các thuộc tính khác của khách hàng
                        return customer;
                    }
                }
            }
       } catch (Exception e) {
        // TODO: handle exception
       }
    }
}

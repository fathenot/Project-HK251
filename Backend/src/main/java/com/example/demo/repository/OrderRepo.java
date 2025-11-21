package com.example.demo.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.springframework.stereotype.Repository;
import com.example.demo.infrastructure.*;

// Repository chịu trách nhiệm tương tác với bảng "orders" trong cơ sở dữ liệu MySQL.
// ở đây làm tạm một phương thức ví dụ findById để tìm đơn hàng theo ID
// có thể thêm các phương thức khác như save, update, delete, findAll, v.v.
@Repository
public class OrderRepo {
    private final DatabaseConnector databaseConnector = new DatabaseConnector();

    public Order findById(int id) {
        Connection connection = null;
        try {
            connection = databaseConnector.getConnection();
            // Thực hiện các thao tác với cơ sở dữ liệu ở đây
            String sql = "SELECT * FROM orders WHERE id = ?";
            // Chuẩn bị câu lệnh, thực thi truy vấn, xử lý kết quả, v.v.
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            // Thực thi và xử lý kết quả
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Order order = new Order();
                order.setId(resultSet.getInt("id"));
                order.setCustomerName(resultSet.getString("customer_name"));
                // Thiết lập các thuộc tính khác của đơn hàng
                return order;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            databaseConnector.closeConnection(connection);
        }
    }

}

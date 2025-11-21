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
    final DatabaseConnector dbConnector = new DatabaseConnector();
}

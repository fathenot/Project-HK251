package com.example.demo.infrastructure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnector chịu trách nhiệm quản lý kết nối tới MySQL.
 * 
 * Đây là ví dụ tối giản, phù hợp với môi trường local và mục đích hướng dẫn
 * cấu trúc dự án. Khi vào dự án thật, bạn có thể thay thế lớp này bằng
 * DataSource hoặc Spring Boot configuration.
 */
public class DatabaseConnector {

    private static final String URL = "jdbc:mysql://localhost:3306/superrmarket_db?useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "root"; // thay thế bằng tên đăng nhập của bạn
    private static final String PASSWORD = "your_password";// thay thế bằng mật khẩu của bạn lưu ý do là hardcode và deadline gấp nên tạm thời để vậy

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // đăng ký driver MySQL
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Không tìm thấy MySQL JDBC Driver", e);
        }
    }

    /**
     * Tạo một connection mới tới MySQL.
     * 
     * @return Connection nếu thành công
     * @throws SQLException nếu không thể kết nối
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    /**
     * Đóng connection an toàn, tránh rò rỉ tài nguyên.
     */
    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignored) {}
        }
    }
}

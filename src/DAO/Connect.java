package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Connect {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydb_udp";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private static Connection connection;
    private static PreparedStatement preparedStatement;

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void closeResources() {
        try {
            if (preparedStatement != null)
                preparedStatement.close();
            if (connection != null)
                connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean login(String username, String password) {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            if (connection != null) {
                System.out.println("Connected to the database successfully!");
            }

            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                System.out.println("Login successful!");
                return true;
            } else {
                System.out.println("Invalid username or password!");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return false;
    }

    public static List<Status> getUserStatus() {
        List<Status> statusList = new ArrayList<>();

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            if (connection != null) {
                System.out.println("Connected to the database successfully!");
            }

            String query = "SELECT * FROM status";
            preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Status status = new Status();
                status.setUsername(resultSet.getString("username"));
                status.setTime(resultSet.getString("time"));
                status.setIp(resultSet.getString("ip"));
                status.setHost(resultSet.getInt("host"));

                statusList.add(status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }

        System.err.println(statusList);
        return statusList;
    }

    public static boolean addUser(String username, String time, String ip, int host) {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            if (connection != null) {
                System.out.println("Connected to the database successfully!");
            }

            String query = "INSERT INTO status (username, time, ip, host) VALUES (?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, time);
            preparedStatement.setString(3, ip);
            preparedStatement.setInt(4, host);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("User added successfully!");
                return true;
            } else {
                System.out.println("Failed to add user!");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return false;
    }
}

package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {
    private static final String db_name = "hospital-management";
    private static final String username = "root";
    private static final String password = "root";
    private static final String db_host = "localhost";
    private static final String db_port = "8889";

    private Connection connection;

    public DBManager() {
        String db_url = "jdbc:mysql://" + this.db_host + ":" + this.db_port + "/" + this.db_name;

        try {
            connection = DriverManager.getConnection(db_url, username, password);
            System.out.println("Connection established.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}


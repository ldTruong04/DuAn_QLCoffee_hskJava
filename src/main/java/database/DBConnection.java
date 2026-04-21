package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection manager for PostgreSQL
 */
public class DBConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/coffee_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    static {
        try {
            // Load PostgreSQL driver
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL driver not found", e);
        }
    }

    /**
     * Get a database connection to PostgreSQL
     * @return Connection to the database
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
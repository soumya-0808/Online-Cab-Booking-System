package com.dal.cabby.dbHelper;

import java.sql.*;

/**
 * This class is database layer for our project.
 */
public class DBHelper implements IPersistence {
    private static Connection connection;
    private static Statement statement;
    private static DBHelper dbHelper;
    private final String DB_HOST = "localhost";
    private final String DEFAULT_MYSQL_USERNAME = "root";
    private final String DEFAULT_MYSQL_PASSWORD = "";
    private final String DEFAULT_MYSQL_DATABASE = "cabby";
    String database;
    String user;
    String password;
    String connUrl;
    String url = "jdbc:mysql://%s:3306/%s?useSSL=false&allowPublicKeyRetrieval=true";
    
    // Maximum number of connection attempts
    private static final int MAX_CONN_ATTEMPTS = 3;

    static {
        // Explicitly load MySQL driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: MySQL JDBC Driver not found!");
            System.err.println("Please ensure mysql-connector-java is in your classpath");
            System.err.println("You can download it from: https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.25/mysql-connector-java-8.0.25.jar");
            e.printStackTrace();
        }
    }

    private DBHelper() {
        this.database = DEFAULT_MYSQL_DATABASE;
        this.user = DEFAULT_MYSQL_USERNAME;
        this.password = DEFAULT_MYSQL_PASSWORD;
        connUrl = String.format(url, DB_HOST, this.database);
    }

    public static DBHelper getInstance() throws SQLException {
        if (dbHelper == null) {
            dbHelper = new DBHelper();
            try {
                dbHelper.initialize();
            } catch (SQLException e) {
                System.err.println("Database connection error: " + e.getMessage());
                System.err.println("Make sure MySQL is running and database 'cabby' exists");
                System.err.println("Connection URL: " + dbHelper.connUrl);
                System.err.println("Username: " + dbHelper.user);
                
                // Print detailed troubleshooting steps
                System.err.println("\nTROUBLESHOOTING STEPS:");
                System.err.println("1. Ensure MySQL/XAMPP is running");
                System.err.println("2. Check if database 'cabby' exists");
                System.err.println("3. Verify username and password in DBHelper.java");
                System.err.println("4. Make sure the MySQL connector JAR is in the classpath");
                
                throw e;
            }
        }
        return dbHelper;
    }

    private void initialize() throws SQLException {
        if (connection == null) {
            int attempts = 0;
            SQLException lastException = null;
            
            // Try to connect multiple times
            while (attempts < MAX_CONN_ATTEMPTS) {
                try {
                    attempts++;
                    System.out.println("Attempting to connect to database (Attempt " + attempts + "/" + MAX_CONN_ATTEMPTS + ")...");
                    connection = DriverManager.getConnection(connUrl, user, password);
                    System.out.println("Connected to database successfully!");
                    break; // Connection successful, exit the loop
                } catch (SQLException e) {
                    lastException = e;
                    System.err.println("Connection attempt " + attempts + " failed: " + e.getMessage());
                    
                    if (attempts < MAX_CONN_ATTEMPTS) {
                        System.out.println("Retrying in 2 seconds...");
                        try {
                            Thread.sleep(2000); // Wait before retrying
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
            
            // If all attempts failed, throw the last exception
            if (connection == null) {
                throw lastException;
            }
        }
        
        if (statement == null) {
            statement = connection.createStatement();
        }
    }

    @Override
    public void close() throws SQLException {
        if (statement != null) {
            statement.close();
            statement = null;
        }
        if (connection != null) {
            connection.close();
            connection = null;
            System.out.println("Database connection closed");
        }
    }

    @Override
    public void executeCreateOrUpdateQuery(String query) throws SQLException {
        if (connection == null) {
            throw new RuntimeException("Please call initialize method in DBHelper before calling this method.");
        }
        try {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println("Error executing update query: " + e.getMessage());
            System.err.println("Query: " + query);
            throw e;
        }
    }

    @Override
    public ResultSet executeSelectQuery(String query) throws SQLException {
        if (connection == null) {
            throw new RuntimeException("Please call initialize method in DBHelper before calling this method.");
        }
        try {
            return statement.executeQuery(query);
        } catch (SQLException e) {
            System.err.println("Error executing select query: " + e.getMessage());
            System.err.println("Query: " + query);
            throw e;
        }
    }
}

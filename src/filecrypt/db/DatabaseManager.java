package filecrypt.db;

import filecrypt.model.OperationRecord;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages JDBC database operations for logging and retrieving
 * file encryption and decryption operation history.
 */
public class DatabaseManager {

    private static final String DEFAULT_DB_FILENAME = "operations.db";
    private static DatabaseManager instance;
    private String connectionUrl;

    private DatabaseManager() {
        initDriverAndUrl();
        initTable();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void initDriverAndUrl() {
        // First check if SQLite JDBC is available on classpath
        try {
            Class.forName("org.sqlite.JDBC");
            this.connectionUrl = "jdbc:sqlite:" + DEFAULT_DB_FILENAME;
            System.out.println("[DatabaseManager] Using SQLite JDBC driver: " + connectionUrl);
            return;
        } catch (ClassNotFoundException ignored) {
            // SQLite JDBC driver not present
        }

        // Check if H2 JDBC is available
        try {
            Class.forName("org.h2.Driver");
            this.connectionUrl = "jdbc:h2:./" + DEFAULT_DB_FILENAME.replace(".db", "") + ";MODE=MySQL";
            System.out.println("[DatabaseManager] Using H2 JDBC driver: " + connectionUrl);
            return;
        } catch (ClassNotFoundException ignored) {
            // H2 JDBC driver not present
        }

        // Use built-in embedded JDBC driver
        try {
            Class.forName("filecrypt.db.EmbeddedJdbcDriver");
            this.connectionUrl = EmbeddedJdbcDriver.URL_PREFIX + DEFAULT_DB_FILENAME;
            System.out.println("[DatabaseManager] Using embedded JDBC driver: " + connectionUrl);
        } catch (ClassNotFoundException e) {
            System.err.println("[DatabaseManager] Error initializing embedded driver: " + e.getMessage());
            this.connectionUrl = EmbeddedJdbcDriver.URL_PREFIX + DEFAULT_DB_FILENAME;
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionUrl);
    }

    /**
     * Creates the 'operations' table if it does not already exist.
     */
    public void initTable() {
        String sql = "CREATE TABLE IF NOT EXISTS operations (" +
                "id INTEGER PRIMARY KEY, " +
                "timestamp VARCHAR(30), " +
                "filename VARCHAR(255), " +
                "cipher VARCHAR(50), " +
                "mode VARCHAR(20), " +
                "key_used VARCHAR(100), " +
                "file_size VARCHAR(50), " +
                "status VARCHAR(20)" +
                ")";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("[DatabaseManager] Failed to initialize table: " + e.getMessage());
        }
    }

    /**
     * Inserts an operation record into the database using PreparedStatement.
     */
    public boolean logOperation(OperationRecord record) {
        String sql = "INSERT INTO operations (timestamp, filename, cipher, mode, key_used, file_size, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, record.getTimestamp());
            pstmt.setString(2, record.getFileName());
            pstmt.setString(3, record.getCipherType());
            pstmt.setString(4, record.getOperationMode());
            pstmt.setString(5, maskKey(record.getKeyUsed()));
            pstmt.setString(6, record.getFileSize());
            pstmt.setString(7, record.getStatus());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("[DatabaseManager] Failed to log operation: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves all recorded operations from the database.
     */
    public List<OperationRecord> getAllOperations() {
        List<OperationRecord> list = new ArrayList<>();
        String sql = "SELECT id, timestamp, filename, cipher, mode, key_used, file_size, status FROM operations ORDER BY id DESC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                OperationRecord rec = new OperationRecord(
                        rs.getInt("id"),
                        rs.getString("timestamp"),
                        rs.getString("filename"),
                        rs.getString("cipher"),
                        rs.getString("mode"),
                        rs.getString("key_used"),
                        rs.getString("file_size"),
                        rs.getString("status")
                );
                list.add(rec);
            }
        } catch (SQLException e) {
            System.err.println("[DatabaseManager] Failed to query operations: " + e.getMessage());
        }
        return list;
    }

    /**
     * Clears all operation records from the database table.
     */
    public boolean clearAllOperations() {
        String sql = "DELETE FROM operations";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            System.err.println("[DatabaseManager] Failed to clear operations: " + e.getMessage());
            return false;
        }
    }

    /**
     * Safely masks the key for display/logging if long, or shows masked version.
     */
    private String maskKey(String key) {
        if (key == null || key.isEmpty()) {
            return "N/A";
        }
        if (key.length() <= 3) {
            return "***";
        }
        return key.substring(0, 2) + "***" + key.substring(key.length() - 1);
    }
}

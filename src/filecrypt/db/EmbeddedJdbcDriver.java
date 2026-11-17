package filecrypt.db;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Embedded pure-Java JDBC Driver that provides standard java.sql.* capabilities
 * without requiring external third-party JARs or external database servers.
 * Data is stored persistently in a local database file.
 */
public class EmbeddedJdbcDriver implements Driver {

    public static final String URL_PREFIX = "jdbc:filecrypt:";

    static {
        try {
            DriverManager.registerDriver(new EmbeddedJdbcDriver());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return url != null && url.startsWith(URL_PREFIX);
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (!acceptsURL(url)) {
            return null;
        }
        String dbPath = url.substring(URL_PREFIX.length()).trim();
        if (dbPath.isEmpty()) {
            dbPath = "operations.db";
        }
        return new EmbeddedConnection(dbPath);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return 1;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        return true;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }

    // -------------------------------------------------------------
    // Connection Implementation
    // -------------------------------------------------------------
    static class EmbeddedConnection implements Connection {
        private final File dbFile;
        private boolean closed = false;

        public EmbeddedConnection(String dbPath) {
            this.dbFile = new File(dbPath);
        }

        public File getDbFile() {
            return dbFile;
        }

        @Override
        public Statement createStatement() throws SQLException {
            checkClosed();
            return new EmbeddedStatement(this);
        }

        @Override
        public PreparedStatement prepareStatement(String sql) throws SQLException {
            checkClosed();
            return new EmbeddedPreparedStatement(this, sql);
        }

        private void checkClosed() throws SQLException {
            if (closed) {
                throw new SQLException("Connection is closed.");
            }
        }

        @Override
        public void close() throws SQLException {
            this.closed = true;
        }

        @Override
        public boolean isClosed() throws SQLException {
            return closed;
        }

        // Stubbed standard methods
        @Override public CallableStatement prepareCall(String sql) throws SQLException { throw new SQLFeatureNotSupportedException(); }
        @Override public String nativeSQL(String sql) { return sql; }
        @Override public void setAutoCommit(boolean autoCommit) {}
        @Override public boolean getAutoCommit() { return true; }
        @Override public void commit() {}
        @Override public void rollback() {}
        @Override public DatabaseMetaData getMetaData() { return null; }
        @Override public void setReadOnly(boolean readOnly) {}
        @Override public boolean isReadOnly() { return false; }
        @Override public void setCatalog(String catalog) {}
        @Override public String getCatalog() { return "filecrypt"; }
        @Override public void setTransactionIsolation(int level) {}
        @Override public int getTransactionIsolation() { return Connection.TRANSACTION_NONE; }
        @Override public SQLWarning getWarnings() { return null; }
        @Override public void clearWarnings() {}
        @Override public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException { return createStatement(); }
        @Override public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException { return prepareStatement(sql); }
        @Override public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException { throw new SQLFeatureNotSupportedException(); }
        @Override public Map<String, Class<?>> getTypeMap() { return Collections.emptyMap(); }
        @Override public void setTypeMap(Map<String, Class<?>> map) {}
        @Override public void setHoldability(int holdability) {}
        @Override public int getHoldability() { return 0; }
        @Override public Savepoint setSavepoint() throws SQLException { throw new SQLFeatureNotSupportedException(); }
        @Override public Savepoint setSavepoint(String name) throws SQLException { throw new SQLFeatureNotSupportedException(); }
        @Override public void rollback(Savepoint savepoint) throws SQLException { throw new SQLFeatureNotSupportedException(); }
        @Override public void releaseSavepoint(Savepoint savepoint) throws SQLException { throw new SQLFeatureNotSupportedException(); }
        @Override public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException { return createStatement(); }
        @Override public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException { return prepareStatement(sql); }
        @Override public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException { throw new SQLFeatureNotSupportedException(); }
        @Override public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException { return prepareStatement(sql); }
        @Override public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException { return prepareStatement(sql); }
        @Override public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException { return prepareStatement(sql); }
        @Override public Clob createClob() throws SQLException { throw new SQLFeatureNotSupportedException(); }
        @Override public Blob createBlob() throws SQLException { throw new SQLFeatureNotSupportedException(); }
        @Override public NClob createNClob() throws SQLException { throw new SQLFeatureNotSupportedException(); }
        @Override public SQLXML createSQLXML() throws SQLException { throw new SQLFeatureNotSupportedException(); }
        @Override public boolean isValid(int timeout) { return !closed; }
        @Override public void setClientInfo(String name, String value) {}
        @Override public void setClientInfo(Properties properties) {}
        @Override public String getClientInfo(String name) { return null; }
        @Override public Properties getClientInfo() { return new Properties(); }
        @Override public Array createArrayOf(String typeName, Object[] elements) throws SQLException { throw new SQLFeatureNotSupportedException(); }
        @Override public Struct createStruct(String typeName, Object[] attributes) throws SQLException { throw new SQLFeatureNotSupportedException(); }
        @Override public void setSchema(String schema) {}
        @Override public String getSchema() { return "PUBLIC"; }
        @Override public void abort(java.util.concurrent.Executor executor) throws SQLException { close(); }
        @Override public void setNetworkTimeout(java.util.concurrent.Executor executor, int milliseconds) {}
        @Override public int getNetworkTimeout() { return 0; }
        @Override public <T> T unwrap(Class<T> iface) throws SQLException { throw new SQLException(); }
        @Override public boolean isWrapperFor(Class<?> iface) { return false; }
    }

    // -------------------------------------------------------------
    // Statement Implementation
    // -------------------------------------------------------------
    static class EmbeddedStatement implements Statement {
        protected final EmbeddedConnection connection;
        protected boolean closed = false;

        public EmbeddedStatement(EmbeddedConnection connection) {
            this.connection = connection;
        }

        @Override
        public ResultSet executeQuery(String sql) throws SQLException {
            checkClosed();
            List<Map<String, Object>> rows = loadTableRows(connection.getDbFile());
            if (sql != null && sql.toUpperCase().contains("ORDER BY") && sql.toUpperCase().contains("DESC")) {
                Collections.reverse(rows);
            }
            return new EmbeddedResultSet(this, rows);
        }

        @Override
        public int executeUpdate(String sql) throws SQLException {
            checkClosed();
            String upper = sql.trim().toUpperCase();
            if (upper.startsWith("CREATE TABLE")) {
                File file = connection.getDbFile();
                if (!file.exists()) {
                    try {
                        File parent = file.getParentFile();
                        if (parent != null && !parent.exists()) parent.mkdirs();
                        file.createNewFile();
                    } catch (IOException e) {
                        throw new SQLException("Failed to create database table file: " + e.getMessage(), e);
                    }
                }
                return 0;
            } else if (upper.startsWith("DELETE") || upper.startsWith("TRUNCATE")) {
                File file = connection.getDbFile();
                if (file.exists()) {
                    file.delete();
                }
                return 1;
            }
            return 0;
        }

        @Override
        public boolean execute(String sql) throws SQLException {
            String upper = sql.trim().toUpperCase();
            if (upper.startsWith("SELECT")) {
                executeQuery(sql);
                return true;
            } else {
                executeUpdate(sql);
                return false;
            }
        }

        protected void checkClosed() throws SQLException {
            if (closed || connection.isClosed()) {
                throw new SQLException("Statement is closed.");
            }
        }

        @Override public void close() throws SQLException { this.closed = true; }
        @Override public int getMaxFieldSize() { return 0; }
        @Override public void setMaxFieldSize(int max) {}
        @Override public int getMaxRows() { return 0; }
        @Override public void setMaxRows(int max) {}
        @Override public void setEscapeProcessing(boolean enable) {}
        @Override public int getQueryTimeout() { return 0; }
        @Override public void setQueryTimeout(int seconds) {}
        @Override public void cancel() {}
        @Override public SQLWarning getWarnings() { return null; }
        @Override public void clearWarnings() {}
        @Override public void setCursorName(String name) {}
        @Override public ResultSet getResultSet() { return null; }
        @Override public int getUpdateCount() { return -1; }
        @Override public boolean getMoreResults() { return false; }
        @Override public void setFetchDirection(int direction) {}
        @Override public int getFetchDirection() { return ResultSet.FETCH_FORWARD; }
        @Override public void setFetchSize(int rows) {}
        @Override public int getFetchSize() { return 0; }
        @Override public int getResultSetConcurrency() { return ResultSet.CONCUR_READ_ONLY; }
        @Override public int getResultSetType() { return ResultSet.TYPE_FORWARD_ONLY; }
        @Override public void addBatch(String sql) {}
        @Override public void clearBatch() {}
        @Override public int[] executeBatch() { return new int[0]; }
        @Override public Connection getConnection() { return connection; }
        @Override public boolean getMoreResults(int current) { return false; }
        @Override public ResultSet getGeneratedKeys() { return null; }
        @Override public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException { return executeUpdate(sql); }
        @Override public int executeUpdate(String sql, int[] columnIndexes) throws SQLException { return executeUpdate(sql); }
        @Override public int executeUpdate(String sql, String[] columnNames) throws SQLException { return executeUpdate(sql); }
        @Override public boolean execute(String sql, int autoGeneratedKeys) throws SQLException { return execute(sql); }
        @Override public boolean execute(String sql, int[] columnIndexes) throws SQLException { return execute(sql); }
        @Override public boolean execute(String sql, String[] columnNames) throws SQLException { return execute(sql); }
        @Override public int getResultSetHoldability() { return 0; }
        @Override public boolean isClosed() { return closed; }
        @Override public void setPoolable(boolean poolable) {}
        @Override public boolean isPoolable() { return false; }
        @Override public void closeOnCompletion() {}
        @Override public boolean isCloseOnCompletion() { return false; }
        @Override public <T> T unwrap(Class<T> iface) throws SQLException { throw new SQLException(); }
        @Override public boolean isWrapperFor(Class<?> iface) { return false; }
    }

    // -------------------------------------------------------------
    // PreparedStatement Implementation
    // -------------------------------------------------------------
    static class EmbeddedPreparedStatement extends EmbeddedStatement implements PreparedStatement {
        private final String sql;
        private final Map<Integer, Object> parameters = new HashMap<>();

        public EmbeddedPreparedStatement(EmbeddedConnection connection, String sql) {
            super(connection);
            this.sql = sql;
        }

        @Override
        public void setInt(int parameterIndex, int x) {
            parameters.put(parameterIndex, x);
        }

        @Override
        public void setString(int parameterIndex, String x) {
            parameters.put(parameterIndex, x);
        }

        @Override
        public void setObject(int parameterIndex, Object x) {
            parameters.put(parameterIndex, x);
        }

        @Override
        public int executeUpdate() throws SQLException {
            checkClosed();
            String upper = sql.trim().toUpperCase();
            if (upper.startsWith("INSERT")) {
                // Read current rows to determine auto ID
                List<Map<String, Object>> rows = loadTableRows(connection.getDbFile());
                int nextId = rows.size() + 1;

                // Parameter mapping:
                // INSERT INTO operations (timestamp, filename, cipher, mode, key_used, file_size, status) VALUES (?, ?, ?, ?, ?, ?, ?)
                Map<String, Object> newRow = new LinkedHashMap<>();
                newRow.put("id", nextId);
                newRow.put("timestamp", String.valueOf(parameters.getOrDefault(1, "")));
                newRow.put("filename", String.valueOf(parameters.getOrDefault(2, "")));
                newRow.put("cipher", String.valueOf(parameters.getOrDefault(3, "")));
                newRow.put("mode", String.valueOf(parameters.getOrDefault(4, "")));
                newRow.put("key_used", String.valueOf(parameters.getOrDefault(5, "")));
                newRow.put("file_size", String.valueOf(parameters.getOrDefault(6, "")));
                newRow.put("status", String.valueOf(parameters.getOrDefault(7, "")));

                rows.add(newRow);
                saveTableRows(connection.getDbFile(), rows);
                return 1;
            }
            return super.executeUpdate(sql);
        }

        @Override
        public ResultSet executeQuery() throws SQLException {
            return super.executeQuery(sql);
        }

        @Override
        public boolean execute() throws SQLException {
            String upper = sql.trim().toUpperCase();
            if (upper.startsWith("SELECT")) {
                executeQuery();
                return true;
            } else {
                executeUpdate();
                return false;
            }
        }

        @Override public void setNull(int parameterIndex, int sqlType) { parameters.put(parameterIndex, null); }
        @Override public void setBoolean(int parameterIndex, boolean x) { parameters.put(parameterIndex, x); }
        @Override public void setByte(int parameterIndex, byte x) { parameters.put(parameterIndex, x); }
        @Override public void setShort(int parameterIndex, short x) { parameters.put(parameterIndex, x); }
        @Override public void setLong(int parameterIndex, long x) { parameters.put(parameterIndex, x); }
        @Override public void setFloat(int parameterIndex, float x) { parameters.put(parameterIndex, x); }
        @Override public void setDouble(int parameterIndex, double x) { parameters.put(parameterIndex, x); }
        @Override public void setBigDecimal(int parameterIndex, java.math.BigDecimal x) { parameters.put(parameterIndex, x); }
        @Override public void setBytes(int parameterIndex, byte[] x) { parameters.put(parameterIndex, x); }
        @Override public void setDate(int parameterIndex, java.sql.Date x) { parameters.put(parameterIndex, x); }
        @Override public void setTime(int parameterIndex, java.sql.Time x) { parameters.put(parameterIndex, x); }
        @Override public void setTimestamp(int parameterIndex, java.sql.Timestamp x) { parameters.put(parameterIndex, x); }
        @Override public void setAsciiStream(int parameterIndex, InputStream x, int length) {}
        @Override public void setUnicodeStream(int parameterIndex, InputStream x, int length) {}
        @Override public void setBinaryStream(int parameterIndex, InputStream x, int length) {}
        @Override public void clearParameters() { parameters.clear(); }
        @Override public void addBatch() {}
        @Override public void setCharacterStream(int parameterIndex, Reader reader, int length) {}
        @Override public void setRef(int parameterIndex, Ref x) {}
        @Override public void setBlob(int parameterIndex, Blob x) {}
        @Override public void setClob(int parameterIndex, Clob x) {}
        @Override public void setArray(int parameterIndex, Array x) {}
        @Override public ResultSetMetaData getMetaData() { return null; }
        @Override public void setDate(int parameterIndex, java.sql.Date x, Calendar cal) {}
        @Override public void setTime(int parameterIndex, java.sql.Time x, Calendar cal) {}
        @Override public void setTimestamp(int parameterIndex, java.sql.Timestamp x, Calendar cal) {}
        @Override public void setNull(int parameterIndex, int sqlType, String typeName) {}
        @Override public void setURL(int parameterIndex, java.net.URL x) {}
        @Override public ParameterMetaData getParameterMetaData() { return null; }
        @Override public void setRowId(int parameterIndex, RowId x) {}
        @Override public void setNString(int parameterIndex, String value) { setString(parameterIndex, value); }
        @Override public void setNCharacterStream(int parameterIndex, Reader value, long length) {}
        @Override public void setNClob(int parameterIndex, NClob value) {}
        @Override public void setClob(int parameterIndex, Reader reader, long length) {}
        @Override public void setBlob(int parameterIndex, InputStream inputStream, long length) {}
        @Override public void setNClob(int parameterIndex, Reader reader, long length) {}
        @Override public void setSQLXML(int parameterIndex, SQLXML xmlObject) {}
        @Override public void setObject(int parameterIndex, Object x, int targetSqlType) { setObject(parameterIndex, x); }
        @Override public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) { setObject(parameterIndex, x); }
        @Override public void setAsciiStream(int parameterIndex, InputStream x, long length) {}
        @Override public void setBinaryStream(int parameterIndex, InputStream x, long length) {}
        @Override public void setCharacterStream(int parameterIndex, Reader reader, long length) {}
        @Override public void setAsciiStream(int parameterIndex, InputStream x) {}
        @Override public void setBinaryStream(int parameterIndex, InputStream x) {}
        @Override public void setCharacterStream(int parameterIndex, Reader reader) {}
        @Override public void setNCharacterStream(int parameterIndex, Reader value) {}
        @Override public void setClob(int parameterIndex, Reader reader) {}
        @Override public void setBlob(int parameterIndex, InputStream inputStream) {}
        @Override public void setNClob(int parameterIndex, Reader reader) {}
    }

    // -------------------------------------------------------------
    // ResultSet Implementation
    // -------------------------------------------------------------
    static class EmbeddedResultSet implements ResultSet {
        private final Statement statement;
        private final List<Map<String, Object>> rows;
        private int currentIndex = -1;
        private boolean closed = false;

        public EmbeddedResultSet(Statement statement, List<Map<String, Object>> rows) {
            this.statement = statement;
            this.rows = rows != null ? rows : Collections.emptyList();
        }

        @Override
        public boolean next() throws SQLException {
            checkClosed();
            currentIndex++;
            return currentIndex < rows.size();
        }

        @Override
        public int getInt(String columnLabel) throws SQLException {
            Object val = getObject(columnLabel);
            if (val instanceof Number) return ((Number) val).intValue();
            try { return Integer.parseInt(String.valueOf(val)); } catch (Exception e) { return 0; }
        }

        @Override
        public int getInt(int columnIndex) throws SQLException {
            return getInt(getColumnNameByIndex(columnIndex));
        }

        @Override
        public String getString(String columnLabel) throws SQLException {
            Object val = getObject(columnLabel);
            return val != null ? String.valueOf(val) : null;
        }

        @Override
        public String getString(int columnIndex) throws SQLException {
            return getString(getColumnNameByIndex(columnIndex));
        }

        @Override
        public Object getObject(String columnLabel) throws SQLException {
            checkClosed();
            if (currentIndex < 0 || currentIndex >= rows.size()) {
                throw new SQLException("Cursor out of range.");
            }
            Map<String, Object> row = rows.get(currentIndex);
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(columnLabel)) {
                    return entry.getValue();
                }
            }
            return null;
        }

        private String getColumnNameByIndex(int columnIndex) {
            String[] cols = {"id", "timestamp", "filename", "cipher", "mode", "key_used", "file_size", "status"};
            if (columnIndex >= 1 && columnIndex <= cols.length) {
                return cols[columnIndex - 1];
            }
            return "";
        }

        private void checkClosed() throws SQLException {
            if (closed) throw new SQLException("ResultSet is closed.");
        }

        @Override public void close() { this.closed = true; }
        @Override public boolean wasNull() { return false; }
        @Override public boolean getBoolean(int columnIndex) { return false; }
        @Override public byte getByte(int columnIndex) { return 0; }
        @Override public short getShort(int columnIndex) { return 0; }
        @Override public long getLong(int columnIndex) { return 0; }
        @Override public float getFloat(int columnIndex) { return 0; }
        @Override public double getDouble(int columnIndex) { return 0; }
        @Override public java.math.BigDecimal getBigDecimal(int columnIndex, int scale) { return null; }
        @Override public byte[] getBytes(int columnIndex) { return new byte[0]; }
        @Override public java.sql.Date getDate(int columnIndex) { return null; }
        @Override public java.sql.Time getTime(int columnIndex) { return null; }
        @Override public java.sql.Timestamp getTimestamp(int columnIndex) { return null; }
        @Override public InputStream getAsciiStream(int columnIndex) { return null; }
        @Override public InputStream getUnicodeStream(int columnIndex) { return null; }
        @Override public InputStream getBinaryStream(int columnIndex) { return null; }
        @Override public boolean getBoolean(String columnLabel) { return false; }
        @Override public byte getByte(String columnLabel) { return 0; }
        @Override public short getShort(String columnLabel) { return 0; }
        @Override public long getLong(String columnLabel) { return 0; }
        @Override public float getFloat(String columnLabel) { return 0; }
        @Override public double getDouble(String columnLabel) { return 0; }
        @Override public java.math.BigDecimal getBigDecimal(String columnLabel, int scale) { return null; }
        @Override public byte[] getBytes(String columnLabel) { return new byte[0]; }
        @Override public java.sql.Date getDate(String columnLabel) { return null; }
        @Override public java.sql.Time getTime(String columnLabel) { return null; }
        @Override public java.sql.Timestamp getTimestamp(String columnLabel) { return null; }
        @Override public InputStream getAsciiStream(String columnLabel) { return null; }
        @Override public InputStream getUnicodeStream(String columnLabel) { return null; }
        @Override public InputStream getBinaryStream(String columnLabel) { return null; }
        @Override public SQLWarning getWarnings() { return null; }
        @Override public void clearWarnings() {}
        @Override public String getCursorName() { return null; }
        @Override public ResultSetMetaData getMetaData() { return null; }
        @Override public Object getObject(int columnIndex) throws SQLException { return getObject(getColumnNameByIndex(columnIndex)); }
        @Override public int findColumn(String columnLabel) { return 1; }
        @Override public Reader getCharacterStream(int columnIndex) { return null; }
        @Override public Reader getCharacterStream(String columnLabel) { return null; }
        @Override public java.math.BigDecimal getBigDecimal(int columnIndex) { return null; }
        @Override public java.math.BigDecimal getBigDecimal(String columnLabel) { return null; }
        @Override public boolean isBeforeFirst() { return currentIndex < 0; }
        @Override public boolean isAfterLast() { return currentIndex >= rows.size(); }
        @Override public boolean isFirst() { return currentIndex == 0; }
        @Override public boolean isLast() { return currentIndex == rows.size() - 1; }
        @Override public void beforeFirst() { currentIndex = -1; }
        @Override public void afterLast() { currentIndex = rows.size(); }
        @Override public boolean first() { if (rows.isEmpty()) return false; currentIndex = 0; return true; }
        @Override public boolean last() { if (rows.isEmpty()) return false; currentIndex = rows.size() - 1; return true; }
        @Override public int getRow() { return currentIndex + 1; }
        @Override public boolean absolute(int row) { return false; }
        @Override public boolean relative(int rows) { return false; }
        @Override public boolean previous() { if (currentIndex > 0) { currentIndex--; return true; } return false; }
        @Override public void setFetchDirection(int direction) {}
        @Override public int getFetchDirection() { return ResultSet.FETCH_FORWARD; }
        @Override public void setFetchSize(int rows) {}
        @Override public int getFetchSize() { return 0; }
        @Override public int getType() { return ResultSet.TYPE_FORWARD_ONLY; }
        @Override public int getConcurrency() { return ResultSet.CONCUR_READ_ONLY; }
        @Override public boolean rowUpdated() { return false; }
        @Override public boolean rowInserted() { return false; }
        @Override public boolean rowDeleted() { return false; }
        @Override public void updateNull(int columnIndex) {}
        @Override public void updateBoolean(int columnIndex, boolean x) {}
        @Override public void updateByte(int columnIndex, byte x) {}
        @Override public void updateShort(int columnIndex, short x) {}
        @Override public void updateInt(int columnIndex, int x) {}
        @Override public void updateLong(int columnIndex, long x) {}
        @Override public void updateFloat(int columnIndex, float x) {}
        @Override public void updateDouble(int columnIndex, double x) {}
        @Override public void updateBigDecimal(int columnIndex, java.math.BigDecimal x) {}
        @Override public void updateString(int columnIndex, String x) {}
        @Override public void updateBytes(int columnIndex, byte[] x) {}
        @Override public void updateDate(int columnIndex, java.sql.Date x) {}
        @Override public void updateTime(int columnIndex, java.sql.Time x) {}
        @Override public void updateTimestamp(int columnIndex, java.sql.Timestamp x) {}
        @Override public void updateAsciiStream(int columnIndex, InputStream x, int length) {}
        @Override public void updateBinaryStream(int columnIndex, InputStream x, int length) {}
        @Override public void updateCharacterStream(int columnIndex, Reader x, int length) {}
        @Override public void updateObject(int columnIndex, Object x, int scaleOrLength) {}
        @Override public void updateObject(int columnIndex, Object x) {}
        @Override public void updateNull(String columnLabel) {}
        @Override public void updateBoolean(String columnLabel, boolean x) {}
        @Override public void updateByte(String columnLabel, byte x) {}
        @Override public void updateShort(String columnLabel, short x) {}
        @Override public void updateInt(String columnLabel, int x) {}
        @Override public void updateLong(String columnLabel, long x) {}
        @Override public void updateFloat(String columnLabel, float x) {}
        @Override public void updateDouble(String columnLabel, double x) {}
        @Override public void updateBigDecimal(String columnLabel, java.math.BigDecimal x) {}
        @Override public void updateString(String columnLabel, String x) {}
        @Override public void updateBytes(String columnLabel, byte[] x) {}
        @Override public void updateDate(String columnLabel, java.sql.Date x) {}
        @Override public void updateTime(String columnLabel, java.sql.Time x) {}
        @Override public void updateTimestamp(String columnLabel, java.sql.Timestamp x) {}
        @Override public void updateAsciiStream(String columnLabel, InputStream x, int length) {}
        @Override public void updateBinaryStream(String columnLabel, InputStream x, int length) {}
        @Override public void updateCharacterStream(String columnLabel, Reader reader, int length) {}
        @Override public void updateObject(String columnLabel, Object x, int scaleOrLength) {}
        @Override public void updateObject(String columnLabel, Object x) {}
        @Override public void insertRow() {}
        @Override public void updateRow() {}
        @Override public void deleteRow() {}
        @Override public void refreshRow() {}
        @Override public void cancelRowUpdates() {}
        @Override public void moveToInsertRow() {}
        @Override public void moveToCurrentRow() {}
        @Override public Statement getStatement() { return statement; }
        @Override public Object getObject(int columnIndex, Map<String, Class<?>> map) { return null; }
        @Override public Ref getRef(int columnIndex) { return null; }
        @Override public Blob getBlob(int columnIndex) { return null; }
        @Override public Clob getClob(int columnIndex) { return null; }
        @Override public Array getArray(int columnIndex) { return null; }
        @Override public Object getObject(String columnLabel, Map<String, Class<?>> map) { return null; }
        @Override public Ref getRef(String columnLabel) { return null; }
        @Override public Blob getBlob(String columnLabel) { return null; }
        @Override public Clob getClob(String columnLabel) { return null; }
        @Override public Array getArray(String columnLabel) { return null; }
        @Override public java.sql.Date getDate(int columnIndex, Calendar cal) { return null; }
        @Override public java.sql.Date getDate(String columnLabel, Calendar cal) { return null; }
        @Override public java.sql.Time getTime(int columnIndex, Calendar cal) { return null; }
        @Override public java.sql.Time getTime(String columnLabel, Calendar cal) { return null; }
        @Override public java.sql.Timestamp getTimestamp(int columnIndex, Calendar cal) { return null; }
        @Override public java.sql.Timestamp getTimestamp(String columnLabel, Calendar cal) { return null; }
        @Override public java.net.URL getURL(int columnIndex) { return null; }
        @Override public java.net.URL getURL(String columnLabel) { return null; }
        @Override public void updateRef(int columnIndex, Ref x) {}
        @Override public void updateRef(String columnLabel, Ref x) {}
        @Override public void updateBlob(int columnIndex, Blob x) {}
        @Override public void updateBlob(String columnLabel, Blob x) {}
        @Override public void updateClob(int columnIndex, Clob x) {}
        @Override public void updateClob(String columnLabel, Clob x) {}
        @Override public void updateArray(int columnIndex, Array x) {}
        @Override public void updateArray(String columnLabel, Array x) {}
        @Override public RowId getRowId(int columnIndex) { return null; }
        @Override public RowId getRowId(String columnLabel) { return null; }
        @Override public void updateRowId(int columnIndex, RowId x) {}
        @Override public void updateRowId(String columnLabel, RowId x) {}
        @Override public int getHoldability() { return 0; }
        @Override public boolean isClosed() { return closed; }
        @Override public void updateNString(int columnIndex, String nString) {}
        @Override public void updateNString(String columnLabel, String nString) {}
        @Override public void updateNClob(int columnIndex, NClob nClob) {}
        @Override public void updateNClob(String columnLabel, NClob nClob) {}
        @Override public NClob getNClob(int columnIndex) { return null; }
        @Override public NClob getNClob(String columnLabel) { return null; }
        @Override public SQLXML getSQLXML(int columnIndex) { return null; }
        @Override public SQLXML getSQLXML(String columnLabel) { return null; }
        @Override public void updateSQLXML(int columnIndex, SQLXML xmlObject) {}
        @Override public void updateSQLXML(String columnLabel, SQLXML xmlObject) {}
        @Override public String getNString(int columnIndex) { return null; }
        @Override public String getNString(String columnLabel) { return null; }
        @Override public Reader getNCharacterStream(int columnIndex) { return null; }
        @Override public Reader getNCharacterStream(String columnLabel) { return null; }
        @Override public void updateNCharacterStream(int columnIndex, Reader x, long length) {}
        @Override public void updateNCharacterStream(String columnLabel, Reader reader, long length) {}
        @Override public void updateAsciiStream(int columnIndex, InputStream x, long length) {}
        @Override public void updateBinaryStream(int columnIndex, InputStream x, long length) {}
        @Override public void updateCharacterStream(int columnIndex, Reader x, long length) {}
        @Override public void updateAsciiStream(String columnLabel, InputStream x, long length) {}
        @Override public void updateBinaryStream(String columnLabel, InputStream x, long length) {}
        @Override public void updateCharacterStream(String columnLabel, Reader reader, long length) {}
        @Override public void updateBlob(int columnIndex, InputStream inputStream, long length) {}
        @Override public void updateBlob(String columnLabel, InputStream inputStream, long length) {}
        @Override public void updateClob(int columnIndex, Reader reader, long length) {}
        @Override public void updateClob(String columnLabel, Reader reader, long length) {}
        @Override public void updateNClob(int columnIndex, Reader reader, long length) {}
        @Override public void updateNClob(String columnLabel, Reader reader, long length) {}
        @Override public void updateNCharacterStream(int columnIndex, Reader x) {}
        @Override public void updateNCharacterStream(String columnLabel, Reader reader) {}
        @Override public void updateAsciiStream(int columnIndex, InputStream x) {}
        @Override public void updateBinaryStream(int columnIndex, InputStream x) {}
        @Override public void updateCharacterStream(int columnIndex, Reader x) {}
        @Override public void updateAsciiStream(String columnLabel, InputStream x) {}
        @Override public void updateBinaryStream(String columnLabel, InputStream x) {}
        @Override public void updateCharacterStream(String columnLabel, Reader reader) {}
        @Override public void updateBlob(int columnIndex, InputStream inputStream) {}
        @Override public void updateBlob(String columnLabel, InputStream inputStream) {}
        @Override public void updateClob(int columnIndex, Reader reader) {}
        @Override public void updateClob(String columnLabel, Reader reader) {}
        @Override public void updateNClob(int columnIndex, Reader reader) {}
        @Override public void updateNClob(String columnLabel, Reader reader) {}
        @Override public <T> T getObject(int columnIndex, Class<T> type) { return null; }
        @Override public <T> T getObject(String columnLabel, Class<T> type) { return null; }
        @Override public <T> T unwrap(Class<T> iface) throws SQLException { throw new SQLException(); }
        @Override public boolean isWrapperFor(Class<?> iface) { return false; }
    }

    // -------------------------------------------------------------
    // Persistent Storage Utilities
    // -------------------------------------------------------------
    private static synchronized List<Map<String, Object>> loadTableRows(File dbFile) {
        List<Map<String, Object>> rows = new ArrayList<>();
        if (dbFile == null || !dbFile.exists() || dbFile.length() == 0) {
            return rows;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(dbFile), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\t", -1);
                if (parts.length >= 8) {
                    Map<String, Object> map = new LinkedHashMap<>();
                    try {
                        map.put("id", Integer.parseInt(parts[0]));
                    } catch (NumberFormatException e) {
                        map.put("id", rows.size() + 1);
                    }
                    map.put("timestamp", parts[1]);
                    map.put("filename", parts[2]);
                    map.put("cipher", parts[3]);
                    map.put("mode", parts[4]);
                    map.put("key_used", parts[5]);
                    map.put("file_size", parts[6]);
                    map.put("status", parts[7]);
                    rows.add(map);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not load table rows: " + e.getMessage());
        }
        return rows;
    }

    private static synchronized void saveTableRows(File dbFile, List<Map<String, Object>> rows) {
        if (dbFile == null) return;
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(dbFile, false), StandardCharsets.UTF_8))) {
            for (Map<String, Object> row : rows) {
                String line = String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s",
                        row.getOrDefault("id", "0"),
                        row.getOrDefault("timestamp", ""),
                        row.getOrDefault("filename", ""),
                        row.getOrDefault("cipher", ""),
                        row.getOrDefault("mode", ""),
                        row.getOrDefault("key_used", ""),
                        row.getOrDefault("file_size", ""),
                        row.getOrDefault("status", "SUCCESS"));
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Could not save table rows: " + e.getMessage());
        }
    }
}

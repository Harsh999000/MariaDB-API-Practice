package com.mariadb.mariadb.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Import of class with custom object to handle table details
import com.mariadb.mariadb.model.TableColumnInfo;

// Import of class with function to normalize type of columns. Used in insert data
import static com.mariadb.mariadb.utility.NormalizeTypeUtil.normalizeType;

// Import of class to set data types in PreparedStatement
import com.mariadb.mariadb.utility.PreparedStatementHelper;

import jakarta.servlet.http.HttpServletResponse;

@Component
public class DefaultSQLQuery {
    private final EstablishDBConnection db_connectionService;

    public DefaultSQLQuery(EstablishDBConnection db_connectionService) {
        this.db_connectionService = db_connectionService;
    }

    // #region - Start - Database SQL Query

    // #region - Start - Create Database
    public boolean createDatabase(String dbName) {
        String createDatabaseSQL = "CREATE DATABASE " + dbName;

        // Using the same established connection for statement to execute query
        try (
                Connection connection = db_connectionService.getConnection();
                Statement statement = connection.createStatement()) {
            statement.execute(createDatabaseSQL);
            System.out.println("Database Created");
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to create database");
            return false;
        }

    }
    // #endregion - End - Create Database

    // #region - Start - Get Database
    public List<String> getDatabases() {
        List<String> databases = new ArrayList<>();
        String getDatabaseSQL = "SHOW DATABASES";
        try (
                Connection connection = db_connectionService.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(getDatabaseSQL)) {
            while (resultSet.next()) {
                String dbName = resultSet.getString(1);
                if (!List.of("information_schema", "mysql", "performance_schema", "sys").contains(dbName)) {
                    databases.add(dbName);
                }
            }
            System.out.println("List of Databases:\n" + databases);
            return databases;
        } catch (SQLException e) {
            System.out.println("Failed to get the list of databases");
        }

        return databases;
    }
    // #endregion - End - Get Database

    // #region - Start - Delete Database
    public boolean deleteDatabase(String dbName) {
        String deleteDatabaseSQL = "DROP DATABASE " + dbName;

        try (
                Connection connection = db_connectionService.getConnection();
                Statement statement = connection.createStatement();) {
            statement.execute(deleteDatabaseSQL);
            System.out.println("Database Deleted: " + dbName);
            return true;
        } catch (SQLException e) {
            System.out.println("Database deletion failed: " + dbName);
            return false;
        }
    }
    // #endregion - End - Delete Database

    // #endregion - End - Databse SQL Query

    // #region - Start - User SQL Query

    // #region - Start - Create User SQL Query
    public boolean createUser(Map<String, String> userDetails) {

        // Required Fields
        String userName = userDetails.get("username");
        String password = userDetails.get("password");
        String host = userDetails.get("host");
        String access = userDetails.get("access");
        String database = userDetails.get("database");

        // Null and Empty Validation
        if (userName == null || password == null || host == null || access == null
                || database == null || userName.trim().isEmpty() || password.trim().isEmpty()
                || host.trim().isEmpty() || access.trim().isEmpty() || database.trim().isEmpty()) {
            System.out.println("User creation failed. Required field missing. " +
                    "Please make sure the below fields are present:\n1. username \n2. password\n" +
                    "3. host \n4. access\n5. Database \\n" + "Make sure that the characters are same.");
            return false;
        }

        userName = userName.trim();
        password = password.trim();
        host = host.trim();
        access = access.trim();
        database = database.trim();

        // SQL commands
        try (Connection connection = db_connectionService.getConnection();
                Statement statement = connection.createStatement()) {
            // Create User
            String createUserSQL = String.format("CREATE USER '%s'@'%s' IDENTIFIED BY '%s'", userName, host, password);
            statement.execute(createUserSQL);

            // Grant Access
            String grantUserSQL = String.format("GRANT %s ON %s.* TO '%s'@'%s'", access, database, userName, host);
            statement.execute(grantUserSQL);
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to create user or grant access: " + userName);
            return false;
        }
    }
    // #endregion - End - Create User SQL Query

    // #region - Start - Get Users SQL Query
    public List<String> getUsers() {
        List<String> userList = new ArrayList<String>();

        // SQL Command
        String fetchUserSQL = "SELECT USER, HOST FROM mysql.user;";
        try (Connection connection = db_connectionService.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(fetchUserSQL)) {
            while (resultSet.next()) {
                String userName = resultSet.getString("User");
                String userHost = resultSet.getString("Host");
                if (!userName.equalsIgnoreCase("root") || !userName.equalsIgnoreCase("mariadb.sys")) {
                    userList.add(userName + " @ " + userHost);
                }
            }
            return userList;
        } catch (

        SQLException e) {
            System.out.println("User Detail Fetch Failed");
            userList.add("User detail fetch Failed\n");
            return userList;
        }
    }
    // #endregion - End - Get Users SQL Query

    // #region - Start - Get User Privileges SQL Query
    public List<String> getUserPrivileges(String userName, String userHost) {
        List<String> userPrivileges = new ArrayList<String>();
        if (userName == null || userName.trim().isEmpty() || userHost == null || userHost.trim().isEmpty()) {
            System.out.println("User's name or user's host is missing.\n"
                    + "Please provide the data in body or urlas below variable:\n1. username \n2. userhost");
            userPrivileges.add("User's name or user's host is missing.\n"
                    + "Please provide the data in body or urlas below variable:\n1. username \n2. userhost");
            return userPrivileges;
        }

        // SQL Command
        String getUserPrivilegesSQL = String.format("SHOW GRANTS for '%s'@'%s'", userName, userHost);

        try (Connection connection = db_connectionService.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(getUserPrivilegesSQL)) {
            while (resultSet.next()) {
                userPrivileges.add(resultSet.getString(1));
            }
            System.out.println("Privileges for " + userName + " @ " + userHost + " : " + userPrivileges);
            return userPrivileges;
        } catch (SQLException e) {
            System.out.println("User Privileges Fetch Failed: " + userName + " @ " + userHost);
            userPrivileges.add("User Privileges Fetch Failed: " + userName + " @ " + userHost);
            return userPrivileges;
        }
    }
    // #endregion - End - Get User Privileges SQL Query

    // #region - Start - User Grant Access SQL Query
    public boolean userGrantAccess(String userName, String userHost, String userAccess, String databaseName,
            String tableName) {
        // Check is username is present and not empty
        if (userName == null || userName.trim().isEmpty()) {
            System.out.println("username is missing in body\n");
            return false;
        }

        // Check if userhost is present and not empty
        if (userHost == null || userHost.trim().isEmpty()) {
            System.out.println("userhost is missing in body\n");
            return false;
        }

        // Check if access is present and not empty
        if (userAccess == null || userAccess.trim().isEmpty()) {
            System.out.println("access is missing in body\n");
            return false;
        }

        // Check if database is present and not empty
        if (databaseName == null || databaseName.trim().isEmpty()) {
            System.out.println("database is missing in body\n");
            return false;
        }

        // Check if the table is present and not empty
        if (tableName == null || tableName.trim().isEmpty()) {
            System.out.println("table is missing in body\n");
            return false;
        }

        // SQL Commad
        try (Connection connection = db_connectionService.getConnection();
                Statement statement = connection.createStatement()) {
            String userGrantAccessSQL;
            if (databaseName.equalsIgnoreCase("All")) {
                // SQL query for all databases
                userGrantAccessSQL = String.format("GRANT %s ON *.* TO '%s'@'%s'", userAccess,
                        userName, userHost);
            } else {
                // SQL query for all tables
                if (tableName.equalsIgnoreCase("All")) {
                    userGrantAccessSQL = String.format("GRANT %s ON %s.* TO '%s'@'%s'", userAccess, databaseName,
                            userName, userHost);
                } else {
                    // SQL query for specific table
                    userGrantAccessSQL = String.format("GRANT %s ON %s.%s TO '%s'@'%s'", userAccess, databaseName,
                            tableName,
                            userName, userHost);
                }
            }

            statement.execute(userGrantAccessSQL);
            System.out.println(
                    "Granted Access " + userAccess + " on " + databaseName + "." + tableName + " to " + userName
                            + " @ " + userHost);
            return true;
        } catch (SQLException e) {
            System.out.println("Access grant failed for " + userName + " @ " + userHost);
            return false;
        }
    }
    // #endregion - End - User Grant Access SQL Query

    // #region - Start - Revoke User Access SQL Query
    public boolean userRevokeAccess(String userName, String userHost, String userAccess, String databaseName,
            String tableName) {
        // Check is username is present and not empty
        if (userName == null || userName.trim().isEmpty()) {
            System.out.println("username is missing in body\n");
            return false;
        }

        // Check if userhost is present and not empty
        if (userHost == null || userHost.trim().isEmpty()) {
            System.out.println("userhost is missing in body\n");
            return false;
        }

        // Check if access is present and not empty
        if (userAccess == null || userAccess.trim().isEmpty()) {
            System.out.println("access is missing in body\n");
            return false;
        }

        // Check if database is present and not empty
        if (databaseName == null || databaseName.trim().isEmpty()) {
            System.out.println("database is missing in body\n");
            return false;
        }

        // Check if the table is present and not empty
        if (tableName == null || tableName.trim().isEmpty()) {
            System.out.println("table is missing in body\n");
            return false;
        }

        // SQL Commad
        try (Connection connection = db_connectionService.getConnection();
                Statement statement = connection.createStatement()) {
            String userRevokeAccessSQL;
            if (databaseName.equalsIgnoreCase("All")) {
                // SQL query for all databases
                userRevokeAccessSQL = String.format("REVOKE %s ON *.* FROM '%s'@'%s'", userAccess,
                        userName, userHost);
            } else {
                // SQL query for all tables
                if (tableName.equalsIgnoreCase("All")) {
                    userRevokeAccessSQL = String.format("REVOKE %s ON %s.* FROM '%s'@'%s'", userAccess, databaseName,
                            userName, userHost);
                } else {
                    // SQL query for specific table
                    userRevokeAccessSQL = String.format("REVOKE %s ON %s.%s FROM '%s'@'%s'", userAccess, databaseName,
                            tableName,
                            userName, userHost);
                }
            }

            statement.execute(userRevokeAccessSQL);
            System.out.println(
                    "Revoked Access " + userAccess + " on " + databaseName + "." + tableName + " to " + userName
                            + " @ " + userHost);
            return true;
        } catch (SQLException e) {
            System.out.println("Access revoke failed for " + userName + " @ " + userHost);
            return false;
        }
    }
    // #endregion - End - Revoke User Access SQL Query

    // #region - Start - Delete User SQL Query
    public boolean deleteUser(String userName, String userHost) {
        // Empty userName or userHost check
        if ((userName == null || userName.trim().isEmpty()) || (userHost == null || userHost.trim().isEmpty())) {
            System.out.println("username or user is missing\n");
            return false;
        }

        // SQL Command
        try (Connection connection = db_connectionService.getConnection();
                Statement statement = connection.createStatement()) {
            String deleteUserSQL = String.format("DROP USER '%s'@'%s'", userName, userHost);
            statement.execute(deleteUserSQL);
            System.out.println("User deletion successful for " + userName + " @ " + userHost);
            return true;
        } catch (SQLException e) {
            System.out.println("User Deletion failed: " + userName + " @ " + userHost);
            return false;
        }
    }
    // #endregion - End - Delete User SQL Query

    // #endregion - End - User SQL Query

    // #region - Start - Table SQL Query

    // #region - Start - Create Table SQL Query
    public boolean createTable(String databaseName, String tableName, List<String> columnDefinitionList) {
        // databaseName validation check
        if (databaseName == null || databaseName.trim().isEmpty()) {
            System.out.println("databasename is missing, please enter the databasename and try again\n.");
            return false;
        }

        // tableName validation check
        if (tableName == null || tableName.trim().isEmpty()) {
            System.out.println("tablename is missing, please enter the tablename and try again\n.");
            return false;
        }

        // no check for contraints as they are optional and in controllers it is
        // validated if all the fields are provided when present

        String columnDefinition = String.join(", ", columnDefinitionList);

        String createTableSQL = String.format("CREATE TABLE %s.%s (%s);", databaseName, tableName, columnDefinition);

        try (Connection connection = db_connectionService.getConnection();
                Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
            System.out.println("Table created successfully: " + tableName);
            return true;
        } catch (SQLException e) {
            System.out.println("\nTable Creation failed: " + tableName + "\n");
            System.out.println("\nSQLException: " + e + "\n");
        }

        return false;
    }
    // #endregion - End - Create Table SQL Query

    // #region - Start - Get Table SQL Query
    public List<String> getTable(String databaseName) {
        List<String> tableList = new ArrayList<String>();

        // Empty databaseName check
        if (databaseName == null || databaseName.trim().isEmpty()) {
            System.out.println("\ndatabasename is missing\n");
            return List.of("databasename is missing");
        }

        // SQL Command
        String SQL = "SHOW TABLES FROM " + databaseName;
        try (Connection connection = db_connectionService.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(SQL);) {
            while (resultSet.next()) {
                String tableName = resultSet.getString(1);
                tableList.add(tableName);
            }
        } catch (Exception e) {
            System.out.println("\nTable fetch Failed. Exception: " + e + " \n");
            tableList.add("Table Fetch failed. Exception: " + e);
            return tableList;
        }

        return tableList;
    }
    // #endregion - End - Get Table SQL Query

    // #region - Start - Get Table Details SQL Query
    public List<TableColumnInfo> getTableDetails(String databaseName, String tableName) {
        List<TableColumnInfo> tableInfo = new ArrayList<TableColumnInfo>();

        // SQL Command
        String SQL = String.format("DESCRIBE %s.%s", databaseName, tableName);

        try (Connection connection = db_connectionService.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(SQL)) {
            while (resultSet.next()) {
                TableColumnInfo tableColumnInfo = new TableColumnInfo();
                tableColumnInfo.setField(resultSet.getString("Field"));
                tableColumnInfo.setType(resultSet.getString("Type"));
                tableColumnInfo.setNull(resultSet.getString("Null"));
                tableColumnInfo.setKey(resultSet.getString("Key"));
                tableColumnInfo.setDefault(resultSet.getString("Default"));
                tableColumnInfo.setExtra(resultSet.getString("Extra"));
                tableInfo.add(tableColumnInfo);
            }
        } catch (Exception e) {
            System.out.println("Table Detail Fetch Failed. Exception: " + e);
            TableColumnInfo errorInfo = new TableColumnInfo();
            errorInfo.setField("Error");
            errorInfo.setType("N/A");
            errorInfo.setNull("N/A");
            errorInfo.setKey("N/A");
            errorInfo.setDefault("N/A");
            errorInfo.setExtra(e.getMessage());

            List<TableColumnInfo> errorList = new ArrayList<>();
            errorList.add(errorInfo);
            return errorList;
        }

        return tableInfo;
    }
    // #endregion - End - Get table Details SQL Query

    // #region - Start - Add Column SQL Query
    public String addColumn(String databaseName, String tableName, String columnName, String dataType, String nullable,
            String defaultValue, String key, String extra) {

        // data validation
        if (databaseName == null || databaseName.trim().isEmpty() ||
                tableName == null || tableName.trim().isEmpty() ||
                columnName == null || columnName.trim().isEmpty() ||
                dataType == null || dataType.trim().isEmpty()) {
            String errorMessage = "one or more required fields are missing";
            System.out.println("\none or more required fields are missing\n");
            return errorMessage;
        }

        // removing null and pass it as string even if empty
        nullable = (nullable != null) ? nullable.trim() : "";
        defaultValue = (defaultValue != null) ? defaultValue.trim() : "";
        key = (key != null) ? key.trim() : "";
        extra = (extra != null) ? extra.trim() : "";

        // SQL Command
        String SQL = String.format("ALTER TABLE %s.%s ADD COLUMN %s %s %s %s %s %s", databaseName, tableName,
                columnName, dataType,
                nullable, defaultValue, key, extra);

        try (Connection connection = db_connectionService.getConnection();
                Statement statement = connection.createStatement();) {
            statement.executeUpdate(SQL);
            System.out.println("\n Column Added.\n");
            return "Success";
        } catch (SQLException e) {
            System.out.println("\n Add column failed. Exception: " + e + "\n");
            String errorMessage = e.getMessage();
            return errorMessage;
        }
    }
    // #endregion - End - Add Column SQL Query

    // #region - Start - Delete Column SQL Query
    public String deleteColumn(String databaseName, String tableName, String columnName) {

        // Data Validation
        if (databaseName == null || databaseName.trim().isEmpty() ||
                tableName == null || tableName.trim().isEmpty() ||
                columnName == null || columnName.trim().isEmpty()) {
            System.out.println("\n One of databasename, tablename, columnname is missing\n");
            String errorMessage = "One of databasename, tablename, columnname is missing";
            return errorMessage;
        }

        // Removing spacing
        databaseName = databaseName.trim();
        tableName = tableName.trim();
        columnName = columnName.trim();

        // SQL Command
        try (Connection connection = db_connectionService.getConnection();
                Statement statement = connection.createStatement()) {
            String SQL = String.format("ALTER TABLE %s.%s DROP COLUMN %s", databaseName, tableName, columnName);
            statement.executeUpdate(SQL);
            System.out.println("\nColumn Deleted\n");
            return "success";
        } catch (SQLException e) {
            System.out.println("\nDelete Column Failed. Exception: " + e + " \n");
            String errorMessage = e.getMessage();
            return "Delete Column Failed. Exception: " + errorMessage;
        }
    }
    // #endregion - End - Delete SQL Query

    // #region - Start - Insert Data SQL Query
    public String insertData(String databaseName, String tableName, List<String> columnNameList,
            List<List<Object>> dataList) {

        // Data Validation for databaseName and tableName
        if (databaseName == null || databaseName.trim().isEmpty() ||
                tableName == null || tableName.trim().isEmpty()) {
            System.out.println("\ndatabasename or table name is missing\n");
            String errorMessage = "databasename or table name is missing";
            return errorMessage;
        }

        // Fetch table schema to get column data type and verify if column exists
        List<TableColumnInfo> columnInfo = getTableDetails(databaseName, tableName);

        // Map to store column and data mapping
        Map<String, String> columnTypeMap = new HashMap<>();

        // if Field != "error", Field is where the name of the column is stored in
        // TableColumnInfo.java. If not error we store the name and datatype after
        // normalizing inputs like varchar(20), int(5) etc
        for (TableColumnInfo column : columnInfo) {
            if (!column.getField().equalsIgnoreCase("Error")) {
                columnTypeMap.put(column.getField(), normalizeType(column.getType()));
            } else {
                return "Failed to fetch one of the column details.";
            }
        }

        // Check if all the column name passed to this function is presnt in the fetched
        // column details from getTableDetails
        for (String column : columnNameList) {
            if (!columnTypeMap.containsKey(column)) {
                return "Invalid Column: " + column;
            }
        }

        // SQL Command
        StringBuilder SQL = new StringBuilder();
        SQL.append("INSERT INTO ").append(databaseName).append(".").append(tableName)
                .append(" (").append(String.join(", ", columnNameList)).append(") VALUES ");

        // Column and row count validation (Mapping data count in one row is equal to
        // column given)
        int columnCount = columnNameList.size();

        // Looping through each row of data
        for (int i = 0; i < dataList.size(); i++) {
            List<Object> dataRow = dataList.get(i);

            // matching the data and column count
            if (dataRow.size() != columnCount) {
                return "Column and data count mismatch in row number " + (i + 1);
            }

            // append place holders when size equal
            SQL.append("(");
            SQL.append("?,".repeat(columnCount));
            // remove last comma
            SQL.setLength(SQL.length() - 1);
            SQL.append(")");

            if (i != dataList.size() - 1) {
                SQL.append(", ");
            }
        }

        // Statement and bind values
        try (Connection connection = db_connectionService.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SQL.toString())) {
            int paramIndex = 1;

            // loop through rows to bind values
            for (List<Object> row : dataList) {
                for (int i = 0; i < columnCount; i++) {
                    String column = columnNameList.get(i);
                    String type = columnTypeMap.get(column);
                    Object value = row.get(i);

                    // Bind values with its data type
                    PreparedStatementHelper.setValue(preparedStatement, paramIndex, value, type);
                    paramIndex++;
                }
            }

            preparedStatement.executeUpdate();
            return "success";
        } catch (Exception e) {
            System.out.println("\nInsert Data Failed. Exception: " + e);
            return "Insert data failed. Exception: " + e.getMessage();
        }
    }
    // #endregion - End - Insert Data SQL Query

    // #region - Start - Get Table Data SQL Query
    public List<Map<String, Object>> getTableData(String databaseName, String tableName) {

        List<Map<String, Object>> result = new ArrayList<>();
        // Data Validation
        if (databaseName == null || databaseName.trim().isEmpty() ||
                tableName == null || tableName.trim().isEmpty()) {
            System.out.println("\ndatabasename or tablename is missing\n");
            String errorMessage = "databasename or tablename is missing";
            result.add(Map.of("error", errorMessage));
            return result;
        }

        // SQL Command
        String SQL = String.format("SELECT * FROM %s.%s;", databaseName, tableName);

        try (Connection connection = db_connectionService.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(SQL)) {
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int columnCount = resultSetMetaData.getColumnCount();

            int rowCount = 1;
            while (resultSet.next()) {
                Map<String, Object> row = new LinkedHashMap<>();

                row.put("row", rowCount++);

                for (int i = 1; i <= columnCount; i++) {
                    row.put(resultSetMetaData.getColumnLabel(i), resultSet.getObject(i));
                }

                result.add(row);

            }

            return result;

        } catch (SQLException e) {
            System.out.println("\nFailed to fetch data. Exception: " + e + "\n");
            String errorMessage = "Failed to fetch data. Exception " + e.getMessage();
            result.add(Map.of("Failed", errorMessage));

            return result;
        }
    }
    // #endregion - End - Get Table Data SQL Query

    // #region - Start - Delete Data SQL Query
    public String deleteData(String databaseName, String tableName, String columnName, Object condition) {

        // Data Validation
        if (databaseName == null || databaseName.trim().isEmpty() ||
                tableName == null || tableName.trim().isEmpty() ||
                columnName == null || columnName.trim().isEmpty() ||
                condition == null) {
            System.out.println("\ndatabasename, tablename, columnName, condition: One of these is missing in body\n");
            String errorMessage = "databasename, tablename, columnName, condition: One of these is missing in body";
            return errorMessage;
        }

        // for removing extra spacing if any
        databaseName = databaseName.trim();
        tableName = tableName.trim();
        columnName = columnName.trim();

        // Delete implemntataion start
        String SQL;

        // To store column Data Type
        String columnDataType = new String();

        // Check if columnName and condition is all
        String check = condition.toString();
        if (columnName.equalsIgnoreCase("all") && check.equalsIgnoreCase("all")) {
            // SQL Command to delete all
            SQL = String.format("DELETE FROM %s.%s;", databaseName, tableName);
        } else {
            SQL = String.format("DELETE FROM %s.%s WHERE %s = ?;", databaseName, tableName, columnName);
            List<TableColumnInfo> columnInfo = getTableDetails(databaseName, tableName);

            // to store column name and data type mapping
            Map<String, String> columnTypeMap = new HashMap<>();

            // Creating column name and data type mapping
            for (TableColumnInfo column : columnInfo) {
                if (!column.getField().equalsIgnoreCase("Error")) {
                    columnTypeMap.put(column.getField(), normalizeType(column.getType()));
                } else {
                    return "Failed to fetch column details.";
                }
            }

            // Checking if column name exists
            if (columnTypeMap.containsKey(columnName)) {

                // Storing column data type
                columnDataType = columnTypeMap.get(columnName);
            } else {
                System.out.println("\nData deletion failed, column name does not exist\n");
                String errorMessage = "Data deletion failed, column name does not exist";
                return errorMessage;
            }
        }

        try (Connection connection = db_connectionService.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {

            PreparedStatementHelper.setValue(preparedStatement, 1, condition, columnDataType);
            // Already prepared the preparedStatement with SQL it will be stored here until
            // execution hence not passing again now
            preparedStatement.executeUpdate();
            System.out.println("\nData deleted successfully\n");
            return "success";

        } catch (SQLException e) {
            System.out.println("Data deletion failed. Exception: " + e);
            String errorMessage = "Data deletion failed. Exception:" + e.getMessage();
            return errorMessage;
        }
    }
    // #endregion - End - Delete data SQL Query

    // #region - Start - Export Data in CSV SQL Query
    public void exportTableToCSV(String databaseName, String tableName, HttpServletResponse response)
            throws IOException {

        // Data Validation
        if (databaseName == null || databaseName.trim().isEmpty() ||
                tableName == null || tableName.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Database name or table name is missing");
            return;
        }

        // Get data from getTableData
        List<Map<String, Object>> tableData = getTableData(databaseName, tableName);

        // Null data check
        if (tableData == null || tableData.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            response.getWriter().write("No data found in table");
            return;
        }

        // Set response headers for CSV download
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=" + tableName + "_export.csv");

        // Write CSV content to HttpServletResponse OutputStream
        PrintWriter writer = response.getWriter();

        // Remove 'row' key from first row to build header
        Map<String, Object> firstRow = new LinkedHashMap<>(tableData.get(0));
        firstRow.remove("row");

        // Write headers
        List<String> headers = new ArrayList<>(firstRow.keySet());
        writer.println(String.join(",", headers));

        // Write data rows
        for (Map<String, Object> row : tableData) {
            row.remove("row"); // Remove row number if present
            List<String> values = new ArrayList<>();

            for (String header : headers) {
                Object value = row.get(header);
                // Escape commas and double-quotes in values if needed
                String safeValue = value != null ? value.toString().replace("\"", "\"\"") : "";
                if (safeValue.contains(",") || safeValue.contains("\"")) {
                    safeValue = "\"" + safeValue + "\"";
                }
                values.add(safeValue);
            }

            writer.println(String.join(",", values));
        }

        writer.flush();
        writer.close();
    }
    // #endregion - End - Export Data in CSV SQL Query

    // #endregion - End - Table SQL Query
}
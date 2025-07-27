package com.mariadb.mariadb.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mariadb.mariadb.services.DefaultSQLQuery;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

// import custom object to store response for get table details
import com.mariadb.mariadb.model.TableColumnInfo;

@RestController
@RequestMapping("/default-sql")
public class DefaultSQLQueryController {
    private final DefaultSQLQuery defaultSQLQuery;

    public DefaultSQLQueryController(DefaultSQLQuery defaultSQLQuery) {
        this.defaultSQLQuery = defaultSQLQuery;
    }

    // #region - Start - Database SQL Query Controller

    // #region - Start - Create Database Controller
    @PostMapping("/create-database")
    public String createDatabase(
            @RequestParam(value = "dbName", required = false) String dbNameParam,
            @RequestBody(required = false) Map<String, String> dbNameBody) {

        String dbName = dbNameParam != null ? dbNameParam : dbNameBody != null ? dbNameBody.get("dbName") : null;

        if (dbName == null || dbName.isEmpty()) {
            return "Database name not provided";
        }
        boolean success = defaultSQLQuery.createDatabase(dbName);
        return success ? "Database Created: " + dbName : "Database Creation Failed: " + dbName;
    }
    // #endregion - End - Create Database Controller

    // #region - Start - Get Database Controller
    @GetMapping("/get-databases")
    public List<String> getDatabases() {
        return defaultSQLQuery.getDatabases();
    }
    // #endregion - End - Get Database Controller

    // #region - Start - Delete Database Controller
    @DeleteMapping("/delete-database")

    public String deleteDatabase(
            @RequestParam(value = "dbName", required = false) String dbNameParam,
            @RequestBody(required = false) Map<String, String> dbNameBody) {
        String dbName = dbNameParam != null ? dbNameParam : dbNameBody != null ? dbNameBody.get("dbName") : null;

        if (dbName == null || dbName.isEmpty()) {
            return "Database name not provided";
        }

        boolean success = defaultSQLQuery.deleteDatabase(dbName);
        return success ? "Database Deleted: " + dbName : "Database Deletion Failed: " + dbName;
    }
    // #endregion - End - Delete Database Controller

    // #endregion - End - Database SQL Query Controller

    // #region - Start - User SQL Query Controller

    // #region - Start - Create User SQL Query Controller
    @PostMapping("/create-user")
    public String createUser(@RequestBody Map<String, String> createUserBody) {
        // Required Fields
        String userName = createUserBody.get("username");
        String password = createUserBody.get("password");
        String host = createUserBody.get("host");
        String access = createUserBody.get("access");
        String database = createUserBody.get("database");

        // Null and Empty Validation
        if (userName == null || password == null || host == null || access == null
                || database == null || userName.trim().isEmpty() || password.trim().isEmpty()
                || host.trim().isEmpty() || access.trim().isEmpty() || database.trim().isEmpty()) {
            System.out.println("Required field missing. " +
                    "Please make sure the below fields are present:\n1. username \n2. password\n" +
                    "3. host \n4. access\n5. Database \n" + "Make sure that the characters are same.\n");
            return "User creation failed.";
        }

        userName = userName.trim();
        password = password.trim();
        host = host.trim();
        access = access.trim();
        database = database.trim();
        boolean success = defaultSQLQuery.createUser(createUserBody);

        return success ? "User created: " + userName : "User Creation Failed" + userName;
    }
    // #endregion - End - Create User SQL Query Controller

    // #region - Start - Get User SQL Query Controller
    @GetMapping("/get-users")
    public List<String> getUsers() {
        return defaultSQLQuery.getUsers();
    }
    // #endregion - End - Get User SQL Controller

    // #region - Start - Get User Privileges SQL Query Controller
    @GetMapping("/get-user-privileges")
    public List<String> getUserPrivileges(@RequestParam(value = "username", required = false) String userNameParam,
            @RequestParam(value = "userhost", required = false) String userHostParam,
            @RequestBody(required = false) Map<String, String> bodyParams) {
        List<String> userPrivileges = new ArrayList<String>();
        String userName = userNameParam == null ? (bodyParams != null ? bodyParams.get("username") : null)
                : (userNameParam.isEmpty() ? (bodyParams != null ? bodyParams.get("username") : null) : userNameParam);
        String userHost = userHostParam == null ? (bodyParams != null ? bodyParams.get("userhost") : null)
                : (userHostParam.isEmpty() ? (bodyParams != null ? bodyParams.get("userhost") : null) : userHostParam);

        if (userName == null || userName.trim().isEmpty() || userHost == null || userHost.trim().isEmpty()) {
            System.out.println("User's name or user's host is missing.\n"
                    + "Please provide the data in body or urlas below variable:\n1. username \n2. userhost");
            userPrivileges.add("User's name or user's host is missing.\n"
                    + "Please provide the data in body or urlas below variable:\n1. username \n2. userhost");
            return userPrivileges;
        }

        userPrivileges = defaultSQLQuery.getUserPrivileges(userName, userHost);

        return userPrivileges;
    }
    // #endregion - End - Get User Privileges SQL Query Controller

    // #region - Start - User Grant Access SQL Query Controller
    @PostMapping("/user-grant-access")
    public boolean userGrantAccessSQL(@RequestBody Map<String, String> bodyParams) {

        // Check if body is null
        if (bodyParams == null) {
            System.out.println("Body for params is missing\n");
            return false;
        }

        // Check is username is present and not empty
        if (bodyParams.get("username") == null || bodyParams.get("username").trim().isEmpty()) {
            System.out.println("username is missing in body\n");
            return false;
        }

        String userName = bodyParams.get("username");

        // Check if userhost is present and not empty
        if (bodyParams.get("userhost") == null || bodyParams.get("userhost").trim().isEmpty()) {
            System.out.println("userhost is missing in body\n");
            return false;
        }

        String userHost = bodyParams.get("userhost");

        // Check if access is present and not empty
        if (bodyParams.get("access") == null || bodyParams.get("access").trim().isEmpty()) {
            System.out.println("access is missing in body\n");
            return false;
        }

        String userAccess = bodyParams.get("access");

        // Check if database is present and not empty
        if (bodyParams.get("database") == null || bodyParams.get("database").trim().isEmpty()) {
            System.out.println("database is missing in body\n");
            return false;
        }

        String databaseName = bodyParams.get("database");

        // Check if the table is present and not empty
        if (bodyParams.get("table") == null || bodyParams.get("table").trim().isEmpty()) {
            System.out.println("table is missing in body\n");
            return false;
        }

        String tableName = bodyParams.get("table");

        return defaultSQLQuery.userGrantAccess(userName, userHost, userAccess, databaseName, tableName);
    }
    // #endregion - End - User Grant Access SQL Query Controller

    // #region - Start - User Revoke Access SQL Query Controller
    @PostMapping("/user-revoke-access")
    public boolean userRevokeAccessSQL(@RequestBody Map<String, String> bodyParams) {

        // Check if body is null
        if (bodyParams == null) {
            System.out.println("Body for params is missing\n");
            return false;
        }

        // Check is username is present and not empty
        if (bodyParams.get("username") == null || bodyParams.get("username").trim().isEmpty()) {
            System.out.println("username is missing in body\n");
            return false;
        }

        String userName = bodyParams.get("username");

        // Check if userhost is present and not empty
        if (bodyParams.get("userhost") == null || bodyParams.get("userhost").trim().isEmpty()) {
            System.out.println("userhost is missing in body\n");
            return false;
        }

        String userHost = bodyParams.get("userhost");

        // Check if access is present and not empty
        if (bodyParams.get("access") == null || bodyParams.get("access").trim().isEmpty()) {
            System.out.println("access is missing in body\n");
            return false;
        }

        String userAccess = bodyParams.get("access");

        // Check if database is present and not empty
        if (bodyParams.get("database") == null || bodyParams.get("database").trim().isEmpty()) {
            System.out.println("database is missing in body\n");
            return false;
        }

        String databaseName = bodyParams.get("database");

        // Check if the table is present and not empty
        if (bodyParams.get("table") == null || bodyParams.get("table").trim().isEmpty()) {
            System.out.println("table is missing in body\n");
            return false;
        }

        String tableName = bodyParams.get("table");

        return defaultSQLQuery.userRevokeAccess(userName, userHost, userAccess, databaseName, tableName);
    }
    // #endregion - End - User Revoke Access SQL Query Controller

    // #region - Start - Delete User SQL Query Controller
    @DeleteMapping("/delete-user")
    public boolean deleteUser(@RequestParam(value = "username", required = false) String userNameParam,
            @RequestParam(value = "userhost", required = false) String userHostParam,
            @RequestBody(required = false) Map<String, String> bodyParams) {
        String userName = userNameParam == null ? (bodyParams != null ? bodyParams.get("username") : null)
                : (userNameParam.isEmpty() ? (bodyParams != null ? bodyParams.get("username") : null) : userNameParam);
        String userHost = userHostParam == null ? (bodyParams != null ? bodyParams.get("userhost") : null)
                : (userHostParam.isEmpty() ? (bodyParams != null ? bodyParams.get("userhost") : null) : userHostParam);

        if (userName == null || userName.trim().isEmpty() || userHost == null || userHost.trim().isEmpty()) {
            System.out.println("User's name or user's host is missing.\n");
            return false;
        }
        return defaultSQLQuery.deleteUser(userName, userHost);
    }
    // #endregion - End - Delete User SQL Query Controller

    // #endregion - End - User SQL Query Controller

    // #region - Start - Table SQL Query Controller

    // #region - Start - Create Table SQL Query Controller
    @PostMapping("/create-table")
    public String createTable(@RequestBody Map<String, Object> bodyParam) {
        String output;
        String databaseName = (String) bodyParam.get("databasename");
        String tableName = (String) bodyParam.get("tablename");

        // Storing all dolumn details in a list which contains a map to fetch string :
        // string
        List<Map<String, String>> columns = (List<Map<String, String>>) bodyParam.get("columns");

        // databaseName validation check
        if (databaseName == null || databaseName.trim().isEmpty()) {
            output = "databasename is missing, please enter the databasename and try again\n.";
            System.out.println(output);
            return output;
        }

        // tableName validation check
        if (tableName == null || tableName.trim().isEmpty()) {
            output = "tablename is missing, please enter the tablename and try again\n.";
            System.out.println(output);
            return output;
        }

        // no check for contraints as they are optional

        // to store column definition list
        List<String> columnDefinitionList = new ArrayList<String>();

        for (Map<String, String> columnDetails : columns) {
            String columnName = columnDetails.get("name");
            String columnDataType = columnDetails.get("type");
            String columnConstraint = columnDetails.get("constraint");

            // columnName validation check
            if (columnName == null || columnName.trim().isEmpty()) {
                output = "one of coulmn name is missing, please enter the column name and try again\n.";
                System.out.println(output);
                return output;
            }

            // columnDataType validation check
            if (columnDataType == null || columnDataType.trim().isEmpty()) {
                output = "one of column data type is missing, please enter the column data type and try again\n.";
                System.out.println(output);
                return output;
            }

            // store details in columnDefination after validation
            columnDefinitionList.add(columnName + " " + columnDataType + " " + columnConstraint);

        }

        if (defaultSQLQuery.createTable(databaseName, tableName, columnDefinitionList)) {
            return "Table created successfully: " + tableName;
        } else {
            return "Table creation failed: " + tableName;
        }

    }
    // #endregion - End - Create Table SQL Query Controller

    // #region - Start - Get Table SQL Query Controller
    @GetMapping("/get-tables")
    public List<String> getTables(@RequestParam("databasename") String databaseName) {
        if (databaseName == null || databaseName.trim().isEmpty()) {
            System.out.println("\ndatabasename is missing.\n");
            List<String> output = new ArrayList<String>();
            output.add("databasename is missing.");
            return output;
        }
        return defaultSQLQuery.getTable(databaseName);
    }
    // #endregion - End - Get Table SQL Query Controller

    // #region - Start - Get Table Details SQL Query Controller
    @GetMapping("/get-table-details")
    public ResponseEntity<?> getTableDetails(@RequestParam("databasename") String databaseName,
            @RequestParam("tablename") String tableName) {
        // Empty input check
        if (databaseName == null || databaseName.trim().isEmpty() || tableName == null || tableName.trim().isEmpty()) {
            System.out.println("\ndatabasename or tablename is missing\n");
            String errormsg = "databasename or tablename is missing";
            return ResponseEntity.badRequest().body(errormsg);
        }

        List<TableColumnInfo> tableinfo = defaultSQLQuery.getTableDetails(databaseName, tableName);
        return ResponseEntity.ok(tableinfo);
    }
    // #endregion - End - Get Table Details SQL Query Controller

    // #region - Start - Add Column SQL Query Controller
    @PostMapping("/add-column")
    public ResponseEntity<?> addColumn(@RequestBody Map<String, String> bodyParam) {
        String databaseName = bodyParam.get("databasename");
        String tableName = bodyParam.get("tablename");
        String columnName = bodyParam.get("columnname");
        String dataType = bodyParam.get("datatype");
        String nullable = bodyParam.get("nullable");
        String defaultValue = bodyParam.get("defaultvalue");
        String key = bodyParam.get("key");
        String extra = bodyParam.get("extra");

        if (databaseName == null || databaseName.trim().isEmpty() ||
                tableName == null || tableName.trim().isEmpty() ||
                columnName == null || columnName.trim().isEmpty() ||
                dataType == null || dataType.trim().isEmpty()) {
            String errorMessage = "one or more required fields are missing";
            System.out.println("\none or more required fields are missing\n");
            return ResponseEntity.badRequest().body(errorMessage);
        }

        // removing white spaces
        databaseName = databaseName.trim();
        tableName = tableName.trim();
        columnName = columnName.trim();
        dataType = dataType.trim();

        // we can pass null value in service function if that function can handle it
        // however not a good practice and hence removing null and pass it as string
        // even if empty
        nullable = (nullable != null) ? nullable.trim() : "";
        defaultValue = (defaultValue != null) ? defaultValue.trim() : "";
        key = (key != null) ? key.trim() : "";
        extra = (extra != null) ? extra.trim() : "";

        String result = defaultSQLQuery.addColumn(databaseName, tableName, columnName, dataType, nullable, defaultValue,
                key, extra);

        if (result.equalsIgnoreCase("success")) {
            return ResponseEntity.ok("Column Added Successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add column\n" + result);
        }
    }
    // #endregion - End - Add Column SQL Query Controller

    // #region - Start - Delete Column SQL Query Controller
    @DeleteMapping("/delete-column")
    public ResponseEntity<?> deleteColumn(@RequestParam(value = "databasename") String databaseName,
            @RequestParam(value = "tablename") String tableName,
            @RequestParam(value = "columnname") String columnName) {

        // Data Validation
        if (databaseName == null || databaseName.trim().isEmpty() ||
                tableName == null || tableName.trim().isEmpty() ||
                columnName == null || columnName.trim().isEmpty()) {
            System.out.println("\n One of databasename, tablename, columnname is missing\n");
            String errorMessage = "One of databasename, tablename, columnname is missing";
            return ResponseEntity.badRequest().body(errorMessage);
        }

        // Remove Spacing
        databaseName = databaseName.trim();
        tableName = tableName.trim();
        columnName = columnName.trim();

        String output = defaultSQLQuery.deleteColumn(databaseName, tableName, columnName);

        if (output.equalsIgnoreCase("success")) {
            System.out.println("\nColumn Deleted\n");
            return ResponseEntity.ok().body("Column Deleted Succesfully");
        } else {
            System.out.println("\nDelete Column Failed: " + output + " \n");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(output);
        }
    }
    // #endregion - End - Delete Column SQL Query Controller

    // #region - Start - Insert Data SQL Query Controller
    @PostMapping("/insert-data")
    public ResponseEntity<?> insertData(@RequestBody Map<String, Object> bodyParam) {
        String databaseName = (String) bodyParam.get("databasename");
        String tableName = (String) bodyParam.get("tablename");
        List<String> columnNameList;
        List<List<Object>> dataList;

        // Data Validation for databaseName and tableName
        if (databaseName == null || databaseName.trim().isEmpty() ||
                tableName == null || tableName.trim().isEmpty()) {
            System.out.println("\ndatabasename or table name is missing\n");
            String errorMessage = "databasename or table name is missing";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }

        // column and data validation
        try {
            columnNameList = (List<String>) bodyParam.get("columns");
            dataList = (List<List<Object>>) bodyParam.get("data");

            if (columnNameList == null || columnNameList.isEmpty() ||
                    dataList == null || dataList.isEmpty()) {
                System.out.println("columnname or data is empty");
                String errorMessage = "columnname or data is empty";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
            }
        } catch (ClassCastException e) {
            System.out.println("Invalid Data format: " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Data format" + e.getMessage());

        }

        columnNameList = (List<String>) bodyParam.get("columns");
        dataList = (List<List<Object>>) bodyParam.get("data");

        String output = defaultSQLQuery.insertData(databaseName, tableName, columnNameList, dataList);

        if (output.equalsIgnoreCase("success")) {
            System.out.println("\nData Inserted Successfully\n");
            return ResponseEntity.status(HttpStatus.OK).body(output);
        } else {
            System.out.println("\nData Insert failed. Exception: " + output);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(output);
        }
    }
    // #endregion - End - Insert Data SQL Query Controller

    // #region - Start - Get Table Data SQL Query Controller
    @GetMapping("/get-table-data")
    public ResponseEntity<?> getTableData(@RequestParam(value = "databasename") String databaseName,
            @RequestParam(value = "tablename") String tableName) {

        // Data Validation
        if (databaseName == null || databaseName.trim().isEmpty() ||
                tableName == null || tableName.trim().isEmpty()) {
            System.out.println("\ndatabasename or tablename is missing\n");
            String errorMessage = "databasename or tablename is missing";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }

        List<Map<String, Object>> result = defaultSQLQuery.getTableData(databaseName, tableName);

        return ResponseEntity.ok().body(result);
    }
    // #endregion - End - Get Data SQL Query Controller

    // #region - Start - Delete Data SQL Query Controller
    @DeleteMapping("/delete-data")
    public ResponseEntity<?> deleteData(@RequestBody Map<String, Object> bodyParam) {

        String databaseName = (String) bodyParam.get("databasename");
        String tableName = (String) bodyParam.get("tablename");
        String columnName = (String) bodyParam.get("columnName");
        Object condition = bodyParam.get("condition");

        // Data Validation
        if (databaseName == null || databaseName.trim().isEmpty() ||
                tableName == null || tableName.trim().isEmpty() ||
                columnName == null || columnName.trim().isEmpty() ||
                condition == null) {
            System.out.println("\ndatabasename, tablename, columnName, condition: One of these is missing in body\n");
            String errorMessage = "databasename, tablename, columnName, condition: One of these is missing in body";

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data Deletion Failed: " + errorMessage);
        }

        // for removing extra spacing if any
        databaseName = databaseName.trim();
        tableName = tableName.trim();
        columnName = columnName.trim();

        String result = defaultSQLQuery.deleteData(databaseName, tableName, columnName, condition);

        if (result.equalsIgnoreCase("success")) {
            return ResponseEntity.ok().body("Data deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Data Deletion Failed. Error: " + result);
        }
    }
    // #endregion - End - Delete data SQL Query Controller

    // #region - Start - Export Table to CSV Controller
    @GetMapping("/export-table")
    public void exportTableToCSV(
            @RequestParam(value = "databasename") String databaseName,
            @RequestParam(value = "tablename") String tableName,
            HttpServletResponse response) throws IOException {

        // Input Validation
        if (databaseName == null || databaseName.trim().isEmpty() ||
                tableName == null || tableName.trim().isEmpty()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return;
        }

        // Remove extra spaces
        databaseName = databaseName.trim();
        tableName = tableName.trim();

        // Set response headers for CSV file download
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=" + tableName + "_export.csv");

        // Call the service to write CSV content directly to the response
        defaultSQLQuery.exportTableToCSV(databaseName, tableName, response);
    }
    // #endregion - End - Export Table to CSV Controller

    // #endregion - End - Table SQL Query Controller

}

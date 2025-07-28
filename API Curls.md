# 📘 API CURL Requests – MariaDB Management Server

This document lists all available cURL requests to interact with the MariaDB management backend API.

If you want to miplement this on your laptop use

http://localhost:8080 instead of https://f164f1086b2a.ngrok-free.app

---

## 🗄️ DATABASE OPERATIONS

### 🔹 Create Database

```bash
curl --location 'https://f164f1086b2a.ngrok-free.app/default-sql/create-database' \
--header 'Content-Type: application/json' \
--data '{
    "dbName" : "test1"
}'
```

### 🔹 Get All Databases

```bash
curl --location 'https://f164f1086b2a.ngrok-free.app/default-sql/get-databases'
```

### 🔹 Delete Database

```bash
curl --location --request DELETE 'https://f164f1086b2a.ngrok-free.app/default-sql/delete-database' \
--header 'Content-Type: application/json' \
--data '{
    "dbName" : "test4"
}'
```

---

## 👤 USER MANAGEMENT

### 🔹 Create User

```bash
curl --location 'https://f164f1086b2a.ngrok-free.app/default-sql/create-user' \
--header 'Content-Type: application/json' \
--data '{
    "username" : "test1",
    "password" : "test1",
    "host" : "192.168.0.194",
    "access": "All",
    "database" : "test4"
}'
```

### 🔹 Get All Users

```bash
curl --location 'https://f164f1086b2a.ngrok-free.app/default-sql/get-users'
```

### 🔹 Get User Privileges

```bash
curl --location --request GET 'https://f164f1086b2a.ngrok-free.app/default-sql/get-user-privileges' \
--header 'Content-Type: application/json' \
--data '{
    "username" : "test3",
    "userhost" : "192.168.0.194"
}'
```

### 🔹 Grant User Access

```bash
curl --location 'https://f164f1086b2a.ngrok-free.app/default-sql/user-grant-access' \
--header 'Content-Type: application/json' \
--data '{
    "username" : "test3",
    "userhost" : "192.168.0.194",
    "access" : "Select, insert",
    "database" : "test3",
    "table" : "all"
}'
```

### 🔹 Revoke User Access

```bash
curl --location 'https://f164f1086b2a.ngrok-free.app/default-sql/user-revoke-access' \
--header 'Content-Type: application/json' \
--data '{
    "username" : "test3",
    "userhost" : "192.168.0.194",
    "access" : "Select, insert",
    "database" : "test3",
    "table" : "all"
}'
```

### 🔹 Delete User

```bash
curl --location --request DELETE 'https://f164f1086b2a.ngrok-free.app/default-sql/delete-user' \
--header 'Content-Type: application/json' \
--data '{
    "username" : "test4",
    "userhost" : "192.168.0.194"
}'
```

---

## 📋 TABLE OPERATIONS

### 🔹 Create Table

```bash
curl --location 'https://f164f1086b2a.ngrok-free.app/default-sql/create-table' \
--header 'Content-Type: application/json' \
--data '{
  "databasename": "test1",
  "tablename": "table2",
  "columns": [
    {
      "name": "id",
      "type": "INT",
      "constraints": "PRIMARY KEY"
    },
    {
      "name": "name",
      "type": "VARCHAR(100)",
      "constraints": ""
    }
  ]
}'
```

### 🔹 Get Tables in a Database

```bash
curl --location 'https://f164f1086b2a.ngrok-free.app/default-sql/get-tables?databasename=test1'
```

### 🔹 Get Table Details

```bash
curl --location 'https://f164f1086b2a.ngrok-free.app/default-sql/get-table-details?databasename=test1&tablename=table2'
```

### 🔹 Add Column

```bash
curl --location 'https://f164f1086b2a.ngrok-free.app/default-sql/add-column' \
--header 'Content-Type: application/json' \
--data '{
    "databasename": "test1",
    "tablename": "table2",
    "columnname": "column4",
    "datatype": "VARCHAR(20)",
    "nullable": "",
    "defaultvalue": "",
    "key": "",
    "extra": ""
}'
```

### 🔹 Delete Column

```bash
curl --location --request DELETE 'https://f164f1086b2a.ngrok-free.app/default-sql/delete-column?databasename=test1&tablename=table1&columnname=name'
```

---

## 🧩 TABLE DATA MANAGEMENT

### 🔹 Insert Data into Table

```bash
curl --location 'https://f164f1086b2a.ngrok-free.app/default-sql/insert-data' \
--header 'Content-Type: application/json' \
--data '{
  "databasename": "test1",
  "tablename": "table1",
  "columns": ["column1", "column2"],
  "data": [
    [14, "D - 14"],
    [23, "D - 23"],
    [32, "D - 32"],
    [41, "D - 41"]
  ]
}'
```

### 🔹 Get Table Data

```bash
curl --location 'https://f164f1086b2a.ngrok-free.app/default-sql/get-table-data?databasename=test1&tablename=table2'
```

### 🔹 Delete Data from Table

```bash
curl --location --request DELETE 'https://f164f1086b2a.ngrok-free.app/default-sql/delete-data' \
--header 'Content-Type: application/json' \
--data '{
  "databasename": "test1",
  "tablename": "table2",
  "columnName": "all",
  "condition": "all"
}'
```

### 🔹 Export Table to CSV

```bash
curl --location 'https://f164f1086b2a.ngrok-free.app/default-sql/export-table?databasename=test1&tablename=table1'
```

---

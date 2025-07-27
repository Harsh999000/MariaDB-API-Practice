package com.mariadb.mariadb.utility;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class PreparedStatementHelper {
    public static void setValue(PreparedStatement preparedStatement, int index, Object value, String type)
            throws SQLException {
        if (value == null) {
            preparedStatement.setNull(index, Types.NULL);
            return;
        }

        switch (type.toLowerCase()) {
            case "int":
                preparedStatement.setInt(index, (Integer) value);
                break;
            case "double":
                preparedStatement.setDouble(index, (Double) value);
                break;
            case "float":
                preparedStatement.setFloat(index, (Float) value);
                break;
            case "boolean":
                preparedStatement.setBoolean(index, (Boolean) value);
                break;
            case "date":
                preparedStatement.setDate(index, java.sql.Date.valueOf(value.toString()));
                break;
            case "time":
                preparedStatement.setTime(index, java.sql.Time.valueOf(value.toString()));
                break;
            case "datetime":
                preparedStatement.setTimestamp(index, java.sql.Timestamp.valueOf(value.toString()));
                break;
            default:
                preparedStatement.setString(index, value.toString());
                break;
        }
    }
}

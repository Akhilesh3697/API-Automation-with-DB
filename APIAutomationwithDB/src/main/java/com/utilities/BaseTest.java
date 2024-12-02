package com.utilities;

import java.io.PrintWriter;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class BaseTest {

    public static Statement statement;
    public static ResultSet resultset;
    public static ResultSet resultSet1;

    public static Connection getConnection() throws SQLException {

        try {
            Class.forName(PropRepo.getString("class.forname"));
        }
        catch (ClassNotFoundException ex) {
            System.err.println("Driver not found");
        }
        // build connection properties
        Properties properties = new Properties();
        properties.put("user", PropRepo.getString("snowflake.username"));
        properties.put("password", PropRepo.getString("snowflake.password"));
        properties.put("account", PropRepo.getString("snowflake.accountname"));
        properties.put("db", PropRepo.getString("snowflake.dbname"));
        properties.put("schema", PropRepo.getString("snowflake.schemaname"));

        // create a new connection
        String connectStr = System.getenv("SF_JDBC_CONNECT_STRING");

        //use the default connection string if it is not set in environment
        if(connectStr == null) {
            connectStr = PropRepo.getString("snowflake.connectorstring");
        }
        return DriverManager.getConnection(connectStr, properties);
    }

    public static void getDBcolumns(String query, List<String> colunames, Connection connection)
            throws SQLException {
        statement=connection.createStatement();
        resultset=statement.executeQuery(query);
        if(resultset !=null) {
            while(resultset.next()) {
                colunames.add(resultset.getString("COLUMN_NAME"));
            }
        }
    }

    public static String compareAPIandDBValues(Map<String, String> APIValues, List<String> columnnames, String column,
                                               PrintWriter out) throws SQLException {
        for(int k=0; k<columnnames.size(); k++) {
            String namecheck = columnnames.get(k);
            String dbname= columnnames.get(k);
            String hashmap="";

            if(!(namecheck.equals("ETLEXECUTIONLOGID") || namecheck.equals("DATECREATED") || namecheck.equals("DATEMODIFIED")
                    || namecheck.equals("DELETEDDATE") || namecheck.equals("ISDELETED"))) {
                if(APIValues.getOrDefault(dbname, "Empty").trim().isEmpty() ||
                        APIValues.getOrDefault(dbname, "Empty").equals("null"))
                    hashmap="Empty";
                else
                    hashmap=APIValues.getOrDefault(dbname, "Empty");

                if((resultSet1.getString(namecheck) == null ? "Empty" : resultSet1.getString(namecheck))
                        .equals(hashmap) == false) {
                    out.println(
                            "DB:" + (resultSet1.getString(namecheck) == null ? "Empty" : resultSet1.getString(namecheck))
                                    + "\t" + "API:" + (hashmap));
                    column = column + namecheck + "\t";
                }
            }
        }
        return column;
    }

    public static void printSQLException(SQLException ex) {
        for (Throwable e :ex) {
            if(e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.print("SQLState: " + ((SQLException) e).getSQLState());
                System.err.print("Error code: "+ ((SQLException) e).getErrorCode());
                System.err.print("Message: " + e.getMessage());
                Throwable t=ex.getCause();
                while(t!=null) {
                    System.out.println("cause="+t);
                    t=t.getCause();
                }
            }
        }
    }
}

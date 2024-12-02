package com.details;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class APIConnectionDetails {

    public final static String serviceNowBasicAuthUsername = "";//Enter your ServiceNow Basic Auth Username

    public final static String serviceNowBasicAuthPassword = "";//Enter your ServiceNow Basic Auth Password

    public final static String serviceNowBaseUrl = "";//Enter your ServiceNow Base URL

    public static String getServiceNowBasicAuthUsername() {
        return serviceNowBasicAuthUsername;
    }

    public static String getServiceNowBasicAuthPassword() { return serviceNowBasicAuthPassword; }

    public static String getServiceNowBaseUrl() { return serviceNowBaseUrl; }

    //Snowflake connection Details
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("net.snowflake.client.jdbc.SnowflakeDriver");
        } catch (ClassNotFoundException ex) {
            System.err.println("Driver not found");
        }

        // build connection properties
        Properties properties = new Properties();
        properties.put("user", ""); //Enter your Snowflake Username
        properties.put("password", ""); //Enter your Snowflake Password
        properties.put("account", ""); //Enter your Snowflake Account Name
        properties.put("db", ""); //Enter your Snowflake Database Name
        properties.put("schema", ""); //Enter your Snowflake Schema Name

        // create a new connection
        String connectStr = System.getenv("SF_JDBC_CONNECT_STRING");

        // use the default connection string if it is not set in environment
        if (connectStr == null) {
            connectStr = "jdbc:snowflake://"; //Enter your Snowflake URL
        }
        return DriverManager.getConnection(connectStr, properties);
    }
}

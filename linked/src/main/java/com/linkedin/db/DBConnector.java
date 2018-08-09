package com.linkedin.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.commons.dbcp.*;


import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;


public class DBConnector {
    private static final String host   = "jdbc:mysql://localhost:3306/linkedin?autoReconnect=true&useSSL=false";
    private static final String uName  = "root";
    private static final String uPass  = "root";
    private static final String driver = "com.mysql.jdbc.Driver";

    private static DBConnector dataSource;
    private BasicDataSource ds;

    public DBConnector () throws IOException, SQLException {
        ds = new BasicDataSource();
        ds.setDriverClassName(driver);
        ds.setUsername(uName);
        ds.setPassword(uPass);
        ds.setUrl(host);

        ds.setMinIdle(5);
        ds.setMaxIdle(20);
        ds.setMaxOpenPreparedStatements(180);
    }

    public  static DBConnector getInstance() throws IOException, SQLException {
        if (dataSource == null) {
            dataSource = new DBConnector();
        }
        return dataSource;
    }

    public Connection getConnection() throws SQLException {
        return this.ds.getConnection();
    }

}

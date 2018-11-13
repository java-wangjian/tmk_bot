package com.zxxkj.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcConn {
    public final static String classpath = Thread.currentThread().getContextClassLoader().getResource("/").getPath();
    public final static String configpath=classpath+"config.properties";
	private final static String URL= ParameterProperties.getValue(configpath,"mysql.dlsrzurl");
	private final static String USERNAME=ParameterProperties.getValue(configpath,"mysql.user");
	private final static String PASSWORD=ParameterProperties.getValue(configpath,"mysql.password");
	
    public static Connection printProducts() {
        Connection c = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            try {
                //建立连接
                c = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return c;
    }

    public static void close(Connection conn, Statement statement, ResultSet resultSet) {

        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

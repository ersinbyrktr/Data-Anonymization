package org.anonymization.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySqlService extends RelationalDBService {
    private static DatabaseConfig dbconfig = null;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("MySql sql driver is not included. Please include the required postgres jar and rerun " +
                    "the program");

            System.exit(1);
        }
    }

    public MySqlService(DatabaseConfig dbconfig) {
        this.dbconfig = dbconfig;
    }

    public Connection getConnection() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(dbconfig.getConnectStr() + "/" + dbconfig.getDb(), dbconfig.getUser(),
                    dbconfig.getPassword());
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return con;
    }


}
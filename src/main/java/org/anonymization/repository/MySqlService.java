package org.anonymization.repository;

import org.anonymization.model.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySqlService extends RelationalDBService {
    private final DatabaseConfig dbConfig;

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

    public MySqlService(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(dbConfig.getConnectStr() + "/" + dbConfig.getDb(), dbConfig.getUser(),
                    dbConfig.getPassword());
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return null;
        }
    }


}
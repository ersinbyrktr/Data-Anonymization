package org.anonymization.examples;

import org.anonymization.model.DatabaseConfig;
import org.anonymization.repository.MySqlService;
import org.deidentifier.arx.ARXResult;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.anonymization.examples.Example.*;

public class ExampleMySQL {

    public static void main(String[] args) throws IOException, SQLException {
        DatabaseConfig mysqlConfig = new DatabaseConfig();
        String query = "select * from anon";
        mysqlConfig.setConnectStr("jdbc:mysql://18.185.114.122:3306");
        mysqlConfig.setDb("peng");
        mysqlConfig.setUser("peng");
        mysqlConfig.setPassword("admin");

        MySqlService ps = new MySqlService(mysqlConfig);
        Connection con = ps.getConnection();
        ResultSet rs = ps.executeQuery(con, query);

        addDataToARX(rs);
        data.getDefinition().setAttributeType("zip", ps.createStarHierarchy(con, "zip", "anon"));

        ARXResult result = getResult();
        processResults(result);

    }

}

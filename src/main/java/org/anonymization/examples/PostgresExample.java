package org.anonymization.examples;

import org.anonymization.repository.DatabaseConfig;
import org.anonymization.repository.PostgresService;
import org.deidentifier.arx.ARXResult;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static org.anonymization.examples.Example.data;
import static org.anonymization.examples.Example.getResult;
import static org.anonymization.examples.Example.processResults;

public class PostgresExample {
    public static void main(String[] args) throws IOException,SQLException {
        DatabaseConfig postgresConf = new DatabaseConfig();
        String query = "select * from anon";
        postgresConf.setConnectStr("jdbc:postgresql://localhost:5432");
        postgresConf.setDb("peng");
        postgresConf.setUser("peng");
        postgresConf.setPassword("admin");

        PostgresService ps = new PostgresService(postgresConf);
        Connection con = ps.getConnection();
        ResultSet rs = ps.executeQuery(con,query);

        if (rs!=null){
            ResultSetMetaData rsmd =rs.getMetaData();
            int maxCols = rsmd.getColumnCount();
            String[] cols= new String[maxCols];
            for(int i=1;i<=maxCols;i++){
                cols[i-1]=rsmd.getColumnName(i);
            }

            data.add(cols);
            while (rs.next()){
                String[] vals = new String[maxCols];
                for (int i = 1; i <= maxCols; i++) {
                    vals[i-1] = rs.getString(i);
                }
                data.add(vals);
            }

        }
        data.getDefinition().setAttributeType("zip",ps.createHierarchy(con,"zip","anon"));
        data.getDefinition().setAttributeType("age",ps.createHierarchy(con,"age","anon"));
        ARXResult result = getResult();
        processResults(result);
    }
}
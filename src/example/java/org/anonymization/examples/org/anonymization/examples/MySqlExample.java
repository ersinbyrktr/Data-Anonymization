package org.anonymization.examples;

import org.anonymization.repository.DatabaseConfig;
import org.anonymization.repository.MySqlService;
import org.deidentifier.arx.ARXResult;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static org.anonymization.examples.Example.*;

public class MySqlExample {
    public static void main(String[] args) throws IOException,SQLException {
        DatabaseConfig mysqlConfig = new DatabaseConfig();
        String query = "select * from anon";
        mysqlConfig.setConnectStr("jdbc:mysql://192.168.52.130:3306");
        mysqlConfig.setDb("wordpress");
        mysqlConfig.setUser("wordpress");
        mysqlConfig.setPassword("wordpress");

        MySqlService ps = new MySqlService(mysqlConfig);
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
                    if(rs.getString(i)==null){
                        vals[i - 1] = "empty";
                    }else{
                        vals[i - 1] = rs.getString(i);
                    }
                }
                data.add(vals);
            }

        }
        data.getDefinition().setAttributeType("zip",ps.createHierarchy(con,"zip","anon"));

        ARXResult result = getResult();
        processResults(result);

    }
}

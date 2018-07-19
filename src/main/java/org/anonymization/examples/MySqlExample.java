package org.anonymization.examples;

import static org.anonymization.examples.Example.data;
import static org.anonymization.examples.Example.getResult;
import static org.anonymization.examples.Example.processResults;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.anonymization.repository.DatabaseConfig;
import org.anonymization.repository.MySqlService;
import org.deidentifier.arx.ARXResult;

public class MySqlExample {

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

        processResult(rs);
        data.getDefinition().setAttributeType("zip", ps.createStarHierarchy(con, "zip", "anon"));

        ARXResult result = getResult();
        processResults(result);

    }

    static void processResult(ResultSet rs) throws SQLException {
        if (rs != null) {
            ResultSetMetaData rsmd = rs.getMetaData();
            int maxCols = rsmd.getColumnCount();
            String[] cols = new String[maxCols];
            for (int i = 1; i <= maxCols; i++) {
                cols[i - 1] = rsmd.getColumnName(i);
            }

            data.add(cols);
            while (rs.next()) {
                String[] vals = new String[maxCols];
                for (int i = 1; i <= maxCols; i++) {

                    vals[i - 1] = rs.getString(i);

                }
                data.add(vals);
            }

        }
    }
}

package org.anonymization.examples;

import org.anonymization.model.DatabaseConfig;
import org.anonymization.repository.PostgresService;
import org.anonymization.repository.RangeCondition;
import org.deidentifier.arx.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import static org.anonymization.examples.Example.getExampleConfiguration;
import static org.anonymization.examples.Example.processResults;

public class ExamplePostgreSQL {
    private static final Data.DefaultData data = Data.create();

    public static void main(String[] args) throws IOException, SQLException {
        ARXConfiguration config = getExampleConfiguration();
        DatabaseConfig postgresConf = setDBConfig();

        PostgresService ps = new PostgresService(postgresConf);
        Connection con = ps.getConnection();

        // Load the data from the table
        String query = "select * from anon";
        ResultSet rs = ps.executeQuery(con, query);
//        ResultSet rs = ps.executeQueryWithSuppression(con,4,query,"anon","disease");
//
        addDataToARX(rs);
        setHierarchy(ps, con);

        // anonymize the data
        ARXAnonymizer anonymizer = new ARXAnonymizer();
        ARXResult result = anonymizer.anonymize(data, config);
        processResults(result);
    }

    private static void setHierarchy(PostgresService ps, Connection con) {
        // Define columns in data as Identity,sensitive,Quasi-identifiers etc

        // createStarHierarchy - Simplification done by us so that user doesn't require prior knowlege for generalization.
        data.getDefinition().setAttributeType("zip", ps.createStarHierarchy(con, "zip", "anon"));

        // RangeCondition is simplification to avoid data preprocessing by user.
        RangeCondition rc = new RangeCondition(40, "<", List.of("<40", "*"));
        RangeCondition rc1 = new RangeCondition(40, ">=", List.of(">=40", "*"));
        data.getDefinition().setAttributeType("age", ps.createRangeHierarchy(con, "age", "anon", rc, rc1));
        data.getDefinition().setAttributeType("disease", AttributeType.SENSITIVE_ATTRIBUTE);
        data.getDefinition().setAttributeType("name", AttributeType.IDENTIFYING_ATTRIBUTE);
    }

    private static DatabaseConfig setDBConfig() {
        // We added this improvement so that data can be read from the Database
        // Set database configuration to load the data
        DatabaseConfig postgresConf = new DatabaseConfig();
        postgresConf.setConnectStr("jdbc:postgresql://18.185.114.122:5432");
        postgresConf.setDb("peng");
        postgresConf.setUser("peng");
        postgresConf.setPassword("admin");
        return postgresConf;
    }

    private static void addDataToARX(ResultSet rs) throws SQLException {
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

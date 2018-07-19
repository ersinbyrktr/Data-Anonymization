package org.anonymization.examples;

import org.anonymization.repository.DatabaseConfig;
import org.anonymization.repository.PostgresService;
import org.anonymization.repository.RangeCondition;
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
        postgresConf.setConnectStr("jdbc:postgresql://18.185.114.122:5432");
        postgresConf.setDb("peng");
        postgresConf.setUser("peng");
        postgresConf.setPassword("admin");

        PostgresService ps = new PostgresService(postgresConf);
        Connection con = ps.getConnection();
        ResultSet rs = ps.executeQuery(con,query);
//        ResultSet rs = ps.executeQueryWithSuppression(con,4,query,"anon","disease");

        MySqlExample.processResult(rs);
        data.getDefinition().setAttributeType("zip",ps.createStarHierarchy(con,"zip","anon"));

        RangeCondition rc=new RangeCondition(40,"<","<40","*");
        RangeCondition rc1=new RangeCondition(40,">=",">=40","*");
        data.getDefinition().setAttributeType("age",ps.createRangeHierarchy(con,"age","anon",rc,rc1));
        ARXResult result = getResult();
        processResults(result);
    }
}

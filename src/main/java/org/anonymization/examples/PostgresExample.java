package org.anonymization.examples;

import org.anonymization.repository.DatabaseConfig;
import org.anonymization.repository.PostgresService;
import org.anonymization.repository.RangeCondition;
import org.deidentifier.arx.*;
import org.deidentifier.arx.criteria.DistinctLDiversity;
import org.deidentifier.arx.criteria.KAnonymity;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static org.anonymization.examples.Example.processResults;

public class PostgresExample {
    public static void main(String[] args) throws IOException,SQLException {

        // Create Data as required by ARX
        Data.DefaultData data = Data.create();

        // Create Configuration with K,l parameters
        ARXConfiguration config = ARXConfiguration.create();
        config.addPrivacyModel(new KAnonymity(2));
        config.setSuppressionLimit(0.02d);
        config.addPrivacyModel(new DistinctLDiversity("disease", 2));

        /*** We added this improvement so that data can be read from the Database***/
        // Set database configuration to load the data
        DatabaseConfig postgresConf = new DatabaseConfig();
        postgresConf.setConnectStr("jdbc:postgresql://localhost:5432");
        postgresConf.setDb("peng");
        postgresConf.setUser("peng");
        postgresConf.setPassword("admin");

        PostgresService ps = new PostgresService(postgresConf);
        Connection con = ps.getConnection();


        // Load the data from the table
        String query = "select * from anon";
        ResultSet rs = ps.executeQuery(con,query);
//        ResultSet rs = ps.executeQueryWithSuppression(con,4,query,"anon","disease");
//
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

        // Define columns in data as Identity,sensitive,Quasi-identifiers etc

        // createStarHierarchy - Simplification done by us so that user doesn't require prior knowlege for generalization.
        data.getDefinition().setAttributeType("zip",ps.createStarHierarchy(con,"zip","anon"));

        // RangeCondition is simplification to avoid data preprocessing by user.
        RangeCondition rc=new RangeCondition(40,"<","<40","*");
        RangeCondition rc1=new RangeCondition(40,">=",">=40","*");
        data.getDefinition().setAttributeType("age",ps.createRangeHierarchy(con,"age","anon",rc,rc1));
        data.getDefinition().setAttributeType("disease", AttributeType.SENSITIVE_ATTRIBUTE);
        data.getDefinition().setAttributeType("name", AttributeType.IDENTIFYING_ATTRIBUTE);

        // anonymize the data

        ARXAnonymizer anonymizer = new ARXAnonymizer();
        ARXResult result= anonymizer.anonymize(data, config);
        processResults(result);
    }
}

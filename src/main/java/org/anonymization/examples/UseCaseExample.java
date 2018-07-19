package org.anonymization.examples;

import org.deidentifier.arx.*;
import org.deidentifier.arx.criteria.DistinctLDiversity;
import org.deidentifier.arx.criteria.KAnonymity;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

class UseCaseExample {
    final static String[] fields = getExampleFields();
    final static Data.DefaultData data = getExampleData();
//    final static Data.DefaultData data = new Data.DefaultData();

    static ARXResult getResult() throws IOException {
        ARXConfiguration config = getExampleConfiguration();

        ARXAnonymizer anonymizer = new ARXAnonymizer();
        return anonymizer.anonymize(data, config);
    }


    static void addDataToARX(ResultSet rs) throws SQLException {
        if (rs != null) {
            ResultSetMetaData rsmd = rs.getMetaData();
            int maxCols = rsmd.getColumnCount();
            String[] cols = new String[maxCols];
            for (int i = 1; i <= maxCols; i++) {
                cols[i - 1] = rsmd.getColumnName(i);
            }

            UseCaseExample.data.add(cols);
            while (rs.next()) {
                String[] vals = new String[maxCols];
                for (int i = 1; i <= maxCols; i++) {
                    vals[i - 1] = rs.getString(i);
                }
                UseCaseExample.data.add(vals);
            }
        }
    }

    private static ARXConfiguration getExampleConfiguration() {
        ARXConfiguration config = ARXConfiguration.create();
        config.addPrivacyModel(new KAnonymity(14));
        config.setSuppressionLimit(0.02d);
        config.addPrivacyModel(new DistinctLDiversity("brand", 10));
//        config.addPrivacyModel(new EqualDistanceTCloseness("disease", 0.2d));
        return config;
    }

    private static String[] getExampleFields() {
        return new String[]{"name", "zip", "age", "nationality", "disease"};
    }

    private static Data.DefaultData getExampleData() {
        Data.DefaultData data = Data.create();

        // Define hierarchies
        AttributeType.Hierarchy.DefaultHierarchy age = AttributeType.Hierarchy.create();
        age.add("21", "<=40", "*");
        age.add("22", "<=40", "*");
        age.add("23", "<=40", "*");
        age.add("28", "<=40", "*");
        age.add("29", "<=40", "*");
        age.add("31", "<=40", "*");
        age.add("35", "<=40", "*");
        age.add("36", "<=40", "*");
        age.add("37", "<=40", "*");
        age.add("47", ">40", "*");
        age.add("49", ">40", "*");
        age.add("50", ">40", "*");
        age.add("55", ">40", "*");

        AttributeType.Hierarchy.DefaultHierarchy zip = AttributeType.Hierarchy.create();
        zip.add("13053", "1305*", "130**", "13***", "1****", "*****");
        zip.add("13068", "1306*", "130**", "13***", "1****", "*****");
        zip.add("14850", "1485*", "148**", "14***", "1****", "*****");
        zip.add("14853", "1485*", "148**", "14***", "1****", "*****");


        AttributeType.Hierarchy.DefaultHierarchy nationality = AttributeType.Hierarchy.create();
        nationality.add("American", "*");
        nationality.add("Russian", "*");
        nationality.add("Indian", "*");
        nationality.add("Japanese", "*");
//
        data.getDefinition().setAttributeType("first_name", AttributeType.IDENTIFYING_ATTRIBUTE);
        data.getDefinition().setAttributeType("zip", zip);
        data.getDefinition().setAttributeType("age", age);
        data.getDefinition().setAttributeType("brand", AttributeType.SENSITIVE_ATTRIBUTE);
        data.getDefinition().setDataType("age", DataType.DECIMAL);
        data.getDefinition().setDataType("zip", DataType.STRING);
        data.getDefinition().setMinimumGeneralization("age", 1);
        return data;
    }
}

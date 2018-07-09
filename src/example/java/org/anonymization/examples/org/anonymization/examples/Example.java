package org.anonymization.examples;

import org.deidentifier.arx.*;
import org.deidentifier.arx.criteria.DistinctLDiversity;
import org.deidentifier.arx.criteria.KAnonymity;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

class Example {
    final static String[] fields = getExampleFields();
    final static Data.DefaultData data = getExampleData();

    static ARXResult getResult() throws IOException {
        ARXConfiguration config = getExampleConfiguration();

        ARXAnonymizer anonymizer = new ARXAnonymizer();
        return anonymizer.anonymize(data, config);
    }

    // Process results -> taken from ARX examples module
    static void processResults(ARXResult result) {
        System.out.println(" - Transformed data:");
        DataHandle handle = result.getOutput(false);
        handle.sort(false, 1);
        Iterator<String[]> transformed = handle.iterator();
        while (transformed.hasNext()) {
            System.out.print("   ");
            System.out.println(Arrays.toString(transformed.next()));
        }
    }

    private static ARXConfiguration getExampleConfiguration() {
        ARXConfiguration config = ARXConfiguration.create();
        config.addPrivacyModel(new KAnonymity(2));
        config.setSuppressionLimit(0.02d);
        config.addPrivacyModel(new DistinctLDiversity("disease", 3));
        //config.addPrivacyModel(new EqualDistanceTCloseness("disease", 0.2d));
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
        // age.add("22", "<=40", "*");
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

        data.getDefinition().setAttributeType("name", AttributeType.IDENTIFYING_ATTRIBUTE);
        data.getDefinition().setAttributeType("zip", zip);
        data.getDefinition().setAttributeType("age", age);
        data.getDefinition().setAttributeType("nationality", nationality);
        data.getDefinition().setAttributeType("disease", AttributeType.SENSITIVE_ATTRIBUTE);
        data.getDefinition().setDataType("age", DataType.DECIMAL);
        data.getDefinition().setDataType("zip", DataType.STRING);
        data.getDefinition().setMinimumGeneralization("age", 0);
        return data;
    }
}

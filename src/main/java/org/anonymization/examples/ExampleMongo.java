package org.anonymization.examples;

import org.anonymization.repository.MongoDBService;
import org.bson.Document;
import org.deidentifier.arx.ARXResult;

import java.io.IOException;
import java.util.List;

import static org.anonymization.examples.Example.*;

public class ExampleMongo {

    final private static String connString = "mongodb://18.185.114.122:27017/test";
    final private static String aggrQuery = getExampleQuery();
    final private static String collectionName = "privacy";

    public static void main(String[] args) throws IOException {
        MongoDBService service = new MongoDBService()
                .setConnectionStringURI(connString)
                .setCollectionName(collectionName)
                .setFields(fields)
                .setData(data)
                .setQueryString(List.of(Document.parse(aggrQuery)));

        service.connect();

        service.executeQuery();

        ARXResult result = getResult();
        processResults(result);
    }

    private static String getExampleQuery() {
        return """
                {
                  "$project":{
                    "name":1,
                    "age":1,
                    "disease":1,
                    "nationality":1,
                    "zip":"$address.zip"
                  }
                }""";
    }
}

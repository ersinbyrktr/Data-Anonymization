package org.anonymization.examples;

import static org.anonymization.examples.Example.data;
import static org.anonymization.examples.Example.fields;
import static org.anonymization.examples.Example.getResult;
import static org.anonymization.examples.Example.processResults;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ValidationOptions;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;
import org.anonymization.repository.MongoDBService;
import org.bson.BsonType;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.deidentifier.arx.ARXResult;

public class ExampleMongo {

    final private static String connString = "mongodb://localhost:27017/test";
    final private static String aggrQuery = getExampleQuery();
    final private static String collectionName = "privacy";

    public static void main(String[] args) throws IOException {
        MongoDBService service = new MongoDBService()
            .setConnectionStringURI(connString)
            .setCollectionName(collectionName)
            .setFields(fields)
            .setData(data)
            .setQueryString(aggrQuery);

        service.connect();

        insertExampleData(service);

        service.executeQuery();

        ARXResult result = getResult();
        processResults(result);
    }

    private static void insertExampleData(MongoDBService service) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        MongoDatabase db = service.getDB();
        MongoCollection<Document> coll = db.getCollection(collectionName);
        coll.drop();
        createCollection(db);

        JsonNode masterJSON = mapper.readTree(new File(
            Objects.requireNonNull(ExampleMongo.class.getClassLoader().getResource("exampleMongoDBData.json"))
                .getFile()
        ));

        masterJSON.iterator()
            .forEachRemaining(e -> coll.insertOne(Document.parse(e.toString())));
    }

    private static void createCollection(MongoDatabase db) {
        db.createCollection(collectionName, new CreateCollectionOptions());
    }

    private static String getExampleQuery() {
        try {
            return new Scanner(new File(
                Objects.requireNonNull(ExampleMongo.class.getClassLoader().getResource("exampleMongoDBQuery.json"))
                    .getFile()
            )).useDelimiter("\\Z").next();
        } catch (FileNotFoundException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }
}

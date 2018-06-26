package org.anonymization.examples;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.anonymization.repository.MongoDBService;
import org.bson.Document;
import org.deidentifier.arx.ARXResult;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.anonymization.examples.Example.*;

public class ExampleMongo {
    final private static String connString = "mongodb://localhost:27017/test";
    final private static List<Document> aggrQuery = getExampleQuery();
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
        if (coll.count() > 0)
            coll.drop();
        ClassLoader classLoader = ExampleMongo.class.getClassLoader();
        JsonNode masterJSON = mapper.readTree(new File(
                Objects.requireNonNull(classLoader.getResource("exampleMongoDBData.json"))
                        .getFile()
        ));

        masterJSON.iterator()
                .forEachRemaining(e -> coll.insertOne(Document.parse(e.toString())));
    }

    private static List<Document> getExampleQuery() {
        return Collections.singletonList(
                new Document("$project", new Document("_id", 0)
                        .append("name", 1)
                        .append("age", 1)
                        .append("disease", 1)
                        .append("nationality", 1)
                        .append("zip", "$address.zip"))
        );
    }
}

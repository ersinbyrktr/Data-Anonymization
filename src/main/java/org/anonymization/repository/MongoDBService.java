package org.anonymization.repository;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.deidentifier.arx.Data.DefaultData;
import org.deidentifier.arx.DataDefinition;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class MongoDBService {
    private MongoDatabase db;
    private MongoCollection<Document> coll;
    private Bson queryString;
    private DefaultData defaultData;

    public void MongoDBService(String connectionStringURI) {
        initialize(connectionStringURI);
    }

    private void initialize(String connectionStringURI) {
        MongoClientURI mongoClientURI = new MongoClientURI(connectionStringURI);
        db = new MongoClient(new MongoClientURI(connectionStringURI)).getDatabase(mongoClientURI.getDatabase());

    }

    public void executeQuery(Bson queryString, String collectionName, DefaultData defaultData) {
        this.queryString = queryString;
        this.defaultData = defaultData;
        coll = db.getCollection(collectionName);
        execute();
    }

    private void execute() {
        FindIterable<Document> docs = coll.find(queryString);
        String[] allAttributes = getAllAttributes();
        defaultData.add(allAttributes);

        StreamSupport.stream(docs.spliterator(), false)
                .filter(this::verifyDocument)
                .forEach(this::addData);
    }

    private String[] getAllAttributes() {
        DataDefinition dataDefinition = defaultData.getDefinition();
        Set<String> insensitiveAttributes = dataDefinition.getInsensitiveAttributes();
        Set<String> sensitiveAttributes = dataDefinition.getSensitiveAttributes();

        return Stream.concat(insensitiveAttributes.stream(), sensitiveAttributes.stream())
                .distinct()
                .toArray(String[]::new);
    }

    private void addData(Document doc) {
        String[] row = Arrays.stream(getAllAttributes())
                .map(attr -> {
                    return doc.get(attr).toString();
                })
                .toArray(String[]::new);

        defaultData.add(row);
    }

    private boolean verifyDocument(Document doc) {
        return true; //TODO implement validation
    }

    public DefaultData getData(){
        return defaultData;
    }

}

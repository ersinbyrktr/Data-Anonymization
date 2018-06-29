package org.anonymization.repository;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.Collections;
import org.bson.Document;
import org.deidentifier.arx.Data.DefaultData;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

public class MongoDBService extends DBService<MongoDBService, Document> {
    private MongoDatabase db;
    private MongoCollection<Document> coll;
    private List<Document> queryString;
    private String collectionName;

    public MongoDBService() {
    }

    public MongoDBService setCollectionName(String collectionName) {
        this.collectionName = collectionName;
        return this;
    }

    public MongoDBService setData(DefaultData data) {
        this.data = data;
        return this;
    }

    public MongoDBService setQueryString(List<Document> queryString) {
        this.queryString = queryString;
        return this;
    }

    public MongoDBService setQueryString(String queryString) {
        this.queryString = Collections.singletonList(
            Document.parse(queryString)
        );
        return this;
    }

    public void connect() {
        MongoClientURI mongoClientURI = new MongoClientURI(connectionStringURI);
        db = new MongoClient(new MongoClientURI(connectionStringURI)).getDatabase(mongoClientURI.getDatabase());
        coll = db.getCollection(collectionName);
    }

    public void executeQuery() {
        AggregateIterable<Document> docs = coll.aggregate(this.queryString);
        data.add(fields);

        StreamSupport.stream(docs.spliterator(), false)
                .filter(this::verifyFields)
                .forEach(this::addData);
    }

    private void addData(Document doc) {
        String[] row = Arrays.stream(fields)
                .map(attr -> doc.get(attr).toString())
                .toArray(String[]::new);
        data.add(row);
    }

    boolean verifyFields(Document row) throws RuntimeException {
        Arrays.stream(fields)
                .filter(((Predicate<String>) row::containsKey).negate())
                .findAny()
                .ifPresent(s -> {
                    throw new RuntimeException(String.format("%s doesn't exist in the result row: %s", s, row));
                });
        return true;
    }

    public DefaultData getData() {
        return data;
    }

    public MongoDatabase getDB() {
        return db;
    }

    MongoDBService getSelf() {
        return this;
    }
}

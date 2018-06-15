package org.anonymization.repository;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.deidentifier.arx.Data;
import org.deidentifier.arx.Data.DefaultData;
import org.deidentifier.arx.DataDefinition;

import java.util.Set;
import java.util.stream.Stream;

public class MongoDBService {
    private MongoDatabase db;
    private MongoCollection<Document> coll;

    public void MongoDBService(String connectionStringURI) {
        initialize(connectionStringURI);
    }

    private void initialize(String connectionStringURI) {
        MongoClientURI mongoClientURI = new MongoClientURI(connectionStringURI);
        db = new MongoClient(new MongoClientURI(connectionStringURI)).getDatabase(mongoClientURI.getDatabase());

    }

    public Data getData(Bson queryString, String collectionName) {
        coll = db.getCollection(collectionName);
        return executeQuery(queryString);
    }

    private Data executeQuery(Bson queryString) {
        FindIterable<Document> docs = coll.find(queryString);
        DefaultData data = Data.create();
        DataDefinition dataDefinition = data.getDefinition(); //TODO pass dataDefinitions from higher level
        Set<String> insensitiveAttributes = dataDefinition.getInsensitiveAttributes();
        Set<String> sensitiveAttributes = dataDefinition.getSensitiveAttributes();
        String[] allAttributes = Stream.concat(insensitiveAttributes.stream(), sensitiveAttributes.stream())
                .distinct()
                .toArray(String[]::new);
        data.add(allAttributes);
        for (Document doc : docs) {
            //TODO convert docs to data
        }
        return data;
    }
}

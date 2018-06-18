package org.anonymization;

import org.anonymization.repository.MongoDBService;
import org.deidentifier.arx.ARXAnonymizer;
import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.Data;

import java.io.IOException;

public class Anonymizer {
    private Data.DefaultData data;
    private ARXAnonymizer anonymizer;
    private ARXConfiguration configuration;

    Anonymizer(MongoDBService dbService, ARXConfiguration config) {
        configuration = config;
        anonymizer = new ARXAnonymizer();
        data = dbService.getData();
    }

    ARXResult getAnonData() throws IOException {
        return anonymizer.anonymize(data, configuration);
    }


}

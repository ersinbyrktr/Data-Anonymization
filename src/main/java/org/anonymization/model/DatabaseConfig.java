package org.anonymization.model;

import lombok.Data;

@Data
public class DatabaseConfig {
    private String db;
    private String user;
    private String password;
    private String connectStr;
}


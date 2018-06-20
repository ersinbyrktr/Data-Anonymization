package org.anonymization.repository;

import org.deidentifier.arx.Data.DefaultData;

public abstract class DBService<T, R> {
    String[] fields;
    String connectionStringURI;
    DefaultData data;

    public T setFields(String[] fields) {
        this.fields = fields;
        return getSelf();
    }

    public T setConnectionStringURI(String connectionStringURI) {
        this.connectionStringURI = connectionStringURI;
        return getSelf();
    }

    abstract T getSelf();

    public abstract void connect();

    public abstract void executeQuery();

    abstract boolean verifyFields(R row) throws RuntimeException;

    public DefaultData getData() {
        return data;
    }
}

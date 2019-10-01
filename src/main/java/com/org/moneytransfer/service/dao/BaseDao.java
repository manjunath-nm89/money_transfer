package com.org.moneytransfer.service.dao;

import com.org.moneytransfer.service.datastore.BaseDataStore;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Random;

public abstract class BaseDao<T extends BaseDataStore> {

    abstract <T extends BaseDataStore> LinkedHashMap<Long, T> getDataTable();

    public <T extends BaseDataStore> T findById(Long primaryKey) {
        return (T) getDataTable().get(primaryKey);
    }

    public <T extends BaseDataStore> T createRecord(T dataStore) {
        LinkedHashMap<Long, T> dataTable = getDataTable();
        setCreateFields(dataStore);
        dataTable.put(dataStore.getId(), dataStore);
        return dataStore;
    }

    public <T extends BaseDataStore> void setCreateFields(T dataStore) {
        dataStore.setId(generateLongId());
        setTimestamps(dataStore);
    }

    private Long generateLongId() {
        Long range = 1234567L;
        Random r = new Random();

        return (long)(r.nextDouble() * range);
    }

    private <T extends BaseDataStore> void setTimestamps(T dataStore) {
        Long currentEpoch = Instant.now().toEpochMilli();
        dataStore.setCreatedAt(currentEpoch);
        dataStore.setUpdatedAt(currentEpoch);
    }

}

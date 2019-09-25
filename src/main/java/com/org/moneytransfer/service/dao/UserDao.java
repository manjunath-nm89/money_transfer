package com.org.moneytransfer.service.dao;

import com.org.moneytransfer.client.User;
import com.org.moneytransfer.service.datastore.UserStore;

import java.util.LinkedHashMap;

public class UserDao extends BaseDao<UserStore> {

    // LinkedHashMap is used here to ensure the ordering of rows inserted into the collection
    private LinkedHashMap<Long, UserStore> dataTable;

    public UserDao() {
        dataTable = new LinkedHashMap<>();
    }

    @Override
    LinkedHashMap<Long, UserStore> getDataTable() {
        return dataTable;
    }


    public UserStore createUser(User user) {

        UserStore userStore = new UserStore();

        setCreateFields(userStore);

        userStore.setEmail(user.getEmail());
        userStore.setFirstName(user.getFirstName());
        userStore.setLastName(user.getLastName());

        return saveData(userStore);

    }

}

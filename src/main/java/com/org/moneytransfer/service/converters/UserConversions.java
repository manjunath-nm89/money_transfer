package com.org.moneytransfer.service.converters;

import com.org.moneytransfer.client.User;
import com.org.moneytransfer.service.datastore.UserStore;

public class UserConversions {

    public static User convertUser(UserStore userStore) {

        User user = new User();

        user.setFirstName(userStore.getFirstName());
        user.setLastName(userStore.getLastName());
        user.setUserId(userStore.getId());
        user.setCreatedAt(userStore.getCreatedAt());
        user.setUpdatedAt(userStore.getUpdatedAt());

        return user;
    }

}

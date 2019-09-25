package com.org.moneytransfer.service.managers.impl;

import com.org.moneytransfer.client.User;
import com.org.moneytransfer.service.converters.UserConversions;
import com.org.moneytransfer.service.dao.UserDao;
import com.org.moneytransfer.service.datastore.UserStore;
import com.org.moneytransfer.service.managers.UserManager;

public class UserManagerImpl implements UserManager {

    private UserDao userDao;

    public UserManagerImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User getUserById(Long userId) {

        UserStore userStore = userDao.findById(userId);

        if(userStore != null) {
            return UserConversions.convertUser(userStore);
        }
        else {
            return null;
        }

    }

    @Override
    public User createUser(User user) {

        UserStore userStore = userDao.createUser(user);
        return UserConversions.convertUser(userStore);

    }

}

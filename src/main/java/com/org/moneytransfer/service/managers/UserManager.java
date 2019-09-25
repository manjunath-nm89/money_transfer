package com.org.moneytransfer.service.managers;

import com.org.moneytransfer.client.User;

public interface UserManager {

    User getUserById(Long userId);

    User createUser(User user);
}

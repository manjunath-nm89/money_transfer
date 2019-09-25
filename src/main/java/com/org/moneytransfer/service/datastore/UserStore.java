package com.org.moneytransfer.service.datastore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserStore extends BaseDataStore {
    private String firstName;
    private String lastName;
    private String email;
}

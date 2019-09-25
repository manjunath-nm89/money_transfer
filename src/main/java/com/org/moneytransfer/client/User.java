package com.org.moneytransfer.client;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private Long createdAt;
    private Long updatedAt;
}

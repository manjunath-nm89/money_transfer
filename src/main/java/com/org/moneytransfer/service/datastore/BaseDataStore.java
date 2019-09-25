package com.org.moneytransfer.service.datastore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseDataStore {
    private Long id;
    private Long createdAt;
    private Long updatedAt;
}

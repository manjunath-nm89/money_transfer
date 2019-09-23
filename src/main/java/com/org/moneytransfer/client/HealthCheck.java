package com.org.moneytransfer.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HealthCheck {
    @JsonProperty
    private boolean healthy;
}

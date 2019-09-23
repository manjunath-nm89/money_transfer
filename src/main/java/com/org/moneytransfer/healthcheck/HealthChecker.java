package com.org.moneytransfer.healthcheck;

import com.codahale.metrics.health.HealthCheck;
import com.org.moneytransfer.MoneyTransferConfiguration;
import io.dropwizard.jetty.HttpConnectorFactory;
import io.dropwizard.server.DefaultServerFactory;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class HealthChecker extends HealthCheck {

    private MoneyTransferConfiguration configuration;
    private Logger LOGGER = LoggerFactory.getLogger(HealthChecker.class);

    public HealthChecker(MoneyTransferConfiguration moneyTransferConfiguration) {
        this.configuration = moneyTransferConfiguration;
    }

    @Override
    protected Result check() throws Exception {
        Client client = new JerseyClientBuilder().build();

        String url = "http://localhost:" + getPort() + "/apihealthcheck";
        WebTarget webTarget = client.target(url);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.get();
        if (response.getStatus() == 200) {
            return Result.healthy();
        }
        else {
            LOGGER.error("Healthcheck call failed. response={}", response);
            return Result.unhealthy("Received " + response.getStatus() + " response from the " + url);
        }
    }

    private int getPort() {
        return ((HttpConnectorFactory) ((DefaultServerFactory) configuration.getServerFactory())
                .getApplicationConnectors().get(0)).getPort();
    }
}

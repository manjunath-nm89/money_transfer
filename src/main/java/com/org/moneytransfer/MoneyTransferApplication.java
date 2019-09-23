package com.org.moneytransfer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.org.moneytransfer.healthcheck.HealthChecker;
import com.org.moneytransfer.resources.HealthCheckResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoneyTransferApplication extends Application<MoneyTransferConfiguration> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MoneyTransferApplication.class);

    public static void main(final String[] args) throws Exception {
        new MoneyTransferApplication().run(args);
    }

    @Override
    public void initialize(final Bootstrap bootstrap) {

        bootstrap.addBundle(new SwaggerBundle<MoneyTransferConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(MoneyTransferConfiguration configuration) {
                return configuration.getSwaggerBundleConfiguration();
            }
        });
    }

    @Override
    public void run(final MoneyTransferConfiguration configuration, final Environment environment) throws Exception{

        LOGGER.info("Application name: {}", configuration.getAppName());

        // Ignores Unknown JSON Properties and does not error out with "Unable to process JSON"
        environment.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        environment.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

        LOGGER.info("Registering Resources..");
         // Api Health Check Resource
        environment.jersey().register(new HealthCheckResource());

        LOGGER.info("Registering HealthChecks");
        environment.healthChecks().register("APIHealthCheck", new HealthChecker(configuration));

    }
}

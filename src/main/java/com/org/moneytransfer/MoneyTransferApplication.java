package com.org.moneytransfer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.org.moneytransfer.healthcheck.HealthChecker;
import com.org.moneytransfer.resources.AccountResource;
import com.org.moneytransfer.resources.HealthCheckResource;
import com.org.moneytransfer.resources.UserResource;
import com.org.moneytransfer.service.dao.AccountDao;
import com.org.moneytransfer.service.dao.AccountTransactionDao;
import com.org.moneytransfer.service.dao.UserDao;
import com.org.moneytransfer.service.managers.AccountManager;
import com.org.moneytransfer.service.managers.UserManager;
import com.org.moneytransfer.service.managers.impl.AccountManagerImpl;
import com.org.moneytransfer.service.managers.impl.UserManagerImpl;
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

        LOGGER.info("Registering DAOs..");
        UserDao userDao = new UserDao();
        AccountTransactionDao accountTransactionDao = new AccountTransactionDao();
        AccountDao accountDao = new AccountDao(accountTransactionDao);

        LOGGER.info("Registering Managers..");
        UserManager userManager = new UserManagerImpl(userDao);
        AccountManager accountManager = new AccountManagerImpl(accountDao, userDao);

        LOGGER.info("Registering Resources..");
        environment.jersey().register(new HealthCheckResource());
        environment.jersey().register(new UserResource(userManager));
        environment.jersey().register(new AccountResource(accountManager));

        LOGGER.info("Registering HealthChecks..");
        environment.healthChecks().register("APIHealthCheck", new HealthChecker(configuration));

    }
}

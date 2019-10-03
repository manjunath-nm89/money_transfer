package com.org.moneytransfer.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.moneytransfer.client.User;
import com.org.moneytransfer.service.dao.UserDao;
import com.org.moneytransfer.service.datastore.UserStore;
import com.org.moneytransfer.service.managers.UserManager;
import com.org.moneytransfer.service.managers.impl.UserManagerImpl;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

public class TestUserResource extends BaseTestResource {

    private static final UserDao userDao = new UserDao();
    private static final UserManager userManager = new UserManagerImpl(userDao);
    private ObjectMapper mapper;

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new UserResource(userManager))
            .build();

    @Before
    public void setup() {
        // Setup Methods
        mapper = new ObjectMapper();
    }

    @Test
    public void getUser404() {

        WebTarget target = resources.target("/users/123");

        Response response = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .method("GET");

        assert response.getStatus() == 404;

    }


    @Test
    public void createUser() throws IOException {

        WebTarget target = resources.target("/users/");

        Response response = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .method("POST", buildUserPayload());

        assert response.getStatus() == 200;

        String responseStr = getResponseFromBuffer((InputStream) response.getEntity());
        User createdUser = mapper.readValue(responseStr, User.class);
        assert createdUser.getUserId() != null;
        assert createdUser.getCreatedAt() != null;
        assert createdUser.getUpdatedAt() != null;
        assert createdUser.getFirstName().equals("Niv");
        assert createdUser.getLastName().equals("M");
        assert createdUser.getEmail().equals("niv.m@gmail.com");

        UserStore userStore = userDao.findById(createdUser.getUserId());
        assert userStore.getId().equals(createdUser.getUserId());
        assert userStore.getCreatedAt().equals(createdUser.getCreatedAt());
        assert userStore.getUpdatedAt().equals(createdUser.getUpdatedAt());
        assert userStore.getFirstName().equals("Niv");
        assert createdUser.getLastName().equals("M");
        assert createdUser.getEmail().equals("niv.m@gmail.com");

    }

    private Entity<User> buildUserPayload() {
        User user = new User();
        user.setFirstName("Niv");
        user.setLastName("M");
        user.setEmail("niv.m@gmail.com");

        return Entity.json(user);

    }


}

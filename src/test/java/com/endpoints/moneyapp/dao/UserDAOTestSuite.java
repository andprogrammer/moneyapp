package com.endpoints.moneyapp.dao;

import com.moneyapp.dao.AbstractFactory;
import com.moneyapp.dao.UserDAO;
import com.moneyapp.exception.CustomException;
import com.moneyapp.model.User;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static spark.Spark.stop;

public class UserDAOTestSuite {

    private final static Logger logger = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName().getClass());

    @Rule
    public ExpectedException expectedExceptionThrown = ExpectedException.none();

    @Before
    public void setUp() {
        if (logger.isDebugEnabled())
            logger.debug(new Throwable().getStackTrace()[0].getMethodName()
                    + "() Starting testSuite "
                    + new Throwable().getStackTrace()[0].getClassName());
    }

    @After
    public void tearDown() {
        stop();
    }

    @Test
    public void testGetAllUsers() throws CustomException {
        UserDAO userDAO = getUserDAO();
        userDAO.createUser("Andrzej", "test@gmail.com");
        userDAO.createUser("Tom", "tom@gmail.com");
        List<User> users = userDAO.getAllUsers();
        assertThat(2, equalTo(users.size()));
    }

    @Test
    public void testGetUser() throws CustomException {
        createUser();
    }

    @Test
    public void testGetNonExistingUserById() throws CustomException {
        String userId = "1024";
        expectedExceptionThrow(CustomException.class, "User with id " + userId + " not found");
        UserDAO userDAO = getUserDAO();
        userDAO.getUser(userId);
    }

    @Test
    public void testCreateUser() throws CustomException {
        createUser();
    }

    @Test
    public void testUpdateUser() throws CustomException {
        UserDAO userDAO = getUserDAO();
        User user = userDAO.createUser("Andrzej", "test@gmail.com");
        user.setName("Tom");
        user.setName("tom@yahoo.com");
        userDAO.updateUser(user.getId(), user.getName(), user.getEmail());
        assertThat(user, equalTo(userDAO.getUser(user.getId())));
    }

    @Test
    public void testUpdateNonExistingUser() throws CustomException {
        String userId = "2048";
        expectedExceptionThrow(CustomException.class, "User with id " + userId + " not found");
        UserDAO userDAO = getUserDAO();
        User user = createUser();
        userDAO.updateUser(userId, user.getName(), user.getEmail());
    }

    @Test
    public void testDeleteUser() throws CustomException {
        UserDAO userDAO = getUserDAO();
        User user = userDAO.createUser("Andrzej", "test@gmail.com");
        assertThat(1, equalTo(userDAO.getAllUsers().size()));
        userDAO.deleteUser(user.getId());
        assertThat(0, equalTo(userDAO.getAllUsers().size()));
    }

    @Test
    public void testDeleteNonExistingUser() throws CustomException {
        String userId = "512";
        expectedExceptionThrow(CustomException.class, "User with id " + userId + " not found");
        UserDAO userDAO = getUserDAO();
        userDAO.deleteUser(userId);
    }

    private <T> void expectedExceptionThrow(Class<T> exceptionType, String exceptionMessage) {
        expectedExceptionThrown.expect((Class<? extends Throwable>) exceptionType);
        expectedExceptionThrown.expectMessage(equalTo(exceptionMessage));
    }

    private User createUser() throws CustomException {
        UserDAO userDAO = getUserDAO();
        User user = userDAO.createUser("Andrzej", "test@gmail.com");
        assertThat(user, equalTo(userDAO.getUser(user.getId())));
        return user;
    }

    private UserDAO getUserDAO() {
        return AbstractFactory.getFactory(AbstractFactory.FactoryType.DAO).getUserDAO();
    }
}

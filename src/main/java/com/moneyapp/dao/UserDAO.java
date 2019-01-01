package com.moneyapp.dao;

import com.moneyapp.exception.CustomException;
import com.moneyapp.model.User;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.moneyapp.utils.Utils.validateId;


public class UserDAO {

    private Map<String, User> users = new HashMap<>();
    private final static Logger logger = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName().getClass());

    public List<User> getAllUsers() throws CustomException {
        validateUsers();
        if (logger.isDebugEnabled())
            logger.debug(new Throwable().getStackTrace()[0].getMethodName() + "() Number of users=" + users.size());
        return new ArrayList<>(users.values());
    }

    public User getUser(String id) throws CustomException {
        validateId(id);
        validateUsers();
        checkIfIdAlreadyExists(id);
        if (logger.isDebugEnabled())
            logger.debug(new Throwable().getStackTrace()[0].getMethodName() + "() " + users.get(id));
        return users.get(id);
    }

    public User createUser(String name, String email) throws CustomException {
        checkConstraint(name, email);
        User user = new User(name, email);
        checkIfUserAlreadyExists(user);
        if (logger.isDebugEnabled())
            logger.debug(new Throwable().getStackTrace()[0].getMethodName() + "() " + user);
        addUser(user);
        return user;
    }

    public User updateUser(String id, String name, String email) throws CustomException {
        validateId(id);
        User user = getUser(id);
        if (logger.isDebugEnabled())
            logger.debug(new Throwable().getStackTrace()[0].getMethodName() + "() " + user);
        synchronized (user) {
            user.setName(name);
            user.setEmail(email);
        }
        return user;
    }

    private void addUser(User user) throws CustomException {
        validateUsers();
        if (logger.isDebugEnabled())
            logger.debug(new Throwable().getStackTrace()[0].getMethodName() + "() " + user);
        users.put(user.getId(), user);
    }

    public int deleteUser(String id) throws CustomException {
        validateId(id);
        validateUsers();
        if (logger.isDebugEnabled())
            logger.debug(new Throwable().getStackTrace()[0].getMethodName() + "() " + getUser(id));
        synchronized (users) {
            users.remove(id);
        }
        return 0;
    }

    private void checkIfIdAlreadyExists(String id) throws CustomException {
        if (!users.containsKey(id)) {
            logger.error(new Throwable().getStackTrace()[0].getMethodName() + "() User with id=" + id + " already exists");
            throw new CustomException("User with id " + id + " not found");
        }
    }

    private boolean checkIfUserAlreadyExists(User user) throws CustomException {
        validateUsers();
        if (users.containsValue(user)) {
            logger.error(new Throwable().getStackTrace()[0].getMethodName() + "() " + user + " already exists");
            throw new CustomException("User already exists");
        }
        return false;
    }

    private void checkConstraint(String name, String email) throws CustomException {
        if (name == null || name.isEmpty()) {
            logger.error(new Throwable().getStackTrace()[0].getMethodName() + "() Incorrect name=" + name);
            throw new CustomException("Argument 'name' cannot be empty");
        }
        if (email == null || email.isEmpty()) {
            logger.error(new Throwable().getStackTrace()[0].getMethodName() + "() Incorrect email=" + email);
            throw new CustomException("Argument 'email' cannot be empty");
        }
    }

    private void validateUsers() throws CustomException {
        if (users == null) {
            logger.error(new Throwable().getStackTrace()[0].getMethodName() + "() No users data");
            throw new CustomException("Error reading users data");
        }
    }
}

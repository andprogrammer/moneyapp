package com.moneyapp.dao.implementation;

import com.moneyapp.dao.UserDAO;
import com.moneyapp.exception.CustomException;
import com.moneyapp.model.User;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.moneyapp.utils.Utils.validateId;

public class UserDAOImplementation implements UserDAO {

    private Map<String, User> users = new HashMap<>();
    private final static Logger logger = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName().getClass());

    public List<User> getAllUsers() throws CustomException {
        if (logger.isDebugEnabled())
            logger.debug(new Throwable().getStackTrace()[0].getMethodName() + "() Number of users=" + users.size());
        return new ArrayList<>(users.values());
    }

    public User getUser(String id) throws CustomException {
        validateId(id);
        if (!users.containsKey(id))
            throw new CustomException("User with id " + id + " not found");
        if (logger.isDebugEnabled())
            logger.debug(new Throwable().getStackTrace()[0].getMethodName() + "() " + users.get(id));
        return users.get(id);
    }

    public User createUser(String name, String email) throws CustomException {
        User user = new User(name, email);
        validate(user);
        if (users.containsValue(user))
            throw new CustomException("User " + user + " already exists");
        if (logger.isDebugEnabled())
            logger.debug(new Throwable().getStackTrace()[0].getMethodName() + "() " + user);
        users.put(user.getId(), user);
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

    public int deleteUser(String id) throws CustomException {
        validateId(id);
        if (logger.isDebugEnabled())
            logger.debug(new Throwable().getStackTrace()[0].getMethodName() + "() " + getUser(id));
        synchronized (users) {
            users.remove(id);
        }
        return 0;
    }

    private void validate(User user) throws CustomException {
        if (user.getName() == null || user.getName().isEmpty())
            throw new CustomException("Incorrect name=" + user.getName());
        if (user.getEmail() == null || user.getEmail().isEmpty())
            throw new CustomException("Incorrect email=" + user.getEmail());
    }
}

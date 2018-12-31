package com.moneyapp.dao;

import com.moneyapp.exception.CustomException;
import com.moneyapp.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.moneyapp.utils.Utils.checkIdConstraint;


public class UserDAO {

    private Map<String, User> users = new HashMap<>();

    public List<User> getAllUsers() throws CustomException {
        checkUsersConstraint();
        return new ArrayList<>(users.values());
    }

    public User getUser(String id) throws CustomException {
        checkIdConstraint(id);
        checkUsersConstraint();
        if(!users.containsKey(id))
            throw new CustomException("User with id " + id + " not found");
        return users.get(id);
    }

    public User createUser(String name, String email) throws CustomException {
        checkConstraint(name, email);
        User user = new User(name, email);
        checkIfUserAlreadyExists(user);
        addUser(user);
        return user;
    }

    public User updateUser(String id, String name, String email) throws CustomException {
        checkIdConstraint(id);
        User user = getUser(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private void addUser(User user) throws CustomException {
        checkUsersConstraint();
        users.put(user.getId(), user);
    }

    public int deleteUser(String id) throws CustomException {
        checkIdConstraint(id);
        checkUsersConstraint();
        users.remove(id);
        return 0;
    }

    private boolean checkIfUserAlreadyExists(User user) throws CustomException {
        checkUsersConstraint();
        if (users.containsValue(user))
            throw new CustomException("User already exists");
        return false;
    }

    private void checkConstraint(String name, String email) throws CustomException {
        if (name == null || name.isEmpty())
            throw new CustomException("Argument 'name' cannot be empty");
        if (email == null || email.isEmpty())
            throw new CustomException("Argument 'email' cannot be empty");
    }

    private void checkUsersConstraint() throws CustomException {
        if (users == null)
            throw new CustomException("Error reading users data");
    }
}

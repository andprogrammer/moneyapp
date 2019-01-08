package com.moneyapp.dao;

import com.moneyapp.exception.CustomException;
import com.moneyapp.model.User;

import java.util.List;

public interface UserDAO {

    List<User> getAllUsers() throws CustomException;

    User getUser(String id) throws CustomException;

    User createUser(String name, String email) throws CustomException;

    User updateUser(String id, String name, String email) throws CustomException;

    int deleteUser(String id) throws CustomException;
}

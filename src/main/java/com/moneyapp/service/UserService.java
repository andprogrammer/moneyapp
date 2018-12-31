package com.moneyapp.service;

import com.moneyapp.ResponseError;
import com.moneyapp.dao.UserDAO;
import com.moneyapp.model.User;
import com.moneyapp.utils.JsonUtil;
import spark.Spark;

import static spark.Spark.*;


public class UserService {

    public UserService(final UserDAO userDAO) {

        Spark.get("/user/all", (req, res) -> userDAO.getAllUsers(), JsonUtil.json());

        get("/user/:id", (req, res) -> {
            String id = req.params(":id");
            User user = userDAO.getUser(id);
            if (user != null) {
                return user;
            }
            res.status(400);
            return new ResponseError("No user with id '%s' found", id);
        }, JsonUtil.json());

        put("/user/create", (req, res) -> userDAO.createUser(
                req.queryParams("name"),
                req.queryParams("email")
        ), JsonUtil.json());

        post("/user/:id", (req, res) -> userDAO.updateUser(
                req.params(":id"),
                req.queryParams("name"),
                req.queryParams("email")
        ), JsonUtil.json());

        delete("/user/:id", (req, res) -> userDAO.deleteUser(
                req.params(":id")
        ), JsonUtil.json());

        after((req, res) -> {
            res.type("application/json");
        });

        exception(IllegalArgumentException.class, (e, req, res) -> {
            res.status(400);
            res.body(JsonUtil.toJson(new ResponseError(e)));
        });
    }
}
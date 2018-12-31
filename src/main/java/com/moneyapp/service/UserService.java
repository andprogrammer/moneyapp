package com.moneyapp.service;

import com.moneyapp.dao.UserDAO;
import com.moneyapp.exception.CustomException;
import com.moneyapp.exception.ResponseError;
import com.moneyapp.model.User;
import com.moneyapp.utils.JsonUtil;

import static com.moneyapp.utils.JsonUtil.FAILED_RESPONSE;
import static spark.Spark.*;


public class UserService {

    public UserService(final UserDAO userDAO) {

        get("/user/all", (request, response) -> userDAO.getAllUsers(), JsonUtil.json());

        get("/user/:id", (request, response) -> {
            String id = request.params(":id");
            User user = userDAO.getUser(id);
            if (user != null)
                return user;
            response.status(FAILED_RESPONSE);
            return new ResponseError("User with id '%s' found", id);
        }, JsonUtil.json());

        put("/user/create", (request, response) -> {
            User user = userDAO.createUser(
                    request.queryParams("name"),
                    request.queryParams("email")
            );
            if (user != null)
                return user;
            response.status(FAILED_RESPONSE);
            return new ResponseError("Error. User not added");
        }, JsonUtil.json());

        post("/user/:id", (request, response) -> {
            User user = userDAO.updateUser(
                    request.params(":id"),
                    request.queryParams("name"),
                    request.queryParams("email")
            );
            if (user != null)
                return user;
            response.status(FAILED_RESPONSE);
            return new ResponseError("Error. User not added");
        }, JsonUtil.json());

        delete("/user/:id", (request, response) -> {
            int responseStatus = userDAO.deleteUser(
                    request.params(":id")
            );
            if (0 != responseStatus) {
                response.status(FAILED_RESPONSE);
                return new ResponseError("Error. User not deleted");
            }
            return 0;
        }, JsonUtil.json());

        after((request, response) -> {
            response.type("application/json");
        });

        exception(IllegalArgumentException.class, (exception, request, response) -> {
            response.status(FAILED_RESPONSE);
            response.body(JsonUtil.toJson(new ResponseError(exception)));
        });

        exception(CustomException.class, (exception, request, response) -> {
            response.status(FAILED_RESPONSE);
            response.body(JsonUtil.toJson(new ResponseError(exception)));
        });
    }
}
package com.moneyapp.service;

import com.moneyapp.dao.UserDAO;
import com.moneyapp.exception.CustomException;
import com.moneyapp.exception.ResponseError;
import com.moneyapp.utils.JSONUtil;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static spark.Spark.*;

public class UserService {

    public UserService(final UserDAO userDAO) {

        get("/user/all", (request, response) -> userDAO.getAllUsers(), JSONUtil.json());

        get("/user/:id", (request, response) -> userDAO.getUser(request.params(":id")), JSONUtil.json());

        put("/user/create", (request, response) -> userDAO.createUser(
                request.queryParams("name"),
                request.queryParams("email")),
                JSONUtil.json());

        post("/user/:id", (request, response) -> userDAO.updateUser(
                request.params(":id"),
                request.queryParams("name"),
                request.queryParams("email")), JSONUtil.json());

        delete("/user/:id", (request, response) -> {
            int responseStatus = userDAO.deleteUser(
                    request.params(":id"));
            if (0 != responseStatus) {
                response.status(SC_BAD_REQUEST);
                return new ResponseError("Error. User not deleted");
            }
            return 0;
        }, JSONUtil.json());

        after((request, response) -> {
            response.type("application/json");
        });

        exception(IllegalArgumentException.class, (exception, request, response) -> {
            response.status(SC_BAD_REQUEST);
            response.body(JSONUtil.toJson(new ResponseError(exception)));
        });

        exception(CustomException.class, (exception, request, response) -> {
            response.status(SC_BAD_REQUEST);
            response.body(JSONUtil.toJson(new ResponseError(exception)));
        });
    }
}

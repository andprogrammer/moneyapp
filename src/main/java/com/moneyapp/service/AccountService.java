package com.moneyapp.service;

import com.moneyapp.dao.AccountDAO;
import com.moneyapp.exception.CustomException;
import com.moneyapp.exception.ResponseError;
import com.moneyapp.utils.JSONUtil;

import java.math.BigDecimal;

import static com.moneyapp.utils.Utils.validateAmountLessThanOrEqualZero;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static spark.Spark.*;

public class AccountService {

    public AccountService(final AccountDAO accountDAO) {

        get("/account/all", (request, response) -> accountDAO.getAllAccounts(), JSONUtil.json());

        get("/account/:id", (request, response) -> accountDAO.getAccount(
                request.params(":id")),
                JSONUtil.json());

        put("/account/create", (request, response) -> accountDAO.createAccount(
                request.queryParams("username"),
                new BigDecimal(request.queryParams("balance")),
                request.queryParams("currencycode")),
                JSONUtil.json());

        get("/account/:id/balance", (request, response) -> accountDAO.getBalance(request.params(":id")),
                JSONUtil.json());

        delete("/account/:id", (request, response) -> {
            int responseStatus = accountDAO.deleteAccount(request.params(":id"));
            if (0 == responseStatus)
                return SC_OK;
            response.status(SC_BAD_REQUEST);
            return new ResponseError("Error. Account not deleted");
        }, JSONUtil.json());

        put("/account/:id/withdraw/:amount", (request, response) -> {
            BigDecimal amount = new BigDecimal(request.params(":amount"));
            validateAmountLessThanOrEqualZero(amount);
            BigDecimal amountDelta = amount.negate();
            String accountId = request.params(":id");
            return accountDAO.updateAccountBalance(accountId, amountDelta);
        }, JSONUtil.json());

        put("/account/:id/deposit/:amount", (request, response) -> {
            BigDecimal amount = new BigDecimal(request.params(":amount"));
            validateAmountLessThanOrEqualZero(amount);
            String accountId = request.params(":id");
            return accountDAO.updateAccountBalance(accountId, amount);
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

package com.moneyapp.service;

import com.moneyapp.exception.CustomException;
import com.moneyapp.exception.ResponseError;
import com.moneyapp.dao.AccountDAO;
import com.moneyapp.model.Account;
import com.moneyapp.utils.JsonUtil;

import java.math.BigDecimal;

import static com.moneyapp.utils.JsonUtil.FAILED_RESPONSE;
import static spark.Spark.*;

import static com.moneyapp.utils.Utils.validateBalanceLessThanOrEqualZero;


public class AccountService {

    public AccountService(final AccountDAO accountDAO) {

        get("/account/all", (request, response) -> accountDAO.getAllAccounts(), JsonUtil.json());

        get("/account/:id", (request, response) -> {
            String id = request.params(":id");
            Account account = accountDAO.getAccount(id);
            if (account != null) {
                return account;
            }
            response.status(FAILED_RESPONSE);
            return new ResponseError("No account with id '%s' found", id);
        }, JsonUtil.json());

        put("/account/create", (requestuest, response) -> {
            Account account = accountDAO.createAccount(
                    requestuest.queryParams("username"),
                    new BigDecimal(requestuest.queryParams("balance")),
                    requestuest.queryParams("currencycode"));
            if (account != null)
                return account;
            response.status(FAILED_RESPONSE);
            return new ResponseError("Error. Account not added");
        }, JsonUtil.json());

        get("/account/:id/balance", (request, response) -> {
            String id = request.params(":id");
            BigDecimal balance = accountDAO.getBalance(id);
            if (balance != null) {
                return balance;
            }
            response.status(FAILED_RESPONSE);
            return new ResponseError("No account with id '%s' found", id);
        }, JsonUtil.json());

        delete("/account/:id", (request, response) -> {
            int responseStatus = accountDAO.deleteAccount(
                    request.params(":id"));
            if (0 != responseStatus) {
                response.status(FAILED_RESPONSE);
                return new ResponseError("Error. Account not deleted");
            }
            return 0;
        }, JsonUtil.json());

        put("/account/:id/withdraw/:amount", (request, response) -> {
            BigDecimal amount = new BigDecimal(request.params(":amount"));
            validateBalanceLessThanOrEqualZero(amount);
            BigDecimal amountDelta = amount.negate();
            String accountId = request.params(":id");
            return accountDAO.updateAccountBalance(accountId, amountDelta);
        }, JsonUtil.json());

        put("/account/:id/deposit/:amount", (request, response) -> {
            BigDecimal amount = new BigDecimal(request.params(":amount"));
            validateBalanceLessThanOrEqualZero(amount);
            String accountId = request.params(":id");
            return accountDAO.updateAccountBalance(accountId, amount);
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

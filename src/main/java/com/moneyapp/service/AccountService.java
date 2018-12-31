package com.moneyapp.service;

import com.moneyapp.ResponseError;
import com.moneyapp.dao.AccountDAO;
import com.moneyapp.model.Account;
import com.moneyapp.utils.JsonUtil;

import java.math.BigDecimal;

import static spark.Spark.*;


public class AccountService {

    public AccountService(final AccountDAO accountDAO) {

        get("/account/all", (req, res) -> accountDAO.getAllAccounts(), JsonUtil.json());

        get("/account/:id", (req, res) -> {
            String id = req.params(":id");
            Account account = accountDAO.getAccount(id);
            if (account != null) {
                return account;
            }
            res.status(400);
            return new ResponseError("No account with id '%s' found", id);
        }, JsonUtil.json());

        put("/account/create", (req, res) -> accountDAO.createAccount(
                req.queryParams("username"),
                new BigDecimal(req.queryParams("balance")),
                req.queryParams("currencycode")
        ), JsonUtil.json());

        get("/account/:id/balance", (req, res) -> {
            String id = req.params(":id");
            BigDecimal balance = accountDAO.getBalance(id);
            if (balance != null) {
                return balance;
            }
            res.status(400);
            return new ResponseError("No account with id '%s' found", id);
        }, JsonUtil.json());

        delete("/account/:id", (req, res) -> accountDAO.deleteAccount(
                req.params(":id")
        ), JsonUtil.json());

        put("/account/:id/withdraw/:amount", (req, res) -> {
            BigDecimal amount = new BigDecimal(req.params(":amount"));

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                //TODO throw exception
                //throw new WebApplicationException("Invalid Deposit amount", Response.Status.BAD_REQUEST);
            }

            BigDecimal delta = amount.negate();
            String id = req.params(":id");
            Account account = accountDAO.updateAccountBalance(id, delta);
            if (account != null) {
                return account;
            }
            res.status(400);
            return new ResponseError("No account with id '%s' found", id);
        }, JsonUtil.json());

        put("/account/:id/deposit/:amount", (req, res) -> {
            BigDecimal amount = new BigDecimal(req.params(":amount"));

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                //TODO throw exception
                //throw new WebApplicationException("Invalid Deposit amount", Response.Status.BAD_REQUEST);
            }

            String id = req.params(":id");
            Account account = accountDAO.updateAccountBalance(id, amount);
            if (account != null) {
                return account;
            }
            res.status(400);
            return new ResponseError("No account with id '%s' found", id);
        }, JsonUtil.json());

        after((req, res) -> {
            res.type("application/json");
        });

        exception(IllegalArgumentException.class, (e, req, res) -> {
            res.status(400);
            res.body(JsonUtil.toJson(new ResponseError(e)));
        });
    }
}

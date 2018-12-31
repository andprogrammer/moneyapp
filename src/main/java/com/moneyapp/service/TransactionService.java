package com.moneyapp.service;

import com.moneyapp.ResponseError;
import com.moneyapp.dao.TransactionDAO;
import com.moneyapp.model.Transaction;
import com.moneyapp.utils.JsonUtil;
import spark.Spark;

import java.math.BigDecimal;

import static spark.Spark.after;
import static spark.Spark.exception;

public class TransactionService {

    public TransactionService(final TransactionDAO transactionDAO) {

        Spark.post("/transaction/:from_id/:to_id/:amount/:currency_code", (req, res) -> {
            BigDecimal amount = new BigDecimal(req.params(":amount"));

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                //TODO throw exception
                //throw new WebApplicationException("Invalid Deposit amount", Response.Status.BAD_REQUEST);
            }

            String fromAccountId = req.params(":from_id");
            String toAccountId = req.params(":to_id");
            String currencyCode = req.params(":currency_code");

            Transaction transaction = new Transaction(fromAccountId, toAccountId, amount, currencyCode);

            int response = transactionDAO.transfer(transaction);
            if (response == 0) {
                return "SUCCESS";
            }
            res.status(400);
            return new ResponseError("Transfer failed");
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

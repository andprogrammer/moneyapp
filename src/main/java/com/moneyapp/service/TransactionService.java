package com.moneyapp.service;

import com.moneyapp.dao.TransactionDAO;
import com.moneyapp.exception.CustomException;
import com.moneyapp.exception.ResponseError;
import com.moneyapp.model.Transaction;
import com.moneyapp.utils.JsonUtil;
import spark.Spark;

import java.math.BigDecimal;

import static com.moneyapp.utils.JsonUtil.FAILED_RESPONSE;
import static com.moneyapp.utils.Utils.validateBalanceLessThanOrEqualZero;
import static spark.Spark.after;
import static spark.Spark.exception;


public class TransactionService {

    public TransactionService(final TransactionDAO transactionDAO) {

        Spark.post("/transaction/:from_id/:to_id/:amount/:currency_code", (request, response) -> {
            BigDecimal amount = new BigDecimal(request.params(":amount"));
            validateBalanceLessThanOrEqualZero(amount);
            String fromAccountId = request.params(":from_id");
            String toAccountId = request.params(":to_id");
            String currencyCode = request.params(":currency_code");
            Transaction transaction = new Transaction(fromAccountId, toAccountId, amount, currencyCode);

            int responseStatus = transactionDAO.transfer(transaction);
            if (0 == responseStatus)
                return "SUCCESS";
            response.status(FAILED_RESPONSE);
            return new ResponseError("Transfer failed");
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

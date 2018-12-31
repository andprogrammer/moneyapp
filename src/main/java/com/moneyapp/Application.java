package com.moneyapp;

import com.moneyapp.dao.AccountDAO;
import com.moneyapp.dao.TransactionDAO;
import com.moneyapp.dao.UserDAO;
import com.moneyapp.service.AccountService;
import com.moneyapp.service.TransactionService;
import com.moneyapp.service.UserService;

public class Application {

    public static final AccountDAO ACCOUNT_SERVICE = new AccountDAO();

    public static void main(String[] args) {
        runApplication();
    }

    private static void runApplication() {
        new UserService(new UserDAO());
        new AccountService(ACCOUNT_SERVICE);
        new TransactionService(new TransactionDAO(ACCOUNT_SERVICE));
    }
}

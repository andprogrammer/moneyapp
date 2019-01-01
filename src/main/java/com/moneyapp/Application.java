package com.moneyapp;

import com.moneyapp.dao.AccountDAO;
import com.moneyapp.dao.TransactionDAO;
import com.moneyapp.dao.UserDAO;
import com.moneyapp.service.AccountService;
import com.moneyapp.service.TransactionService;
import com.moneyapp.service.UserService;
import org.apache.log4j.Logger;

public class Application {

    public static final AccountDAO ACCOUNT_SERVICE = new AccountDAO();
    private final static Logger logger = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName().getClass());

    public static void main(String[] args) {
        runApplication();
    }

    private static void runApplication() {

        if (logger.isDebugEnabled())
            logger.info(new Throwable().getStackTrace()[0].getMethodName() + "() Starting Money Application");
        new UserService(new UserDAO());
        new AccountService(ACCOUNT_SERVICE);
        new TransactionService(new TransactionDAO(ACCOUNT_SERVICE));
        if (logger.isDebugEnabled())
            logger.info(new Throwable().getStackTrace()[0].getMethodName() + "() Stopping Money Application");
    }
}

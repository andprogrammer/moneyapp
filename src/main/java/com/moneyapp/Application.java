package com.moneyapp;

import com.moneyapp.dao.AbstractFactory;
import com.moneyapp.dao.AccountDAO;
import com.moneyapp.dao.TransactionDAO;
import com.moneyapp.dao.UserDAO;
import com.moneyapp.service.AccountService;
import com.moneyapp.service.TransactionService;
import com.moneyapp.service.UserService;
import org.apache.log4j.Logger;

public class Application {

    private final static Logger logger = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName().getClass());

    public static void main(String[] args) {
        runApplication();
    }

    private static void runApplication() {
        logger.info(new Throwable().getStackTrace()[0].getMethodName() + "() Starting Money Application");
        startServices();
    }

    private static void startServices() {
        AbstractFactory daoFactory = AbstractFactory.getFactory(AbstractFactory.FactoryType.DAO);
        UserDAO userDAO = daoFactory.getUserDAO();
        AccountDAO accountDAO = daoFactory.getAccountDAO();
        TransactionDAO transactionDAO = daoFactory.getTransactionDAO();
        new UserService(userDAO);
        new AccountService(accountDAO);
        new TransactionService(transactionDAO);
    }
}

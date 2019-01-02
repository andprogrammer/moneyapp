package com.moneyapp.dao;

import com.moneyapp.dao.implementation.AccountDAOImplementation;
import com.moneyapp.dao.implementation.TransactionDAOImplementation;
import com.moneyapp.dao.implementation.UserDAOImplementation;

public class DAOFactory extends AbstractFactory {

    private final UserDAOImplementation userDAO = new UserDAOImplementation();
    private final AccountDAOImplementation accountDAO = new AccountDAOImplementation();
    private final TransactionDAOImplementation transactionDAO = new TransactionDAOImplementation(accountDAO);

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public AccountDAO getAccountDAO() {
        return accountDAO;
    }

    public TransactionDAO getTransactionDAO() {
        return transactionDAO;
    }
}

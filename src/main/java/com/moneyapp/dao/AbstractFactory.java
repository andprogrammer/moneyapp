package com.moneyapp.dao;

public abstract class AbstractFactory {

    public abstract UserDAO getUserDAO();

    public abstract AccountDAO getAccountDAO();

    public abstract TransactionDAO getTransactionDAO();

    public static DAOFactory getFactory() {
        return new DAOFactory();
    }
}

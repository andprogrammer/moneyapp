package com.moneyapp.dao;

import com.moneyapp.exception.CustomException;
import com.moneyapp.model.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDAO {

    List<Account> getAllAccounts() throws CustomException;

    Account getAccount(String id) throws CustomException;

    Account createAccount(String userName, BigDecimal balance, String currencyCode) throws CustomException;

    BigDecimal getBalance(String id) throws CustomException;

    int deleteAccount(String id) throws CustomException;

    Account updateAccountBalance(String id, BigDecimal amount) throws CustomException;
}

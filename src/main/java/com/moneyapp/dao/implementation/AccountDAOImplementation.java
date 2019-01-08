package com.moneyapp.dao.implementation;

import com.moneyapp.dao.AccountDAO;
import com.moneyapp.exception.CustomException;
import com.moneyapp.model.Account;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.moneyapp.utils.Utils.validateAmountLessThanZero;
import static com.moneyapp.utils.Utils.validateId;

public class AccountDAOImplementation implements AccountDAO {

    private Map<String, Account> accounts = new HashMap<>();
    private final static Logger logger = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName().getClass());

    public List<Account> getAllAccounts() throws CustomException {
        if (logger.isDebugEnabled())
            logger.debug(new Throwable().getStackTrace()[0].getMethodName() + "() Number of accounts=" + accounts.size());
        return new ArrayList<>(accounts.values());
    }

    public Account getAccount(String id) throws CustomException {
        validateId(id);
        if (!accounts.containsKey(id))
            throw new CustomException("Account with id " + id + " not found");
        if (logger.isDebugEnabled())
            logger.debug(new Throwable().getStackTrace()[0].getMethodName() + "() " + accounts.get(id));
        return accounts.get(id);
    }

    public Account createAccount(String userName, BigDecimal balance, String currencyCode) throws CustomException {
        Account account = new Account(userName, balance, currencyCode);
        validateAccount(account);
        if (accounts.containsValue(account))
            throw new CustomException("Account " + account + " already exists");
        if (logger.isDebugEnabled())
            logger.debug(new Throwable().getStackTrace()[0].getMethodName() + "() " + account);
        accounts.put(account.getId(), account);
        return account;
    }

    public BigDecimal getBalance(String id) throws CustomException {
        return getAccount(id).getBalance();
    }

    public int deleteAccount(String id) throws CustomException {
        validateId(id);
        if (logger.isDebugEnabled())
            logger.debug(new Throwable().getStackTrace()[0].getMethodName() + "() " + getAccount(id));
        synchronized (accounts) {
            accounts.remove(id);
        }
        return 0;
    }

    public Account updateAccountBalance(String id, BigDecimal amount) throws CustomException {
        Account account = getAccount(id);
        if (logger.isDebugEnabled())
            logger.debug(new Throwable().getStackTrace()[0].getMethodName() + "() " + account);
        synchronized (account) {
            BigDecimal balance = account.getBalance().add(amount);
            account.setBalance(balance);
        }
        return account;
    }

    private void validateAccount(Account account) throws CustomException {
        if (account.getUserName() == null || account.getUserName().isEmpty())
            throw new CustomException("Incorrect username=" + account.getUserName());
        validateAmountLessThanZero(account.getBalance());
        if (account.getCurrencyCode() == null || account.getCurrencyCode().isEmpty())
            throw new CustomException("Incorrect currency code=" + account.getCurrencyCode());
    }
}

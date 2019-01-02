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

import static com.moneyapp.utils.Utils.*;

public class AccountDAOImplementation implements AccountDAO {

    private Map<String, Account> accounts = new HashMap<>();
    private final static Logger logger = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName().getClass());

    public List<Account> getAllAccounts() throws CustomException {
        validateAccount();
        if (logger.isDebugEnabled())
            logger.debug(new Throwable().getStackTrace()[0].getMethodName() + "() Number of accounts=" + accounts.size());
        return new ArrayList<>(accounts.values());
    }

    public Account getAccount(String id) throws CustomException {
        validateId(id);
        validateAccount();
        checkIfIdAlreadyExists(id);
        if (logger.isDebugEnabled())
            logger.debug(new Throwable().getStackTrace()[0].getMethodName() + "() " + accounts.get(id));
        return accounts.get(id);
    }

    public Account createAccount(String userName, BigDecimal balance, String currencyCode) throws CustomException {
        validateAccount(userName, balance, currencyCode);
        Account account = new Account(userName, balance, currencyCode);
        checkIfAccountAlreadyExists(account);
        if (logger.isDebugEnabled())
            logger.debug(new Throwable().getStackTrace()[0].getMethodName() + "() " + account);
        addAccount(account);
        return account;
    }

    public BigDecimal getBalance(String id) throws CustomException {
        return getAccount(id).getBalance();
    }

    public int deleteAccount(String id) throws CustomException {
        validateId(id);
        validateAccount();
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

    public void addAccount(Account account) throws CustomException {
        validateAccount();
        if (logger.isDebugEnabled())
            logger.debug(new Throwable().getStackTrace()[0].getMethodName() + "() " + account);
        accounts.put(account.getId(), account);
    }

    private void checkIfIdAlreadyExists(String id) throws CustomException {
        if (!accounts.containsKey(id)) {
            logger.error(new Throwable().getStackTrace()[0].getMethodName() + "() Account with id=" + id + " already exists");
            throw new CustomException("Account with id " + id + " not found");
        }
    }

    private boolean checkIfAccountAlreadyExists(Account account) throws CustomException {
        validateAccount();
        if (accounts.containsValue(account)) {
            logger.error(new Throwable().getStackTrace()[0].getMethodName() + "() " + account + " already exists");
            throw new CustomException("Account already exists");
        }
        return false;
    }

    private void validateAccount(String userName, BigDecimal balance, String currencyCode) throws CustomException {
        if (userName == null || userName.isEmpty()) {
            logger.error(new Throwable().getStackTrace()[0].getMethodName() + "() Incorrect username=" + userName);
            throw new CustomException("Empty 'userName' parameter");
        }
        validateAmountLessThanZero(balance);
        if (currencyCode == null || currencyCode.isEmpty()) {
            logger.error(new Throwable().getStackTrace()[0].getMethodName() + "() Incorrect currency code=" + currencyCode);
            throw new CustomException("Empty 'currencyCode' parameter");
        }
    }

    private void validateAccount() throws CustomException {
        if (accounts == null) {
            logger.error(new Throwable().getStackTrace()[0].getMethodName() + "() No accounts data");
            throw new CustomException("Error reading accounts data");
        }
    }
}

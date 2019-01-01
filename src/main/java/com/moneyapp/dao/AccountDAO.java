package com.moneyapp.dao;

import com.moneyapp.exception.CustomException;
import com.moneyapp.model.Account;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.moneyapp.utils.Utils.validateBalanceLessThanZero;
import static com.moneyapp.utils.Utils.validateId;


public class AccountDAO {

    private Map<String, Account> accounts = new HashMap<>();

    public List<Account> getAllAccounts() throws CustomException {
        validateAccount();
        return new ArrayList<>(accounts.values());
    }

    public Account getAccount(String id) throws CustomException {
        validateId(id);
        validateAccount();
        if (!accounts.containsKey(id))
            throw new CustomException("Account with id " + id + " not found");
        return accounts.get(id);
    }

    public Account createAccount(String userName, BigDecimal balance, String currencyCode) throws CustomException {
        validateAccount(userName, balance, currencyCode);
        Account account = new Account(userName, balance, currencyCode);
        checkIfAccountAlreadyExists(account);
        addAccount(account);
        return account;
    }

    public BigDecimal getBalance(String id) throws CustomException {
        return getAccount(id).getBalance();
    }

    public int deleteAccount(String id) throws CustomException {
        validateId(id);
        validateAccount();
        synchronized(accounts) {
            accounts.remove(id);
        }
        return 0;
    }

    public Account updateAccountBalance(String id, BigDecimal amount) throws CustomException {
        Account account = getAccount(id);
        synchronized (account) {
            BigDecimal balance = account.getBalance().add(amount);
            account.setBalance(balance);
        }
        return account;
    }

    private void addAccount(Account account) throws CustomException {
        validateAccount();
        accounts.put(account.getId(), account);
    }

    private boolean checkIfAccountAlreadyExists(Account account) throws CustomException {
        validateAccount();
        if (accounts.containsValue(account))
            throw new CustomException("Account already exists");
        return false;
    }

    private void validateAccount(String userName, BigDecimal balance, String currencyCode) throws CustomException {
        if (userName == null || userName.isEmpty())
            throw new CustomException("Empty 'userName' parameter");
        validateBalanceLessThanZero(balance);
        if (currencyCode == null || currencyCode.isEmpty())
            throw new CustomException("Empty 'currencyCode' parameter");
    }

    private void validateAccount() throws CustomException {
        if (accounts == null)
            throw new CustomException("Error reading accounts data");
    }
}

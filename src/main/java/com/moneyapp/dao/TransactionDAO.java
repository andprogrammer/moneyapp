package com.moneyapp.dao;

import com.moneyapp.exception.CustomException;
import com.moneyapp.model.Transaction;

public interface TransactionDAO {

    int transfer(Transaction transaction) throws CustomException;
}

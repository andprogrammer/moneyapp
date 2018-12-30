import java.math.BigDecimal;

public class TransactionService {

    private AccountService accountService;

    public TransactionService(final AccountService accountService) {
        this.accountService = accountService;
    }

    public int transfer(Transaction transaction) {
        Account fromAccount = accountService.getAccount(transaction.getFromAccountId());
        if (fromAccount == null) {
            throw new IllegalArgumentException("No account with id '" + fromAccount.getId() + "' found");
        }
        Account toAccount = accountService.getAccount(transaction.getToAccountId());
        if (toAccount == null) {
            throw new IllegalArgumentException("No account with id '" + toAccount.getId() + "' found");
        }

        BigDecimal amount = transaction.getAmount();
        if (amount.compareTo(BigDecimal.ZERO) <= 0){
            //TODO throw exception
            //throw new CustomException("Not sufficient Fund for account: " + accountId);
        }
        String currencyCode = transaction.getCurrencyCode();
        if (currencyCode == null) {
            throw new IllegalArgumentException("Incorrect currency code");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        BigDecimal fromAccountNewBalance = fromAccount.getBalance().subtract(amount);
        if (fromAccountNewBalance.compareTo(BigDecimal.ZERO) < 0){
            //throw new CustomException("Not enough Fund from source Account ");
            //TODO throw exception
        }

        toAccount.setBalance(toAccount.getBalance().add(amount));

        return 0;
    }
}

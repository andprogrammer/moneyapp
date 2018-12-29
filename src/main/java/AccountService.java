import java.math.BigDecimal;
import java.util.*;

public class AccountService {

    private Map<String, Account> Accounts = new HashMap<>();

    public List<Account> getAllAccounts() {
        return new ArrayList<>(Accounts.values());
    }

    public Account getAccount(String id) {
        return Accounts.get(id);
    }

    public Account createAccount(String userName, BigDecimal balance, String currencyCode) {
        failIfInvalid(userName, balance, currencyCode);
        Account Account = new Account(userName, balance, currencyCode);
        Accounts.put(Account.getId(), Account);
        return Account;
    }

    public BigDecimal getBalance(String id) {
        return getAccount(id).getBalance();
    }

    public Account deleteAccount(String id) {
        Accounts.remove(id);
        return null;    //TODO change
    }

    public Account updateAccountBalance(String id, BigDecimal deltaAmount) {
        Account account = Accounts.get(id);
        if (account == null) {
            throw new IllegalArgumentException("No account with id '" + id + "' found");
        }

        BigDecimal balance = account.getBalance().add(deltaAmount);
        if (balance.compareTo(BigDecimal.ZERO) <= 0){
            //TODO throw exception
            //throw new CustomException("Not sufficient Fund for account: " + accountId);
        }
        account.setBalance(balance);
        return account;
    }

    private void failIfInvalid(String userName, BigDecimal balance, String currencyCode) {
        if (userName == null || userName.isEmpty()) {
            throw new IllegalArgumentException("Parameter 'userName' cannot be empty");
        }
        if (balance.compareTo(BigDecimal.ZERO) < 0 || balance == null) {
            throw new IllegalArgumentException("Parameter 'balance' cannot be less than zero");
        }
        if (currencyCode == null || currencyCode.isEmpty()) {
            throw new IllegalArgumentException("Parameter 'currencyCode' cannot be empty");
        }
    }
}
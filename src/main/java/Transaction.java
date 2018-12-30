import java.math.BigDecimal;

public class Transaction {

    private String fromAccountId;

    private String toAccountId;

    private BigDecimal amount;

    private String currencyCode;

    public Transaction(String fromAccountId, String toAccountId, BigDecimal amount, String currencyCode) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.currencyCode = currencyCode;
    }

    public String getFromAccountId() {
        return fromAccountId;
    }
    public String getToAccountId() {
        return toAccountId;
    }
    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

}

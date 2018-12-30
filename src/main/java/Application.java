public class Application {

    public static final AccountService ACCOUNT_SERVICE = new AccountService();

    public static void main(String[] args) {

        new UserController(new UserService());
        new AccountController(ACCOUNT_SERVICE);
        new TransactionController(new TransactionService(ACCOUNT_SERVICE));
    }
}
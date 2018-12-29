public class Application {
    public static void main(String[] args) {

        new UserController(new UserService());
        new AccountController(new AccountService());
    }
}
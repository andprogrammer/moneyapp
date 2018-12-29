import java.math.BigDecimal;

import static spark.Spark.*;


public class AccountController {

    public AccountController(final AccountService accountService) {

        get("/account/all", (req, res) -> accountService.getAllAccounts(), JsonUtil.json());

        get("/account/:id", (req, res) -> {
            String id = req.params(":id");
            Account account = accountService.getAccount(id);
            if (account != null) {
                return account;
            }
            res.status(400);
            return new ResponseError("No account with id '%s' found", id);
        }, JsonUtil.json());

        put("/account/create", (req, res) -> accountService.createAccount(
                req.queryParams("username"),
                new BigDecimal(req.queryParams("balance")),
                req.queryParams("currencycode")
        ), JsonUtil.json());

        get("/account/:id/balance", (req, res) -> {
            String id = req.params(":id");
            BigDecimal balance = accountService.getBalance(id);
            if (balance != null) {
                return balance;
            }
            res.status(400);
            return new ResponseError("No account with id '%s' found", id);
        }, JsonUtil.json());

        delete("/account/:id", (req, res) -> accountService.deleteAccount(
                req.params(":id")
        ), JsonUtil.json());

        put("/account/:id/withdraw/:amount", (req, res) -> {
            BigDecimal amount = new BigDecimal(req.params(":amount"));

            if (amount.compareTo(BigDecimal.ZERO) <= 0){
                //TODO throw exception
                //throw new WebApplicationException("Invalid Deposit amount", Response.Status.BAD_REQUEST);
            }

            BigDecimal delta = amount.negate();
            String id = req.params(":id");
            Account account = accountService.updateAccountBalance(id, delta);
            if (account != null) {
                return account;
            }
            res.status(400);
            return new ResponseError("No account with id '%s' found", id);
        }, JsonUtil.json());

        put("/account/:id/deposit/:amount", (req, res) -> {
            BigDecimal amount = new BigDecimal(req.params(":amount"));

            if (amount.compareTo(BigDecimal.ZERO) <= 0){
                //TODO throw exception
                //throw new WebApplicationException("Invalid Deposit amount", Response.Status.BAD_REQUEST);
            }

            String id = req.params(":id");
            Account account = accountService.updateAccountBalance(id, amount);
            if (account != null) {
                return account;
            }
            res.status(400);
            return new ResponseError("No account with id '%s' found", id);
        }, JsonUtil.json());

        after((req, res) -> {
            res.type("application/json");
        });

        exception(IllegalArgumentException.class, (e, req, res) -> {
            res.status(400);
            res.body(JsonUtil.toJson(new ResponseError(e)));
        });
    }
}
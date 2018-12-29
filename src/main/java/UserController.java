import static spark.Spark.*;


public class UserController {

    public UserController(final UserService userService) {

        get("/user/all", (req, res) -> userService.getAllUsers(), JsonUtil.json());

        get("/user/:id", (req, res) -> {
            String id = req.params(":id");
            User user = userService.getUser(id);
            if (user != null) {
                return user;
            }
            res.status(400);
            return new ResponseError("No user with id '%s' found", id);
        }, JsonUtil.json());

        put("/user/create", (req, res) -> userService.createUser(
                req.queryParams("name"),
                req.queryParams("email")
        ), JsonUtil.json());

        post("/user/:id", (req, res) -> userService.updateUser(
                req.params(":id"),
                req.queryParams("name"),
                req.queryParams("email")
        ), JsonUtil.json());

        delete("/user/:id", (req, res) -> userService.deleteUser(
                req.params(":id")
        ), JsonUtil.json());

        after((req, res) -> {
            res.type("application/json");
        });

        exception(IllegalArgumentException.class, (e, req, res) -> {
            res.status(400);
            res.body(JsonUtil.toJson(new ResponseError(e)));
        });
    }
}
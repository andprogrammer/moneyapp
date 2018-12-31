package com.moneyapp.utils;

import com.google.gson.Gson;
import spark.ResponseTransformer;

public class JsonUtil {

    public static final int SUCCESS_RESPONSE = 200;
    public static final int FAILED_RESPONSE = 400;

    public static String toJson(Object object) {
        return new Gson().toJson(object);
    }
    public static ResponseTransformer json() {
        return JsonUtil::toJson;
    }
}

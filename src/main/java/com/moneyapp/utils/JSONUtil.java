package com.moneyapp.utils;

import com.google.gson.Gson;
import spark.ResponseTransformer;

public class JSONUtil {

    public static final int SUCCESSFUL_RESPONSE = 200;
    public static final int FAILED_RESPONSE = 400;

    public static String toJson(Object object) {
        return new Gson().toJson(object);
    }

    public static ResponseTransformer json() {
        return JSONUtil::toJson;
    }
}

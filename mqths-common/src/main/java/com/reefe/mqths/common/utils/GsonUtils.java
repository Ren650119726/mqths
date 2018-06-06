package com.reefe.mqths.common.utils;

import com.google.gson.Gson;

public class GsonUtils {

//    private static final GsonUtils GSON_UTILS = new GsonUtils();

    private static final Gson GSON = new Gson();

//    public static GsonUtils getInstance() {
//        return GSON_UTILS;
//    }

    public static String toJson(Object object) {
        return GSON.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> tClass) {
        return GSON.fromJson(json, tClass);
    }
}
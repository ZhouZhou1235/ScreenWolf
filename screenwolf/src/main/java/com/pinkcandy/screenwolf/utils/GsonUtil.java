package com.pinkcandy.screenwolf.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * json解析工具类
 * 使用google gson
 */
public class GsonUtil {
    private static Gson gson = new GsonBuilder().create();
    public static String bean2Json(Object obj){return gson.toJson(obj);}
    public static <T>T json2Bean(String jsonStr,Class<T> objClass){return gson.fromJson(jsonStr, objClass);}
}

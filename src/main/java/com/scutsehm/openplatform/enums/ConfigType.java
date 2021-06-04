package com.scutsehm.openplatform.enums;

/**
 * 配置项类型的枚举类
 * type：类型，javaName：对应的java对象类型，jsonName：对应的fastJson解析得到的对象类型
 */
public enum ConfigType {
    String("String", "class java.lang.String", "class java.lang.String"),
    List("List", "class java.util.List", "class com.alibaba.fastjson.JSONArray"),
    Map("Map", "class java.util.Map", "class com.alibaba.fastjson.JSONObject"),
    Boolean("Boolean", "class java.lang.Boolean", "class java.lang.Boolean")
    ;

    private String type;
    private String javaName;
    private String jsonName;

    ConfigType(String type, String javaName, String jsonName){
        this.type = type;
        this.javaName = javaName;
        this.jsonName = jsonName;
    }

    public static String getJavaName(String type){
        return ConfigType.valueOf(type).javaName;
    }
    public static String getJsonName(String type){
        return ConfigType.valueOf(type).jsonName;
    }
}

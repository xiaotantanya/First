package com.scutsehm.openplatform.util;

/**
 * 枚举类
 * TODO 将来更换json解析模块时可能用到
 * JSONUtil解包jsonObject时可能需要用到的转换列表
 * javaObjectName：在java中的对象class名称，jasonObjectName：在jsonObject中的对象class名称
 * type：在Config文件中规定类型时的名称，务必保证type和对应enum名称一致
 */
public enum FastJsonEnum {
    String("String", "class java.lang.String", "class java.lang.String"),
    Array("List", "class java.util.List", "class com.alibaba.fastjson.JSONArray"),
    Float("Float", "class java.lang.Double", "class java.math.BigDecimal"),
    Int("Integer", "class java.lang.Integer", "class java.lang.Integer"),
    Long("Long", "class java.lang.Long", "class java.lang.Integer"),
    Map("Map", "class java.util.Map", "class com.alibaba.fastjson.JSONObject");

    private String type;
    private String javaObjectName;
    private String jsonObjectName;

    FastJsonEnum(String type, String javaObjectName, String jsonObjectName){
        this.type = type;
        this.javaObjectName = javaObjectName;
        this.jsonObjectName = jsonObjectName;
    }

    public static String getJavaName(String type){
        return FastJsonEnum.valueOf(type).javaObjectName;
    }

    public static String getJsonName(String type){
        return FastJsonEnum.valueOf(type).jsonObjectName;
    }

    public static String javaName2jsonName(String javaName){
        for(FastJsonEnum fast: FastJsonEnum.values()){
            if(fast.javaObjectName.equals(javaName)) {return fast.jsonObjectName;}
        }
        return null;
    }

    public static String jsonName2javaName(String jsonName){
        for(FastJsonEnum fast: FastJsonEnum.values()){
            if(fast.jsonObjectName.equals(jsonName)) {return fast.javaObjectName;}
        }
        return null;
    }

    public static void main(String[] args){
        System.out.println(FastJsonEnum.getJavaName("String"));
    }
}

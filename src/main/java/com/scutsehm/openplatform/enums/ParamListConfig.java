package com.scutsehm.openplatform.enums;

/**
 * 参数列表的参数枚举类
 * 公共子属性项，在config.ini中保存为param_list
 * 建议与后端参考文档搭配以了解这些参数的详细使用
 */
public enum ParamListConfig {
    Integer("Integer", "class java.lang.Integer", "整数类型"),
    Double("Double", "class java.lang.Double", "浮点数类型，包括单双精度"),
    String("String", "class java.lang.String", "字符串类型")
    ;

    private String type;
    private String javaType;
    private String description;

    ParamListConfig(String type, String javaType, String description){
        this.type = type;
        this.javaType = javaType;
        this.description = description;
    }

    /** 检查param类型是否已在枚举类中被定义
     */
    public static boolean isLegal(String type){
        for(ParamListConfig paramListConfig : ParamListConfig.values()){
            if(paramListConfig.type.equals(type)) {return true;}
        }
        return false;
    }

    public static String getJavaType(String type){
        return ParamListConfig.valueOf(type).javaType;
    }

    /** 将java类型转换为对应param类型
     * @param javaType
     * @return javaType或者null
     */
    public static String javaType2type(String javaType){
        for(ParamListConfig paramListConfig : ParamListConfig.values()){
            if(paramListConfig.javaType.equals(javaType))
                return paramListConfig.type;
        }
        return null;
    }

    /** 将param类型转换为java类型
     * @param type
     * @return type或者null
     */
    public static String type2javaType(String type){
        for(ParamListConfig paramListConfig : ParamListConfig.values()){
            if(paramListConfig.type.equals(type))
                return paramListConfig.javaType;
        }
        return null;
    }
}

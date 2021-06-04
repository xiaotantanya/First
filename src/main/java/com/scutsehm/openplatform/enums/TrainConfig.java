package com.scutsehm.openplatform.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 训练模型使用的参数枚举类
 * 参数解释：key（参数key），description（参数描述），isNecessary（该项是否必要），classType（类型）
 * 建议与后端参考文档搭配以了解这些参数的详细使用
 */
public enum TrainConfig {
    type("type", "固定项-TrainConfig", true, "String"),
    model_name("model_name", "模型名称", true, "String"),
    introduce("introduce", "模型介绍", false, "String"),
    entry_path("entry_path", "调用入口文件路径", true, "String"),
    param_type("param_type", "参数传入类型", true, "String", new String[]{"sequence", "appoint", "none"}),
    param_sequence("param_sequence", "参数顺序，param_type为sequence时必须", false, "List"),
    param_list("param_list", "参数列表",false, "Map"),      //用Map保存无法确保顺序一致性
    input_type("input_type", "输入文件类型", true, "String", new String[]{"data", "user", "none"}),
    output_type("output_type", "输出文件类型", true, "String", new String[]{"data", "user", "none"}),
//    input_path("input_path", "输入文件相对路径", false, "String"), //若input_type不为none，则应进行检查
//    output_path("output_path", "输出文件的相对路径", false, "String"),
    auto_check("auto_check", "是否自动检查文件完整", true, "Boolean"),
    folder_config("folder_config","文件列表，auto_check为true时必有", false, "Map"),
    auto_deploy("auto_deploy", "模型训练完毕后是否自动部署", true, "Boolean"),
    deploy_config("deploy_config", "部署参数，auto_deploy为true时必有", false, "Map");

    private String key;
    private String description;
    private boolean isNecessary;
    private String classType;
    private String[] option =null;

    TrainConfig(String key, String description, boolean isNecessary, String classType){
        this.key = key;
        this.description = description;
        this.isNecessary = isNecessary;
        this.classType = classType;
        this.option = null;
    }
    TrainConfig(String key, String description, boolean isNecessary, String classType, String[] option){
        this.key = key;
        this.description = description;
        this.isNecessary = isNecessary;
        this.classType = classType;
        this.option = option;
    }

    /** 获取必要参数列表
     */
    public static ArrayList<String> getConfigList(){
        ArrayList<String> configList = new ArrayList<>();
        for(TrainConfig trainConfig : TrainConfig.values()){
            if(trainConfig.isNecessary)
                configList.add(trainConfig.key);
        }
        return configList;
    }

    public static String getClassType(String key){
        return TrainConfig.valueOf(key).classType;
    }

    /** 根据key获取对应的可选值列表
     * @param key, 无合法性检查
     */
    public static ArrayList<String> getOption(String key){
        if(TrainConfig.valueOf(key).option == null) return null;
        return new ArrayList<String>(Arrays.asList(TrainConfig.valueOf(key).option));
    }

    /** 获取带有可选值列表的参数的列表
     */
    public static ArrayList<String> getOptionConfigList(){
        ArrayList<String> configList = new ArrayList<>();
        for(TrainConfig trainConfig : TrainConfig.values()){
            if(trainConfig.option != null)
                configList.add(trainConfig.key);
        }
        return configList;
    }

}

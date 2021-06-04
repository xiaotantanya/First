package com.scutsehm.openplatform.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 自动部署的参数枚举类
 * 是TrainConfig的属性项，保存为deploy_config
 * 建议与后端参考文档搭配以了解这些参数的详细使用
 */
public enum DeployConfig {
    deploy_model_name("deploy_model_name", "模型名称", true,  "String"),
    deploy_introduce("deploy_introduce", "模型介绍",  false,"String"),
    deploy_entry_path("deploy_entry_path", "调用入口文件路径",  true,"String"),
    deploy_param_type("deploy_param_type", "参数传入类型",  true,"String", new String[]{"sequence", "appoint", "none"}),
    deploy_param_sequence("deploy_param_sequence", "参数顺序，param_type为sequence时必须", false, "List"),
    deploy_param_list("deploy_param_list", "参数列表", false, "Map"),      //注意必须按顺序，应采用list或者linkedHashMap保存
    deploy_input_type("deploy_input_type", "输入文件类型",  true,"String", new String[]{"data", "user", "none"}),
    deploy_output_type("deploy_output_type", "输出文件类型",  true,"String", new String[]{"data", "user", "none"});
    //    input_path("input_path", "输入文件相对路径", false, "String"), //若input_type不为none，则应进行检查
//    output_path("output_path", "输出文件的相对路径", false, "String"),

    private String key;
    private String description;
    private boolean isNecessary;
    private String classType;
    private String[] option =null;

    DeployConfig(String key, String description, boolean isNecessary, String classType){
        this.key = key;
        this.description = description;
        this.isNecessary = isNecessary;
        this.classType = classType;
        this.option = null;
    }

    DeployConfig(String key, String description, boolean isNecessary, String classType, String[] option){
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
        for(DeployConfig deployConfig : DeployConfig.values()){
            if(deployConfig.isNecessary)
                configList.add(deployConfig.key);
        }
        return configList;
    }

    public static String getClassType(String key){
        return DeployConfig.valueOf(key).classType;
    }

    /** 根据key获取对应可选值列表
     * @param key
     */
    public static ArrayList<String> getOption(String key){
        if(DeployConfig.valueOf(key).option == null) return null;
        return new ArrayList<String>(Arrays.asList(DeployConfig.valueOf(key).option));
    }

    /** 获取带有可选值列表的参数的列表
     */
    public static ArrayList<String> getOptionConfigList(){
        ArrayList<String> configList = new ArrayList<>();
        for(DeployConfig deployConfig: DeployConfig.values()){
            if(deployConfig.option != null)
                configList.add(deployConfig.key);
        }
        return configList;
    }

}

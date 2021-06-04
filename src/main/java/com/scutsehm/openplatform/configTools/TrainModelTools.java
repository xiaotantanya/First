package com.scutsehm.openplatform.configTools;

import com.scutsehm.openplatform.enums.DeployConfig;
import com.scutsehm.openplatform.enums.TrainConfig;
import com.scutsehm.openplatform.util.FileUtil;
import com.scutsehm.openplatform.util.PathUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * 用以处理单个处理模型文件夹的工具类
 * 注意每个模型的所有文件（代码文件、调用文件、配置文件等）均被存放在同一个文件夹中
 * checkConfig 检查配置文件完整性和正确性
 * checkDeployConfig 检查deploy_config，自动配置子项的完整和正确性
 * scanFolder 扫描文件夹，检查其完备性，并返回configMap
 */
public class TrainModelTools {
    /** 检查config.ini文件中的内容是否合法完整,
     * 包括检查固定项、必须项、选择项、特殊项,
     * 不对子项内容进行检查，相关检查任务由scanFolder调用进行
     * @param configMap 属性列表
     */
    public static boolean checkConfig(Map<String, Object> configMap){
        //检查固定项
        if(!configMap.get("type").equals("TrainConfig")) {return false;}
        //检查必须选项 isNecessary
        for(String config: TrainConfig.getConfigList()){
            if(!configMap.containsKey(config)) {System.out.println(config);return false;}
        }
        //检查固定选择内容项 isOptional
        for(String optionConfig: TrainConfig.getOptionConfigList()){
            ArrayList<String> options = TrainConfig.getOption(optionConfig);
            if(!options.contains(configMap.get(optionConfig).toString())) { System.out.println(optionConfig);return false; }
        }
        //检查特殊规则项 isSpecial
        if(!ConfigCheckTools.checkPublicSpecialConfig(configMap)) {return false;}
        //TrainModel需要检查的特殊项，当auto_deploy为true时，deploy_config应当存在
        if(configMap.get("auto_deploy").toString().equals("true")){
            if(!configMap.containsKey("deploy_config")) {System.out.println("ad");return false;}
        }
        //检查param_list内容是否符合要求
        if(configMap.get("param_type").equals("sequence")){
            if(!ConfigCheckTools.checkParamSequence(configMap.get("param_sequence"))) {System.out.println("check_pl"); return false;}
        }
        else{
            if(!ConfigCheckTools.checkParamList(configMap.get("param_list"))) {System.out.println("check_pl_2"); return false;}
        }
        //检查各项类型是否符合要求
        if(!ConfigCheckTools.checkTrainConfigType(configMap)) {System.out.println("type"); return false;};
        return true;
    }

    /** 检查配置子属性是否合法完整
     * @param folder_config 文件夹配置属性
     * @param deploy_config 配置子属性
     */
    public static boolean checkDeployConfig(Map<String, String> folder_config, Map<String, Object> deploy_config, String output_type){
        //检查必要项的存在
        for(String config: DeployConfig.getConfigList()){
            if(!deploy_config.containsKey(config)) {System.out.println("deploy_missing:\t"+config);return false;}
        }
        //检查固定选择项内容是否正确
        for(String optionConfig: DeployConfig.getOptionConfigList()){
            ArrayList<String> options = DeployConfig.getOption(optionConfig);
            if(!options.contains(deploy_config.get(optionConfig).toString())) { System.out.println(optionConfig);return false; }
        }
        //检查特殊项完整性
        //deploy_param_type不为appoint时，必须有deploy_param_list
        if(deploy_config.get("deploy_param_type").toString().equals("appoint")){
            if(!deploy_config.containsKey("deploy_param_list")) {System.out.println("deploy_pl"); return false;}
        }
        //deploy_param_type为sequence时，必须有deploy_param_sequence
        if(deploy_config.get("deploy_param_type").toString().equals("sequence")){
            if(!deploy_config.containsKey("deploy_param_sequence")) {System.out.println("deploy_ps"); return false;}
        }
        //deploy_entry_path为 equals_output 时，检查output_type
        if(deploy_config.get("deploy_entry_path").toString().equals("equals_output")){
            if(!output_type.equals("file")) {System.out.println("deploy_ep"); return false;}
        }
        //检查deploy_param_list内容是否符合要求
        if(deploy_config.get("deploy_param_type").equals("sequence")){
            if(!ConfigCheckTools.checkParamSequence(deploy_config.get("deploy_param_sequence"))) {System.out.println("deploy_check_pl"); return false;}
        }
        else{
            if(!ConfigCheckTools.checkParamList(deploy_config.get("deploy_param_list"))) {System.out.println("deploy_check_pl_2"); return false;}
        }
        //检查deploy_config中子项的类型是否符合要求
        if(!ConfigCheckTools.checkDeployConfigType(deploy_config)) {System.out.println("deploy_type"); return false;}
        return true;
    }

    /**检查单个模型文件夹里的内容。实质是读取config文件，检查文件夹中文件完整性
     * @param path 文件夹的路径
     * @return 完备则返回结果map，否则为null
     */
    public static Map<String, Object> scanFolder(String path) throws IOException{
        File folder = new File(path);
        if(!folder.isDirectory()) return null;

        Map<String, Object> configMap = null;
        try {
            configMap = FileUtil.readMap(PathUtil.join(path, "config.ini"));
        }catch (IOException e){ throw e; }
        if(configMap==null) {return null;}

        //检查config内容，必须
        if(!checkConfig(configMap)) {return null;}

        //文件配置检查和自动部署选项检查，非必须
        boolean auto_check =  (boolean)configMap.get("auto_check");
        boolean auto_deploy = (boolean)configMap.get("auto_deploy");
        Map<String, String> folder_config = (Map<String, String>)configMap.get("folder_config");
        Map<String, Object> deploy_config = (Map<String, Object>)configMap.get("deploy_config");
        //检查文件完整性
        if(auto_check){
            if(!ConfigCheckTools.checkFolderConfig(path, folder_config)) {return null;}
        }
        //检查自动配置子项
        if(auto_deploy){
            if(!checkDeployConfig(folder_config, deploy_config, configMap.get("output_type").toString())) {return null;}
        }

        return configMap;
    }
}

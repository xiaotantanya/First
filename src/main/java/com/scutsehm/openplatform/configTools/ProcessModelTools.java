package com.scutsehm.openplatform.configTools;

import com.scutsehm.openplatform.enums.ProcessConfig;
import com.scutsehm.openplatform.util.FileUtil;
import com.scutsehm.openplatform.util.PathUtil;

import java.io.*;
import java.util.*;

/**
 * 用以处理单个处理模型文件夹的工具类
 * 注意每个模型的所有文件（代码文件、调用文件、配置文件等）均被存放在同一个文件夹中
 * checkConfig 检查配置文件完整性和正确性
 * scanFolder 扫描文件夹，检查其完备性，并返回configMap
 */
public class ProcessModelTools {
    /** 检查配置文件（config.ini）的完整性和正确性，
     * 包括检查固定项、必须项、选择项、特殊项，
     * 不对子项内部内容进行检查，相关检查任务由scanFolder调用进行，
     * @param configMap 属性列表
     */
    public static boolean checkConfig(Map<String, Object> configMap){
        //检查固定项
        if(!configMap.get("type").equals("ProcessConfig")) {return false;}
        //检查必须选项 isNecessary
        for(String config: ProcessConfig.getConfigList()){
            if(!configMap.containsKey(config)) {System.out.println(config); return false;}
        }
        //检查固定选择内容项 isOptional
        for(String optionConfig: ProcessConfig.getOptionConfigList()){
            ArrayList<String> options = ProcessConfig.getOption(optionConfig);
            if(!options.contains(configMap.get(optionConfig).toString())) {System.out.println(optionConfig); return false;}
        }
        //检查特殊规则项
        if(!ConfigCheckTools.checkPublicSpecialConfig(configMap)) {return false;}
        //检查param_list是否符合要求
        if(configMap.get("param_type").equals("sequence")){
            if(!ConfigCheckTools.checkParamSequence(configMap.get("param_sequence"))) {System.out.println("check_pl"); return false;}
        }
        else if(configMap.get("param_list") != null){
            //param_list可以为空
            if(!ConfigCheckTools.checkParamList(configMap.get("param_list"))) {System.out.println("check_pl_2"); return false;}
        }
        //检查各项类型是否符合要求
        if(!ConfigCheckTools.checkProcessConfigType(configMap)) {System.out.println("type"); return false;}
        return true;
    }

    /**读取config文件，检查该模型的配置和文件完整性
     * @param path 文件夹的绝对路径
     * @return 完备则返回结果configMap，否则为null
     */
    public static Map<String, Object> scanFolder(String path) throws IOException{
        File folder = new File(path);
        if(!folder.isDirectory()) return null;

        System.out.println("begin folder scan for:\t"+path);
        Map<String, Object> configMap = null;
        try {
            configMap = FileUtil.readMap(PathUtil.join(path, "config.ini"));
        }catch (IOException e){
            throw e;
        }

        if(configMap==null) {return null;}
        //检查config内容
        if(!checkConfig(configMap)) {return null;}
        //检查文件完整性
        boolean auto_check = (boolean)configMap.get("auto_check");
        Map<String, String> folder_config = null;
        if(auto_check){
            if(!ConfigCheckTools.checkFolderConfig(path, configMap.get("folder_config"))) {return null;}
        }
        System.out.println("config correct for\t"+path);
        return configMap;
    }

}

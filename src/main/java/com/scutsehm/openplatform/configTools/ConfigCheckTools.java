package com.scutsehm.openplatform.configTools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.scutsehm.openplatform.enums.*;
import com.scutsehm.openplatform.util.FileUtil;

import java.io.File;
import java.util.*;

/**
 * 用以处理各种配置项检查的工具类，包括一下内容：
 * param_list检查、folder文件完整性检查、特殊项检查、config_type子项类型检查
 * //TODO 增加对configMap子项是否存在以及删除多余项的功能
 */
public class ConfigCheckTools {

    /** 检查param_list是否合乎类型要求
     */
    public static boolean checkParamList(Map<String, String> param_list){
        for(Map.Entry entry: param_list.entrySet()){
            if(!ParamListConfig.isLegal(entry.getValue().toString())) {System.out.println("param_list "+ entry.getKey()); return false;}
        }
        return true;
    }

    /** 检查param_list是否合乎类型要求，
     * 直接传入param_list,函数内部会进行检查
     */
    public static boolean checkParamList(Object param_list){
        if(!param_list.getClass().toString().contains("JSONObject")) {System.out.println("type error "+param_list.getClass().toString());return false;}
        Map<String, String> paramList = (Map<String, String>) param_list;
        return checkParamList(paramList);
    }

    /** 检查param_sequence中的参数是否符合要求
     */
    public static boolean checkParamSequence(List<String> param_sequence){
        for(String type: param_sequence){
            if(!ParamListConfig.isLegal(type)) {System.out.println("param_sequence "+type);return false;}
        }
        return true;
    }

    /** 检查param_sequence中的参数是否符合要求
     * 直接传入param_sequence，函数内会进行类型检查
     */
    public static boolean checkParamSequence(Object param_sequence){
        if(!param_sequence.getClass().toString().contains("JSONArray")) {System.out.println("type error "+ param_sequence.getClass().toString());return false;}
        List<String> paramSequence = JSONObject.parseArray(JSON.toJSONString(param_sequence)).toJavaList(String.class);

        return checkParamSequence(paramSequence);
    }

    /** 根据folder_config检查文件夹中文件完整性
     * @param path 文件夹路径
     * @param folder_config 文件夹配置属性
     */
    public static boolean checkFolderConfig(String path, Map<String, String> folder_config){
        File dir = new File(path);
        //检查path对应文件夹是否存在
        if(!(dir.exists()&&dir.isDirectory())) {return false;}
        //检查文件夹是否为空
        String[] list = dir.list();
        if(list==null) {return false;}

        ArrayList<String> fileList = new ArrayList<String>(Arrays.asList(list));
        //确定folder_config中的文件都存在，不确保不存在多余文件
        for(Map.Entry entry: folder_config.entrySet()){
            if(!fileList.contains(entry.getKey().toString())){System.out.println(entry.getKey().toString());return false;}
        }
        return true;
    }

    /** 根据folder_config检查文件夹中文件完整性，
     * 直接传入object，函数内部会进行检查
     * @param path 文件夹路径
     * @param folder_config 文件夹配置属性
     */
    public static boolean checkFolderConfig(String path, Object folder_config){
        if(!folder_config.getClass().toString().contains("JSONObject")) {return false;}
        return checkFolderConfig(path, (Map<String, String>)folder_config);
    }

    /** 检查数个公共的特殊项
     */
    public static boolean checkPublicSpecialConfig(Map<String, Object> configMap){
        //当param_type为appoint时，param_list应当存在
        if(configMap.get("param_type").toString().equals("appoint")){
            if(!configMap.containsKey("param_list")) {
                System.out.println("pl");
                return false;
            }
        }
        //当param_type为sequence时，param_sequence应当存在
        if(configMap.get("param_type").toString().equals("sequence")){
            if(!configMap.containsKey("param_sequence")) {
                System.out.println("param_sequence不存在");
                return false;
            }
        }
        //当auto_check为true时，folder_config应当存在
        if(configMap.get("auto_check").toString().equals("true")){
            if(!configMap.containsKey("folder_config")) {
                System.out.println("ac");
                return false;
            }
        }
        return true;
    }

    /** 检查deploy_config子项的类型合法性
     */
    public static boolean checkDeployConfigType(Map<String, Object> deploy_config){
        Set<String> keys = deploy_config.keySet();
        for(String key: keys){
            String type = DeployConfig.getClassType(key);
            String jsonName = ConfigType.getJsonName(type);
            if(!deploy_config.get(key).getClass().toString().equals(jsonName)){
                System.out.println("checkDeployConfigType "+key);
                System.out.println(jsonName);
                System.out.println(deploy_config.get(key).getClass().toString());
                return false;
            }
        }
        return true;
    }

    /** 检查deploy_config子项的类型合法性，
     * 直接传入object，函数内部进行类型检查
     */
    public static boolean checkDeployConfigType(Object deploy_config){
        if(!deploy_config.getClass().toString().contains("JSONObject")) {System.out.println("type error "+deploy_config.getClass().toString()); return false;}
        return checkDeployConfigType((Map<String, Object>) deploy_config);
    }

    /** 检查trainConfig子项的类型合法性
     */
    public static boolean checkTrainConfigType(Map<String, Object> configMap){
        Set<String> keys = configMap.keySet();
        for(String key:keys){
            String type = TrainConfig.getClassType(key);
            String jsonName = ConfigType.getJsonName(type);
            if(!configMap.get(key).getClass().toString().equals(jsonName)){
                System.out.println("checkTrainConfigType "+key);
                System.out.println(jsonName);
                System.out.println(configMap.get(key).getClass().toString());
                return false;
            }
        }
        return true;
    }

    /** 检查processConfig子项的类型合法性
     */
    public static boolean checkProcessConfigType(Map<String, Object> configMap){
        Set<String> keys = configMap.keySet();
        for(String key:keys){
            String type = ProcessConfig.getClassType(key);
            String jsonName = ConfigType.getJsonName(type);
            if(!configMap.get(key).getClass().toString().equals(jsonName)){
                System.out.println("checkProcessConfigType "+key);
                System.out.println(jsonName);
                System.out.println(configMap.get(key).getClass().toString());
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args){
//        String json =
//                " {" +
//                " \"param_sequence\":	[ \"factorNums\", \"iterNums\", \"alpha\", \"k\", \"parse\" ], "+
//                " \"param_list\" : "+
//                "   {                                   "+
//                "       \"factorNums\" : \"Integer\", "+
//                "       \"iterNums\" : \"Integer\", "+
//                "       \"alpha\" : \"Float\", "+
//                "       \"k\" : \"Float\", "+
//                "       \"parse\" : \"String\" "+
//                "   }"+
//                "}"
//                ;
//        Map<String, Object> jsonMap = JSONUtil.DecodeMap(json);

        Map<String, Object> configMap = null;
        try{
            configMap = FileUtil.readMap("D:\\Repository\\OpenPlatform\\ProcessModel\\mnist-pytorch\\config.ini");
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(checkProcessConfigType(configMap));
    }
}

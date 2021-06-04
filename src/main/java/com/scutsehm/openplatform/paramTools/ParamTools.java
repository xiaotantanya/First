package com.scutsehm.openplatform.paramTools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.scutsehm.openplatform.config.GlobalConfig;
import com.scutsehm.openplatform.enums.ParamListConfig;
import com.scutsehm.openplatform.resourceManager.ProcessModelManager;
import com.scutsehm.openplatform.resourceManager.TrainModelManager;
import com.scutsehm.openplatform.util.FileUtil;
import com.scutsehm.openplatform.util.PathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component("paramTools")
public class ParamTools {
    @Autowired
    GlobalConfig globalConfig;
    @Autowired
    ProcessModelManager processModelManager;
    @Autowired
    TrainModelManager trainModelManager;
    @Autowired
    FileProbe fileProbe;

    public boolean getParamList(LinkedHashMap<String, Object> RecvParams, Param params, LinkedHashMap<String,Object> param_list){
        String type = null;

        if(params==null) params = new Param();
        for(Map.Entry<String, Object> entry: RecvParams.entrySet()){
            switch (entry.getKey()) {
                case "type":
                    type = entry.getValue().toString(); break;
                case "model":
                    params.model = entry.getValue().toString(); break;
                case "deploy_model_name":
                    params.deploy_model_name = entry.getValue().toString(); break;
                case "input":
                    params.input = entry.getValue().toString(); break;
                case "input_path":
                    params.input_path = entry.getValue().toString(); break;
                case "output":
                    params.output = entry.getValue().toString(); break;
                case "output_path":
                    params.output_path = entry.getValue().toString(); break;
                default:
                    param_list.put(entry.getKey(), entry.getValue());
                    break;
            }
        }

        System.out.println("begin param analysis");

        Map<String, Object> modeList=null;
        if(type.equals("train")){ modeList = trainModelManager.getModelList(); }
        else if(type.equals("process")){ modeList = processModelManager.getModelList(); }
        else {return false;}

        //检查modelList中是否存在目标模型
        if(modeList==null) { System.out.println("get model list error"); return false;}
        if(!modeList.containsKey(params.model)) {System.out.println("can not find this model"); return false;}

        //获取model_config
        if(!modeList.get(params.model).getClass().toString().contains("JSONObject")) {
            System.out.println("type error in model list ");
            return false;
        }
        Map<String, String> model_config = (Map<String, String>)modeList.get(params.model);

        //获取configMap
        Map<String, Object> configMap = null;
        try{
            String model_path = fileProbe.BufferedPathAssemble(type, model_config.get("path"));
            configMap = FileUtil.readMap(PathUtil.join(model_path, "config.ini"));

        }catch (IOException e){
            e.printStackTrace();
        }
        if(configMap==null) { System.out.println("get configMap error for " + type + " "+model_config.get("path")); return false;}

        //补全input参数
        //TODO 这里应该对用户是否有目标文件夹的读取权进行检查
        if(params.input==null || params.input_path==null){
            String input = configMap.get("input_type").toString();
            //目前未对input_path项作检查，考虑在之后重构enums时放入
            String input_path = configMap.get("input_path")==null? null : configMap.get("input_path").toString();

            if(input.equals("none")){
                params.input = null;
                params.input_path = null;
            }
            else{
                params.input = input;
                params.input_path = input_path;
            }
        }

        //补全output参数
        //TODO 这里应该检查用户是否有目标文件夹的写入权限
        if(params.output==null || params.output_path==null){
            String output = configMap.get("output_type").toString();
            //未对output_path项作检查，考虑在之后重构enums时放入
            String output_path = configMap.get("output_path")==null? null : configMap.get("output_path").toString();

            if(output.equals("none")){
                params.output = null;
                params.output_path = null;
            }
            else{
                params.output = output;
                params.output_path = output_path;
            }

        }

        //TODO checkProcessParam
        //TODO 若param_type为sequence，重新组装参数，确保参数顺序
        //TODO FileProbe检查时

        //若为train，将params中的缺失参数用configMap中的default参数补全
        if(type.equals("train") && (boolean)configMap.get("auto_deploy")){
            Map<String, Object> deploy_config = (Map<String, Object>) configMap.get("deploy_config");

            if(params.deploy_model_name==null) { params.deploy_model_name = deploy_config.get("deploy_model_name").toString(); }

            //auto_deploy为true时，若有output type但没path，则随机给予，若无output type，则设置为process
            if(params.output==null){
                params.output = "process";
            }
            else{
                //若output_path为空，则需要随机给予
                if(params.output_path == null){
                    //TODO 可能会造成文件锁冲突，应该在随机获取文件夹路径时进行创建
                    params.output_path = FileUtil.getRandomFile(globalConfig.getProcess_model_path(), params.deploy_model_name);
                    FileUtil.createFolder(params.output_path);
                }
                else{
                    //清空目标文件夹
                    FileUtil.overwriteFolder(fileProbe.BufferedPathAssemble(params.output, params.output_path));
                }
            }

        }

        return true;
    }

    public static boolean checkProcessParam(String model, String input, String input_path, String output, String out_path,
                                            LinkedHashMap<String, Object> paramList, Map<String, Object> modeList, Map<String, Object> configMap){
        //检查mode是否正确
        if(model==null) { System.out.println("model为空"); return false;}
        if(!modeList.containsKey(model)) { System.out.println("无此model"); return false;}

        //检查input和output文件
        FileProbe fileProbe = new FileProbe();
        if(!configMap.get("input_type").toString().equals("none")){
            if(input==null) { System.out.println("input为空"); return false;}
            if(!fileProbe.fileExist(input, input_path)) {System.out.println("input文件不存在"); return false;}
        }
        if(!configMap.get("output_type").toString().equals("none")){
            if(output==null) { System.out.println(); return false;}
            if(!fileProbe.fileExist(output, out_path)) {System.out.println("output文件不存在"); return false;}
        }

        //检查params
        if(configMap.get("param_type").toString().equals("sequence")){
            if(!checkParam(paramList, configMap.get("param_list"), configMap.get("param_sequence"))) {System.out.println("param list not fit"); return false;}
        }
        else{
            if(!checkParam(paramList, configMap.get("param_list"))){System.out.println("param list not fit"); return false;}
        }
        return true;
    }

    public static boolean checkParam(LinkedHashMap<String, Object> paramList, Map<String, String> param_list){
        Set<String> keys = param_list.keySet();
        for(String key: keys){
            //检查参数是否存在
            if(!paramList.containsKey(key)) {System.out.println("该参数不存在 "+key); return false;}
            //检查参数类型
            String type = param_list.get(key);
            String javaType = ParamListConfig.getJavaType(type);
            if(!paramList.get(key).getClass().toString().equals(javaType)) {System.out.println("参数类型不正确 "+key); return false;}
        }
        return true;
    }

    public static boolean checkParam(LinkedHashMap<String, Object> paramList, Object param_list){
        if(!param_list.getClass().toString().contains("JSONObject")) { return false; }
        return checkParam(paramList, (Map<String, String>)param_list);
    }

    public static boolean checkParam(LinkedHashMap<String, Object> paramList, Map<String, String> param_list, List<String> param_sequence){
        for(String param: param_sequence){
            //检查参数是否存在
            if(!paramList.containsKey(param)) {System.out.println("参数不存在 " +param); return false;}
            //检查参数类型
            String type = param_list.get(param);
            String javaType = ParamListConfig.getJavaType(type);
            if(!paramList.get(param).getClass().toString().equals(javaType)) {System.out.println("参数类型不正确 "+param); return false;}
        }
        return true;
    }

    public static boolean checkParam(LinkedHashMap<String, Object> paramList, Object param_list, Object param_sequence){
        if(!param_list.getClass().toString().contains("JSONObject")) { return false; }
        if(!param_sequence.getClass().toString().contains("JSONArray")) { return false; }
        List<String> paramSequence = JSONObject.parseArray(JSON.toJSONString(param_sequence)).toJavaList(String.class);
        return checkParam(paramList, (Map<String, String>)param_list,  paramSequence);
    }
}

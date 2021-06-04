package com.scutsehm.openplatform.service.util;

import com.scutsehm.openplatform.paramTools.FileProbe;
import com.scutsehm.openplatform.paramTools.Param;
import com.scutsehm.openplatform.util.PathUtil;

import java.util.Map;

/**
 * 为service提供命令行组装的功能
 */
public class CommendUtil {
    /**
     * 组装命令用于启动shell进程，包括组装参数和input、output
     */
    public static String commendAssemble(String model_path, Map<String, String> model_config, Map<String, Object> configMap,
                                  Param params, Map<String, Object> param_list, FileProbe fileProbe){
        //创建新的命令
        String commend = null;
        if(System.getProperty("os.name").contains("Windows")) { commend = "cmd.exe /c python "; }
        else if(System.getProperty("os.name").contains("Linux")) { commend = "stdbuf -o0 python "; }
        //现在commend为 stdbuf -o0 python

        commend+= PathUtil.join(model_path, model_config.get("path"), configMap.get("entry_path").toString());
        //现在commend为 stdbuf -o0 python /home/hm/cheungilin/TestModel/test.py

        String param_type = configMap.get("param_type").toString();

        //加入参数项
        if(param_type.equals("appoint")){
            for(Map.Entry entry: param_list.entrySet()){
                commend += " --"+entry.getKey()+" "+entry.getValue();
            }
        }
        else if(param_type.equals("sequence")){
            for(Map.Entry entry: param_list.entrySet()){
                commend += " "+entry.getValue();
            }
        }

        //加入input和output参数
        if(params.input!=null){
            String input_path = fileProbe.pathAssemble(params.input, params.input_path);
            if(param_type.equals("appoint")) { commend += " --"+input_path; }
            if(param_type.equals("sequence")) { commend += " "+input_path; }
        }
        if(params.output!=null){
            String output_path = fileProbe.pathAssemble(params.output, params.output_path);
            if(param_type.equals("appoint")) { commend += " --"+ output_path; }
            if(param_type.equals("sequence")) { commend += " "+output_path; }
        }
        return commend;
    }
}

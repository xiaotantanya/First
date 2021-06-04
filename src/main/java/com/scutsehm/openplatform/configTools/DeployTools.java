package com.scutsehm.openplatform.configTools;

import com.scutsehm.openplatform.config.GlobalConfig;
import com.scutsehm.openplatform.paramTools.FileProbe;
import com.scutsehm.openplatform.paramTools.Param;
import com.scutsehm.openplatform.resourceManager.ProcessModelManager;
import com.scutsehm.openplatform.util.FileUtil;
import com.scutsehm.openplatform.util.PathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//TODO 用以train完成后进行文件部署，包括文件拷贝、删除多余文件、生产config.ini
@Component("deployTools")
public class DeployTools {
    @Autowired
    GlobalConfig globalConfig;
    @Autowired
    FileProbe fileProbe;


    public boolean deploy(Param params, Map<String,Object> train_configMap){
        String model_path = null;
        if(params.output.equals("process")){
            model_path = PathUtil.join(globalConfig.getProcess_model_path(), params.output_path);
        }
        else{
            String path = FileUtil.getRandomFile(globalConfig.getProcess_model_path(), params.deploy_model_name);
            if(path==null) {
                return false;
                //TODO log
            }
            model_path = PathUtil.join(globalConfig.getProcess_model_path(), path);

            //这样copy有极限情况下文件锁不安全的问题
            if(!FileUtil.copy(fileProbe.pathAssemble(params.output, params.output_path), model_path))
            {
                return false;
                //TODO log
            }

        }

        Map<String, Object> deploy_config = (Map<String, Object>) train_configMap.get("deploy_config");

        Map<String, Object> configMap = new HashMap<>();

        configMap.put("type", "ProcessConfig");
        //自动部署暂时定为不进行文件完整性检查
        //TODO 写入file_config内容
        configMap.put("auto_check", false);

        //将deploy_config中的选项名称去掉 deploy_ 这个头部并复制内容到新模型的configMap中
        for(Map.Entry entry: deploy_config.entrySet()){
            configMap.put(entry.getKey().toString().replace("deploy_", ""), entry.getValue());
        }

        //若params中规定了model_name，则替换该项内容
        //TODO deploy_model_name一定不会为空，若传入时为空则会在paramTools中使用配置文件中的默认值填充，可以省去判断
        if( !(params.deploy_model_name.equals("") || params.deploy_model_name==null) ){
            configMap.put("model_name", params.deploy_model_name);
        }

        try{
            FileUtil.writeMap(PathUtil.join(model_path, "config.ini"), configMap);
        }catch (IOException e){
            return false;
            //TODO log
        }

        return true;
    }
}

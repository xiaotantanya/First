package com.scutsehm.openplatform.paramTools;


import com.scutsehm.openplatform.config.GlobalConfig;
import com.scutsehm.openplatform.util.FileUtil;
import com.scutsehm.openplatform.util.PathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 用于进行文件探查、路径组装的工具类
 * 注册为Component，便于调用GlobalConfig中的绝对根文件路径
 */
@Component("fileProbe")
public class FileProbe {
    @Autowired
    GlobalConfig globalConfig;

    public String pathAssemble(String type, String path){
        String fatherPath=null;
        switch (type){
            case "data": fatherPath=globalConfig.getData_path(); break;
            case "user": fatherPath= globalConfig.getUser_path(); break;
            default: return null;
        }
        return PathUtil.join(fatherPath, path);
    }

    public String BufferedPathAssemble(String type, String path){
        String fatherPath = null;
        switch (type){
            case "process": fatherPath = globalConfig.getProcess_model_path(); break;
            case "train": fatherPath = globalConfig.getTrain_model_path(); break;
            case "data": fatherPath = globalConfig.getData_path(); break;
            case "user": fatherPath = globalConfig.getUser_path(); break;
            default: return null;
        }
        return PathUtil.join(fatherPath, path);
    }

    public boolean fileExist(String type, String path){
        String filePath = BufferedPathAssemble(type, path);

        return FileUtil.exist(filePath);
    }

    //TODO 其他需要使用GlobalConfig的文件操作函数

}

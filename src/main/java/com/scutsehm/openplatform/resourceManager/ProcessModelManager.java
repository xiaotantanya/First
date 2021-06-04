package com.scutsehm.openplatform.resourceManager;

import com.scutsehm.openplatform.config.GlobalConfig;
import com.scutsehm.openplatform.configTools.ProcessModelTools;
import com.scutsehm.openplatform.util.FileUtil;
import com.scutsehm.openplatform.util.PathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于处理模型文件夹的数据对象类
 * 一般情况下只需调用getModeList函数获取当前处理模型的列表
 * mode_list: Map<String id, Map fileConfig> fileConfig键值为path和model_name
 */
@Component("processModelManager")
public class ProcessModelManager {

    @Autowired
    GlobalConfig globalConfig;

    //不要加注Autowired标注
    private Map<String, Object> modelList = null;

    /** 获取从内存中modeList
     */
    public Map<String, Object> getModelList(){
        if(modelList==null) {System.out.println("mode_list is null, reload it from local file");loadModelList();}
        return modelList;
    }

    /** 扫描整个processModel文件夹，根据config.ini检查每个子文件夹（模型）的完整性
     * @return 返回map，模型编号：模型内容（路径， 名称）
     */
    public Map<String, Object> scanProcessModelFolder(){
        String path = globalConfig.getProcess_model_path();

        Map<String, Object> result = new HashMap<>();

        File dir = new File(path);
        String[] fileList = null;

        if(dir.exists() && dir.isDirectory()) {
            fileList = dir.list();
            for (String file: fileList) {
                try{
                    Map<String, Object> file_config = new HashMap<>();
                    Map<String, Object> file_configMap = ProcessModelTools.scanFolder(PathUtil.join(path, file));
                    if(file_configMap == null){
                        // TODO 这里应该是处理错问文件夹的代码（如删除）
                    }
                    else{
                        file_config.put("path", file);
                        file_config.put("model_name", file_configMap.get("model_name").toString());
                        result.put(file, file_config);
                    }
                } catch (IOException e){
                    // TODO write log
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /** 重新扫描文件夹，并写入list文件和更新modeList
     */
    public void updateModelList(){
        try{
            FileUtil.writeMap(globalConfig.getProcess_model_list(), scanProcessModelFolder());
            modelList = FileUtil.readMap(globalConfig.getProcess_model_list());
        }catch (IOException e){
            //TODO write log
            e.printStackTrace();
        }
    }

    /** 重新从list文件中读取modeList，否则先扫描文件夹写入list文件然后更新modeList
     */
    public void loadModelList(){
        try{
            modelList = FileUtil.readMap(globalConfig.getProcess_model_list());
        }catch (IOException e){
            //TODO write log
            modelList = scanProcessModelFolder();
            try{
                FileUtil.writeMap(globalConfig.getProcess_model_list(), modelList);
            }catch (IOException ioException){
                //TODO write log
            }
        }
    }

    /** 将内存中的modeList写入list文件，注意由于不会做比较检查，因此这并不安全
     */
    public void writeModeList(){
        try{
            FileUtil.writeMap(globalConfig.getProcess_model_list(), modelList);
        }catch (IOException e){
            //TODO write log
        }
    }

}

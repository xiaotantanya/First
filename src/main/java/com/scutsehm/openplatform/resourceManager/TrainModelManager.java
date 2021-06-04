package com.scutsehm.openplatform.resourceManager;

import com.scutsehm.openplatform.config.GlobalConfig;
import com.scutsehm.openplatform.configTools.TrainModelTools;
import com.scutsehm.openplatform.util.FileUtil;
import com.scutsehm.openplatform.util.PathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component("trainModelManager")
public class TrainModelManager {
    @Autowired
    GlobalConfig globalConfig;

    private Map<String, Object> modelList = null;

    /** 获取从内存中modeList，如没有则重新载入modelList
     */
    public Map<String, Object> getModelList(){
        if(modelList==null) {loadModelList();}
        return modelList;
    }

    /** 扫描整个trainModel文件夹，根据config.ini检查每个子文件夹（模型）的完整性
     * @return 返回map，模型编号：模型内容（路径， 名称）
     */
    public Map<String, Object> scanTrainModelFolder(){
        String path = globalConfig.getTrain_model_path();

        Map<String, Object> result = new HashMap<>();

        File dir = new File(path);
        String[] fileList = null;

        if(dir.exists() && dir.isDirectory()) {
            fileList = dir.list();
            for (String file: fileList) {
                try{
                    Map<String, Object> file_config = new HashMap<>();
                    Map<String, Object> file_configMap = TrainModelTools.scanFolder(PathUtil.join(path, file));
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
    public synchronized void updateModelList(){
        try{
            FileUtil.writeMap(globalConfig.getTrain_model_list(), scanTrainModelFolder());
            modelList = FileUtil.readMap(globalConfig.getTrain_model_list());
        }catch (IOException e){
            //TODO write log
            e.printStackTrace();
        }
    }

    /** 重新从list文件中读取modeList，否则先扫描文件夹写入list文件然后更新modeList
     */
    public synchronized void loadModelList(){
        try{
            modelList = FileUtil.readMap(globalConfig.getTrain_model_list());
        }catch (IOException e){
            //TODO write log
            modelList = scanTrainModelFolder();
            try{
                FileUtil.writeMap(globalConfig.getTrain_model_list(), modelList);
            }catch (IOException ioException){
                //TODO write log
            }
        }
    }

    /** 将内存中的modeList写入list文件，注意由于不会做比较检查，因此这并不安全
     */
    public synchronized void writeModeList(){
        try{
            FileUtil.writeMap(globalConfig.getTrain_model_list(), modelList);
        }catch (IOException e){
            //TODO write log
        }
    }
}

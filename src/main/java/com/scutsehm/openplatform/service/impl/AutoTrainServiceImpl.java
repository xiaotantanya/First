package com.scutsehm.openplatform.service.impl;

import com.scutsehm.openplatform.VO.Result;
import com.scutsehm.openplatform.config.GlobalConfig;
import com.scutsehm.openplatform.configTools.TrainModelTools;
import com.scutsehm.openplatform.paramTools.FileProbe;
import com.scutsehm.openplatform.paramTools.Param;
import com.scutsehm.openplatform.resourceManager.ProcessModelManager;
import com.scutsehm.openplatform.resourceManager.TrainModelManager;
import com.scutsehm.openplatform.service.AutoTrainService;
import com.scutsehm.openplatform.service.util.CommendUtil;
import com.scutsehm.openplatform.util.PathUtil;
import com.scutsehm.openplatform.util.ProcessManager;
import com.scutsehm.openplatform.util.ShellThread;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 用于自动调用模型进行训练的service
 * 目前commend处理和线程创建/调用全部集中在这里
 */
@Service
public class AutoTrainServiceImpl implements AutoTrainService {
    @Autowired
    ProcessManager processManager;

    @Autowired
    GlobalConfig globalConfig;

    @Autowired
    TrainModelManager trainModelManager;
    @Autowired
    ProcessModelManager processModelManager;

    @Autowired
    FileProbe fileProbe;

    @SneakyThrows
    @Override
    // 启动线程后函数直接返回，不会等待线程结束运行，线程信息会保存在processManager中
    public Result Execute(Param params, LinkedHashMap<String, Object> param_list) {
        Map<String, Object> modeList = trainModelManager.getModelList();
        Map<String, String> model_config = null;

        //查找目标模型
        if(modeList.containsKey(params.model)) { model_config = (Map<String, String>)modeList.get(params.model); }
        else { return Result.BAD().msg("Can not find this model").build();}

        //检查和获取目标模型的配置信息（对比config.ini）
        Map<String, Object> configMap = TrainModelTools.scanFolder(
                PathUtil.join(globalConfig.getTrain_model_path(), model_config.get("path"))
        );
        if(configMap==null) { return Result.BAD().msg("model files damaged").build(); }

        //在processManager中申请新位置
        int index = processManager.allocate();

        //创建新的命令
        String commend = CommendUtil.commendAssemble(globalConfig.getTrain_model_path(), model_config, configMap, params, param_list, fileProbe);

        ShellThread t = new ShellThread(commend, processManager, index, params, configMap);
        t.start();

        System.out.println(params.model + " started.\tnow process nums: " + processManager.size);


        return Result.OK().data(index).build();

    }

    @Override
    public Result Stop(int id) {
        if(!processManager.processMap.containsKey(id)){
            return Result.BAD().msg("can not find id: " + id).build();
        }
        Process process = processManager.processMap.get(id);

        if(process.isAlive()){
            process.destroy();
            //强制关闭进程，暂时用不上
            //process.destroyForcibly();
            System.out.println("T_process "+id+" is still alive");
        }
        processManager.remove(id);
        return Result.OK().msg("close T_process "+ id + " success.").build();
    }

}

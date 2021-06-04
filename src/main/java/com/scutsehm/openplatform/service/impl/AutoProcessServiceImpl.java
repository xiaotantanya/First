package com.scutsehm.openplatform.service.impl;

import com.scutsehm.openplatform.VO.Result;
import com.scutsehm.openplatform.config.GlobalConfig;
import com.scutsehm.openplatform.configTools.ProcessModelTools;
import com.scutsehm.openplatform.paramTools.FileProbe;
import com.scutsehm.openplatform.paramTools.Param;
import com.scutsehm.openplatform.resourceManager.ProcessModelManager;
import com.scutsehm.openplatform.service.AutoProcessService;
import com.scutsehm.openplatform.service.util.CommendUtil;
import com.scutsehm.openplatform.util.PathUtil;
import com.scutsehm.openplatform.util.ProcessManager;
import com.scutsehm.openplatform.util.ShellThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 用于自动调用已训练模型进行数据处理的service
 * 目前commend处理和线程创建/调用全部集中在这里
 */
@Service
public class AutoProcessServiceImpl implements AutoProcessService {
    @Autowired
    private GlobalConfig globalConfig;

    @Autowired
    private ProcessModelManager processModelManager;

    @Autowired
    private ProcessManager processManager;

    @Autowired FileProbe fileProbe;

    @Override
    public Result Execute(Param params, LinkedHashMap<String, Object> param_list) throws Exception {
        Map<String, Object> modeList = processModelManager.getModelList();
        //processModelList中的子项，代表一个模型，包括path和model_name两部分内容
        Map<String, String> model_config = null;

        //查找目标模型
        if(modeList.containsKey(params.model)){ model_config = (Map<String, String>)modeList.get(params.model); }
        else { return Result.BAD().msg("Can not find this model").build(); }

        //检查和获取目标模型的配置信息（对比config.ini）
        Map<String, Object> configMap = ProcessModelTools.scanFolder(
                PathUtil.join(globalConfig.getProcess_model_path(), model_config.get("path"))
                );
        if(configMap==null) { return Result.BAD().msg("Model files damaged").build(); }

        //在processManager中申请新的位置
        int index = processManager.allocate();

        String commend = CommendUtil.commendAssemble(globalConfig.getProcess_model_path(), model_config, configMap, params, param_list, fileProbe);

        ShellThread t = new ShellThread(commend, processManager, index);
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
            System.out.println("P_process "+id+" is still alive");
        }
        processManager.remove(id);
        return Result.OK().msg("close P_process "+ id + " success.").build();
    }

}

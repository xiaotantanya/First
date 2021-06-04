package com.scutsehm.openplatform.controller;

import com.scutsehm.openplatform.VO.Result;
import com.scutsehm.openplatform.config.GlobalConfig;
import com.scutsehm.openplatform.paramTools.FileProbe;
import com.scutsehm.openplatform.paramTools.ParamTools;
import com.scutsehm.openplatform.resourceManager.ProcessModelManager;
import com.scutsehm.openplatform.resourceManager.TrainModelManager;
import com.scutsehm.openplatform.util.FileUtil;
import com.scutsehm.openplatform.util.PathUtil;
import com.scutsehm.openplatform.util.ProcessManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;


@RestController
public class TestController {
    @Autowired
    ProcessManager processManager;

    @Autowired
    GlobalConfig globalconfig;

    @Autowired
    ProcessModelManager processModelManager;
    @Autowired
    TrainModelManager trainModelManager;
    @Autowired
    FileProbe fileProbe;

    @RequestMapping("/test")
    @ResponseBody
    public Result test(){
        return Result.OK().build();
    }

    @RequestMapping("/getProcessModel")
    @ResponseBody
    public Result getProcessModel(){
        processModelManager.updateModelList();
        Map<String, Object> modelList = processModelManager.getModelList();
        return Result.OK().data(modelList).build();
    }

    @RequestMapping("/getTrainModel")
    @ResponseBody
    public Result getTrainModel(){
        trainModelManager.updateModelList();
        Map<String, Object> modeList = trainModelManager.getModelList();
        return Result.OK().data(modeList).build();
    }

    @RequestMapping("/testParam")
    @ResponseBody
    public Result testParam(@RequestBody LinkedHashMap<String, Object> params){
        Result result = null;

        for(Map.Entry entry: params.entrySet()){
            System.out.println(entry.getKey().toString() + "\t" + entry.getValue().toString() + "\t" + entry.getValue().getClass().toString());
        }

        LinkedHashMap<String, Object> paramList = new LinkedHashMap<>();
        String model=null;
        String input=null;
        String input_path=null;
        String output=null;
        String output_path=null;

        for(Map.Entry<String, Object> entry: params.entrySet()){
            switch (entry.getKey()) {
                case "type": break;
                case "model":
                    model = entry.getValue().toString(); break;
                case "input":
                    input = entry.getValue().toString(); break;
                case "input_path":
                    input_path = entry.getValue().toString(); break;
                case "output":
                    output = entry.getValue().toString(); break;
                case "output_path":
                    output_path = entry.getValue().toString(); break;
                default:
                    paramList.put(entry.getKey(), entry.getValue());
                    break;
            }
        }

        System.out.println(input.getClass());


//        String testPath = PathUtil.join(globalconfig.getProcess_model_path(), "test", "config.ini");
//        try{
//            ParamTools.checkProcessParam(model, input, input_path, output, output_path, paramList, processModelManager.getModelList(), FileUtil.readMap(testPath));
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        return Result.OK().build();
    }

    @GetMapping("/show")
    public Result show(){
        return Result.OK().data(processManager.getTotalMsg()).build();
    }
}

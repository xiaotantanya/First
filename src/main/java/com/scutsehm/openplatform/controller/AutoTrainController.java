package com.scutsehm.openplatform.controller;


import com.scutsehm.openplatform.VO.Result;
import com.scutsehm.openplatform.paramTools.Param;
import com.scutsehm.openplatform.paramTools.ParamTools;
import com.scutsehm.openplatform.service.impl.AutoTrainServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class AutoTrainController {
    @Autowired
    AutoTrainServiceImpl autoTrainService;
    @Autowired
    ParamTools paramTools;

    @GetMapping("/")
    @ResponseBody
    public String test(){
        return "hello";
    }

    @RequestMapping("/AutoTrain/excute")
    @ResponseBody
    public Result excute(@RequestBody LinkedHashMap<String, Object> recvParams){
        Result result=null;
        try{
            LinkedHashMap<String, Object> param_list = new LinkedHashMap<>();
            Param params = new Param();

            //检查接受参数并进行重新打包
            if(!paramTools.getParamList(recvParams, params, param_list)){
                return Result.BAD().msg("params illegal").build();
                //TODO 如果参数错误的代码
            }
            result = autoTrainService.Execute(params, param_list);

        }catch(Exception e){
            //TODO record log here
            System.out.println("Auto Train Service Failed");
            return Result.BAD().msg(e.toString()).build();
        }
        return result;
    }


    @RequestMapping("/AutoTrain/stop")
    @ResponseBody
    public Result stop(@RequestBody Map<String, Integer> params){
        if(!params.containsKey("index"))
            return Result.BAD().msg("unable to find param \"index\"").build();
        int index = params.get("index");
        Result  result = null;
        try{
            result = autoTrainService.Stop(index);
        }catch(Exception e){
            //TODO record log here
            return Result.BAD().msg(e.toString()).build();
        }
        return result;
    }

}

package com.scutsehm.openplatform.controller;

import com.scutsehm.openplatform.VO.Result;
import com.scutsehm.openplatform.paramTools.Param;
import com.scutsehm.openplatform.paramTools.ParamTools;
import com.scutsehm.openplatform.service.AutoProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 用于处理自动模型调用的Controller
 * 收集参数时将RequestBody封装为LinkedHashMap，以防止参数顺序混乱
 * 会将请求包携带参数解包为model、input、output、param_list参数
 */
@RestController
public class AutoProcessController {
    @Autowired
    AutoProcessService autoProcessService;
    @Autowired
    ParamTools paramTools;

    @PostMapping("/AutoProcess/excute")
    @ResponseBody
    public Result excute (@RequestBody LinkedHashMap<String, Object> recvParams){
        Result result=null;
        try{
            //使用LinkedHashMap，防止参数顺序错乱
            LinkedHashMap<String, Object> param_list = new LinkedHashMap<>();
            Param params = new Param();

            //检查接受参数，并进行重新打包
            if(!paramTools.getParamList(recvParams, params, param_list)){
                return Result.BAD().msg("params illegal").build();
                //TODO 如果参数错误的代码
            }

            result = autoProcessService.Execute(params, param_list);

        }catch(Exception e){
            //TODO record log here
            System.out.println("Auto Process Service Failed");
            return Result.BAD().msg(e.toString()).build();
        }
        return result;
    }

    @RequestMapping("/AutoProcess/stop")
    @ResponseBody
    public Result stop(@RequestBody Map<String, Integer> params){
        if(!params.containsKey("index"))
            return Result.BAD().msg("unable to find param \"index\"").build();
        int index = params.get("index");
        Result  result = null;
        try{
            result = autoProcessService.Stop(index);
        }catch(Exception e){
            //TODO record log here
            return Result.BAD().msg(e.toString()).build();
        }
        return result;
    }
}

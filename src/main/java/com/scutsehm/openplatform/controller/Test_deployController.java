package com.scutsehm.openplatform.controller;

import com.scutsehm.openplatform.VO.Result;
import com.scutsehm.openplatform.configTools.DeployTools;
import com.scutsehm.openplatform.paramTools.Param;
import com.scutsehm.openplatform.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class Test_deployController {
    @Autowired
    DeployTools deployTools;

    @RequestMapping("/test_deploy")
    @ResponseBody
    public Result test_deploy(){

        Param params = new Param("test", "deploy_test", null, null, "data", "test_auto_deploy");
        Map<String, Object> configMap = null;
        try{
            configMap = FileUtil.readMap("D:\\Repository\\OpenPlatform\\TrainModel\\test_auto_deploy\\config.ini");
        }catch (IOException e){
            System.out.println("configMap read failed");
            e.printStackTrace();
        }

        deployTools.deploy(params, configMap);
        return Result.OK().build();
    }

}

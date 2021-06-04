package com.scutsehm.openplatform.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 用以保存全局配置的变量类
 * 将变量按照windows和linux平台分别保存至两个map中
 * get方法已经封装，调用时会根据平台返回所需变量
 */
@Data
@Component("globalConfig")
@ConfigurationProperties(prefix = "global")
public class GlobalConfig {
//    private String back_end_path;
//    private String train_model_path;
//    private String process_model_path;
//    private String train_model_list;
//    private String process_model_list;
    private Map<String, String> windows;
    private Map<String, String> linux;

    String OSName = System.getProperty("os.name");

    public String getBack_end_path(){
        String re = null;
        if(OSName.contains("Windows")) { re= windows.get("back_end_path"); }
        else if(OSName.contains("Linux")) { re = linux.get("back_end_path"); }
        return re;
    }
    public String getTrain_model_path(){
        String re = null;
        if(OSName.contains("Windows")) { re= windows.get("train_model_path"); }
        else if(OSName.contains("Linux")) { re = linux.get("train_model_path"); }
        return re;
    }
    public String getProcess_model_path(){
        String re = null;
        if(OSName.contains("Windows")) { re= windows.get("process_model_path"); }
        else if(OSName.contains("Linux")) { re = linux.get("process_model_path"); }
        return re;
    }
    public String getTrain_model_list(){
        String re = null;
        if(OSName.contains("Windows")) { re= windows.get("train_model_list"); }
        else if(OSName.contains("Linux")) { re = linux.get("train_model_list"); }
        return re;
    }
    public String getProcess_model_list(){
        String re = null;
        if(OSName.contains("Windows")) { re= windows.get("process_model_list"); }
        else if(OSName.contains("Linux")) { re = linux.get("process_model_list"); }
        return re;
    }
    public String getData_path(){
        String re = null;
        if(OSName.contains("Windows")) { re = windows.get("data_path"); }
        else if(OSName.contains("Linux")) { re = linux.get("data_path"); }
        return re;
    }
    public String getUser_path(){
        String re = null;
        if(OSName.contains("Windows")) { re = windows.get("user_path"); }
        else if(OSName.contains("Linux")) { re = linux.get("user_path"); }
        return re;
    }
}

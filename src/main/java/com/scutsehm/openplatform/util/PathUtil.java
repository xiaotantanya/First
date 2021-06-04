package com.scutsehm.openplatform.util;

/**
 * 用于处理Path的工具类
 * 根据系统将path进行拼接
 * 区分linux风格的"/"和windows风格的"\\"
 */
public class PathUtil {
    public static String join(String pre, String next){
        String result = null;
        if(System.getProperty("os.name").contains("Windows")){
            result = pre + "\\" + next;
        }
        else if(System.getProperty("os.name").contains("Linux")){
            result = pre + "/" + next;
        }
        return result;
    }
    public static String join(String pre, String mid, String next){
        String result = null;
        if(System.getProperty("os.name").contains("Windows")){
            result = pre + "\\" + mid + "\\" + next;
        }
        else if(System.getProperty("os.name").contains("Linux")){
            result = pre + "/" + mid + "/" + next;
        }
        return result;
    }
}

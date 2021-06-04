package com.scutsehm.openplatform.util;

/**
 * 用于生成指定空间中的随机数或者随机字符串
 */
public class RandomUtil {
    /** 获取一个随机数
     */
    public static int getRandNum(int min, int max){
        return (int)(Math.random()*(max-min)+min);
    }
}

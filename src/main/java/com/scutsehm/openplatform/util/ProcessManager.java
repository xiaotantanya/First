package com.scutsehm.openplatform.util;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用以管理创建的进程的数据类
 * //TODO 增加新的变量成员，以保存更多信息，比如进程启动时间
 * //TODO 在其他地方修改调用，以保存需要的输出信息
 */
@Component("processManager")
public class ProcessManager {
    public ConcurrentHashMap<Integer, String> stateMap = new ConcurrentHashMap<>();
    public ConcurrentHashMap<Integer, Process> processMap = new ConcurrentHashMap<>();
    public ConcurrentHashMap<Integer, String> msgMap = new ConcurrentHashMap<>();
    public int size = 0;

    /** 申请新的process保存空间
     * @return 新创建空间的index
     */
    public synchronized int allocate(){
        if(size>1024)
            return -1;
        int index = getRandNum();
        stateMap.put(index, "allocated");
        size++;
        return index;
    }

    /** 按照index填入必要参数，在调用此函数后方才完成一个新process的保存
     */
    public boolean offer(int index, Process process, String result){
        if(!stateMap.containsKey(index))
            return false;
        stateMap.put(index, "created");
        processMap.put(index, process);
        msgMap.put(index, result);
        return true;
    }

    /** 获取目前所有进程的信息
     * @return index: msg isAlive
     */
    public Map<Integer, String> getTotalMsg(){
        Map<Integer, String> data = new HashMap<>();
        for(int n: processMap.keySet()){
            data.put(n, msgMap.get(n) +"\t" + processMap.get(n).isAlive());
        }
        return data;
    }

    /** 设置msg
     */
    public synchronized boolean setMsg(int n, String result){
        if(!msgMap.containsKey(n)){
            return false;
        }
        msgMap.put(n, result);
        return true;
    }

    /** 删除某一进程的记录信息
     */
    public synchronized boolean remove(int n){
        if(!stateMap.containsKey(n))
            return false;
        stateMap.remove(n);
        processMap.remove(n);
        msgMap.remove(n);
        size--;
        return true;
    }

    /** 获取一个随机数
     */
    private int getRandNum(){
        int max= Integer.MAX_VALUE ,min=0;
        int num;
        do{
            num = (int) (Math.random()*(max-min)+min);
        }while(msgMap.containsKey(num));
        return num;
    }

}


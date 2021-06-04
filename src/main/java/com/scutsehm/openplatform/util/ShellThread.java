package com.scutsehm.openplatform.util;

import com.scutsehm.openplatform.configTools.DeployTools;
import com.scutsehm.openplatform.paramTools.Param;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 自定义的用于启动shell/cmd进程的runnable线程
 * //TODO 进程正常退出后的自动配置操作
 * //TODO 进程正常退出后的进程摧毁和资源释放操作
 * //TODO 如果可以的话，改用ZTProcess进行封装，或者使用java.future库控制线程最大运行时间，防止僵尸线程
 */
public class ShellThread extends Thread{
    String commend;
    int index;
    volatile ProcessManager processManager;
    //以下为autoTrain之后进行自动部署需要的属性，若启动shell的为autoProcess，则均为null
    Param params;
//    String output_path;
    Map<String, Object> configMap;

    public ShellThread(String commend, ProcessManager processManager, int index){
        this.commend = commend;
        this.processManager = processManager;
        this.index = index;
        this.params = null;
//        this.output_path = null;
        this.configMap = null;
    }
    public ShellThread(String commend, ProcessManager processManager, int index, Param params, Map<String, Object> configMap){
        this.commend = commend;
        this.processManager = processManager;
        this.index = index;
        this.params = params;
//        this.output_path = output_path;
        this.configMap = configMap;
    }

    @SneakyThrows
    public void run(){
        System.out.println("commend begin for: " + commend);
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(commend);


        processManager.offer(index, process, "begin running");

        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line = null;
        while ((line = br.readLine()) != null){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            System.out.println(line + "\t" + df.format(new Date()));
            processManager.setMsg(index, line);
        }
        process.waitFor();
        br.close();
        process.destroy();
        System.out.println(index +" quit!");

        //TODO 在这里进行自动配置操作
        if(process.exitValue()==0){
            if(configMap==null) {
                System.out.println("Train shell process "+index+"\tconfigMap error");
                //TODO log
            }
            else if((boolean)configMap.get("auto_deploy")){
                DeployTools deployTools = new DeployTools();
                if(!deployTools.deploy(params, configMap)){

                }
            }
        }

//        if(output_path!=null && process.exitValue() == 0)
//        {
//            if(configMap==null) {System.out.println("Train shell process "+index+"\tconfigMap error");}
//            if((boolean)configMap.get("auto_deploy")){
//                if(!DeployTools.deploy(params, output_path, configMap)) {
//                    System.out.println("auto deploy failed");
//                    //TODO 记录自动部署失败情况
//                }
////                //删除output文件
////                if(!FileUtil.delete(output_path)){
////                    System.out.println("delete output temporary files failed");
////                    //TODO 记录删除文件失败情况
////                }
//            }
//        }
        //TODO 测试期间暂时屏蔽自动删除processMap
//        synchronized (this){processManager.remove(n);}
    }

}

package com.scutsehm.openplatform.util;

import java.io.*;
import java.nio.file.Files;
import java.util.Map;

/**
 * 处理JSON文件读写和基本文件操作的工具类
 * 子属性为列表时，仍为fastjson.array类型，使用时需要进行转换
 * 子属性为map时，使用时需要进行内存重解释（超类引用）
 */
public class FileUtil {
    /** 在dirPath文件夹下获取一个与现有文件不冲突的文件路径（模型名_随机数），
     * 默认随机数范围为0-10239，最多随机抽取20480次，当前参数下有放回的抽取能覆盖到到的随机数样本数量的期望为8854.3
     * @param dirPath 文件夹路径
     * @param model_name 模型名称
     * @return 返回null，则未在限制次数里找到不碰撞的结果
     */
    public static String getRandomFile(String dirPath, String model_name){

        String result = null;
        int times_limit = 20480, i=0;
        do{
            result = model_name + "@" + RandomUtil.getRandNum(0, 10240);
            if(!exist(PathUtil.join(dirPath, result))){
                break;
            }
        }while(i++<times_limit);

        return i>=times_limit? null:result;
    }

    /** 从模型文件夹的路径中提取随机id，由@分隔模型名和随机值。
     * 举例：LFM@114514，返回114514
     */
    public static String getFilePathID(String path){
        int index = path.lastIndexOf('@');
        if(index==-1) return null;
        return path.substring(index+1);
    }

    /** 创建文件夹，若已经存在则返回false
     * @param path
     * @return 若已经存在或者创建失败则返回false
     */
    public static boolean createFolder(String path){
        if(exist(path)) {return false;}
        File file = new File(path);
        return file.mkdir();
    }

    /** 创建文件夹，若文件夹已存在则清空
     * @param path 文件夹绝对路径
     * @return 若文件夹未成功情况或创建，则返回false
     */
    public static boolean overwriteFolder(String path){
        if(exist(path)){
            if(!delete(path)) return false;
        }
        File file = new File(path);
        return file.mkdir();
    }

    /** 根据绝对路径检查文件是否存在
     * @param path 文件绝对路径
     */
    public static boolean exist(String path){
        if(path==null) {return false;}

        File file = new File(path);
        return file.exists();
    }

    /** 将source文件拷贝到dest位置，
     * 若source为file，则为普通拷贝，
     * 若source为文件夹，则会递归拷贝所有内容
     * @param source 源文件位置
     * @param dest 目标文件位置
     * @return true：拷贝成功， false：拷贝失败，源文件不存在或者目标文件已经存在
     * @throws IOException
     */
    public static boolean copy(String source, String dest){
        File sou = new File(source);
        File tar = new File(dest);

        if(!sou.exists()) return false;
        if(tar.exists()) return false;
        try{
            Files.copy(sou.toPath(), tar.toPath());
        }catch (IOException e){
            return false;
            //TODO write log
        }

        String[] files = sou.list();
        if(files==null) return true;
        for(String file: files){
            if(!copy(PathUtil.join(source, file), PathUtil.join(dest, file))) return false;
        }

        return true;
    }

    /** 删除文件或文件夹（包括子文件）
     * @param path 绝对路径
     * @return true：文件不存在或成功删除
     */
    public static boolean delete(String path){
        File file = new File(path);
        if(file.exists()){
            if(file.isFile())
                return deleteFile(file);
            else
                return deleteDir(file);
        }
        else{
            return true;
        }
    }

    private static boolean deleteFile(File file){
        if(file.exists() && file.isFile()){
            return file.delete();
        }else{
            return true;
        }
    }

    private static boolean deleteDir(File dir){
        if(!dir.exists() || !dir.isDirectory()){return true;}
        File[] files = dir.listFiles();
        for(File file: files){
            if(file.isFile()){
                if(!deleteFile(file)) return false;
            }
            else{
                if(!deleteDir(file)) return false;
            }
        }
        return dir.delete();
    }


    /** 将文本文件读取为String，并进行utf-8解码
     * @param path 绝对路径
     */
    public static String readStr(String path) throws IOException{
        File file = new File(path);
        String encoding = "UTF-8";
        Long fileLength = file.length();
        byte[] fileContent = new byte[fileLength.intValue()];

        try {
            FileInputStream in = new FileInputStream(file);
            in.read(fileContent);
            in.close();
        }catch(IOException e){
            //TODO 写入log
            throw e;
        }
        try {
            return new String(fileContent, encoding);

        } catch (UnsupportedEncodingException e) {
            //TODO 写入log
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
            return null;
        }
    }

    /** 将path对应文件读取为Map<String, String>
     * @param path 绝对路径
     */
    public static Map<String, String> readStrMap(String path) throws IOException{
        return JSONUtil.DecodeStrMap(readStr(path));
    }

    /** 将path对应文件读取为Map<String, Object>，
     * 注意当前Object仍来自fastjson定义，使用时需要进行转换
     * @param path 绝对路径
     */
    public static Map<String, Object> readMap(String path) throws IOException{
        return JSONUtil.DecodeMap(readStr(path));
    }

    /** 将StrJSON进行格式化后写入文件中
     * @param path 绝对路径
     */
    public static void writeStr(String path, String json) throws IOException{
        try{
            BufferedWriter out = new BufferedWriter(new FileWriter(path));
            out.write(json);
            out.close();
        }catch (IOException e){
            //TODO write log
            throw e;
        }
    }

    /** 将StrMap格式化后写入文件中
     * @param path 绝对路径
     * @param json StrMap的json
     */
    public static void writeStrMap(String path, Map<String, String> json) throws IOException{
        writeStr(path, JSONUtil.EncodeStrMap(json));
    }

    /** 将Map格式化后写入文件中
     * @param path 绝对路径
     * @param json StrMap的json
     */
    public static void writeMap(String path, Map<String, Object> json) throws IOException{
        writeStr(path, JSONUtil.EncodeMap(json));
    }

}

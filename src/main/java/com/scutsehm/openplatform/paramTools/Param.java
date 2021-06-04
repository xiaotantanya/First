package com.scutsehm.openplatform.paramTools;

/**
 * 接受的前端参数中的非commend参数列表
 * 通常保存的变量命名为params
 */
public class Param {
    public String model; //使用的模型名称
    public String deploy_model_name; //模式为train时，需要部署为的模型名称
    public String input; //模型输入文件的根路径（位于哪一个文件空间）
    public String input_path; //模型输入文件的相对路径
    public String output; //模型输出文件的根路径（位于哪一个文件空间）
    public String output_path; //模型输出文件的相对路径
    public Param(){
        this.model = null;
        this.deploy_model_name = null;
        this.input = null;
        this.input_path = null;
        this.output = null;
        this.output_path = null;
    }
    public Param(String model, String deploy_model_name, String input, String input_path, String output, String output_path){
        this.model = model;
        this.deploy_model_name = deploy_model_name;
        this.input = input;
        this.input_path = input_path;
        this.output = output;
        this.output_path = output_path;
    }
}

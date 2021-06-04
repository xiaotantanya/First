package com.scutsehm.openplatform.resourceManager;

public class ModelManagerTools {


//    public static Map<String, Object> scanModelFolder(String path, String type){
//        Map<String, Object> result = new HashMap<>();
//
//        File dir = new File(path);
//        String[] fileList = null;
//
//        if(dir.exists() && dir.isDirectory()) {
//            fileList = dir.list();
//            for (String file: fileList) {
//                try{
//                    Map<String, Object> file_configMap = null;
//                    if(type.equals("process")) { file_configMap = ProcessModelTools.scanFolder(PathUtil.join(path, file)); }
//                    else if(type.equals("train")) { file_configMap = TrainModelTools.scanFolder(PathUtil.join(path, file)); }
//
//                    Map<String, Object> file_config = new HashMap<>();
//                    if(file_configMap == null){
//                        // TODO 这里应该是处理错问文件夹的代码（如删除）
//                    }
//                    else{
//                        file_config.put("path", file);
//                        file_config.put("model_name", file_configMap.get("model_name").toString());
//                        result.put(file, file_config);
//                    }
//                } catch (IOException e){
//                    // TODO write log
//                    e.printStackTrace();
//                }
//            }
//        }
//        return result;
//    }
}

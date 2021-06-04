package com.scutsehm.openplatform.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.*;

/**
 * 用于处理JSON的工具类，主要转换的类型有：
 * StrMap：{@code Map<String, String>}
 * Map:{@code Map<String. Object>}
 * Str:{@code String}
 * 目前不会将JSONObject中的JSONArray转换为Array，而是转为String。建议尽量避免使用复杂json结构
 */
public class JSONUtil {
    /** 将str转换为JSONObject
     */
    private static JSONObject DecodeJSON(String js){
        return JSONObject.parseObject(js);
    }

    /** 将String类型的json转换为Map<String, Object>
     *  TODO: 目前不会将JSONObject中的JSONArray转换为Array，而是转为String。建议尽量避免使用复杂json结构
     *  TODO: 编写递归解析Object的代码
     * @param js 字符串格式的json
     * @return String:Object的Map
     */
    public static Map<String, Object> DecodeMap(String js){
//        JSONObject jsonObject = DecodeJSON(js);
//        Map<String,Object> map = (Map<String,Object>)jsonObject;
        Map<String, Object> map = JSONObject.parseObject(js, Map.class);
        return map;
    }

    /** 将String类型的json转换为Map<String, String>
     */
    public static Map<String, String> DecodeStrMap(String js){
        JSONObject json = JSONObject.parseObject(js);

        Map<String, String>  map = new HashMap<>();
        for(JSONObject.Entry entry: json.entrySet()){
            map.put(entry.getKey().toString(), entry.getValue().toString());
        }
        return map;
    }

    /** 将Map类型的json转换为JSONObject
     */
    private static JSONObject Encode(Map jsonMap){
        return new JSONObject(jsonMap);
    }

    /** 将Map<String, Object>转换为String
     */
    public static String EncodeMap(Map<String, Object> jsonMap){
        return JSON.toJSONString(jsonMap, SerializerFeature.PrettyFormat);
    }

    /** 将Map<String, String>转为String格式json
     */
    public static String EncodeStrMap(Map<String, String> jsonMap){
        return JSON.toJSONString(jsonMap, SerializerFeature.PrettyFormat);
    }

    //testMainClass
    public static void main(String args[]){
        Map<String, Object> m = new HashMap<>();

        ArrayList<Integer> array = new ArrayList();
        array.add(1);
        array.add(2);
        array.add(3);

        Map<String, Object> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", 2);

        m.put("array", array);
        m.put("int", 1);
        m.put("string", "str");
        m.put("float", 0.5);
        m.put("long", 0L);
        m.put("dit", map);
        m.put("auto_check", true);

        List<Map> list = new LinkedList<>();
        list.add(map);
        list.add(map);
        m.put("list",list);

        String json = EncodeMap(m);
        System.out.println(json);
//        Map<String, Object> s = DecodeMap(json);
//        for(Map.Entry entry: s.entrySet()){
//            System.out.println(entry.getKey()+"\t"+entry.getValue().getClass());
//        }

//        Map<String, Object> outMap = (Map<String, Object>)s.get("dit");
//        for(Map.Entry entry: s.entrySet()){
//            System.out.println(entry.getKey() + "\t" + entry.getValue());
//        }
//        List<Integer> outArray = JSONObject.parseArray(JSON.toJSONString(s.get("array")), Integer.class);
//        for(int i: outArray){
//            System.out.println(i);
//        }
//        List<Map> outlist = JSONObject.parseArray(JSON.toJSONString(s.get("list")), Map.class);
//        for(Map<String, Integer> mm:outlist){
//            for(Map.Entry entry: mm.entrySet()){
//                System.out.println(entry.getKey()+"\t"+entry.getValue());
//            }
//        }
        Map<String, String> jsonStr = DecodeStrMap(json);
        List<Map> outList = JSONObject.parseArray(jsonStr.get("list"), Map.class);
        for(Map<String, Integer> mm:outList){
            for(Map.Entry entry: mm.entrySet()){
                System.out.println(entry.getKey()+"\t"+entry.getValue());
            }
        }
    }
}



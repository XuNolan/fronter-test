package project.xunolan.web.amisRespVo.utils;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ReflectUtil;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Convert4Amis {
    public static String prefixSpliter = "_";
    //list对象转换为无嵌套的Map。
    public static <T> List<Map<String, Object>> createItemsMap(List<T> param) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (T t : param) {
            Map<String, Object> map = flatSingleMap(t);
            result.add(map);
        }
        return result;
    }

    //单个对象转换为无嵌套的MAP。 用于兼容amis。
    public static Map<String, Object> flatSingleMap(Object obj) {
        if(obj == null){
            return null;
        }
        Map<String, Object> result = MapUtil.newHashMap(32);
        Field[] fields = ReflectUtil.getFields(obj.getClass());
        for (Field field : fields) {
            if(field.getName().equals("created") || field.getName().equals("updated")){
                Integer timeValue = (Integer) ReflectUtil.getFieldValue(obj, field);
                result.put(field.getName(),
                        LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(String.valueOf(timeValue))), ZoneId.systemDefault()).toString());
            }
            else
                result.put(field.getName(), ReflectUtil.getFieldValue(obj, field));
        }
        return result;
    }

    public static Map<String, Object> flatSingleMapWithPrefix(String prefix, Object obj) {
        if(obj == null){
            return null;
        }
        Map<String, Object> result = MapUtil.newHashMap(32);
        Field[] fields = ReflectUtil.getFields(obj.getClass());
        for (Field field : fields) {
            if(field.getName().equals("created") || field.getName().equals("updated")){
                Integer timeValue = (Integer) ReflectUtil.getFieldValue(obj, field);
                result.put(prefix + prefixSpliter + field.getName(),
                        LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(String.valueOf(timeValue))), ZoneId.systemDefault()).toString());
            }
            else
                result.put(prefix + prefixSpliter + field.getName(), ReflectUtil.getFieldValue(obj, field));
        }
        return result;
    }
}

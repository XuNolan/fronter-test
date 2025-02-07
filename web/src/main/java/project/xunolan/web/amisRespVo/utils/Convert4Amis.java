package project.xunolan.web.utils;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Convert4Amis {
    /*
    {
  "status": 0,
  "msg": "",
  "data": {
    "items": [
      {
        // 每一行的数据
        "id": 1,
        "xxx": "xxxx"
      }
    ],

    "total": 200,
    "page": 20
  }
}
     */
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
        Map<String, Object> result = MapUtil.newHashMap(32);
        Field[] fields = ReflectUtil.getFields(obj.getClass());
        for (Field field : fields) {
            result.put(field.getName(), ReflectUtil.getFieldValue(obj, field)); //todo:时间转str。
        }
        return result;
    }
}

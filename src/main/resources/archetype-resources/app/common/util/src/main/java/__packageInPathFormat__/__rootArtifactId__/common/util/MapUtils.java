#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${rootArtifactId}.common.util;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

/**
 * @author haiyang.song
 * @version 2015-6-5
 */
public class MapUtils {
    private static final Logger LOG = LoggerFactory.getLogger(MapUtils.class);

    /**
     * json型字符串转成map
     * @param jsonStr
     * @return
     */
    public static Map<String, Object> parseJSON2Map(String jsonStr){
    Map<String, Object> map = new HashMap<>();
        try{
            JSONObject json = JSONObject.parseObject(jsonStr);
                for(Object k : json.keySet()){
                Object v = json.get(k);
                map.put(k.toString(), v);
            }
        }catch(Exception e){
            LOG.error("参数类型错误");
        }
        return map;
  }

}

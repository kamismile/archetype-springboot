#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${rootArtifactId}.common.util;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ThreadLocalUtils {
    private static ThreadLocal<Map<String, String>> threadLocal;

    private static Map<String, String> initThreadLoad() {
        if (threadLocal == null) {
            threadLocal = new ThreadLocal<>();
        }
        Map<String, String> map = threadLocal.get();
        if (map == null) {
            map = new HashMap<>();
        }
        threadLocal.set(map);
        return map;
    }

    public static void setAttachment(String key, String value) {
        Map<String, String> map = initThreadLoad();
        map.put(key, value);
    }

    public static String getAttachment(String key) {
        Map<String, String> map = initThreadLoad();
        if (map.containsKey(key)) {
            return map.get(key);
        }
        return null;
    }
}

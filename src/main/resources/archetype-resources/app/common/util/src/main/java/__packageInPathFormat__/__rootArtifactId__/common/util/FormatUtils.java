#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${rootArtifactId}.common.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Date;

/**
 * 格式化数据
 * Created by haiyang.song on 15/12/11.
 */
public class FormatUtils {
    /**
     * 将对象格式化成String
     *
     * @param object
     * @return
     */
    public static String toString(Object object) {
        if (object == null) {
            return null;
        }

        if (object instanceof String) {
            return String.valueOf(object);
        }
        if (object instanceof JSONObject) {
            return ((JSONObject) object).toJSONString();
        }
        return JSONObject.toJSONString(object);
    }

    /**
     * 将对象转化成Int,如果出错返回0
     *
     * @param object
     * @return
     */
    public static int toInt(Object object) {
        return toInt(object, 0);
    }

    /**
     * 将对象转化成Int,如果出错指定返回值
     *
     * @param object
     * @return
     */
    public static int toInt(Object object, int def) {
        if (object == null) {
            return def;
        }
        if (object instanceof Integer) {
            return (Integer) object;
        }
        return NumberUtils.toInt(toString(object));
    }

    /**
     * 将对象转化成double,如果出错返回0
     *
     * @param object
     * @return
     */
    public static double toDouble(Object object) {
        return toDouble(object, 0);
    }

    /**
     * 将对象转化成double,如果出错指定返回值
     *
     * @param object
     * @return
     */
    public static double toDouble(Object object, int def) {
        if (object == null) {
            return def;
        }
        if (object instanceof Double) {
            return (Double) object;
        }
        if (object instanceof Integer) {
            return (Integer) object;
        }
        return NumberUtils.toDouble(toString(object));
    }

    /**
     * 将对象(数据为new Date().getTime())转化成Date,如果出错指定返回null
     *
     * @param object
     * @return
     */
    public static Date toDate(Object object) {
        return toDate(object, 1);
    }


    /**
     * 将对象(数据为new Date().getTime())转化成Date,允许执行倍率单位,如果出错指定返回null
     * type 1:单位秒
     * type 1000:直接getTime结果
     *
     * @param object
     * @return
     */
    public static Date toDate(Object object, int type) {
        long time = toInt(object);
        if (time == 0) {
            return null;
        }

        return new Date(time * type);
    }
}

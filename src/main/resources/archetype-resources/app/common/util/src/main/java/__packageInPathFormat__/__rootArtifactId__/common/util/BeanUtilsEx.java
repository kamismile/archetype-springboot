#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${rootArtifactId}.common.util;

/**
 * 对象属性拷贝,保证属性名称一致,并提供get/set方法
 *
 * Created by haiyang.song on 16/1/11.
 */

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.SqlDateConverter;

public class BeanUtilsEx extends BeanUtils {
    static {
        //注册sql.date的转换器，即允许BeanUtils.copyProperties时的源目标的sql类型的值允许为空
        ConvertUtils.register(new SqlDateConverter(), java.sql.Date.class);
        //注册util.date的转换器，即允许BeanUtils.copyProperties时的源目标的util类型的值允许为空
        ConvertUtils.register(new UtilDateConverter(), java.util.Date.class);
    }

    /**
     * 转换原数据
     *
     * @param target
     * @param source
     */
    public static void copyProperties(Object target, Object source) {
        try {
            BeanUtils.copyProperties(target, source);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

class UtilDateConverter implements Converter {
    public Object convert(Class arg0, Object arg1) {
        if (arg1 == null) {
            return null;
        }
        return arg1;
    }
}

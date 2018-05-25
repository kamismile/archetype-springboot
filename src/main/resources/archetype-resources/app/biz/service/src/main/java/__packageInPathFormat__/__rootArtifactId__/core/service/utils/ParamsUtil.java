#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${rootArtifactId}.core.service.utils;

import ${package}.${rootArtifactId}.core.service.exception.ServiceException;
import ${package}.${rootArtifactId}.core.service.exception.ErrorEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 参数校验工具类
 *
 * @author haiyang.song
 * @version 2015-6-5
 */
public class ParamsUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ParamsUtil.class);

    /**
     * 当传递的参数为null或者不传递参数时,抛出参数异常
     * 当检测到参数为空时,抛出参数异常
     * 当且仅当参数全部非空时,返回false
     *
     * @param args
     * @return
     */
    public static boolean hasEmptyParam(Object... args) {
        if (args == null || args.length == 0) {
            LOG.error("至少传递一个参数!");
            throw new ServiceException(ErrorEnum.ERROR_PARAM_EMPTY.getErrorCode(), "至少传递一个参数!");
        } else {
            for (Object arg : args) {
                if (arg instanceof String) {
                    String str = (String) arg;
                    if (StringUtils.isEmpty(str)) {
                        throw new ServiceException(ErrorEnum.ERROR_PARAM_EMPTY.getErrorCode(), str.concat("参数为空"));
                    }
                } else {
                    if (arg == null) {
                        throw new ServiceException(ErrorEnum.ERROR_PARAM_EMPTY.getErrorCode(), "参数为空");
                    }
                }
            }
        }
        return false;
    }

}

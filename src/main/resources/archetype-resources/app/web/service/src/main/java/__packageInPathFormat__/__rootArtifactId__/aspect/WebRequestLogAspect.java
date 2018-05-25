#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${rootArtifactId}.aspect;

import ${package}.${rootArtifactId}.common.util.DubboUtils;
import ${package}.${rootArtifactId}.core.service.exception.BizException;
import ${package}.${rootArtifactId}.core.service.exception.ErrorEnum;
import ${package}.${rootArtifactId}.res.BaseRes;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * 通用外部接口调用处理
 * <p/>
 * @author haiyang.song
 * @version 2015-6-5
 */
@Aspect
@Component
public class WebRequestLogAspect {
    private static final Logger digestLogger = LoggerFactory.getLogger("WEB-API-DIGEST-LOGGER");
    private static final Logger defaultLogger = LoggerFactory.getLogger("WEB-API-DEFAULT-LOGGER");

    @Pointcut("execution(* ${package}.${rootArtifactId}.api.*.*(..))")
    private void allMethod() {
    }

    @Around("allMethod()")
    public Object doAround(ProceedingJoinPoint call) throws Throwable {
        MethodSignature signature = (MethodSignature) call.getSignature();
        Method method = signature.getMethod();

        BaseRes baseRes = (BaseRes) method.getReturnType().newInstance();

        String[] classNameArray = method.getDeclaringClass().getName().split("\\.");
        String methodName = classNameArray[classNameArray.length - 1] + "." + method.getName();
        String params = ToStringBuilder.reflectionToString(call.getArgs(), ToStringStyle.SHORT_PREFIX_STYLE);
        String reqId = DubboUtils.getReqId();

        Date startDate = new Date();
        String head = methodName + "," + reqId;
        try {
            defaultLogger.info(head + ",REQ," + params);
            baseRes = (BaseRes) call.proceed();
            return baseRes;
        } catch (BizException e) {
            baseRes.setCode(e.getError().getErrorCode());
            baseRes.setErrmsg(e.getError().getErrorMessage());
            defaultLogger.error(head + ",BIZ_ERROR", e);
            return baseRes;
        } catch (Exception e) {
            baseRes.setCode(ErrorEnum.ERROR_DEFAULT.getErrorCode());
            baseRes.setErrmsg(ErrorEnum.ERROR_DEFAULT.getErrorMessage());
            defaultLogger.error(head + ",UNKNOWN_ERROR," + e.getMessage(), e);
            return baseRes;
        } finally {
            String resStr = ToStringBuilder.reflectionToString(baseRes, ToStringStyle.SHORT_PREFIX_STYLE);
            defaultLogger.info(head + ",RES," + resStr);
            long runTimes = new Date().getTime() - startDate.getTime();
            digestLogger.info(head + "," + baseRes.getCode() + "," + runTimes + "ms");
        }
    }
}

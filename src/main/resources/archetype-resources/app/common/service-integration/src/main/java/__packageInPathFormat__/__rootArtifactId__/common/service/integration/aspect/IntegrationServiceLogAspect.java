#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${rootArtifactId}.common.service.integration.aspect;

import ${package}.${rootArtifactId}.common.service.integration.exception.IntegrationServiceException;
import ${package}.${rootArtifactId}.common.util.DubboUtils;
import org.apache.commons.lang3.StringUtils;
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
public class IntegrationServiceLogAspect {
    private static final Logger digestLogger = LoggerFactory.getLogger("INTEGRATION-DIGEST-LOGGER");
    private static final Logger defaultLogger = LoggerFactory.getLogger("INTEGRATION-DEFAULT-LOGGER");

    @Pointcut("execution(* ${package}.common.service.integration.*.*.*(..))")
    private void allMethod() {
    }

    @Around("allMethod()")
    public Object doAround(ProceedingJoinPoint call) throws Throwable {
        MethodSignature signature = (MethodSignature) call.getSignature();
        Method method = signature.getMethod();

        if (!isIntegrationService(method.getReturnType())) {
            return call.proceed();
        }

        Object baseRes = method.getReturnType().newInstance();
        String[] classNameArray = method.getDeclaringClass().getName().split("\\.");
        String className = classNameArray[classNameArray.length - 1];
        String methodName = method.getName();
        String cmName = className + "." + methodName;
        String reqId = DubboUtils.getReqId();
        String params = ToStringBuilder.reflectionToString(call.getArgs(), ToStringStyle.SHORT_PREFIX_STYLE);

        Date startDate = new Date();
        int code = -1;
        String head = cmName + "," + reqId;
        try {
            defaultLogger.info(head + ",REQ," + params);
            baseRes = call.proceed();
            //检查外部服务返回信息
            code = (Integer) baseRes.getClass().getMethod("getCode").invoke(baseRes);
            return baseRes;
        } catch (IntegrationServiceException e) {
            throw e;
        } catch (Exception e) {
            defaultLogger.error(head + ",UNKNOWN_ERROR", e);
            return baseRes;
        } finally {
            String resStr = ToStringBuilder.reflectionToString(baseRes, ToStringStyle.SHORT_PREFIX_STYLE);
            defaultLogger.info(head + ",RES," + resStr);

            long runTimes = new Date().getTime() - startDate.getTime();
            digestLogger.info(head + "," + code + "," + runTimes + "ms");
        }
    }

    private boolean isIntegrationService(Class resClass) {
        String[] classNameArray = resClass.getName().split("\\.");
        String className = classNameArray[classNameArray.length - 1];
        if (StringUtils.equals(className, "BaseRes")) {
            return true;
        } else if (StringUtils.equals(className, "Object")) {
            return false;
        }
        Class superClass = resClass.getSuperclass();
        return superClass != null && isIntegrationService(superClass);
    }
}

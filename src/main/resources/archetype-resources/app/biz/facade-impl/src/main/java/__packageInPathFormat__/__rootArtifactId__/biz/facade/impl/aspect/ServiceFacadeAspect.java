#set($symbol_pound='#')
#set($symbol_dollar='$')
#set($symbol_escape='\' )
package ${package}.${rootArtifactId}.biz.facade.impl.aspect;

import ${package}.${rootArtifactId}.common.service.model.res.BaseRes;
import ${package}.${rootArtifactId}.common.util.DubboUtils;
import ${package}.${rootArtifactId}.core.service.exception.BizException;
import ${package}.${rootArtifactId}.core.service.exception.ErrorEnum;
import ${package}.${rootArtifactId}.core.service.exception.ServiceException;
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
 * 服务请求拦截
 * 1.请求处理
 * 2.参数拆分
 * 3.签名认证.
 * 4.map转请求对象
 * <p>
 * 异常说明：
 * 提供外部服务进行检查，统一抛出ServiceException
 * 如果有外部调用则在检查后抛出，IntegrationServiceException
 * 如果业务逻辑内处理异常则统一抛出BizException
 * <p>
 * Exception 都会包装成 false,false
 * <p/>
 *
 * @author haiyang.song
 * @version 2015-6-5
 */
@Aspect
@Component
public class ServiceFacadeAspect {
    private static final Logger digestLogger = LoggerFactory.getLogger("SERVICE-API-DIGEST-LOGGER");
    private static final Logger defaultLogger = LoggerFactory.getLogger("SERVICE-API-DEFAULT-LOGGER");

    @Pointcut("execution(* ${package}.${rootArtifactId}.biz.facade.impl.*.*(..))")
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


        String head = methodName + "," + reqId;
        Date startDate = new Date();
        try {
            defaultLogger.info(head + ",REQ," + params);
            return call.proceed();
        } catch (ServiceException e) {
            baseRes.setCode(e.getErrorCode());
            baseRes.setErrmsg(e.getErrorMessage());
            defaultLogger.error(head + ",SERVICE_ERROR", e);
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
            defaultLogger.info(head + ",RES," + reqId + "," + resStr);
            long runTimes = new Date().getTime() - startDate.getTime();
            digestLogger.info(head + "," + baseRes.getCode() + "," + runTimes + "ms");
        }
    }
}
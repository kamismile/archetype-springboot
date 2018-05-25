//package ${package}.${rootArtifactId}.common.dal.aspect;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//import java.lang.reflect.Method;
//
///**
// * DAO请求拦截
// * <p/>
// *
// * @author haiyang.song
// * @version 2015-6-5
// */
//@Aspect
//@Component
//public class DaoAspect {
//    private static final Logger logger = LoggerFactory.getLogger("DATA-DAL-DIGEST");
//
//    @Pointcut("execution(* ${package}.${rootArtifactId}.common.dal.dao.*.*(..))")
//    private void allMethod() {
//    }
//
//    @Around("allMethod()")
//    public Object doAround(ProceedingJoinPoint call) throws Throwable {
//        MethodSignature signature = (MethodSignature) call.getSignature();
//        Method method = signature.getMethod();
//
//        String[] classNameArray = method.getDeclaringClass().getName().split("\\.");
//        String methodName = classNameArray[classNameArray.length - 1] + "." + method.getName();
//
//        long current = System.currentTimeMillis();
//        Object obj = call.proceed();
//        String message = String.format("%s,%sms", methodName, (System.currentTimeMillis() - current));
//        logger.info(message);
//        return obj;
//
//    }
//}
//

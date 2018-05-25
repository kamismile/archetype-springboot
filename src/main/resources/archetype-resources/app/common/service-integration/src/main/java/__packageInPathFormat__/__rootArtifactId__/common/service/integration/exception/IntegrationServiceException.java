#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${rootArtifactId}.common.service.integration.exception;

/**
 * 接口异常错误
 * <p/>
 * @author haiyang.song
 * @version 2015-6-5
 */
public class IntegrationServiceException extends RuntimeException {
    private String message;
    private String clazz;
    private String method;
    private int serviceError;

    public IntegrationServiceException(String clazz, String method, String message) {
        this.message = message;
        this.setClazz(clazz);
        this.setMethod(method);
    }

    public IntegrationServiceException(int serviceError, String message) {
        this.message = message;
        this.serviceError = serviceError;
    }

    public IntegrationServiceException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getServiceError() {
        return serviceError;
    }

    public void setServiceError(int serviceError) {
        this.serviceError = serviceError;
    }
}

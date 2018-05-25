#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${rootArtifactId}.core.service.exception;

/**
 * 服务错误
 * <p/>
 *
 * @author haiyang.song
 * @version 2015-6-5
 */
public class SequenceException extends BizException {
    public SequenceException(ErrorEnum errorEnum, String message) {
        super(errorEnum, message);
    }
}

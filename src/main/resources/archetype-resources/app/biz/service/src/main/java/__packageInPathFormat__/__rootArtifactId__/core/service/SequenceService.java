#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${rootArtifactId}.core.service;

/**
 * @author haiyang.song
 * @version 2015-6-5
 */
public interface SequenceService {
    /**
     * <p>
     * 获取指定sequence的序列号
     * </p>
     *
     * @param seqName sequence名
     * @return String 序列号
     */
    String nextVal(String seqName);
}

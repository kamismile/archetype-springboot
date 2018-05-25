#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${rootArtifactId}.common.dal.mapper;


import ${package}.${rootArtifactId}.common.dal.dataobject.SysSequenceDO;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 由于需要对分页支持,请直接使用对应的DAO类
 * The Table sys_sequence.
 * 流水号生成表
 */
public interface SysSequenceDOMapper{

    /**
     * desc:更新表:sys_sequence.<br/>
     * descSql =  <![CDATA[ UPDATE sys_sequence SET SEQ_VALUE = #{seqValue,jdbcType=BIGINT} ,GMT_MODIFIED = now() WHERE SEQ_NAME = #{seqName,jdbcType=VARCHAR} ]]>
     * @param seqValue seqValue
     * @param seqName seqName
     * @return Long
     */
    Long updateSeqValue(@Param("seqValue")Long seqValue,@Param("seqName")String seqName);
    /**
     * desc:根据主键获取数据:sys_sequence.<br/>
     * descSql =  SELECT * FROM sys_sequence WHERE <![CDATA[ SEQ_NAME = #{seqName,jdbcType=VARCHAR} ]]>
     * @param seqName seqName
     * @return SysSequenceDO
     */
    SysSequenceDO getBySeqName(@Param("seqName")String seqName);
    /**
     * desc:查询所有:sys_sequence.<br/>
     * descSql =  SELECT * FROM sys_sequence
     * @return List<SysSequenceDO>
     */
    List<SysSequenceDO> getAllSeq();
}

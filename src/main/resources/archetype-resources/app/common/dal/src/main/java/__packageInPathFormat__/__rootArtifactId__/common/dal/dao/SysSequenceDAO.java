#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${rootArtifactId}.common.dal.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ${package}.${rootArtifactId}.common.dal.dataobject.SysSequenceDO;
import java.util.List;
import ${package}.${rootArtifactId}.common.dal.mapper.SysSequenceDOMapper;

/**
* The Table sys_sequence.
* 流水号生成表
*/
@Repository
public class SysSequenceDAO{

    @Autowired
    private SysSequenceDOMapper sysSequenceDOMapper;

    /**
     * desc:更新表:sys_sequence.<br/>
     * @param seqValue seqValue
     * @param seqName seqName
     * @return Long
     */
    public Long updateSeqValue(Long seqValue,String seqName){
        return sysSequenceDOMapper.updateSeqValue(seqValue, seqName);
    }
    /**
     * desc:根据主键获取数据:sys_sequence.<br/>
     * @param seqName seqName
     * @return SysSequenceDO
     */
    public SysSequenceDO getBySeqName(String seqName){
        return sysSequenceDOMapper.getBySeqName(seqName);
    }
    /**
     * desc:查询所有:sys_sequence.<br/>
     * @return List<SysSequenceDO>
     */
    public List<SysSequenceDO> getAllSeq(){
        return sysSequenceDOMapper.getAllSeq();
    }
}

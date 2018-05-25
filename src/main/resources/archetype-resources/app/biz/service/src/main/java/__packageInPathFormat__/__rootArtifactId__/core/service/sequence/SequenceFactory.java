#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${rootArtifactId}.core.service.sequence;

import ${package}.${rootArtifactId}.common.dal.dao.SysSequenceDAO;
import ${package}.${rootArtifactId}.common.dal.dataobject.SysSequenceDO;
import ${package}.${rootArtifactId}.core.service.exception.ErrorEnum;
import ${package}.${rootArtifactId}.core.service.exception.SequenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class SequenceFactory {
    private static final Logger logger = LoggerFactory.getLogger(SequenceFactory.class);

    private final Lock lock = new ReentrantLock();

    private Map<String, SequenceHolder> holderMap = new ConcurrentHashMap<>();

    @Autowired
    private SysSequenceDAO sysSequenceDAO;

    @Value("${seq.enable:false}")
    private boolean switchStatus;
    //单个sequence初始化乐观锁更新失败重试次数
    @Value("${seq.init.retry:5}")
    private int initRetryNum;
    //单个sequence更新序列区间乐观锁更新失败重试次数
    @Value("${seq.get.retry:20}")
    private int getRetryNum;

    @PostConstruct
    private void init() {
        //初始化所有sequence
        if (switchStatus) {
            initAll();
        }
    }


    /**
     * <p> 加载表中所有sequence，完成初始化 </p>
     *
     * @return void
     * @author coderzl
     */
    private void initAll() {
        try {
            lock.lock();
            List<SysSequenceDO> seqList = sysSequenceDAO.getAllSeq();
            if (seqList == null) {
                throw new IllegalArgumentException("SEQUENCE_IS_NULL");
            }
            for (SysSequenceDO seqDO : seqList) {
                SequenceHolder holder = new SequenceHolder(sysSequenceDAO, seqDO, initRetryNum, getRetryNum);
                holder.init();
                logger.info("LOAD SEQUENCE:" + seqDO.getSeqName());
                holderMap.put(seqDO.getSeqName(), holder);
            }
        } finally {
            lock.unlock();
        }
    }


    /**
     * <p>  </p>
     *
     * @param seqName
     * @return long
     * @author coderzl
     */
    public long getNextVal(String seqName) {
        if (!switchStatus) {
            throw new SequenceException(ErrorEnum.ERROR_DEFAULT, "SEQUENCE_CLOSED");
        }

        SequenceHolder holder = holderMap.get(seqName);
        if (holder == null) {
            try {
                lock.lock();
                holder = holderMap.get(seqName);
                if (holder != null) {
                    return holder.getNextVal();
                }
                SysSequenceDO seqDO = sysSequenceDAO.getBySeqName(seqName);
                if (seqDO == null) {
                    throw new SequenceException(ErrorEnum.ERROR_DEFAULT, "SEQUENCE_NOT_EXIST");
                }

                holder = new SequenceHolder(sysSequenceDAO, seqDO, initRetryNum, getRetryNum);
                holder.init();
                holderMap.put(seqName, holder);
            } finally {
                lock.unlock();
            }
        }
        return holder.getNextVal();
    }
}

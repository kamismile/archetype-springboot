#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${rootArtifactId}.core.service.sequence;

import ${package}.${rootArtifactId}.common.dal.dao.SysSequenceDAO;
import ${package}.${rootArtifactId}.common.dal.dataobject.SysSequenceDO;
import ${package}.${rootArtifactId}.core.service.exception.ErrorEnum;
import ${package}.${rootArtifactId}.core.service.exception.SequenceException;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SequenceHolder {
    private final Lock lock = new ReentrantLock();

    private String seqName;
    private SysSequenceDAO sequenceDAO;
    private SysSequenceDO sequenceDO;
    private SequenceRange sequenceRange;

    private int initRetryNum;
    private int getRetryNum;

    /**
     * <p> 构造方法 </p>
     *
     * @param sequenceDAO
     * @param sysSequenceDO
     * @param initRetryNum  初始化时，数据库更新失败后重试次数
     * @param getRetryNum   获取nextVal时，数据库更新失败后重试次数
     * @return
     * @Title MysqlSequenceHolder
     * @author coderzl
     */
    SequenceHolder(SysSequenceDAO sequenceDAO, SysSequenceDO sysSequenceDO, int initRetryNum, int getRetryNum) {
        this.sequenceDAO = sequenceDAO;
        this.sequenceDO = sysSequenceDO;
        this.initRetryNum = initRetryNum;
        this.getRetryNum = getRetryNum;
        if (sequenceDO != null)
            this.seqName = sequenceDO.getSeqName();
    }

    private void checkHolder() {
        if (sequenceDAO == null || seqName == null || seqName.trim().length() == 0 || sequenceDO == null) {
            throw new SequenceException(ErrorEnum.ERROR_DEFAULT, "INIT_ERROR");
        }
        if (!validate(sequenceDO)) {
            throw new SequenceException(ErrorEnum.ERROR_DEFAULT, "[" + seqName + "]VALIDATE_FAIL");
        }
    }

    /**
     * <p> 初始化 </p>
     *
     * @param
     * @return void
     * @Title init
     * @author coderzl
     */
    public void init() {
        checkHolder();

        // 初始化该sequence
        initSequenceRecord(sequenceDO);
    }

    /**
     * <p> 获取下一个序列号 </p>
     *
     * @param
     * @return long
     * @Title getNextVal
     * @author coderzl
     */
    public long getNextVal() {
        checkHolder();

        long curValue = sequenceRange.getAndIncrement();
        if (curValue == -1) {
            try {
                lock.lock();
                curValue = sequenceRange.getAndIncrement();
                if (curValue != -1) {
                    return curValue;
                }
                sequenceRange = retryRange();
                curValue = sequenceRange.getAndIncrement();
            } finally {
                lock.unlock();
            }
        }
        return curValue;
    }

    /**
     * <p> 初始化当前这条记录 </p>
     *
     * @param sequenceDO
     * @return void
     * @Title initSequenceRecord
     * @Description
     * @author coderzl
     */
    private void initSequenceRecord(SysSequenceDO sequenceDO) {
        //在限定次数内，乐观锁更新数据库记录
        for (int i = 1; i < initRetryNum; i++) {
            //查询bo
            SysSequenceDO curDO = sequenceDAO.getBySeqName(sequenceDO.getSeqName());
            if (curDO == null) {
                throw new SequenceException(ErrorEnum.ERROR_DEFAULT, "[" + seqName + "]NOT_INIT");
            }
            if (!validate(curDO)) {
                throw new SequenceException(ErrorEnum.ERROR_DEFAULT, "[" + seqName + "]VALIDATE_FAIL");
            }
            //改变当前值
            long newValue = curDO.getSeqValue() + curDO.getStep();
            //检查当前值
            if (!checkCurrentValue(newValue, curDO)) {
                newValue = resetCurrentValue(curDO);
            }
            long result = sequenceDAO.updateSeqValue(newValue, sequenceDO.getSeqName());
            if (result > 0) {
                sequenceRange = new SequenceRange(curDO.getSeqValue(), newValue - 1);
                curDO.setSeqValue(newValue);
                this.sequenceDO = curDO;
                return;
            }
        }
        //限定次数内，更新失败，抛出异常
        throw new SequenceException(ErrorEnum.ERROR_DEFAULT, "[" + seqName + "]UPDATE_ERROR");
    }

    /**
     * <p> 检查新值是否合法 新的当前值是否在最大最小值之间</p>
     *
     * @param curValue
     * @param curDo
     * @return boolean
     * @author coderzl
     */
    private boolean checkCurrentValue(long curValue, SysSequenceDO curDo) {
        return curValue > curDo.getMinValue() && curValue <= curDo.getMaxValue();
    }

    /**
     * <p> 重置sequence当前值 ：当前sequence达到最大值时，重新从最小值开始 </p>
     *
     * @param curDo
     * @return long
     * @Title resetCurrentValue
     * @author coderzl
     */
    private long resetCurrentValue(SysSequenceDO curDo) {
        return curDo.getMinValue();
    }

    /**
     * <p> 缓存区间使用完毕时，重新读取数据库记录，缓存新序列段 </p>
     *
     * @Title retryRange
     * @author coderzl
     */
    private SequenceRange retryRange() {
        for (int i = 1; i < getRetryNum; i++) {
            //查询bo
            SysSequenceDO curDO = sequenceDAO.getBySeqName(sequenceDO.getSeqName());
            if (curDO == null) {
                throw new SequenceException(ErrorEnum.ERROR_DEFAULT, "[" + seqName + "]INIT_ERROR");
            }
            if (!validate(curDO)) {
                throw new SequenceException(ErrorEnum.ERROR_DEFAULT, "[" + seqName + "]VALIDATE_FAIL");
            }
            //改变当前值
            long newValue = curDO.getSeqValue() + curDO.getStep();
            //检查当前值
            if (!checkCurrentValue(newValue, curDO)) {
                newValue = resetCurrentValue(curDO);
            }
            long result = sequenceDAO.updateSeqValue(newValue, sequenceDO.getSeqName());
            if (result > 0) {
                sequenceRange = new SequenceRange(curDO.getSeqValue(), newValue - 1);
                curDO.setSeqValue(newValue);
                this.sequenceDO = curDO;
                return sequenceRange;
            }
        }
        throw new SequenceException(ErrorEnum.ERROR_DEFAULT, "[" + seqName + "]UPDATE_ERROR");
    }

    /**
     * 检查是否正确
     *
     * @param curDO
     * @return
     */
    private boolean validate(SysSequenceDO curDO) {
        //一些简单的校验。如当前值必须在最大最小值之间。step值不能大于max与min的差
        return !StringUtils.isBlank(seqName) && curDO.getMinValue() >= 0 && curDO.getMaxValue() > 0 &&
                curDO.getStep() > 0 && curDO.getMinValue() < curDO.getMaxValue() &&
                curDO.getMaxValue() - curDO.getMinValue() > curDO.getStep() &&
                curDO.getSeqValue() >= curDO.getMinValue() && curDO.getSeqValue() <= curDO.getMaxValue();
    }
}

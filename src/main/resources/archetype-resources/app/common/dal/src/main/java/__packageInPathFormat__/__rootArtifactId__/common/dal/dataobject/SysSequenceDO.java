#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${rootArtifactId}.common.dal.dataobject;


import java.util.Date;

/**
 * The table 流水号生成表
 */
public class SysSequenceDO{

    /**
     * step 每次取值的数量.
     */
    private Long step;
    /**
     * maxValue 最大值.
     */
    private Long maxValue;
    /**
     * minValue 最小值.
     */
    private Long minValue;
    /**
     * seqValue 目前序列值.
     */
    private Long seqValue;
    /**
     * seqName 序列名称.
     */
    private String seqName;
    /**
     * gmtCreated 创建时间.
     */
    private Date gmtCreated;
    /**
     * gmtModified 修改时间.
     */
    private Date gmtModified;

    /**
     * Set step 每次取值的数量.
     */
    public void setStep(Long step){
        this.step = step;
    }

    /**
     * Get step 每次取值的数量.
     *
     * @return the string
     */
    public Long getStep(){
        return step;
    }

    /**
     * Set maxValue 最大值.
     */
    public void setMaxValue(Long maxValue){
        this.maxValue = maxValue;
    }

    /**
     * Get maxValue 最大值.
     *
     * @return the string
     */
    public Long getMaxValue(){
        return maxValue;
    }

    /**
     * Set minValue 最小值.
     */
    public void setMinValue(Long minValue){
        this.minValue = minValue;
    }

    /**
     * Get minValue 最小值.
     *
     * @return the string
     */
    public Long getMinValue(){
        return minValue;
    }

    /**
     * Set seqValue 目前序列值.
     */
    public void setSeqValue(Long seqValue){
        this.seqValue = seqValue;
    }

    /**
     * Get seqValue 目前序列值.
     *
     * @return the string
     */
    public Long getSeqValue(){
        return seqValue;
    }

    /**
     * Set seqName 序列名称.
     */
    public void setSeqName(String seqName){
        this.seqName = seqName;
    }

    /**
     * Get seqName 序列名称.
     *
     * @return the string
     */
    public String getSeqName(){
        return seqName;
    }

    /**
     * Set gmtCreated 创建时间.
     */
    public void setGmtCreated(Date gmtCreated){
        this.gmtCreated = gmtCreated;
    }

    /**
     * Get gmtCreated 创建时间.
     *
     * @return the string
     */
    public Date getGmtCreated(){
        return gmtCreated;
    }

    /**
     * Set gmtModified 修改时间.
     */
    public void setGmtModified(Date gmtModified){
        this.gmtModified = gmtModified;
    }

    /**
     * Get gmtModified 修改时间.
     *
     * @return the string
     */
    public Date getGmtModified(){
        return gmtModified;
    }
}

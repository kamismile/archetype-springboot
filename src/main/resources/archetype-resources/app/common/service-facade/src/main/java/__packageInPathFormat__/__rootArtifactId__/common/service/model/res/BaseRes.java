#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${rootArtifactId}.common.service.model.res;

import java.io.Serializable;

/**
 * 基础返回信息
 * <p/>
 * @author haiyang.song
 * @version 2015-6-5
 */
public class BaseRes implements Serializable {
    /**
     * 接口返回错误码
     */
    private Integer code = 0;

    /**
     * 接口调用错误信息
     */
    private String errmsg = "";

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    @Override
    public String toString() {
        return "BaseRes{" + code + "," + errmsg + "}";
    }
}

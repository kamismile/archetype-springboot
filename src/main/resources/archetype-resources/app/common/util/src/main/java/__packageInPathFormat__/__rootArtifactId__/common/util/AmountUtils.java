#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${rootArtifactId}.common.util;

import ${package}.${rootArtifactId}.common.util.sign.MD5;
import org.apache.commons.lang3.StringUtils;

/**
 * 资金工具类
 * <p/>
 * Created by haiyang.song on 16/6/30.
 */
public class AmountUtils {
    /**
     * 计算用户资金检查信息
     *
     * @param amount
     * @param amountFreeze
     * @return
     */
    public static String getAmountCheck(String accountNo, long amount, long amountFreeze) {
        String info = accountNo + amount + amountFreeze;
        return MD5.sign(info, "CHECK_AMOUNT__123", "utf8");
    }

    /**
     * 检查用户资金是否正常
     *
     * @param amount
     * @param amountFreeze
     * @param checkAmount
     * @return
     */
    public static boolean checkAmount(String accountNo, long amount, long amountFreeze, String checkAmount) {
        return StringUtils.equals(getAmountCheck(accountNo, amount, amountFreeze), checkAmount);
    }

}

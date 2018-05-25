#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${rootArtifactId}.common.util;

/**
 * @author haiyang.song
 * @version 2015-6-5
 */
public class SensitiveFormatUtils {
    /**
     * 格式化手机号
     *
     * @param mobileNo
     * @return
     */
    public static String formatMobile(String mobileNo) {
        return mobileNo.substring(0, 3) + "****" + mobileNo.substring(7, 11);
    }
}

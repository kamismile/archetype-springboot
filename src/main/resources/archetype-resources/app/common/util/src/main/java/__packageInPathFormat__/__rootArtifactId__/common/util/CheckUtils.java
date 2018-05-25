#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${rootArtifactId}.common.util;

/**
 * Created by haiyang.song on 15/12/14.
 */
public class CheckUtils {
    /**
     * 手机号检查
     *
     * @param mobileNo
     * @return
     */
    public static boolean checkMobileNo(String mobileNo) {

        if (mobileNo.length() != 11) {
            return false;
        }

        //2.手机号检查
        String[] prefixArray = new String[]{
                "130", "131", "132", "133", "134", "135", "136", "137", "138", "139",
                "150", "151", "152", "153", "154", "155", "156", "157", "158", "159",
                "170", "171", "172", "173", "174", "175", "176", "177", "178", "179",
                "180", "181", "182", "183", "184", "185", "186", "187", "188", "189"};

        for (String prefix : prefixArray) {
            if (mobileNo.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

}

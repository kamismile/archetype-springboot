#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${rootArtifactId}.common.util.sign;

/**
 * @author haiyang.song
 * @version 2015-6-5
 */
public abstract class ConvertUtils {
    public static String toHex(byte[] input) {
        if (input == null) {
            return null;
        } else {
            StringBuilder output = new StringBuilder(input.length * 2);

            for (byte anInput : input) {
                int current = anInput & 255;
                if (current < 16) {
                    output.append("0");
                }

                output.append(Integer.toString(current, 16));
            }
            return output.toString();
        }
    }


}
/**
 * 
 */
#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${rootArtifactId}.common.util;

import java.util.UUID;

/**
 * @author haiyang.song
 * @version 2015-6-5
 */
public class TokenUtil {

   /**
	* 
	* @Description: 生成accessToken
	* @return String    返回类型 
	* @throws
	*/
	public static String generateToken(){
		return UUID.randomUUID().toString().replace("-", "");
	}
}

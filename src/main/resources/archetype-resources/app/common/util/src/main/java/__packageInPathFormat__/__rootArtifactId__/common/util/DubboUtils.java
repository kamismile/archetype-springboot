#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${rootArtifactId}.common.util;

import com.alibaba.dubbo.rpc.RpcContext;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class DubboUtils {

    public static String getReqId() {
        RpcContext rpcContext = RpcContext.getContext();
        String reqId = rpcContext.getAttachment("REQ_ID");
        if (StringUtils.isBlank(reqId)) {
            reqId = ThreadLocalUtils.getAttachment("REQ_ID");
            if (StringUtils.isBlank(reqId)) {
                reqId = genReqId();
            }
        }
        rpcContext.setAttachment("REQ_ID", reqId);
        ThreadLocalUtils.setAttachment("REQ_ID", reqId);
        return reqId;
    }

    private static String genReqId() {
        SimpleDateFormat form = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        String dt = form.format(date);
        return dt + "-" + UUID.randomUUID().toString().replace("-", "");
    }
}

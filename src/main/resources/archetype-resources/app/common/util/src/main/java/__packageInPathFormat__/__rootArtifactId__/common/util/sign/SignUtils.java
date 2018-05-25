#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${rootArtifactId}.common.util.sign;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

/**
 * @author haiyang.song
 * @version 2015-6-5
 */
public class SignUtils {
    /**
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     *
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, String> params) {
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);

        String preStr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
                preStr = preStr + key + "=" + value;
            } else {
                preStr = preStr + key + "=" + value + "&";
            }
        }

        return preStr;
    }

    /**
     * 除去数组中的空值和签名参数
     *
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    public static Map<String, String> paraFilter(Map<String, String> sArray) {
        Map<String, String> result = new HashMap<String, String>();

        if (sArray == null || sArray.size() <= 0) {
            return result;
        }

        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || value.equals("") || key.equalsIgnoreCase("sign") || key.equalsIgnoreCase("sign_type")) {
                continue;
            }
            result.put(key, value);
        }

        return result;
    }

    public static String sign(String content, String privateKey) {
        String charset = "UTF-8";

        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey.getBytes()));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);
            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initSign(priKey);
            signature.update(content.getBytes(charset));
            byte[] signed = signature.sign();
            return new String(Base64.encodeBase64(signed));
        } catch (Exception var8) {
            return null;
        }
    }

    public static boolean checkSign(String content, String sign, String publicKey) {
        try {
            KeyFactory e = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64.decode2(publicKey);
            PublicKey pubKey = e.generatePublic(new X509EncodedKeySpec(encodedKey));
            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initVerify(pubKey);
            signature.update(content.getBytes("utf-8"));
            return signature.verify(Base64.decode2(sign));
        } catch (Exception var8) {
            var8.printStackTrace();
            return false;
        }
    }


    /**
     * 签名
     *
     * @param params
     * @param signKey
     * @return
     */
    public static String sign(Map<String, String> params, String signKey) {
        Map<String, String> cleanParam = SignUtils.paraFilter(params);
        String content = SignUtils.createLinkString(cleanParam);
        return SignUtils.sign(content, signKey);
    }


    /**
     * 检查签名信息
     *
     * @param params
     * @param signKey
     * @return
     */
    public static boolean checkSign(Map<String, String> params, String sign, String signKey) {
        Map<String, String> cleanParam = SignUtils.paraFilter(params);
        String content = SignUtils.createLinkString(cleanParam);
        return SignUtils.checkSign(content, sign, signKey);
    }

    /**
     * 根据标准加密规则进行数据加密
     * 数据使用RSA私钥进行签名然后对整体数据(包含签名)进行AES加密,将aes密钥使用机构公钥进行加密
     *
     * @param params            请求参数
     * @param encryptPrivateKey 签名私钥
     * @param signPublicKey     加密公钥
     * @return
     */
    public static Map<String, String> encryptData(
            Map<String, Object> params, String encryptPrivateKey, String signPublicKey) {

        Map<String, String> dataMap = new HashMap<>();
        for (String key : params.keySet()) {
            Object value = params.get(key);
            if (value instanceof List || value instanceof Map) {
                dataMap.put(key, JSONObject.toJSONString(params.get(key)));
            } else {
                dataMap.put(key, String.valueOf(value));
            }
        }

        //数据组装
        String sign = SignUtils.sign(dataMap, encryptPrivateKey);
        dataMap.put("sign", sign);
        dataMap.put("sign_type", "RSA");
        String info = JSON.toJSONString(dataMap);

        //数据加密
        String aesKey = AES.genRandomKey();
        String encryptData = AES.encryptToBcd(info, aesKey);
        String encryptAesKey = RSA.encryptByPublicKey(aesKey, signPublicKey);

        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("encrypt_data", encryptData);
        requestMap.put("encrypt_aes_key", encryptAesKey);
        return requestMap;
    }
}

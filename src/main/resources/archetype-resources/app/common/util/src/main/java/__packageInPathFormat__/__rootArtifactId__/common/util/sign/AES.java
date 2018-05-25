#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${rootArtifactId}.common.util.sign;

import ${package}.${rootArtifactId}.common.util.RandomUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;

/**
 * @author haiyang.song
 * @version 2015-6-5
 */
public class AES {
    public static String AES = "AES";

    public static byte[] encrypt(byte[] data, byte[] key) {
        if (key.length != 16) {
            throw new RuntimeException("Invalid AES key length (must be 16 bytes)");
        } else {
            try {
                SecretKeySpec e = new SecretKeySpec(key, AES);
                byte[] enCodeFormat = e.getEncoded();
                SecretKeySpec seckey = new SecretKeySpec(enCodeFormat, AES);
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(1, seckey);
                return cipher.doFinal(data);
            } catch (Exception e) {
                return null;
            }
        }
    }

    public static byte[] decrypt(byte[] data, byte[] key) {
        if (key.length != 16) {
            throw new RuntimeException("Invalid AES key length (must be 16 bytes)");
        } else {
            try {
                SecretKeySpec e = new SecretKeySpec(key, "AES");
                byte[] enCodeFormat = e.getEncoded();
                SecretKeySpec seckey = new SecretKeySpec(enCodeFormat, AES);
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(2, seckey);
                return cipher.doFinal(data);
            } catch (Exception e) {
                return null;
            }
        }
    }

    public static String encryptToBase64(String data, String key) {
        try {
            byte[] e = encrypt(data.getBytes("UTF-8"), key.getBytes("UTF-8"));
            return new String(Base64.encode(e));
        } catch (UnsupportedEncodingException var3) {
            return null;
        }
    }

    public static String decryptFromBase64(String data, String key) {
        try {
            byte[] e = Base64.decode(data.getBytes());
            byte[] valueByte = decrypt(e, key.getBytes("UTF-8"));
            return new String(valueByte, "UTF-8");
        } catch (UnsupportedEncodingException var4) {
            return null;
        }
    }


    public static String encryptToBcd(String data, String key) {
        try {
            byte[] e = encrypt(data.getBytes("UTF-8"), key.getBytes("UTF-8"));
            return RSA.bcd2Str(Base64.encode(e));
        } catch (UnsupportedEncodingException var3) {
            throw new RuntimeException("encrypt fail!", var3);
        }
    }

    public static String decryptFromBcd(String data, String key) {
        try {
            byte[] e = Base64.decode(RSA.str2Bcd(data));
            byte[] valueByte = decrypt(e, key.getBytes("UTF-8"));
            return new String(valueByte, "UTF-8");
        } catch (UnsupportedEncodingException var4) {
            throw new RuntimeException("decrypt fail!", var4);
        }
    }

    public static String genRandomKey() {
        return RandomUtils.getRandom(16);
    }
}
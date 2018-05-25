#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${rootArtifactId}.common.util.sign;

import java.io.UnsupportedEncodingException;

/**
 * @author haiyang.song
 * @version 2015-6-5
 */

public class Base64 {
    static final int CHUNK_SIZE = 76;
    static final byte[] CHUNK_SEPARATOR = "\r\n".getBytes();
    static final int BASELENGTH = 255;
    static final int LOOKUPLENGTH = 64;
    static final int EIGHTBIT = 8;
    static final int SIXTEENBIT = 16;
    static final int TWENTYFOURBITGROUP = 24;
    static final int FOURBYTE = 4;
    static final int SIGN = -128;
    static final byte PAD = 61;
    private static byte[] base64Alphabet = new byte[255];
    private static byte[] lookUpBase64Alphabet = new byte[64];

    static {
        int i;
        for (i = 0; i < 255; ++i) {
            base64Alphabet[i] = -1;
        }

        for (i = 90; i >= 65; --i) {
            base64Alphabet[i] = (byte) (i - 65);
        }

        for (i = 122; i >= 97; --i) {
            base64Alphabet[i] = (byte) (i - 97 + 26);
        }

        for (i = 57; i >= 48; --i) {
            base64Alphabet[i] = (byte) (i - 48 + 52);
        }

        base64Alphabet[43] = 62;
        base64Alphabet[47] = 63;

        for (i = 0; i <= 25; ++i) {
            lookUpBase64Alphabet[i] = (byte) (65 + i);
        }

        i = 26;

        int j;
        for (j = 0; i <= 51; ++j) {
            lookUpBase64Alphabet[i] = (byte) (97 + j);
            ++i;
        }

        i = 52;

        for (j = 0; i <= 61; ++j) {
            lookUpBase64Alphabet[i] = (byte) (48 + j);
            ++i;
        }

        lookUpBase64Alphabet[62] = 43;
        lookUpBase64Alphabet[63] = 47;
    }

    public Base64() {
    }

    private static boolean isBase64(byte octect) {
        return octect == 61 ? true : base64Alphabet[octect] != -1;
    }

    public static boolean isArrayByteBase64(byte[] arrayOctect) {
        arrayOctect = discardWhitespace(arrayOctect);
        int length = arrayOctect.length;
        if (length == 0) {
            return true;
        } else {
            for (int i = 0; i < length; ++i) {
                if (!isBase64(arrayOctect[i])) {
                    return false;
                }
            }

            return true;
        }
    }

    public static byte[] encodeBase64(byte[] binaryData) {
        return encodeBase64(binaryData, false);
    }

    public static byte[] encodeBase64Chunked(byte[] binaryData) {
        return encodeBase64(binaryData, true);
    }

    public static byte[] decode(byte[] pArray) {
        return decodeBase64(pArray);
    }

    public static byte[] encodeBase64(byte[] binaryData, boolean isChunked) {
        int lengthDataBits = binaryData.length * 8;
        int fewerThan24bits = lengthDataBits % 24;
        int numberTriplets = lengthDataBits / 24;
        byte[] encodedData = (byte[]) null;
        boolean encodedDataLength = false;
        int nbrChunks = 0;
        int var21;
        if (fewerThan24bits != 0) {
            var21 = (numberTriplets + 1) * 4;
        } else {
            var21 = numberTriplets * 4;
        }

        if (isChunked) {
            nbrChunks = CHUNK_SEPARATOR.length == 0 ? 0 : (int) Math.ceil((double) ((float) var21 / 76.0F));
            var21 += nbrChunks * CHUNK_SEPARATOR.length;
        }

        encodedData = new byte[var21];
        boolean k = false;
        boolean l = false;
        boolean b1 = false;
        boolean b2 = false;
        boolean b3 = false;
        int encodedIndex = 0;
        boolean dataIndex = false;
        boolean i = false;
        int nextSeparatorIndex = 76;
        int chunksSoFar = 0;

        byte val2;
        byte val1;
        byte var23;
        byte var22;
        byte var25;
        byte var24;
        int var27;
        int var28;
        for (var27 = 0; var27 < numberTriplets; ++var27) {
            var28 = var27 * 3;
            var25 = binaryData[var28];
            var24 = binaryData[var28 + 1];
            byte var26 = binaryData[var28 + 2];
            var23 = (byte) (var24 & 15);
            var22 = (byte) (var25 & 3);
            val1 = (var25 & -128) == 0 ? (byte) (var25 >> 2) : (byte) (var25 >> 2 ^ 192);
            val2 = (var24 & -128) == 0 ? (byte) (var24 >> 4) : (byte) (var24 >> 4 ^ 240);
            byte val3 = (var26 & -128) == 0 ? (byte) (var26 >> 6) : (byte) (var26 >> 6 ^ 252);
            encodedData[encodedIndex] = lookUpBase64Alphabet[val1];
            encodedData[encodedIndex + 1] = lookUpBase64Alphabet[val2 | var22 << 4];
            encodedData[encodedIndex + 2] = lookUpBase64Alphabet[var23 << 2 | val3];
            encodedData[encodedIndex + 3] = lookUpBase64Alphabet[var26 & 63];
            encodedIndex += 4;
            if (isChunked && encodedIndex == nextSeparatorIndex) {
                System.arraycopy(CHUNK_SEPARATOR, 0, encodedData, encodedIndex, CHUNK_SEPARATOR.length);
                ++chunksSoFar;
                nextSeparatorIndex = 76 * (chunksSoFar + 1) + chunksSoFar * CHUNK_SEPARATOR.length;
                encodedIndex += CHUNK_SEPARATOR.length;
            }
        }

        var28 = var27 * 3;
        if (fewerThan24bits == 8) {
            var25 = binaryData[var28];
            var22 = (byte) (var25 & 3);
            val1 = (var25 & -128) == 0 ? (byte) (var25 >> 2) : (byte) (var25 >> 2 ^ 192);
            encodedData[encodedIndex] = lookUpBase64Alphabet[val1];
            encodedData[encodedIndex + 1] = lookUpBase64Alphabet[var22 << 4];
            encodedData[encodedIndex + 2] = 61;
            encodedData[encodedIndex + 3] = 61;
        } else if (fewerThan24bits == 16) {
            var25 = binaryData[var28];
            var24 = binaryData[var28 + 1];
            var23 = (byte) (var24 & 15);
            var22 = (byte) (var25 & 3);
            val1 = (var25 & -128) == 0 ? (byte) (var25 >> 2) : (byte) (var25 >> 2 ^ 192);
            val2 = (var24 & -128) == 0 ? (byte) (var24 >> 4) : (byte) (var24 >> 4 ^ 240);
            encodedData[encodedIndex] = lookUpBase64Alphabet[val1];
            encodedData[encodedIndex + 1] = lookUpBase64Alphabet[val2 | var22 << 4];
            encodedData[encodedIndex + 2] = lookUpBase64Alphabet[var23 << 2];
            encodedData[encodedIndex + 3] = 61;
        }

        if (isChunked && chunksSoFar < nbrChunks) {
            System.arraycopy(CHUNK_SEPARATOR, 0, encodedData, var21 - CHUNK_SEPARATOR.length, CHUNK_SEPARATOR.length);
        }

        return encodedData;
    }

    public static byte[] decodeBase64(byte[] base64Data) {
        base64Data = discardNonBase64(base64Data);
        if (base64Data.length == 0) {
            return new byte[0];
        } else {
            int numberQuadruple = base64Data.length / 4;
            byte[] decodedData = (byte[]) null;
            boolean b1 = false;
            boolean b2 = false;
            boolean b3 = false;
            boolean b4 = false;
            boolean marker0 = false;
            boolean marker1 = false;
            int encodedIndex = 0;
            boolean dataIndex = false;
            int i = base64Data.length;

            while (base64Data[i - 1] == 61) {
                --i;
                if (i == 0) {
                    return new byte[0];
                }
            }

            decodedData = new byte[i - numberQuadruple];

            for (i = 0; i < numberQuadruple; ++i) {
                int var18 = i * 4;
                byte var16 = base64Data[var18 + 2];
                byte var17 = base64Data[var18 + 3];
                byte var12 = base64Alphabet[base64Data[var18]];
                byte var13 = base64Alphabet[base64Data[var18 + 1]];
                byte var14;
                if (var16 != 61 && var17 != 61) {
                    var14 = base64Alphabet[var16];
                    byte var15 = base64Alphabet[var17];
                    decodedData[encodedIndex] = (byte) (var12 << 2 | var13 >> 4);
                    decodedData[encodedIndex + 1] = (byte) ((var13 & 15) << 4 | var14 >> 2 & 15);
                    decodedData[encodedIndex + 2] = (byte) (var14 << 6 | var15);
                } else if (var16 == 61) {
                    decodedData[encodedIndex] = (byte) (var12 << 2 | var13 >> 4);
                } else if (var17 == 61) {
                    var14 = base64Alphabet[var16];
                    decodedData[encodedIndex] = (byte) (var12 << 2 | var13 >> 4);
                    decodedData[encodedIndex + 1] = (byte) ((var13 & 15) << 4 | var14 >> 2 & 15);
                }

                encodedIndex += 3;
            }

            return decodedData;
        }
    }

    static byte[] discardWhitespace(byte[] data) {
        byte[] groomedData = new byte[data.length];
        int bytesCopied = 0;
        int packedData = 0;

        while (packedData < data.length) {
            switch (data[packedData]) {
                default:
                    groomedData[bytesCopied++] = data[packedData];
                case 9:
                case 10:
                case 13:
                case 32:
                    ++packedData;
            }
        }

        byte[] var4 = new byte[bytesCopied];
        System.arraycopy(groomedData, 0, var4, 0, bytesCopied);
        return var4;
    }

    static byte[] discardNonBase64(byte[] data) {
        byte[] groomedData = new byte[data.length];
        int bytesCopied = 0;

        for (int packedData = 0; packedData < data.length; ++packedData) {
            if (isBase64(data[packedData])) {
                groomedData[bytesCopied++] = data[packedData];
            }
        }

        byte[] var4 = new byte[bytesCopied];
        System.arraycopy(groomedData, 0, var4, 0, bytesCopied);
        return var4;
    }

    public static byte[] encode(byte[] pArray) {
        return encodeBase64(pArray, false);
    }

    public static String encode(String str) throws UnsupportedEncodingException {
        String baseStr = new String(encode(str.getBytes("UTF-8")));
        String tempStr = Digest.digest(str).toUpperCase();
        String result = tempStr + baseStr;
        return new String(encode(result.getBytes("UTF-8")));
    }

    public static String decode(String cryptoStr) throws UnsupportedEncodingException {
        if (cryptoStr.length() < 40) {
            return "";
        } else {
            try {
                String ex = new String(decode(cryptoStr.getBytes("UTF-8")));
                String result = ex.substring(40, ex.length());
                return new String(decode(result.getBytes("UTF-8")));
            } catch (ArrayIndexOutOfBoundsException var3) {
                return "";
            }
        }
    }

    public static byte[] decode2(String encoded) {
        if (encoded == null) {
            return null;
        } else {
            char[] base64Data = encoded.toCharArray();
            int len = removeWhiteSpace(base64Data);
            if (len % 4 != 0) {
                return null;
            } else {
                int numberQuadruple = len / 4;
                if (numberQuadruple == 0) {
                    return new byte[0];
                } else {
                    byte[] decodedData = (byte[]) null;
                    boolean b1 = false;
                    boolean b2 = false;
                    boolean b3 = false;
                    boolean b4 = false;
                    boolean d1 = false;
                    boolean d2 = false;
                    boolean d3 = false;
                    boolean d4 = false;
                    int i = 0;
                    int encodedIndex = 0;
                    int dataIndex = 0;

                    byte var17;
                    byte var19;
                    byte var18;
                    char var21;
                    byte var20;
                    char var23;
                    char var22;
                    char var24;
                    for (decodedData = new byte[numberQuadruple * 3]; i < numberQuadruple - 1; ++i) {
                        if (!isData(var21 = base64Data[dataIndex++]) || !isData(var23 = base64Data[dataIndex++]) || !isData(var22 = base64Data[dataIndex++]) || !isData(var24 = base64Data[dataIndex++])) {
                            return null;
                        }

                        var17 = base64Alphabet[var21];
                        var18 = base64Alphabet[var23];
                        var19 = base64Alphabet[var22];
                        var20 = base64Alphabet[var24];
                        decodedData[encodedIndex++] = (byte) (var17 << 2 | var18 >> 4);
                        decodedData[encodedIndex++] = (byte) ((var18 & 15) << 4 | var19 >> 2 & 15);
                        decodedData[encodedIndex++] = (byte) (var19 << 6 | var20);
                    }

                    if (isData(var21 = base64Data[dataIndex++]) && isData(var23 = base64Data[dataIndex++])) {
                        var17 = base64Alphabet[var21];
                        var18 = base64Alphabet[var23];
                        var22 = base64Data[dataIndex++];
                        var24 = base64Data[dataIndex++];
                        if (isData(var22) && isData(var24)) {
                            var19 = base64Alphabet[var22];
                            var20 = base64Alphabet[var24];
                            decodedData[encodedIndex++] = (byte) (var17 << 2 | var18 >> 4);
                            decodedData[encodedIndex++] = (byte) ((var18 & 15) << 4 | var19 >> 2 & 15);
                            decodedData[encodedIndex++] = (byte) (var19 << 6 | var20);
                            return decodedData;
                        } else {
                            byte[] tmp;
                            if (isPad(var22) && isPad(var24)) {
                                if ((var18 & 15) != 0) {
                                    return null;
                                } else {
                                    tmp = new byte[i * 3 + 1];
                                    System.arraycopy(decodedData, 0, tmp, 0, i * 3);
                                    tmp[encodedIndex] = (byte) (var17 << 2 | var18 >> 4);
                                    return tmp;
                                }
                            } else if (!isPad(var22) && isPad(var24)) {
                                var19 = base64Alphabet[var22];
                                if ((var19 & 3) != 0) {
                                    return null;
                                } else {
                                    tmp = new byte[i * 3 + 2];
                                    System.arraycopy(decodedData, 0, tmp, 0, i * 3);
                                    tmp[encodedIndex++] = (byte) (var17 << 2 | var18 >> 4);
                                    tmp[encodedIndex] = (byte) ((var18 & 15) << 4 | var19 >> 2 & 15);
                                    return tmp;
                                }
                            } else {
                                return null;
                            }
                        }
                    } else {
                        return null;
                    }
                }
            }
        }
    }

    private static boolean isWhiteSpace(char octect) {
        return octect == 32 || octect == 13 || octect == 10 || octect == 9;
    }

    private static boolean isData(char octect) {
        return octect < 255 && base64Alphabet[octect] != -1;
    }

    private static boolean isPad(char octect) {
        return octect == 61;
    }

    private static int removeWhiteSpace(char[] data) {
        if (data == null) {
            return 0;
        } else {
            int newSize = 0;
            int len = data.length;

            for (int i = 0; i < len; ++i) {
                if (!isWhiteSpace(data[i])) {
                    data[newSize++] = data[i];
                }
            }

            return newSize;
        }
    }
}
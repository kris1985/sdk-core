package com.deepexi.sdk.core;

/**
 * @author HuangTao
 * @version 1.0
 * @date 2020-02-21 2:48
 */

import sun.misc.BASE64Encoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * 签名工具
 */
public class SignUtil {

    //签名算法HmacSha256
    public static final String HMAC_SHA256 = "HmacSHA256";
    //编码UTF-8
    public static final String ENCODING = "UTF-8";
    //UserAgent
    public static final String USER_AGENT = "demo/aliyun/java";
    //换行符
    public static final String LF = "\n";
    //串联符
    public static final String SPE1 = ",";
    //示意符
    public static final String SPE2 = ":";
    //连接符
    public static final String SPE3 = "&";
    //赋值符
    public static final String SPE4 = "=";
    //问号符
    public static final String SPE5 = "?";
    //默认请求超时时间,单位毫秒
    public static final int DEFAULT_TIMEOUT = 1000;
    //参与签名的系统Header前缀,只有指定前缀的Header才会参与到签名中
    public static final String CA_HEADER_TO_SIGN_PREFIX_SYSTEM = "X-Ca-";

    //请求Header Accept
    public static final String HTTP_HEADER_ACCEPT = "Accept";
    //请求Body内容MD5 Header
    public static final String HTTP_HEADER_CONTENT_MD5 = "Content-MD5";
    //请求Header Content-Type
    public static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
    //请求Header UserAgent
    public static final String HTTP_HEADER_USER_AGENT = "User-Agent";
    //请求Header Date
    public static final String HTTP_HEADER_DATE = "Date";

    //签名Header
    public static final String X_CA_SIGNATURE = "X-Ca-Signature";
    //所有参与签名的Header
    public static final String X_CA_SIGNATURE_HEADERS = "X-Ca-Signature-Headers";
    //请求时间戳
    public static final String X_CA_TIMESTAMP = "X-Ca-Timestamp";
    //请求放重放Nonce,15分钟内保持唯一,建议使用UUID
    public static final String X_CA_NONCE = "X-Ca-Nonce";
    //APP KEY
    public static final String X_CA_KEY = "X-Ca-Key";
    /**
     * 计算签名
     *
     * @param secret APP密钥
     * @param method HttpMethod
     * @param path
     * @param headers
     * @param querys
     * @param bodys
     * @param signHeaderPrefixList 自定义参与签名Header前缀
     * @return 签名后的字符串
     */
    public static String sign(String secret, String method, String path,
                              Map<String, String> headers,
                              Map<String, String> querys,
                              Map<String, String> bodys,
                              List<String> signHeaderPrefixList) {
        try {
            Mac hmacSha256 = Mac.getInstance(HMAC_SHA256);
            byte[] keyBytes = secret.getBytes(ENCODING);
            hmacSha256.init(new SecretKeySpec(keyBytes, 0, keyBytes.length, HMAC_SHA256));
            BASE64Encoder encoder = new BASE64Encoder();
            return new String(encoder.encode(
                    hmacSha256.doFinal(buildStringToSign(method, path, headers, querys, bodys, signHeaderPrefixList)
                            .getBytes(ENCODING))));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 构建待签名字符串
     * @param method
     * @param path
     * @param headers
     * @param querys
     * @param bodys
     * @param signHeaderPrefixList
     * @return
     */
    private static String buildStringToSign(String method, String path,
                                            Map<String, String> headers,
                                            Map<String, String> querys,
                                            Map<String, String> bodys,
                                            List<String> signHeaderPrefixList) {
        StringBuilder sb = new StringBuilder();

        sb.append(method.toUpperCase()).append(LF);
        if (null != headers) {
            if (null != headers.get(HTTP_HEADER_ACCEPT)) {
                sb.append(headers.get(HTTP_HEADER_ACCEPT));
            }
            sb.append(LF);
            if (null != headers.get(HTTP_HEADER_CONTENT_MD5)) {
                sb.append(headers.get(HTTP_HEADER_CONTENT_MD5));
            }
            sb.append(LF);
            if (null != headers.get(HTTP_HEADER_CONTENT_TYPE)) {
                sb.append(headers.get(HTTP_HEADER_CONTENT_TYPE));
            }
            sb.append(LF);
            if (null != headers.get(HTTP_HEADER_DATE)) {
                sb.append(headers.get(HTTP_HEADER_DATE));
            }
        }
        sb.append(LF);
        sb.append(buildHeaders(headers, signHeaderPrefixList));
        sb.append(buildResource(path, querys, bodys));

        return sb.toString();
    }

    /**
     * 构建待签名Path+Query+BODY
     *
     * @param path
     * @param querys
     * @param bodys
     * @return 待签名
     */
    private static String buildResource(String path, Map<String, String> querys, Map<String, String> bodys) {
        StringBuilder sb = new StringBuilder();

        if (!StringUtil.isBlank(path)) {
            sb.append(path);
        }
        Map<String, String> sortMap = new TreeMap<String, String>();
        if (null != querys) {
            for (Map.Entry<String, String> query : querys.entrySet()) {
                if (!StringUtil.isBlank(query.getKey())) {
                    sortMap.put(query.getKey(), query.getValue());
                }
            }
        }

        if (null != bodys) {
            for (Map.Entry<String, String> body : bodys.entrySet()) {
                if (!StringUtil.isBlank(body.getKey())) {
                    sortMap.put(body.getKey(), body.getValue());
                }
            }
        }

        StringBuilder sbParam = new StringBuilder();
        for (Map.Entry<String, String> item : sortMap.entrySet()) {
            if (!StringUtil.isBlank(item.getKey())) {
                if (0 < sbParam.length()) {
                    sbParam.append(SPE3);
                }
                sbParam.append(item.getKey());
                if (!StringUtil.isBlank(item.getValue())) {
                    sbParam.append(SPE4).append(item.getValue());
                }
            }
        }
        if (0 < sbParam.length()) {
            sb.append(SPE5);
            sb.append(sbParam);
        }

        return sb.toString();
    }

    /**
     * 构建待签名Http头
     *
     * @param headers 请求中所有的Http头
     * @param signHeaderPrefixList 自定义参与签名Header前缀
     * @return 待签名Http头
     */
    private static String buildHeaders(Map<String, String> headers, List<String> signHeaderPrefixList) {
        StringBuilder sb = new StringBuilder();

        if (null != signHeaderPrefixList) {
            signHeaderPrefixList.remove(X_CA_SIGNATURE);
            signHeaderPrefixList.remove(HTTP_HEADER_ACCEPT);
            signHeaderPrefixList.remove(HTTP_HEADER_CONTENT_MD5);
            signHeaderPrefixList.remove(HTTP_HEADER_CONTENT_TYPE);
            signHeaderPrefixList.remove(HTTP_HEADER_DATE);
            Collections.sort(signHeaderPrefixList);
            if (null != headers) {
                Map<String, String> sortMap = new TreeMap<String, String>();
                sortMap.putAll(headers);
                StringBuilder signHeadersStringBuilder = new StringBuilder();
                for (Map.Entry<String, String> header : sortMap.entrySet()) {
                    if (isHeaderToSign(header.getKey(), signHeaderPrefixList)) {
                        sb.append(header.getKey());
                        sb.append(SPE2);
                        if (!StringUtil.isBlank(header.getValue())) {
                            sb.append(header.getValue());
                        }
                        sb.append(LF);
                        if (0 < signHeadersStringBuilder.length()) {
                            signHeadersStringBuilder.append(SPE1);
                        }
                        signHeadersStringBuilder.append(header.getKey());
                    }
                }
                headers.put(X_CA_SIGNATURE_HEADERS, signHeadersStringBuilder.toString());
            }
        }

        return sb.toString();
    }

    /**
     * Http头是否参与签名 return
     */
    private static boolean isHeaderToSign(String headerName, List<String> signHeaderPrefixList) {
        if (StringUtil.isBlank(headerName)) {
            return false;
        }

        if (headerName.startsWith(CA_HEADER_TO_SIGN_PREFIX_SYSTEM)) {
            return true;
        }

        if (null != signHeaderPrefixList) {
            for (String signHeaderPrefix : signHeaderPrefixList) {
                if (headerName.equalsIgnoreCase(signHeaderPrefix)) {
                    return true;
                }
            }
        }

        return false;
    }
}

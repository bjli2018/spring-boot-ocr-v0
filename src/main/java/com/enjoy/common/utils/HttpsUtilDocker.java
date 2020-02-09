package com.enjoy.common.utils;

import java.io.*;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.alibaba.fastjson.JSON;
 
public class HttpsUtilDocker {
 
    private static final class DefaultTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
 
        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
 
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
    		
    public static void main(String[] args) throws Exception{
        String strURL = "https://192.168.43.42:8444/ocr/pdfToText";
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("inputFilePath", "/root/ocr/testfile/sin_1.pdf");
        paramMap.put("outputFilePath", "/root/ocr/testfile/sin_6_result.txt");
        paramMap.put("language", "sin");
        paramMap.put("tessPath", "/usr/bin");
        OutputStreamWriter out = null;
        InputStream is = null;
        SSLContext ctx = null;
        BufferedReader in = null;// 读取响应输入流  
        String result = "";// 返回的结果  
        try {
            ctx = SSLContext.getInstance("TLS");
            ctx.init(new KeyManager[0], new TrustManager[] { new DefaultTrustManager() }, new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        SSLSocketFactory ssf = ctx.getSocketFactory();
        try {
        	URL url = new URL(strURL);// 创建连接
        	HttpsURLConnection httpsConn = (HttpsURLConnection) url.openConnection();
        	httpsConn.setSSLSocketFactory(ssf);
        	httpsConn.setRequestMethod("POST"); // 设置请求方式
        	httpsConn.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式
        	httpsConn.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式
            httpsConn.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
            httpsConn.setDoInput(true);
            httpsConn.setDoOutput(true);
            httpsConn.connect();
            out = new OutputStreamWriter(httpsConn.getOutputStream(), "UTF-8"); // utf-8编码
            out.append(JSON.toJSONString(paramMap));
            out.flush();
            
            // 读取响应
            is = httpsConn.getInputStream();
            // 定义BufferedReader输入流来读取URL的响应，设置编码方式    
            in = new BufferedReader(new InputStreamReader(httpsConn    
                    .getInputStream(), "UTF-8"));    
            String line;    
            // 读取返回的内容    
            while ((line = in.readLine()) != null) {    
                result += line;    
            }
            System.out.println("主机返回:" + result);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
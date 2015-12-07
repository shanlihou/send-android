package com.example.send;

import android.util.Log;
import org.apache.http.util.ByteArrayBuffer;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Created by root on 15-11-23.
 */
public class HandleUrl {
    private HandleUrl(){
    }
    private static HandleUrl instance = new HandleUrl();
    public static HandleUrl getInstance() {
        return instance;
    }

    public  String urlOpen(String strUrl){
        String result = "";
        try {
            URL url = new URL(strUrl);
            HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
//            urlConn.setSSLSocketFactory(sslcontext.getSocketFactory());
            urlConn.setRequestMethod("GET");
            urlConn.setDoOutput(false);
            urlConn.setDoInput(true);

            //    urlConn.setUseCaches(false);
            urlConn.connect();
            String encoding = urlConn.getHeaderField("Content-encoding");
            if (encoding != null && encoding.equals("gzip")){
                int num;
                byte[] tmp = new byte[4096];
                ByteArrayBuffer bt = new ByteArrayBuffer(4096);
                GZIPInputStream gis = new GZIPInputStream(urlConn.getInputStream());
                while((num = gis.read(tmp)) != -1){
                    bt.append(tmp, 0, num);
                }
                result = new String(bt.toByteArray(), "utf-8");
                gis.close();
            }
            else{
                String readLine = null;
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                while((readLine = bufferedReader.readLine()) != null){
                    Log.d("shanlihou", "read len:" + readLine.length());
                    result += readLine;
                }
                bufferedReader.close();
            }

            urlConn.disconnect();

        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public String urlPostHttp(String strUrl, String data){
        String ret = "";
        Log.d("shanlihou", strUrl);
        try {
            URL url = new URL(strUrl);
            HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
            urlConn.setRequestMethod("POST");
            urlConn.setDoOutput(true);
            urlConn.setDoInput(true);
            urlConn.setUseCaches(false);
            urlConn.setRequestProperty("Content-Type", "application/json");
            urlConn.connect();
            DataOutputStream dop = new DataOutputStream(urlConn.getOutputStream());
            dop.writeBytes(data);
            dop.flush();
            dop.close();
            String encoding = urlConn.getHeaderField("Content-encoding");
            int retCode = urlConn.getResponseCode();
            if (encoding != null && encoding.equals("gzip")){
                int num;
                byte[] tmp = new byte[4096];
                ByteArrayBuffer bt = new ByteArrayBuffer(4096);
                GZIPInputStream gis;
                if (retCode != 200){
                    gis = new GZIPInputStream(urlConn.getErrorStream());
                }else {
                    gis = new GZIPInputStream(urlConn.getInputStream());
                }
                while((num = gis.read(tmp)) != -1){
                    bt.append(tmp, 0, num);
                }
                ret = new String(bt.toByteArray(), "utf-8");
                gis.close();
            }
            else{
                String readLine = "";
                BufferedReader bufferedReader;
                if (retCode != 200){
                    bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getErrorStream()));
                }else {
                    bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                }
                while((readLine = bufferedReader.readLine()) != null){
                    ret += readLine;
                }
                bufferedReader.close();
            }

            urlConn.disconnect();

        }catch(Exception e){
            e.printStackTrace();
        }
        Log.d("shanlihou", "ret:" + ret);
        return ret;
    }

    public String urlPost(String strUrl, String data){
        String result = "";
        Log.d("shanlihou", "data:" + data);
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
            public X509Certificate[] getAcceptedIssuers(){return null;}
            public void checkClientTrusted(X509Certificate[] certs, String authType){}
            public void checkServerTrusted(X509Certificate[] certs, String authType){}
        }};
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            URL url = new URL(strUrl);
            HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
//            urlConn.setSSLSocketFactory(sslcontext.getSocketFactory());
            urlConn.setRequestMethod("POST");
            urlConn.setDoOutput(true);
            urlConn.setDoInput(true);
            urlConn.setUseCaches(false);
            urlConn.setRequestProperty("Content-Type", "application/json");
            urlConn.connect();
            DataOutputStream dop = new DataOutputStream(urlConn.getOutputStream());
            dop.writeBytes(data);
            dop.flush();
            dop.close();
            String encoding = urlConn.getHeaderField("Content-encoding");
            int retCode = urlConn.getResponseCode();
            if (encoding != null && encoding.equals("gzip")){
                int num;
                byte[] tmp = new byte[4096];
                ByteArrayBuffer bt = new ByteArrayBuffer(4096);
                GZIPInputStream gis;
                if (retCode != 200){
                    gis = new GZIPInputStream(urlConn.getErrorStream());
                }else {
                    gis = new GZIPInputStream(urlConn.getInputStream());
                }
                while((num = gis.read(tmp)) != -1){
                    bt.append(tmp, 0, num);
                }
                result = new String(bt.toByteArray(), "utf-8");
                gis.close();
            }
            else{
                String readLine = null;
                BufferedReader bufferedReader;
                if (retCode != 200){
                    bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getErrorStream()));
                }else {
                    bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                }
                while((readLine = bufferedReader.readLine()) != null){
                    result += readLine;
                }
                bufferedReader.close();
            }

            urlConn.disconnect();

        }catch(Exception e){
            e.printStackTrace();
        }
        Log.d("shanlihou", result);
        return result;
    }
}

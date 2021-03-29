package com.example.vodafone_fu_h300s.logic;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class Η300sCredentialsRetriever {

    private String url;
    private String username;
    private String password;

    public Η300sCredentialsRetriever(String ip, String username, String password)
    {
        this.url="https://"+url;
        this.username = username;
        this.password = password;
    }



//    public function authenticateUser()
//    {
//        URL url = new URL(this.url+"/login.html");
//        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
//        try {
//            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//            readStream(in);
//        } catch (IOException e) {
//
//        } finally {
//            connecion.disconnect();
//        }
//    }

    public String retrieveCSRFToken(String htmlPageUrl) throws IOException {

//        URL url = new URL(htmlPageUrl);
//        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
//        try {
//            BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
//
//            String line;
//
//            Pattern p = Pattern.compile("var csrf_token = '.+'");
//            StringBuilder response = new StringBuilder();
//            byte[]
//            while(in.available()) {
//
//                Matcher m = p.matcher(line);
//                if(m.find()){
//                    return line;
//                }
//            }
//
//        } catch (IOException e) {
//            throw e;
//        } finally {
//            connection.disconnect();
//        }
//
//        return null;
        return "";
    }
}

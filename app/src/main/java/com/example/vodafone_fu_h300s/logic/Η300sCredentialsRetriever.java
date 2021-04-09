package com.example.vodafone_fu_h300s.logic;

import com.example.vodafone_fu_h300s.exceptions.CsrfTokenNotFound;
import com.example.vodafone_fu_h300s.exceptions.LoginFailed;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.net.ssl.HttpsURLConnection;

public class Η300sCredentialsRetriever {

    private String url;

    private OkHttpClient httpClient;

    public Η300sCredentialsRetriever(String ip)
    {
        this.url="https://"+url;
        this.setHttpClient(new OkHttpClient());

    }


    public void setHttpClient(OkHttpClient client)
    {
        this.httpClient = client;
    }

    public boolean login(String username, String password) {
        try{
            String token = this.retrieveCsrfTokenFromUrl("/login.html");
            String challengeJson = this.retrieveUrlContents("/login.json",token);

            Pattern challengeRegex = Pattern.compile("\"challenge\":\s*\"/*\" ");
            Matcher challengeMatch = challengeRegex.matcher(challengeJson);

            if(!challengeMatch.find()){
                return false;
            }

            String challenge = challengeMatch.group();
            challenge = challenge.replaceAll("\"|challenge|:","").trim();

            if(challenge.equals("")){
                return false;
            }


        } catch (Exception e){
            return false;
        }

        return true;
    }


    public String retrieveCSRFTokenFromHtml(String htmlPageUrl) throws IOException {

        if(htmlPageUrl.trim().equals("")){
           return "";
        }

        Pattern csrfRegex = Pattern.compile("var csrf_token\\s*=\\s*'.+'");
        Matcher match     = csrfRegex.matcher(htmlPageUrl);

        if(match.find()){
            String matched = match.group();
            matched=matched.replaceAll("var|csrf_token|'|\\s|=","");
            return matched;
        }
        return "";
    }

    public String retrieveUrlContents(String url, String csrfToken) throws Exception
    {
        url = this.url.replaceAll("/$","")+"/"+url.replaceAll("^/","");

        if(!csrfToken.equals("")){
            long unixtime = System.currentTimeMillis() / 1000L;
            // AJAX Calls also require to offer the _ with a unix timestamp alongside csrf token
            url+="?_="+unixtime+"&csrf_token="+csrfToken;
        }

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = this.httpClient.newCall(request).execute();

        int code = response.code();
        if( code != 200){
            throw new Exception("The url "+url+" returned code "+code);
        }

        return response.body().string();
    }

    public String retrieveUrlContents(String url) throws Exception
    {
       return retrieveUrlContents(url,"");
    }

    public String retrieveCsrfTokenFromUrl(String url) throws CsrfTokenNotFound {
        try {
            long unixTime = System.currentTimeMillis() / 1000L;
            String html = retrieveUrlContents(url);
            return retrieveCSRFTokenFromHtml(html);
        } catch (Exception e) {
            throw new CsrfTokenNotFound(url);
        }
    }
}

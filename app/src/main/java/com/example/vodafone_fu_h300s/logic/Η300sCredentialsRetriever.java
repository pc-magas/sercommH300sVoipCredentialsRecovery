package com.example.vodafone_fu_h300s.logic;

import com.example.vodafone_fu_h300s.logic.exceptions.CsrfTokenNotFound;
import com.example.vodafone_fu_h300s.logic.exceptions.SettingsFailedException;

import com.example.vodafone_fu_h300s.logic.lambdas.ExceptionHandler;
import com.example.vodafone_fu_h300s.logic.lambdas.LoginHandler;
import com.example.vodafone_fu_h300s.logic.lambdas.RetrieveSettingsHandler;
import com.example.vodafone_fu_h300s.logic.lambdas.SettingsRetrievalFailedHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.FormBody.Builder;

import java.security.MessageDigest;

public class Η300sCredentialsRetriever implements Runnable {

    private String url;

    private OkHttpClient httpClient;

    private String username;
    private String password;

    private LoginHandler loginHandler;
    private ExceptionHandler exceptionHandler;
    private RetrieveSettingsHandler settingsHandler;
    private SettingsRetrievalFailedHandler failedHandler;

    private String session_id;

    public Η300sCredentialsRetriever()
    {
        this.exceptionHandler = (Exception e)->{};
        this.loginHandler     = (boolean loginStatus)->{};
        this.settingsHandler  = (H300sVoipSettings settings)->{};
        this.failedHandler    = ()->{};

        this.setHttpClient(new OkHttpClient());
    }

    public void setSettingsHandler(RetrieveSettingsHandler handler){
        this.settingsHandler = handler;
    }

    public void setFailedHandler(SettingsRetrievalFailedHandler handler){
        this.failedHandler = handler;
    }

    public void setExceptionHandler(ExceptionHandler handler){
        this.exceptionHandler= handler;
    }

    public void setLoginHandler(LoginHandler handler){
        this.loginHandler=handler;
    }

    public void setUrl(String url){
        url = "http://"+url.replaceAll("http(s?)://","");
        this.url = url;
    }

    public String getUrl(){
        return this.url;
    }

    public void setHttpClient(OkHttpClient client)
    {
        this.httpClient = client;
    }

    public void setUsername(String username){
        this.username = username.trim();
    }

    public void setPassword(String password){
        this.password = password.trim();
    }

    public String getSessionId()
    {
        return this.session_id;
    }

    public String retrieveCSRFTokenFromHtml(String htmlPageUrl) throws IOException {

        if(htmlPageUrl==null || htmlPageUrl.trim().equals("")){
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

    public String retrieveUrlContents(String url, String csrfToken, String referer) throws Exception
    {
        url = this.url.replaceAll("/$","")+"/"+url.replaceAll("^/","");
        csrfToken=(csrfToken == null)?"":csrfToken;

        if(!csrfToken.equals("")){
            long unixtime = System.currentTimeMillis() / 1000L;
            // AJAX Calls also require to offer the _ with a unix timestamp alongside csrf token
            url+="?_="+unixtime+"&csrf_token="+csrfToken;
        }
        Request.Builder request = new Request.Builder()
                .url(url)
                .header("User-Agent","Mozila/5.0 (X11;Ubuntu; Linux x86_64; rv:87.0) Gecko/20100101 Firefox/87.0")
                .header("Accept","text/html,application/xhtml+html;application/xml;q=0.9,image/webp,*/*;q=0.8")
                .header("Upgrade-Insecure-Requests","1")
                .header("Sec-GPC","1");

        String session_id = this.getSessionId();
        session_id = session_id==null?"":session_id;

        if(!session_id.equals("")){
            request.header("Cookie","login_uid="+Math.random()+"; session_id="+session_id);
        }

        referer = (referer==null)?"":referer;

        if(!referer.trim().equals("")){
            request.header("Referer",referer);
        }

        Response response = this.httpClient.newCall(request.build()).execute();

        int code = response.code();
        if( code != 200){
            throw new Exception("The url "+url+" returned code "+code);
        }
        String responseBody = response.body().string();
        return responseBody;
    }

    public String retrieveUrlContents(String url, String csrfToken) throws Exception
    {
        return retrieveUrlContents(url,csrfToken,"");
    }

    public String retrieveUrlContents(String url) throws Exception {
       return retrieveUrlContents(url,"");
    }

    public String retrieveCsrfTokenFromUrl(String url,String referer) throws CsrfTokenNotFound {
        try {
            String html = retrieveUrlContents(url,"",referer);
            return retrieveCSRFTokenFromHtml(html);
        } catch (Exception e) {
            exceptionHandler.handle(e);
            throw new CsrfTokenNotFound(url);
        }
    }

    public boolean login() {

        if(
                username == null || username.equals("") ||
                password == null || password.equals("")){
            return false;
        }

        try {
            this.session_id = null;
            String token = this.retrieveCsrfTokenFromUrl("/login.html",null);

            if(token == null){
                return false;
            }

            if(token.trim().equals("")){
                return false;
            }

            String challengeJson = this.retrieveUrlContents("/data/login.json");

            if(challengeJson == null){
                return false;
            }

            if(challengeJson.trim().equals("")){
                return false;
            }

            JSONObject json = (JSONObject) (new JSONArray(challengeJson)).get(0);

            String challenge = json.getString("challenge");

            if(challenge == null){
                return false;
            }

            if(challenge.trim().equals("")){
                return false;
            }

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String stringToDigest = password+challenge;
            byte []hash = md.digest(stringToDigest.getBytes(StandardCharsets.UTF_8));

            BigInteger number = new BigInteger(1, hash);
            StringBuilder hexString = new StringBuilder(number.toString(16));

            String loginPwd = hexString.toString();


            RequestBody requestBody = new Builder()
                    .add("LoginName", username)
                    .add("LoginPWD", loginPwd)
                    .add("challenge",challenge)
                    .build();

            long unixTime = System.currentTimeMillis() / 1000L;
            Request request = new Request.Builder()
                    .url(this.url+"/data/login.json?_="+unixTime+"&csrfToken="+token)
                    .post(requestBody)
                    .header("Cookie","login_uid="+Math.random())
                    .header("Referer","http://192.158.2.1/login.html")
                    .header("X-Requested-With","XMLHttpRequest")
                    .header("Content-Type","application/x-www-form-urnencoded; charset=UTF-8")
                    .header("User-Agent","Mozila/5.0 (X11;Ubuntu; Linux x86_64; rv:87.0) Gecko/20100101 Firefox/87.0")
                    .header("Origin","http://192.168.2.1")
                    .build();

            Call call = this.httpClient.newCall(request);
            Response response = call.execute();

            String responseString = response.body().string();
            String cookies = response.header("Set-Cookie");
            if(cookies == null || cookies.trim().equals("")){
                return false;
            }
            cookies=cookies.replaceAll("path=/|session_id=|;","");
            this.session_id=cookies;
            return responseString.equals("1");
        } catch (Exception e){
            exceptionHandler.handle(e);
            return false;
        }
    }

    public H300sVoipSettings retrieveVOIPSettings()  throws Exception {
        H300sVoipSettings settings;

        String csrfToken = retrieveCsrfTokenFromUrl("/overview.html",this.url+"/login.html");

        if(csrfToken == null || csrfToken.trim().equals("")){
            throw new SettingsFailedException();
        }

        String contents = retrieveUrlContents("/data/phone_voip.json",csrfToken,this.url+"/phone.html");

        if(contents == null || contents.trim().equals("")){
            throw new SettingsFailedException();
        }

        try{
            settings = H300sVoipSettings.createFromJson(contents);
        } catch (Exception e){
            this.exceptionHandler.handle(e);
            throw new SettingsFailedException();
        }


        return settings;
    }

    public void run() {
        try {
            boolean loginStatus = login();
            loginHandler.loginCallback(loginStatus);
            if (loginStatus) {
                H300sVoipSettings settings = retrieveVOIPSettings();
                settingsHandler.retrieveSettings(settings);
            }
        } catch (SettingsFailedException f){
            failedHandler.handler();
            exceptionHandler.handle(f);
        } catch (Exception e){
            exceptionHandler.handle(e);
        }
    }
}

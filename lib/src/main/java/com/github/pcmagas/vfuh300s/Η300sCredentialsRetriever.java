package com.github.pcmagas.vfuh300s;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.ResponseBody;

import com.github.pcmagas.vfuh300s.exceptions.CsrfTokenNotFound;
import com.github.pcmagas.vfuh300s.exceptions.InvalidVersionException;
import com.github.pcmagas.vfuh300s.exceptions.SettingsFailedException;

import com.github.pcmagas.vfuh300s.lambdas.ExceptionHandler;
import com.github.pcmagas.vfuh300s.lambdas.LoginHandler;
import com.github.pcmagas.vfuh300s.lambdas.RetrieveSettingsHandler;
import com.github.pcmagas.vfuh300s.lambdas.SettingsRetrievalFailedHandler;

import java.io.File;
import java.io.FileOutputStream;
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

import java.security.MessageDigest;

public class Η300sCredentialsRetriever  implements Runnable {

    private String url;

    private OkHttpClient httpClient;

    private String username;
    private String password;

    private LoginHandler loginHandler;
    private ExceptionHandler exceptionHandler;
    private RetrieveSettingsHandler settingsHandler;
    private SettingsRetrievalFailedHandler failedHandler;

    private String session_id;

    public static final String ERROR_GENERIC = "GENERIC";
    public static final String ERROR_VERSION = "VERSION";

    public static final String REDIRECT_URL="<script>top.location.href=\"/login.html\";</script>";

    public static final String USER_AGENT="Mozilla/5.0 (Linux; Android 9) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.93 Mobile Safari/537.36";

    private File tempDir;

    public Η300sCredentialsRetriever()
    {
        this(null,null);
    }
    public Η300sCredentialsRetriever(File dirPath)
    {
        this(null,dirPath);
    }

    public Η300sCredentialsRetriever(OkHttpClient client, File dirPath)
    {
        this.exceptionHandler = (Exception e)->{};
        this.loginHandler     = (boolean loginStatus)->{};
        this.settingsHandler  = (H300sVoipSettings settings)->{};
        this.failedHandler    = (String type)->{};

//        if(client==null){
//            client = new OkHttpClient.Builder()
//                    .protocols(Arrays.asList(Protocol.HTTP_1_1))
//                    .readTimeout(40, TimeUnit.SECONDS)
//                    .connectTimeout(40, TimeUnit.SECONDS)
//                    .retryOnConnectionFailure(true)
//                    .build();
//        }

        this.setHttpClient(client);
        this.setTempDir(dirPath);
    }

    public void setTempDir(File path){
        this.tempDir = path;
    }

    public File getTempDir(){
        if(this.tempDir == null){
            return new File(System.getProperty("java.io.tmpdir"));
        }

        return this.tempDir;
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

        Pattern csrfRegex = Pattern.compile("csrf_token\\s*=\\s*'.+'");
        Matcher match     = csrfRegex.matcher(htmlPageUrl);

        if(match.find()){
            String matched = match.group();
            matched=matched.replaceAll("var|csrf_token|'|\\s|=","");

            return matched;
        }
        return "";
    }

    /**
     * Create the url with CSrf token and router's Ip
     * @param url The Url we want to visit
     * @param csrfToken The CSrftoken
     * @return The full url with Get parameters.
     */
    public String createUrl(String url,String csrfToken)
    {
        url = this.url.replaceAll("/$","")+"/"+url.replaceAll("^/","");
        csrfToken=(csrfToken == null)?"":csrfToken;

        if(!csrfToken.equals("")){
            long unixtime = System.currentTimeMillis() / 1000L;
            // AJAX Calls also require to offer the _ with a unix timestamp alongside csrf token
            url+="?_="+unixtime+"&csrf_token="+csrfToken;
        }
        return url;
    }

    /**
     * Common Bootstrapping for a request Both in GET and Post Method
     * @param url The url that we want to visit
     * @param csrfToken The Csrf token
     * @param referer Http referer
     * @return A request builder where we can append more stuff to it
     */
    public Request.Builder createBasicRequest(String url, String csrfToken, String referer)
    {
        url = createUrl(url,csrfToken);

        Request.Builder request = new Request.Builder()
                .url(url)
                .header("User-Agent",USER_AGENT)
                .header("Accept","text/html,application/xhtml+html;application/xml;q=0.9,image/webp,*/*;q=0.8")
                .header("Sec-GPC","1")
                .header("Connection", "close");

        // usually ajax needs csrf token
        if(csrfToken != null && csrfToken.trim() != ""){
            request.addHeader("X-Requested-With","XMLHttpRequest");
        }

        String session_id = this.getSessionId();
        session_id = session_id==null?"":session_id;

        if(!session_id.equals("")){
            request.header("Cookie","login_uid=0.7314905699843539; session_id="+session_id);
        }

        referer = (referer==null)?"":referer;

        if(!referer.trim().equals("")){
            request.header("Referer",referer);
        }

        return request;
    }

    /**
     * Retruevw url contents via Post
     * @param url The url we need to submit the data
     * @param csrfToken The Csrftoken
     * @param referer Url Referer
     * @param values Request Body
     * @return The response of the request
     * @throws Exception In case of an error
     */
    public ResponseBody retrieveUrlContentsViaPost(String url, String csrfToken, String referer, String values) throws Exception
    {
        Request.Builder request = this.createBasicRequest(url,csrfToken,referer);
        RequestBody body = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8"), values);
        request.post(body);
        request.header("X-Requested-With","XMLHttpRequest");
        Response response = this.httpClient.newCall(request.build()).execute();
        ResponseBody b = response.body();

        return b;
    }

    /**
     * Retrieve Url Contents via GET
     * @param url The url we need to submit the data
     * @param csrfToken The Csrftoken
     * @param referer Url Referer
     * @return
     * @throws Exception
     */
    public String retrieveUrlContents(String url, String csrfToken, String referer) throws Exception
    {
        Response response = this.retrieveUrlContentsNonString(url,csrfToken,referer);

        ResponseBody responseBody = response.body();

        return responseBody.string();
    }

    /**
     * Retrieve Url Contents via GET
     * @param url The url we need to submit the data
     * @param csrfToken The Csrftoken
     * @param referer Url Referer
     * @return
     * @throws Exception
     */
    public Response retrieveUrlContentsNonString(String url, String csrfToken, String referer) throws Exception
    {
        Request.Builder request = this.createBasicRequest(url,csrfToken,referer);

        Response response = this.httpClient.newCall(request.build()).execute();
        int code = response.code();

        if( code != 200){
            throw new Exception("The url "+url+" returned code "+code);
        }
        return response;
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

    public String retrieveCsrfTokenFromUrl(String url,String referer,String csrfToken) throws CsrfTokenNotFound {
        try {
            String html = retrieveUrlContents(url,csrfToken,referer);
            return retrieveCSRFTokenFromHtml(html);
        } catch (Exception e) {
            exceptionHandler.handle(e);
            throw new CsrfTokenNotFound(url);
        }
    }

    public boolean login() {

        if(username == null || username.equals("") || password == null || password.equals("")){
            return false;
        }

        try {

            String overviewPage = this.retrieveUrlContents("/overview.html");

            // Skip Login already logged in and valid session
            if(!overviewPage.replaceAll(" ","").equals(REDIRECT_URL)){
                return true;
            }

            this.session_id = null;

            String token = this.retrieveCsrfTokenFromUrl("/login.html",null);

            if(token == null || token.trim().equals("") ){
                return false;
            }

            String challengeJson = this.retrieveUrlContents("/data/login.json");

            if(challengeJson == null){
                return false;
            }

            if(challengeJson.trim().equals("")){
                return false;
            }

            String[] array = challengeJson.trim().split(",");
            Pattern challengeRegex = Pattern.compile("challenge");
            String challenge = "";
            for(String item : array){
                Matcher match = challengeRegex.matcher(item);
                if(match.find()){
                    item = item.replaceAll("\"challenge\":","")
                                .replace("}","")
                                .replace("{","")
                                .replace("[","")
                                .replace("]","")
                                .replace("\"","");
                    challenge = item.trim();
                    break;
                }
            }

            if(challenge == null || challenge.trim().equals("")){
                return false;
            }

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String stringToDigest = password+challenge;
            byte []hash = md.digest(stringToDigest.getBytes(StandardCharsets.UTF_8));

            BigInteger number = new BigInteger(1, hash);
            StringBuilder hexString = new StringBuilder(number.toString(16));

            String loginPwd = hexString.toString();


            RequestBody requestBody = new FormBody.Builder()
                    .add("LoginName", username)
                    .add("LoginPWD", loginPwd)
                    .add("challenge",challenge)
                    .build();


            long unixTime = System.currentTimeMillis() / 1000L;
            Request request = new Request.Builder()
                    .url(this.url+"/data/login.json?_="+unixTime+"&csrfToken="+token)
                    .post(requestBody)
                    .header("Referer",this.url+"/login.html")
                    .header("X-Requested-With","XMLHttpRequest")
                    .header("Content-Type","application/x-www-form-urlencoded; charset=UTF-8")
                    .header("User-Agent",USER_AGENT)
                    .header("Cookie","login_uid=0.7314905699843539")
                    .header("Origin",this.url)
                    .build();

            Call call = this.httpClient.newCall(request);
            Response response = call.execute();
            String responseString = response.body().string();
            if(responseString.equals("3")){
                return false;
            }

            String cookies = response.header("Set-Cookie");
            if(cookies == null || cookies.trim().equals("")){
                return false;
            }
            cookies=cookies.replaceAll("path=/|session_id=|;","");
            this.session_id=cookies;

            return true;
        } catch (Exception e){
            exceptionHandler.handle(e);
            return false;
        }
    }

    public H300sVoipSettings retrieveVOIPSettings()  throws Exception {
        H300sVoipSettings settings;

        // I visit the overview first and then voip_diagnostics so I can bypass any possible security feature
        String csrfToken = retrieveCsrfTokenFromUrl("/overview.html",this.url+"/login.html");

        if(csrfToken == null || csrfToken.trim().equals("")){
            throw new SettingsFailedException();
        }


        ResponseBody contents=null;
        String response = null;

        csrfToken = retrieveCsrfTokenFromUrl("/status-and-support.html",this.url+"/overview.html",csrfToken);

        this.retrieveUrlContentsNonString("data/reset_pages.json",csrfToken,this.url+"/status-and-support.html");

        contents = this.retrieveUrlContentsViaPost("data/login.json",csrfToken,this.url+"/status-and-support.html","loginUserChkLoginTimeout=admin");
        response = contents.string();

        if(response == null || !response.trim().equals("[ ]")){
            throw new SettingsFailedException();
        }

        csrfToken = retrieveCsrfTokenFromUrl("/statusandsupport/voip_diagnostics.html",this.url+"/overview.html",csrfToken);

        contents = this.retrieveUrlContentsViaPost("data/voip_diagnostics.json",csrfToken,this.url+"/status-and-support.html","download=all_info");
        response = contents.string();

        if(response == null || !response.trim().equals("1")){
            throw new SettingsFailedException();
        }

        Response downloadedTar = this.retrieveUrlContentsNonString("download/voip_diagnose_info.tar.gz",csrfToken,this.url+"/status-and-support.html");
        if (downloadedTar == null) {
            throw new SettingsFailedException();
        }
        
        File file = File.createTempFile(System.currentTimeMillis()+"_voip_diagnose_info", ".tar.gz", this.getTempDir());

        FileOutputStream download = new FileOutputStream(file);

        download.write(downloadedTar.body().bytes());
        download.flush();
        download.close();

        if(!file.exists() || (double)file.length() == 0){
            throw new SettingsFailedException();
        }
                try {
            settings = H300sVoipSettings.createFromTarGZ(file);
            file.delete();
        } catch (Exception e){
            this.exceptionHandler.handle(e);
            throw new SettingsFailedException();
        }

        return settings;
    }

    /**
     * Login + Voip Settings Retrieval.
     * The settings are retrieved via settingsHandler
     * The login is handled via loginCallback
     * Any exception is logged in exceptionHandler
     */
    public void retrieveVoipCredentials()
    {
        try {
            boolean loginStatus = login();
            loginHandler.loginCallback(loginStatus);
            if (loginStatus) {
                H300sVoipSettings settings = retrieveVOIPSettings();
                settingsHandler.retrieveSettings(settings);
            }
        } catch (SettingsFailedException f){
            failedHandler.handler(this.ERROR_GENERIC);
            exceptionHandler.handle(f);
        } catch(InvalidVersionException ve) {
            failedHandler.handler(this.ERROR_VERSION);
            exceptionHandler.handle(ve);
        } catch (Exception e){
            exceptionHandler.handle(e);
            failedHandler.handler(this.ERROR_GENERIC);
            exceptionHandler.handle(e);
        }
    }

    public void run(){
        this.retrieveVoipCredentials();
    }
}

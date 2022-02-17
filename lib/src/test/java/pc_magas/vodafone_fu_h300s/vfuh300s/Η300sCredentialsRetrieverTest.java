package com.github.pcmagas.vfuh300s;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import okhttp3.mock.Behavior;
import okhttp3.mock.MockInterceptor;
import okhttp3.mock.Rule;

import com.github.pcmagas.vfuh300s.exceptions.CsrfTokenNotFound;
import com.github.pcmagas.vfuh300s.exceptions.SettingsFailedException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class Η300sCredentialsRetrieverTest {

    private static OkHttpClient mockCsrfHttpRequest() throws IOException {
        File file = (new File("src/test/resources/csrfValid.html")).getAbsoluteFile();
        String path = file.getPath();
        System.out.println(path);
        Scanner fileReader = new Scanner(file);
        String contents = fileReader.useDelimiter("\\Z").next();

        return mockHttpClient(contents,false,200);
    }

    private static OkHttpClient mockHttpClient(final String serializedBody, final boolean json, int code) throws IOException {
        final OkHttpClient okHttpClient = mock(OkHttpClient.class);

        final Call remoteCall = mock(Call.class);

        code = code<0?200:code;

        final Response response = new Response.Builder()
                .request(new Request.Builder().url("http://url.com").build())
                .protocol(Protocol.HTTP_1_1)
                .code(code).message("").body(
                        ResponseBody.create(
                                MediaType.parse(json?"application/json":"text/html"),
                                serializedBody
                        ))
                .build();

        when(remoteCall.execute()).thenReturn(response);
        when(okHttpClient.newCall(any())).thenReturn(remoteCall);

        return okHttpClient;
    }

    private static OkHttpClient mockHttpClientWithSessionId(final String serializedBody, String url ,final boolean json, int code) throws IOException {
        OkHttpClient okHttpClient = mock(OkHttpClient.class);

        final Call remoteCall = mock(Call.class);

        code = code<0?200:code;

        final Response response = new Response.Builder()
                .request(new Request.Builder().url(url).build())
                .protocol(Protocol.HTTP_1_1)
                .code(code).message("").body(
                        ResponseBody.create(
                                MediaType.parse(json?"application/json":"text/html"),
                                serializedBody
                        ))
                .addHeader("Set-Cookie","session_id=dummysession;path=/")
                .build();

        when(remoteCall.execute()).thenReturn(response);
        when(okHttpClient.newCall(any())).thenReturn(remoteCall);

        return okHttpClient;
    }

    @Test
    public void testSetUrl()
    {
        Η300sCredentialsRetriever retriever = new Η300sCredentialsRetriever();
        retriever.setUrl("192.168.2.1");
        String expectedUrl = "http://192.168.2.1";

        Assert.assertEquals(expectedUrl, retriever.getUrl());
    }

    @Test
    public void testSetUrlHttps()
    {
        Η300sCredentialsRetriever retriever = new Η300sCredentialsRetriever();
        retriever.setUrl("https://192.168.2.1");
        String expectedUrl = "http://192.168.2.1";

        Assert.assertEquals(expectedUrl, retriever.getUrl());
    }

    @Test
    public void testSetUrlHttp()
    {
        Η300sCredentialsRetriever retriever = new Η300sCredentialsRetriever();
        retriever.setUrl("http://192.168.2.1");
        String expectedUrl = "http://192.168.2.1";

        Assert.assertEquals(expectedUrl, retriever.getUrl());
    }

    @Test
    public void retrieveCSRFTokenFromHtmlReturnsCSRFToken() throws IOException {

        File file = (new File("src/test/resources/csrfValid.html")).getAbsoluteFile();
        String path = file.getPath();
        Scanner fileReader = new Scanner(file);
        String contents = fileReader.useDelimiter("\\Z").next();

        Η300sCredentialsRetriever retriever = new Η300sCredentialsRetriever();
        retriever.setUrl("192.168.2.1");
        String expectedToken="HelloHowAreYou";

        String csrftoken = retriever.retrieveCSRFTokenFromHtml(contents);
        System.out.println(csrftoken);

        Assert.assertTrue(expectedToken.equals(csrftoken));
    }

    @Test
    public void retrieveCSRFTokenFromHtmlWithEmptyHtmlDoesNotReturnCsrfToken() throws IOException {
        Η300sCredentialsRetriever retriever = new Η300sCredentialsRetriever();
        String csrftoken = retriever.retrieveCSRFTokenFromHtml("");

        Assert.assertTrue(csrftoken.equals(""));
    }

    @Test
    public void retrieveCSRFTokenFromHtmlWithNoCsrfTokenInHtmlDoesNotReturnCsrfToken() throws IOException {
        File file = (new File("src/test/resources/csrfInvalid.html")).getAbsoluteFile();
        String path = file.getPath();
        Scanner fileReader = new Scanner(file);
        String contents = fileReader.useDelimiter("\\Z").next();

        Η300sCredentialsRetriever retriever = new Η300sCredentialsRetriever();
        String csrftoken = retriever.retrieveCSRFTokenFromHtml(contents);

        Assert.assertTrue(csrftoken.equals(""));
    }

    @Test
    public void testRetrieveUrlContents() throws Exception {
        File file = (new File("src/test/resources/csrfInvalid.html")).getAbsoluteFile();
        String path = file.getPath();
        Scanner fileReader = new Scanner(file);
        String contents = fileReader.useDelimiter("\\Z").next();
        OkHttpClient client = this.mockHttpClient(contents,false,200);

        Η300sCredentialsRetriever retriever = new Η300sCredentialsRetriever();
        retriever.setUrl("192.168.2.1");
        retriever.setHttpClient(client);

        String response = retriever.retrieveUrlContents("/example.html");
        Assert.assertTrue(contents.equals(response));

    }

    @Test
    public void retrieveCsrfTokenFromUrlOn404()throws IOException{
        OkHttpClient client = this.mockHttpClient("",false,404);

        Η300sCredentialsRetriever retriever = new Η300sCredentialsRetriever();
        retriever.setUrl("192.168.2.1");
        retriever.setHttpClient(client);

        Assert.assertThrows(Exception.class,()->{
            String response = retriever.retrieveUrlContents("/example.html");
        });
    }

    @Test
    public void retrieveCsrfTokenFromUrlOn500()throws IOException{
        OkHttpClient client = this.mockHttpClient("",false,500);

        Η300sCredentialsRetriever retriever = new Η300sCredentialsRetriever();
        retriever.setHttpClient(client);

        Assert.assertThrows(Exception.class,()->{
            String response = retriever.retrieveUrlContents("/example.html");
        });
    }

    @Test
    public void retrieveCsrfTokenFromUrl() throws IOException {
        OkHttpClient client = mockCsrfHttpRequest();
        String expectedToken="HelloHowAreYou";

        Η300sCredentialsRetriever retriever = new Η300sCredentialsRetriever();
        retriever.setUrl("192.168.2.1");
        retriever.setHttpClient(client);
        try{
            String csrftoken = retriever.retrieveCsrfTokenFromUrl("/example.html",null);
            Assert.assertTrue(expectedToken.equals(csrftoken));
        } catch (CsrfTokenNotFound e){
            Assert.fail("CSRF Token Not Found");
        }
    }

    @Test
    public void testLogin() throws CsrfTokenNotFound,Exception
    {
        final Η300sCredentialsRetriever retriever = spy(Η300sCredentialsRetriever.class);
        doReturn("HelloHowAreYou").when(retriever).retrieveCsrfTokenFromUrl("/login.html",null);
        doReturn("<script>top.location.href=\"/login.html\";</script>").when(retriever).retrieveUrlContents("/overview.html");
        doReturn("[{\"challenge\":\"Hello\"},{\"timeout\":0}]").when(retriever).retrieveUrlContents("/data/login.json");
        retriever.setUrl("192.168.2.1");

        final OkHttpClient okHttpClient = mockHttpClientWithSessionId("1","http://192.168.2.1/data/login.json",true,200);
        retriever.setHttpClient(okHttpClient);

        retriever.setUsername("admin");
        retriever.setPassword("1234");
        retriever.setExceptionHandler((Exception e)->{
            Assert.assertFalse(true);
        });
        boolean success = retriever.login();
        Assert.assertTrue(success);
        Assert.assertEquals("dummysession",retriever.getSessionId());
    }

    @Test
    public void testLoginWithoutCsrf() throws CsrfTokenNotFound,Exception
    {
        final Η300sCredentialsRetriever retriever = spy(Η300sCredentialsRetriever.class);
        doReturn("").when(retriever).retrieveCsrfTokenFromUrl("/login.html",null);
        doReturn("<script>top.location.href=\"/login.html\";</script>").when(retriever).retrieveUrlContents("/overview.html");
        doReturn("[{\"challenge\":\"Hello\"},{\"timeout\":0}]").when(retriever).retrieveUrlContents("/data/login.json");
        retriever.setUrl("192.168.2.1");

        final OkHttpClient okHttpClient = mockHttpClientWithSessionId("1","http://192.168.2.1/data/login.json",true,200);
        retriever.setHttpClient(okHttpClient);

        retriever.setUsername("admin");
        retriever.setPassword("1234");
        retriever.setExceptionHandler((Exception e)->{
            Assert.assertFalse(true);
        });
        boolean success = retriever.login();
        Assert.assertFalse(success);
        Assert.assertNotEquals("dummysession",retriever.getSessionId());
    }

    @Test
    public void testLoginWithoutChallengeOnWrongJson() throws CsrfTokenNotFound,Exception
    {
        final Η300sCredentialsRetriever retriever = spy(Η300sCredentialsRetriever.class);
        doReturn("HelloHowAreYou").when(retriever).retrieveCsrfTokenFromUrl("/login.html",null);
        doReturn("[]").when(retriever).retrieveUrlContents("/data/login.json");
        doReturn("<script>top.location.href=\"/login.html\";</script>").when(retriever).retrieveUrlContents("/overview.html");
        retriever.setUrl("192.168.2.1");

        final OkHttpClient okHttpClient = mockHttpClientWithSessionId("1","http://192.168.2.1/data/login.json",true,200);
        retriever.setHttpClient(okHttpClient);

        retriever.setUsername("admin");
        retriever.setPassword("1234");

        boolean success = retriever.login();
        Assert.assertFalse(success);
        Assert.assertNotEquals("dummysession",retriever.getSessionId());
    }

    @Test
    public void testLoginWithoutChallengeOnNonJson() throws CsrfTokenNotFound,Exception
    {
        final Η300sCredentialsRetriever retriever = spy(Η300sCredentialsRetriever.class);
        doReturn("HelloHowAreYou").when(retriever).retrieveCsrfTokenFromUrl("/login.html",null);
        doReturn("").when(retriever).retrieveUrlContents("/data/login.json");
        doReturn("<script>top.location.href=\"/login.html\";</script>").when(retriever).retrieveUrlContents("/overview.html");
        retriever.setUrl("192.168.2.1");

        final OkHttpClient okHttpClient = mockHttpClientWithSessionId("1","http://192.168.2.1/data/login.json",true,200);
        retriever.setHttpClient(okHttpClient);

        retriever.setUsername("admin");
        retriever.setPassword("1234");
        retriever.setExceptionHandler((Exception e)->{
            System.out.println(e.getMessage());
        });
        boolean success = retriever.login();
        Assert.assertFalse(success);
        Assert.assertNotEquals("dummysession",retriever.getSessionId());
    }

    @Test
    public void retrieveVoipSettings() throws FileNotFoundException, JSONException,CsrfTokenNotFound,IOException,Exception
    {
        File file = (new File("src/test/resources/voip_diagnose_info.tar.gz")).getAbsoluteFile();
        String path = file.getPath();
        System.out.println(path);
        byte[] data = Files.readAllBytes(file.toPath());

        File csrfResponse = (new File("src/test/resources/csrfValid.html")).getAbsoluteFile();
        Scanner fileReader = new Scanner(csrfResponse);
        String csrfContents = fileReader.useDelimiter("\\Z").next();

        H300sVoipSettings expectedSettings = H300sVoipSettings.createFromTarGZ(file);

        MockInterceptor interceptor = new MockInterceptor();
        interceptor.behavior(Behavior.UNORDERED)
                .addRule(new Rule.Builder()
                        .get()
                        .url("http://192.168.2.1/download/voip_diagnose_info.tar.gz?_=1234&csrf_token=HelloHowAreYou")
                        .respond(200)
                        .message("")
                        .protocol(Protocol.HTTP_1_1)
                        .body(ResponseBody.create(MediaType.parse("application/x-tar"),data))
                ).addRule(
                        new Rule.Builder()
                                .post()
                                .url("http://192.168.2.1/data/voip_diagnostics.json?_=1234&csrf_token=HelloHowAreYou")
                                .respond(200)
                                .message("")
                                .protocol(Protocol.HTTP_1_1)
                                .header("Referer","http://192.168.2.1/status-and-support.html")
                                .body(ResponseBody.create(MediaType.parse("application/json"),"1"))
                ).addRule(
                    new Rule.Builder().post()
                        .url("http://192.168.2.1/data/login.json?_=1234&csrf_token=HelloHowAreYou")
                        .respond(200)
                        .message("")
                        .protocol(Protocol.HTTP_1_1)
                        .header("Referer","http://192.168.2.1/status-and-support.html")
                        .body(ResponseBody.create(MediaType.parse("application/json"),"[ ]"))
                ).addRule(new Rule.Builder()
                    .get()
                    .url("http://192.168.2.1/overview.html")
                    .respond(200)
                    .message("")
                    .protocol(Protocol.HTTP_1_1)
                    .header("Referer","http://192.168.2.1/login.html")
                    .body(ResponseBody.create(MediaType.parse("text/html"),csrfContents))
                ).addRule(new Rule.Builder()
                        .get()
                        .url("http://192.168.2.1/status-and-support.html?_=1234&csrf_token=HelloHowAreYou")
                        .respond(200)
                        .message("")
                        .protocol(Protocol.HTTP_1_1)
                        .header("Referer","http://192.168.2.1/overview.html")
                        .body(ResponseBody.create(MediaType.parse("text/html"),csrfContents))

                ).addRule(new Rule.Builder()
                    .get()
                    .url("http://192.168.2.1/statusandsupport/voip_diagnostics.html?_=1234&csrf_token=HelloHowAreYou")
                    .respond(200)
                    .message("")
                    .protocol(Protocol.HTTP_1_1)
                    .header("Referer","http://192.168.2.1/overview.html")
                    .body(ResponseBody.create(MediaType.parse("text/html"),csrfContents))
                ).addRule(new Rule.Builder()
                    .get()
                    .url("http://192.168.2.1/data/reset_pages.json?_=1234&csrf_token=HelloHowAreYou")
                    .respond(200)
                    .message("")
                    .protocol(Protocol.HTTP_1_1)
                    .header("Referer","http://192.168.2.1/status-and-support.html")
                    .body(ResponseBody.create(MediaType.parse("application/json"),"[ { \"reset_pages\": \"\" } ]\n"))
                );

        final OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        final Η300sCredentialsRetriever retriever = new Η300sCredentialsRetriever(client,null){
            @Override
            public String createUrl(String url,String csrfToken)
            {
                url = this.getUrl().replaceAll("/$","")+"/"+url.replaceAll("^/","");
                csrfToken=(csrfToken == null)?"":csrfToken;

                if(!csrfToken.equals("")){
                    long unixtime = 1234;
                    // AJAX Calls also require to offer the _ with a unix timestamp alongside csrf token
                    url+="?_="+unixtime+"&csrf_token="+csrfToken;
                }
                return url;
            }
        };
        retriever.setUrl("192.168.2.1");

        H300sVoipSettings settings = retriever.retrieveVOIPSettings();
        System.out.println(settings.toString());
        Assert.assertTrue(expectedSettings.equals(settings));
    }

    @Test
    public void retrieveVoipSettingsMissingCSRFToken() throws FileNotFoundException, JSONException,CsrfTokenNotFound,IOException,Exception
    {
        File file = (new File("src/test/resources/voipSettings.json")).getAbsoluteFile();
        String path = file.getPath();
        System.out.println(path);
        Scanner fileReader = new Scanner(file);
        String contents = fileReader.useDelimiter("\\Z").next();

        final Η300sCredentialsRetriever retriever = spy(Η300sCredentialsRetriever.class);
        doReturn(null).when(retriever).retrieveCsrfTokenFromUrl("/overview.html","http://192.168.2.1/login.html");
        doReturn("fdsfsdfdsfsd").when(retriever).getSessionId();

        OkHttpClient client = mockHttpClient(contents,true,200);
        retriever.setHttpClient(client);
        retriever.setUrl("192.168.2.1");

        try {
            H300sVoipSettings settings = retriever.retrieveVOIPSettings();
            Assert.assertTrue(false);
        } catch(SettingsFailedException f){
            Assert.assertTrue(true);
        }
    }

    @Test
    public void retrieveVoipSettingsCSRFTokenEmptyString() throws FileNotFoundException, JSONException,CsrfTokenNotFound,IOException,Exception
    {
        File file = (new File("src/test/resources/voipSettings.json")).getAbsoluteFile();
        String path = file.getPath();
        System.out.println(path);
        Scanner fileReader = new Scanner(file);
        String contents = fileReader.useDelimiter("\\Z").next();

        final Η300sCredentialsRetriever retriever = spy(Η300sCredentialsRetriever.class);
        doReturn("").when(retriever).retrieveCsrfTokenFromUrl("/overview.html","http://192.168.2.1/login.html");
        doReturn("fdsfsdfdsfsd").when(retriever).getSessionId();

        OkHttpClient client = mockHttpClient(contents,true,200);
        retriever.setHttpClient(client);
        retriever.setUrl("192.168.2.1");

        try {
            H300sVoipSettings settings = retriever.retrieveVOIPSettings();
            Assert.assertTrue(false);
        } catch(SettingsFailedException f){
            Assert.assertTrue(true);
        }
    }
}

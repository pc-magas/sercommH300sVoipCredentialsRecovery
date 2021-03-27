package com.example.vodafone_fu_h300s.logic;

import java.net.CookieManager;
import java.net.HttpClient;

public class Η300sCredentialsRetriever {

    private CookieManager manager;
    HttpClient client;
    public H300sCredentialsRetriever(String url, String username, String password)
    {
        manager = new CookieManager();
        client = HttpClient.newBuilder().version(Version.HTTP_1_1).build();
    }

    piblic function 
}

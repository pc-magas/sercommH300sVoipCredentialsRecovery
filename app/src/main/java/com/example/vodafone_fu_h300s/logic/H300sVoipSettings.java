package com.example.vodafone_fu_h300s.logic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class H300sVoipSettings implements Serializable
{
    private String username = null;

    private String password = null;

    private String primary_registar =  null;

    private String primary_registar_port = null;

    private String secondary_registar = null;

    private String secondary_registar_port = null;

    private String primary_proxy = null;

    private String primary_proxy_port = null;

    private String secondary_proxy = null;

    private String secondary_proxy_port = null;

    private String sip_domain = null;

    private String sip_number = null;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPrimary_registar() {
        return primary_registar;
    }

    public void setPrimary_registar(String primary_registar) {
        this.primary_registar = primary_registar;
    }

    public String getPrimary_registar_port() {
        return primary_registar_port;
    }

    public void setPrimary_registar_port(String primary_registar_port) {
        this.primary_registar_port = primary_registar_port;
    }

    public String getSecondary_registar() {
        if(secondary_registar == null || secondary_registar.trim().equals("")){
            return null;
        }
        return secondary_registar;
    }

    public void setSecondary_registar(String secondary_registar) {
        this.secondary_registar = secondary_registar;
    }

    public String getSecondary_registar_port() {
        return secondary_registar_port;
    }

    public void setSecondary_registar_port(String secondary_registar_port) {
        this.secondary_registar_port = secondary_registar_port;
    }

    public String getPrimary_proxy() {
        return primary_proxy;
    }

    public void setPrimary_proxy(String primary_proxy) {
        this.primary_proxy = primary_proxy;
    }

    public String getPrimary_proxy_port() {
        return primary_proxy_port;
    }

    public void setPrimary_proxy_port(String primary_proxy_port) {
        this.primary_proxy_port = primary_proxy_port;
    }

    public String getSecondary_proxy() {
        return secondary_proxy;
    }

    public void setSecondary_proxy(String secondary_proxy) {
        this.secondary_proxy = secondary_proxy;
    }

    public String getSecondary_proxy_port() {
        return secondary_proxy_port;
    }

    public void setSecondary_proxy_port(String secondary_proxy_port) {
        this.secondary_proxy_port = secondary_proxy_port;
    }

    public String getSip_domain() {
        return sip_domain;
    }

    public void setSip_domain(String sip_domain) {
        this.sip_domain = sip_domain;
    }

    public String getSip_number() {
        return sip_number;
    }

    public void setSip_number(String sip_number) {
        this.sip_number = sip_number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static H300sVoipSettings createFromJson(String jsonString) throws IllegalArgumentException, JSONException {
        if(jsonString == null || jsonString.trim().equals("")){
            throw new IllegalArgumentException("JsonString Should not be empty");
        }

        JSONArray settingsJson = new JSONArray(jsonString);
        H300sVoipSettings settings = new H300sVoipSettings();

        for (int i = 0; i < settingsJson.length(); i++) {
            JSONObject item = settingsJson.getJSONObject(i);
            if(item.getString("type").equals("provider")){
                settings.setPrimary_registar(item.getString("primary_registrar"));
                settings.setPrimary_registar_port(item.getString("primary_registrar_port"));
                settings.setPrimary_proxy(item.getString("primary_proxy"));
                settings.setPrimary_proxy_port(item.getString("primary_proxy_port"));
                settings.setSip_domain(item.getString("sip_domain"));
                String secondary_proxy = item.getString("secondary_proxy");
                if(secondary_proxy != null && !secondary_proxy.trim().equals("")){
                    settings.setSecondary_proxy(secondary_proxy.trim());
                }
                settings.setSecondary_proxy_port(item.getString("secondary_proxy_port"));
                settings.setSecondary_registar(item.getString("secondary_registrar"));
                settings.setSecondary_registar_port(item.getString("secondary_registrar_port"));
            } else if(item.getString("type").equals("number")){
                settings.setSip_number(item.getString("sip_number"));
                settings.setUsername(item.getString("username"));
                settings.setPassword(item.getString("password"));
            }
        }

        return settings;
    }

    public boolean equals(H300sVoipSettings other){

        return other.getPassword().equals(this.getPassword()) &&
               other.getUsername().equals(this.getUsername()) &&
               other.getSip_number().equals(this.getSip_number()) &&
               other.getSip_domain().equals(this.getSip_domain()) &&
               other.getPrimary_proxy().equals(this.getPrimary_proxy()) &&
               other.getPrimary_proxy_port().equals(this.getPrimary_proxy_port()) &&
               other.getPrimary_registar().equals(this.getPrimary_registar()) &&
               other.getPrimary_registar_port().equals(this.getPrimary_registar_port()) &&
               other.getSecondary_proxy().equals(this.getSecondary_proxy()) &&
               other.getSecondary_proxy_port().equals(this.getSecondary_proxy_port()) &&
               other.getSecondary_registar().equals(this.getSecondary_registar()) &&
               other.getSecondary_registar_port().equals(this.getSecondary_registar_port());

    }
}

package com.example.vodafone_fu_h300s.logic;

public class H300sVoipSettings
{
    private String username;

    private String password;

    private String primary_registar;

    private String primary_registar_port;

    private String secondary_registar;

    private String secondary_registar_port;

    private String primary_proxy;

    private String primary_proxy_port;

    private String secondary_proxy;

    private String secondary_proxy_port;

    private String sip_domain;

    private String sip_port;

    private String sip_number;

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

    public String getSip_port() {
        return sip_port;
    }

    public void setSip_port(String sip_port) {
        this.sip_port = sip_port;
    }

    public String getSip_number() {
        return sip_number;
    }

    public void setSip_number(String sip_number) {
        this.sip_number = sip_number;
    }

    public static H300sVoipSettings createFromJson(String jsonString)
    {

    }
}

package com.example.vodafone_fu_h300s.logic.exceptions;

public class CsrfTokenNotFound extends Exception {
    public CsrfTokenNotFound(String url){
        super("The url "+url+" does not contain a csrfToken");
    }
}

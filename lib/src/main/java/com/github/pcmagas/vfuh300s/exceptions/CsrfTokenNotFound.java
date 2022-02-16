package com.github.pcmagas.vfuh300s.exceptions;

public class CsrfTokenNotFound extends Exception {
    public CsrfTokenNotFound(String url){
        super("The url "+url+" does not contain a csrfToken");
    }
}

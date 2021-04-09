package com.example.vodafone_fu_h300s.exceptions;

public class LoginFailed extends Exception {
    public LoginFailed(String reason){
        super("[Login Failed] reason: "+reason);
    }
}

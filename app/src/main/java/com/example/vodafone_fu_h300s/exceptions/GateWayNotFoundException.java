package com.example.vodafone_fu_h300s.exceptions;

public class GateWayNotFoundException extends Exception {
    public GateWayNotFoundException() {
        super("Gateway failes to be detected");
    }
}

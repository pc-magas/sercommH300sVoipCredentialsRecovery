package com.example.vodafone_fu_h300s;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.vodafone_fu_h300s.exceptions.FailedWiFiException;
import com.example.vodafone_fu_h300s.logic.H300sDetector;
import com.example.vodafone_fu_h300s.screens.InstructionsActivity;
import com.example.vodafone_fu_h300s.screens.ErrorActivity;

import static com.example.vodafone_fu_h300s.R.layout.activity_main;

public class MainActivity extends AppCompatActivity {

    public static final String ERROR_MESSAGE = "com.example.vodafone_fu_h300s.screens.ErrorActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);
        String ip = null;
        try {
            ip = H300sDetector.gateWayIp(this);

            if(ip == null){
                Log.e("DetectedIP","IP is NULL");
            } else {
                Log.i("DetectedIP",ip);
            }
            this.connectScreen(ip);
        } catch (FailedWiFiException e) {
            e.printStackTrace();
            Log.e("DetectedIP",e.getMessage());
            wiFiNotFound();
        }
    }

    protected void wiFiNotFound()
    {
        Intent error_msg_activity = new Intent(this, ErrorActivity.class);
        error_msg_activity.putExtra("error_message",this.getResources().getString(R.string.wifi_not_found));
        startActivity(error_msg_activity);
    }


    protected void connectScreen(String ip)
    {
        Intent instructions_extra = new Intent(this, InstructionsActivity.class);
        String router_url=(ip==null)?"":"https://"+ip;
        instructions_extra.putExtra("router_url",router_url);
        startActivity(instructions_extra);
    }
}
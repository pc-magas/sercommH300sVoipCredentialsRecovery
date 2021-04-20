package com.example.vodafone_fu_h300s.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.example.vodafone_fu_h300s.R;
import com.example.vodafone_fu_h300s.logic.H300sVoipSettings;

public class DisplaySettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_settings);

        H300sVoipSettings settings = (H300sVoipSettings) getIntent().getSerializableExtra("H300sVoipSettings");
        TextView phone = (TextView)findViewById(R.id.voip_phone);
        phone.setText(settings.getSip_number());

        TextView username = (TextView)findViewById(R.id.voip_username);
        username.setText(settings.getUsername());

        TextView password = (TextView)findViewById(R.id.voip_password);
        password.setText(settings.getPassword());



        TextView sip_domain = (TextView)findViewById(R.id.sip_domain);
        sip_domain.setText(settings.getSip_domain());

        TextView primary_registar = (TextView)findViewById(R.id.primary_registar);
        primary_registar.setText(settings.getPrimary_registar());

        TextView primary_registar_port = (TextView)findViewById(R.id.primary_registar_port);
        primary_registar_port.setText(settings.getPrimary_registar_port());

        TextView primary_proxy = (TextView)findViewById(R.id.primary_proxy);
        primary_proxy.setText(settings.getPrimary_proxy());

        TextView primary_proxy_port = (TextView)findViewById(R.id.primary_proxy_port);
        primary_proxy_port.setText(settings.getPrimary_proxy_port());

        String secondary_registar = settings.getSecondary_registar();
        if(secondary_registar== null || secondary_registar.trim().equals("")){
            TextView secondaryRegistar = (TextView)findViewById(R.id.secondary_registar);
            secondaryRegistar.setText(secondary_registar);
        }

        String secondary_registar_port = settings.getSecondary_registar_port();
        if(secondary_registar_port== null || secondary_registar_port.trim().equals("")){
            TextView secondaryRegistarPort = (TextView)findViewById(R.id.secondary_registar_port);
            secondaryRegistarPort.setText(secondary_registar_port);
        }

        String secondary_proxy = settings.getSecondary_proxy();
        if(secondary_proxy== null || secondary_proxy.trim().equals("")){
            TextView secondaryProxy = (TextView)findViewById(R.id.secondary_proxy);
            secondaryProxy.setText(secondary_proxy);
        }

        String secondary_proxy_port = settings.getSecondary_proxy_port();
        if(secondary_proxy_port== null || secondary_proxy_port.trim().equals("")){
            TextView secondaryProxyPort = (TextView)findViewById(R.id.secondary_proxy_port);
            secondaryProxyPort.setText(secondary_proxy_port);
        }

    }
}
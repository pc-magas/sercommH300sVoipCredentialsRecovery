package com.example.vodafone_fu_h300s.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.example.vodafone_fu_h300s.R;

public class ConnectIntoRouterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_into_router);
        Intent activityIntent = getIntent();
        String menu_url = activityIntent.getStringExtra("router_url");
        EditText url = (EditText)findViewById(R.id.menu_url);
        url.setText(menu_url);
        EditText admin = (EditText)findViewById(R.id.username);
        admin.setText("admin");
    }
}
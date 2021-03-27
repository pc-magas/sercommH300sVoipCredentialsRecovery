package com.example.vodafone_fu_h300s.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.vodafone_fu_h300s.R;

public class ErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        String error_message = getIntent().getStringExtra("error_message");
        TextView error_msg_view=(TextView)findViewById(R.id.error_message);
        error_msg_view.setText(error_message);
    }
}
package com.example.vodafone_fu_h300s.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.vodafone_fu_h300s.R;

public class InstructionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_instructions);
        ImageView img = (ImageView)findViewById(R.id.instructions_image);

        int imageResource = getResources().getIdentifier("@drawable/h300s",null,this.getPackageName());
        img.setImageResource(imageResource);
    }

    public void connectionForm(){

    }
}
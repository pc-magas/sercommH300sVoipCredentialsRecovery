package com.example.vodafone_fu_h300s.screens;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.vodafone_fu_h300s.R;
import com.example.vodafone_fu_h300s.logic.H300sDetector;
import com.example.vodafone_fu_h300s.logic.Η300sCredentialsRetriever;

public class ConnectIntoRouterActivity extends AppCompatActivity implements View.OnClickListener {

    Η300sCredentialsRetriever retriever;

    EditText url;
    EditText admin;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_into_router);

        Intent activityIntent = getIntent();

        String menu_url = activityIntent.getStringExtra("router_url");

        this.retriever = new Η300sCredentialsRetriever();

        this.retriever.setExceptionHandler((Exception e) -> {
            Log.e("Η300s",ConnectIntoRouterActivity.class+e.getMessage());
        });

        this.retriever.setLoginHandler((boolean loginStatus)->{
            if(!loginStatus){
                Log.e("Η300s",ConnectIntoRouterActivity.class+" Login Failed");
            }
        });


        this.url = (EditText)findViewById(R.id.menu_url);
        url.setText(menu_url);

        this.admin = (EditText)findViewById(R.id.username);
        admin.setText("admin");

        this.password = (EditText)findViewById(R.id.password);

        Button submit = (Button)findViewById(R.id.connect_btn);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){

        String menu_url = this.url.getText().toString();
        String admin = this.admin.getText().toString();
        String password = this.password.getText().toString();

        if(menu_url.equals("")||admin.equals("")||password.equals("")){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Νο url, username and password has been provided");
            alertDialogBuilder.setTitle("Input Error");
            AlertDialog alertDialog = alertDialogBuilder.create();

            alertDialog.show();

            return;
        }

        this.retriever.setUrl(menu_url);
        this.retriever.setUsername(admin);
        this.retriever.setPassword(password);

        Thread thread = new Thread(this.retriever);
        thread.start();
    }
}
package com.example.vodafone_fu_h300s.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.text.TextWatcher;

import com.example.vodafone_fu_h300s.R;
import com.example.vodafone_fu_h300s.logic.H300sVoipSettings;
import com.example.vodafone_fu_h300s.logic.Η300sCredentialsRetriever;

public class ConnectIntoRouterActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

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

        Button submit = (Button)findViewById(R.id.connect_btn);
        submit.setOnClickListener(this);

        this.retriever.setExceptionHandler((Exception e) -> {
            Log.e("Η300s",ConnectIntoRouterActivity.class+e.getMessage());
        });

        this.retriever.setLoginHandler((boolean loginStatus)->{
            if(!loginStatus){
                Log.e("Η300s",ConnectIntoRouterActivity.class+" Login Failed");
                submit.setEnabled(true);
            }
        });

        this.retriever.setSettingsHandler((H300sVoipSettings settings)->{
            submit.setEnabled(true);
        });

        this.url = (EditText)findViewById(R.id.menu_url);
        url.setText(menu_url);
        url.addTextChangedListener(this);

        this.admin = (EditText)findViewById(R.id.username);
        admin.setText("admin");
        admin.addTextChangedListener(this);

        this.password = (EditText)findViewById(R.id.password);
        password.addTextChangedListener(this);


    }

    private void onUpdateForm() {
        String menu_url = this.url.getText().toString();
        String admin = this.admin.getText().toString();
        String password = this.password.getText().toString();

        if(menu_url.equals("")||admin.equals("")||password.equals("")){
            Button submit = (Button)findViewById(R.id.connect_btn);
            submit.setEnabled(false);
            return;
        }

        Button submit = (Button)findViewById(R.id.connect_btn);
        submit.setEnabled(true);
        this.retriever.setUrl(menu_url);
        this.retriever.setUsername(admin);
        this.retriever.setPassword(password);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        onUpdateForm();
    }

    @Override
    public void onClick(View v){

        Button submit = (Button)findViewById(R.id.connect_btn);
        submit.setEnabled(false);

        Thread thread = new Thread(this.retriever);
        thread.start();
    }
}
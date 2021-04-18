package com.example.vodafone_fu_h300s.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

    private Η300sCredentialsRetriever retriever;

    private EditText url;
    private EditText admin;
    private EditText password;
    private Button submit;

    private Handler handler;

    private Thread retriever_thread;

    private static final int RETRIEVE_SETTINGS_MSG  = 100;
    private static final int LOGIN_FAILED_MSG       = 101;
    private static final int SETTINGS_FAILED_MSG    = 102;
    private static final int SETTINGS_RETRIEVED_MSG = 103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_into_router);

        Intent activityIntent = getIntent();

        String menu_url = activityIntent.getStringExtra("router_url");

        this.retriever = new Η300sCredentialsRetriever();

        submit = (Button)findViewById(R.id.connect_btn);
        submit.setOnClickListener(this);

        this.retriever.setExceptionHandler((Exception e) -> {
            Log.e("Η300s",ConnectIntoRouterActivity.class+e.getMessage());
        });

        this.retriever.setLoginHandler((boolean loginStatus)->{
            if(!loginStatus){
                Log.e("Η300s",ConnectIntoRouterActivity.class+" Login Failed");
                handler.sendEmptyMessage(LOGIN_FAILED_MSG);
            }
        });

        this.retriever.setSettingsHandler((H300sVoipSettings settings)->{
            Message message = new Message();
            message.what = SETTINGS_RETRIEVED_MSG;
            message.obj = settings;
            handler.sendMessage(message);
        });

        this.retriever.setFailedHandler(()->{
            handler.sendEmptyMessage(SETTINGS_FAILED_MSG);
        });

        retriever_thread = new Thread(this.retriever);

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
        submit.setEnabled(false);
        handler.sendEmptyMessage(RETRIEVE_SETTINGS_MSG);
    }

    protected void onResume() {
        super.onResume();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case RETRIEVE_SETTINGS_MSG:
                        retriever_thread.run();
                        break;
                    case LOGIN_FAILED_MSG:
                        submit.setEnabled(true);
                        break;
                    case SETTINGS_FAILED_MSG:
                        submit.setEnabled(true);
                        break;
                    case SETTINGS_RETRIEVED_MSG:
                        submit.setEnabled(true);
                        break;
                }
            }
        };
    }
}
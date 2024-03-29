package pc_magas.vodafone_fu_h300s.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.text.TextWatcher;
import android.widget.TextView;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

import pc_magas.vodafone_fu_h300s.R;
import com.github.pcmagas.vfuh300s.H300sVoipSettings;
import com.github.pcmagas.vfuh300s.Η300sCredentialsRetriever;

public class ConnectIntoRouterActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private Η300sCredentialsRetriever retriever;

    private EditText url;
    private EditText admin;
    private EditText password;
    private Button submit;
    private TextView error_message;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_into_router);

        Intent activityIntent = getIntent();
        String menu_url = activityIntent.getStringExtra("router_url");

        this.url = (EditText)findViewById(R.id.menu_url);
        url.setText(menu_url);
        url.addTextChangedListener(this);

        this.admin = (EditText)findViewById(R.id.username);
        admin.setText("admin");
        admin.addTextChangedListener(this);

        this.password = (EditText)findViewById(R.id.password);
        password.addTextChangedListener(this);

        this.error_message = (TextView)findViewById(R.id.error_message);


        OkHttpClient client = new OkHttpClient.Builder()
                .protocols(Arrays.asList(Protocol.HTTP_1_1))
                .readTimeout(40, TimeUnit.SECONDS)
                .connectTimeout(40, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(new Interceptor() {
                    @NonNull
                    @Override
                    public Response intercept(@NonNull Chain chain) throws IOException {
                        Request request = chain.request();

                        String log = "";
                        log+="\nRequest: "+request.method()+" "+request.url()+"\nRequest Headers:\n"+request.headers().toString()+"\n";
                        Response response = chain.proceed(request);
                        Log.d("Http",log);

                        return response;
                    }
                })
                .retryOnConnectionFailure(true).build();
        this.retriever = new Η300sCredentialsRetriever(client,this.getApplicationContext().getCacheDir());

        submit = (Button)findViewById(R.id.connect_btn);
        submit.setOnClickListener(this);

        this.retriever.setExceptionHandler((Exception e) -> {
            Log.e("Η300s",ConnectIntoRouterActivity.class+" Exception: "+e.getMessage());
            Log.e("Η300s",ConnectIntoRouterActivity.class+" Exception: "+ Log.getStackTraceString(e));

        });

        this.retriever.setLoginHandler((boolean loginStatus)->{
            if(!loginStatus){
                Log.e("Η300s",ConnectIntoRouterActivity.class+" Login Failed");
                handler.post(new Runnable() {
                    @Override

                    public void run() {
                        error_message.setText(getString(R.string.login_failed));
                        error_message.setVisibility(View.VISIBLE);
                        submit.setEnabled(true);
                    }
                });
            }
        });

        Intent settings_activity = new Intent(this, DisplaySettingsActivity.class);

        this.retriever.setSettingsHandler((H300sVoipSettings settings)->{
            handler.post(new Runnable() {
                @Override
                public void run() {
                    submit.setEnabled(true);
                    settings_activity.putExtra("H300sVoipSettings", settings);
                    startActivity(settings_activity);
                    finish();
                }
            });
        });

        this.retriever.setFailedHandler((String type)->{
            handler.post(new Runnable() {
                @Override
                public void run() {
                    switch(type){
                        case Η300sCredentialsRetriever.ERROR_VERSION:
                            error_message.setText(getString(R.string.unsupported_version));
                            break;
                        default:
                            error_message.setText(getString(R.string.settings_retrieval_failed));
                    }

                    error_message.setVisibility(View.VISIBLE);
                    submit.setEnabled(true);
                }
            });
        });
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
        error_message.setText("");
        error_message.setVisibility(View.INVISIBLE);
        Thread retriever_thread = new Thread(this.retriever);
        retriever_thread.start();
    }
}
package pc_magas.vodafone_fu_h300s.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.Date;

import pc_magas.vodafone_fu_h300s.R;
import pc_magas.vodafone_fu_h300s.logic.H300sVoipSettings;

public class DisplaySettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private H300sVoipSettings settings;

    Button saveIntoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_settings);

        this.settings = (H300sVoipSettings) getIntent().getSerializableExtra("H300sVoipSettings");
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
        if(secondary_registar!= null && !secondary_registar.trim().equals("")){
            TextView secondaryRegistar = (TextView)findViewById(R.id.secondary_registar);
            secondaryRegistar.setText(secondary_registar);
        }

        String secondary_registar_port = settings.getSecondary_registar_port();
        if(secondary_registar_port != null && secondary_registar_port.trim().equals("")){
            TextView secondaryRegistarPort = (TextView)findViewById(R.id.secondary_registar_port);
            secondaryRegistarPort.setText(secondary_registar_port);
        }

        String secondary_proxy = settings.getSecondary_proxy();
        if(secondary_proxy != null && !secondary_proxy.trim().equals("")){
            TextView secondaryProxy = (TextView)findViewById(R.id.secondary_proxy);
            secondaryProxy.setText(secondary_proxy);
        }

        String secondary_proxy_port = settings.getSecondary_proxy_port();
        if(secondary_proxy_port != null && !secondary_proxy_port.trim().equals("")){
            TextView secondaryProxyPort = (TextView)findViewById(R.id.secondary_proxy_port);
            secondaryProxyPort.setText(secondary_proxy_port);
        }

        this.saveIntoFile = (Button)findViewById(R.id.save);
        this.saveIntoFile.setOnClickListener(this);
    }

    private String saveFile(){
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            Log.e("H300s","Unable to detect external storage");
            return null;
        }
        Date date = new Date();
        File file = new File(getExternalFilesDir(null), "voip_h300s_"+date.toString()+".txt");
        try {
            file.createNewFile();
            Log.d("H300s","Saving");
            this.settings.save(file);
            Log.d("H300s","Saved");
            Log.d("H300s",file.getAbsolutePath());
            return file.getAbsolutePath();
        } catch (Exception e) {
            Log.e("H300s",e.toString());
            Log.e("H300s",Log.getStackTraceString(e));
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public void onClick(View v) {
        Log.d("H300s","Tapped");
        this.saveIntoFile.setEnabled(false);
        TextView msg = (TextView)findViewById(R.id.saveMsg);
        String savePath = saveFile();
        if(savePath == null){
            msg.setText(R.string.could_not_save_settings);
            msg.setBackgroundColor(R.color.error);
        } else {
            msg.setText(R.string.save_success);
            msg.setBackgroundColor(R.color.success);
        }
        msg.setVisibility(View.VISIBLE);
        this.saveIntoFile.setEnabled(true);
    }
}
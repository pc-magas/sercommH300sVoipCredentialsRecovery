package pc_magas.vodafone_fu_h300s.screens;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.activity.result.ActivityResultLauncher;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


import pc_magas.vodafone_fu_h300s.R;
import pc_magas.vodafone_fu_h300s.logic.H300sVoipSettings;

public class DisplaySettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private H300sVoipSettings settings;

    Button saveIntoFile;
    TextView msg;

    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_settings);

        msg = (TextView) findViewById(R.id.saveMsg);

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            Log.d("H300s","Permissions Callback");

            if (isGranted) {
                Log.d("H300s","Permission Accepted 2");
                saveFile();
            } else {
                permissionSaveDenied();
            }
        });

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

        TextView codec = (TextView)findViewById(R.id.codec);
        codec.setText(settings.getCodec());

        TextView fax_codec = (TextView)findViewById(R.id.fax_codec);
        fax_codec.setText(settings.getFax_codec());

        TextView dtml_mode = (TextView)findViewById(R.id.dtml_mode);
        dtml_mode.setText(settings.getDtml_mode());

        TextView packetization_time = (TextView)findViewById(R.id.packetization_time);
        packetization_time.setText(settings.getPacketization_time());

        TextView silence_suppression = (TextView)findViewById(R.id.silence_suppression);
        silence_suppression.setText(settings.getSilence_suppression());

        TextView ingress_gain = (TextView)findViewById(R.id.ingress_gain);
        ingress_gain.setText(settings.getIngress_gain());

        TextView engress_gain = (TextView)findViewById(R.id.engress_gain);
        engress_gain.setText(settings.getEgress_gain());

        this.saveIntoFile = (Button)findViewById(R.id.save);
        this.saveIntoFile.setOnClickListener(this);
    }

    private void saveFile(){
        Log.d("Η300s","Saving");
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            Log.e("H300s","Unable to detect external storage");
            saveMsgHandler(null);
            return;
        }

        this.saveIntoFile.setEnabled(false);

        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyMMdd");
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        file = new File( file.getAbsolutePath(),"voip_h300s_"+pattern.format(LocalDate.now())+".txt");
        Log.d("H300s",file.toString());
        try {
            file.createNewFile();
            Log.d("H300s","Saving");
            this.settings.save(file);
            Log.d("H300s","Saved");
            Log.d("H300s",file.getAbsolutePath());
            saveMsgHandler(file.getAbsolutePath());
        } catch (Exception e) {
            Log.e("H300s",e.toString());
            Log.e("H300s",Log.getStackTraceString(e));
            saveMsgHandler(null);
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }

    private void saveMsgHandler(String savePath){
        if (savePath == null) {
            msg.setText(R.string.could_not_save_settings);
            int errorColor = ContextCompat.getColor(this, R.color.error);
            msg.setBackgroundColor(errorColor);
        } else {
            String string = String.format(getString(R.string.save_success),savePath);
            msg.setText(string);
            int success = ContextCompat.getColor(this, R.color.success);
            msg.setBackgroundColor(success);
        }
        msg.setVisibility(View.VISIBLE);
        this.saveIntoFile.setEnabled(true);
    }

    private void permissionSaveDenied(){
        msg.setVisibility(View.VISIBLE);
        msg.setText(R.string.could_not_save_settings);
        int errorColor = ContextCompat.getColor(this, R.color.error);
        msg.setBackgroundColor(errorColor);
        this.saveIntoFile.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.d("H300s","Permission Accepted");
            saveFile();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE );
        }
    }
}
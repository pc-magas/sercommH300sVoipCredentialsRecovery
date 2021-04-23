package pc_magas.vodafone_fu_h300s.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import pc_magas.vodafone_fu_h300s.R;

public class MainScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        Intent detectIp = new Intent(this,DetectGateWayIPActivity.class);
        Button retrieve = (Button)findViewById(R.id.retrieve);
        retrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(detectIp);
            }
        });

        Intent licence = new Intent(this,LicenceActivity.class);
        Button licenceBtn = (Button)findViewById(R.id.licence);
        licenceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(licence);
            }
        });
    }
}
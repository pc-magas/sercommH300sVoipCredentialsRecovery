package pc_magas.vodafone_fu_h300s.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import pc_magas.vodafone_fu_h300s.R;

public class InstructionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_instructions);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            Log.e("GOBACK",e.getMessage());
        }

        final Button next = (Button)findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener(){
            private InstructionsActivity activity;

            public void onClick(View v){
                activity.connectionForm();
            }

            public View.OnClickListener init(InstructionsActivity activity){
                this.activity = activity;
                return this;
            }
        }.init(this));
    }

    public void connectionForm(){
        Intent connect_activity = new Intent(this, ConnectIntoRouterActivity.class);
        Intent thisActivityItnent = this.getIntent();
        String router_url = thisActivityItnent.getStringExtra("router_url");
        connect_activity.putExtra("router_url",router_url);
        startActivity(connect_activity);
    }
}
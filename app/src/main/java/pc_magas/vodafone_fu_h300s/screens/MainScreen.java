package pc_magas.vodafone_fu_h300s.screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.RouteInfo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.List;

import pc_magas.vodafone_fu_h300s.R;
import pc_magas.vodafone_fu_h300s.exceptions.FailedWiFiException;

public class MainScreen extends AppCompatActivity {

    Button retrieve;
    String ip = null;
    TextView msg = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        msg = (TextView)findViewById(R.id.usr_msg);

        retrieve = (Button)findViewById(R.id.retrieve);
        retrieve.setEnabled(false);

        retrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectScreen();
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


        try {
            Context context = getApplicationContext();
            Toast toast = Toast.makeText(context, getResources().getString(R.string.loading_msg), Toast.LENGTH_LONG);
            toast.show();

            ip = gateWayIp(this);
            if(ip == null){
                Log.e("H300s", MainScreen.class+" IP is NULL");
            } else {
                Log.i("Η300s", MainScreen.class+" DetectedIP: "+ip);
                displayIP();
            }

            retrieve.setEnabled(true);
        } catch (FailedWiFiException e) {
            Log.e("H300s", MainScreen.class+" "+e.getMessage());
            wiFiNotFound();
        }
    }

    protected void wiFiNotFound()
    {
        retrieve.setEnabled(false);
        msg.setVisibility(View.VISIBLE);
        msg.setText(R.string.wifi_not_found);
        int errorColor = ContextCompat.getColor(this, R.color.error);
        msg.setBackgroundColor(errorColor);
    }

    protected void displayIP()
    {
        msg.setVisibility(View.VISIBLE);
        msg.setText("Router IP: "+ip);
        int errorColor = ContextCompat.getColor(this, R.color.success);
        msg.setBackgroundColor(errorColor);
    }

    protected void connectScreen()
    {
        Intent instructions_extra = new Intent(this, InstructionsActivity.class);
        String router_url=(ip==null)?"":ip;
        instructions_extra.putExtra("router_url",router_url);
        startActivity(instructions_extra);
    }

    /**
     * Retrieves the gateway Ip from a connected Wi-FI
     *
     * @param context The activity Context
     * @throws FailedWiFiException
     *
     * @return An Object Containing the MAC address and the gateway Ip
     */
    public static String gateWayIp(Context context) throws FailedWiFiException {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) {
            throw new FailedWiFiException("Wi-Fi has not been detected");
        }

        Network connectedNetwork = cm.getActiveNetwork();
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());

        if (capabilities == null || !capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            throw new FailedWiFiException("Wi-Fi has not been detected");
        }

        LinkProperties linkProperties = cm.getLinkProperties(connectedNetwork);

        List<RouteInfo> routes = linkProperties.getRoutes();

        for(RouteInfo route : routes){
            InetAddress ip = route.getGateway();
            String ipString = ip.getHostAddress();
            if(ipString.trim().equals("0.0.0.0")|| ipString.replaceAll("\\s|:","").equals("")){
                continue;
            }
            Log.d("DetectedIP", ipString);
            return ipString;
        }

        return null;
    }
}
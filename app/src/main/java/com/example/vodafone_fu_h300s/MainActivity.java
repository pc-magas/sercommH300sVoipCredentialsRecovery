package com.example.vodafone_fu_h300s;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.RouteInfo;
import android.os.Bundle;
import android.util.Log;

import com.example.vodafone_fu_h300s.exceptions.FailedWiFiException;
import com.example.vodafone_fu_h300s.screens.InstructionsActivity;
import com.example.vodafone_fu_h300s.screens.ErrorActivity;

import java.net.InetAddress;
import java.util.List;

import static com.example.vodafone_fu_h300s.R.layout.activity_main;

public class MainActivity extends AppCompatActivity {

    public static final String ERROR_MESSAGE = "com.example.vodafone_fu_h300s.screens.ErrorActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);
        String ip = null;
        try {
            ip = gateWayIp(this);

            if(ip == null){
                Log.e("H300s",MainActivity.class+" IP is NULL");
            } else {
                Log.i("Η300s",MainActivity.class+" DetectedIP: "+ip);
            }
            this.connectScreen(ip);
        } catch (FailedWiFiException e) {
            e.printStackTrace();
            Log.e("H300s", MainActivity.class+" "+e.getMessage());
            wiFiNotFound();
        }
    }

    protected void wiFiNotFound()
    {
        Intent error_msg_activity = new Intent(this, ErrorActivity.class);
        error_msg_activity.putExtra("error_message",this.getResources().getString(R.string.wifi_not_found));
        startActivity(error_msg_activity);
    }


    protected void connectScreen(String ip)
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
    @SuppressWarnings("deprecation")
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

        String gatewayIp=null;

        for(RouteInfo route : routes){
            if(!route.hasGateway()) continue;
            InetAddress ip = route.getGateway();
            Log.e("DetectedIP",(route.hasGateway()?"GATEWAY TRUE ":"GATEWAY FALSE ")+ip.getHostAddress());
            return  ip.getHostAddress();
        }

        return null;
    }
}
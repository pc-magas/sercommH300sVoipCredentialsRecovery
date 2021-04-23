package pc_magas.vodafone_fu_h300s.screens;

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

import pc_magas.vodafone_fu_h300s.R;

import pc_magas.vodafone_fu_h300s.exceptions.FailedWiFiException;

import java.net.InetAddress;
import java.util.List;

import static pc_magas.vodafone_fu_h300s.R.layout.detect_ip;

public class DetectGateWayIPActivity extends AppCompatActivity {

    public static final String ERROR_MESSAGE = "com.example.vodafone_fu_h300s.screens.ErrorActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(detect_ip);
        String ip = null;
        try {
            ip = gateWayIp(this);

            if(ip == null){
                Log.e("H300s", DetectGateWayIPActivity.class+" IP is NULL");
            } else {
                Log.i("Η300s", DetectGateWayIPActivity.class+" DetectedIP: "+ip);
            }
            this.connectScreen(ip);
        } catch (FailedWiFiException e) {
            e.printStackTrace();
            Log.e("H300s", DetectGateWayIPActivity.class+" "+e.getMessage());
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
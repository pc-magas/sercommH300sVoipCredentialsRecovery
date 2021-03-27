package com.example.vodafone_fu_h300s.logic;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.RouteInfo;
import android.util.Log;

import com.example.vodafone_fu_h300s.exceptions.FailedWiFiException;

import java.net.InetAddress;
import java.util.List;

public class H300sDetector {

    /**
     * Retrieves the MAC Address and the Ip from a connected Wi-FI
     *
     * @param context
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

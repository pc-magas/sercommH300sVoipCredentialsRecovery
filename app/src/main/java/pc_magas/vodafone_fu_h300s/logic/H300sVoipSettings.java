package pc_magas.vodafone_fu_h300s.logic;

import android.util.Log;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.ini4j.Ini;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Date;

public class H300sVoipSettings implements Serializable
{
    private String username = null;

    private String password = null;

    private String primary_registar =  null;

    private String primary_registar_port = null;

    private String secondary_registar = null;

    private String secondary_registar_port = null;

    private String primary_proxy = null;

    private String primary_proxy_port = null;

    private String secondary_proxy = null;

    private String secondary_proxy_port = null;

    private String sip_domain = null;

    private String sip_number = null;

    private String local_port = null;

    private String codec = null;

    private String fax_codec = null;

    private String dtml_mode = null;

    private String packetization_time = null;

    private String silence_suppression = null;

    private String ingress_gain = null;

    private String egress_gain = null;

    private String echo_cancellation = null;

    private String rawSettings = null;


    public void setRawSettings(String rawSettings){
        this.rawSettings = rawSettings;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPrimary_registar() {
        return primary_registar;
    }

    public void setPrimary_registar(String primary_registar) {
        this.primary_registar = primary_registar;
    }

    public String getPrimary_registar_port() {
        return primary_registar_port;
    }

    public void setPrimary_registar_port(String primary_registar_port) {
        this.primary_registar_port = primary_registar_port;
    }

    public String getSecondary_registar() {
        if(secondary_registar == null || secondary_registar.trim().equals("")){
            return null;
        }
        return secondary_registar;
    }

    public void setSecondary_registar(String secondary_registar) {
        this.secondary_registar = secondary_registar;
    }

    public String getSecondary_registar_port() {
        return secondary_registar_port;
    }

    public void setSecondary_registar_port(String secondary_registar_port) {
        this.secondary_registar_port = secondary_registar_port;
    }

    public String getPrimary_proxy() {
        return primary_proxy;
    }

    public void setPrimary_proxy(String primary_proxy) {
        this.primary_proxy = primary_proxy;
    }

    public String getPrimary_proxy_port() {
        return primary_proxy_port;
    }

    public void setPrimary_proxy_port(String primary_proxy_port) {
        this.primary_proxy_port = primary_proxy_port;
    }

    public String getSecondary_proxy() {
        return secondary_proxy;
    }

    public void setSecondary_proxy(String secondary_proxy) {
        this.secondary_proxy = secondary_proxy;
    }

    public String getSecondary_proxy_port() {
        return secondary_proxy_port;
    }

    public void setSecondary_proxy_port(String secondary_proxy_port) {
        this.secondary_proxy_port = secondary_proxy_port;
    }

    public String getSip_domain() {
        return sip_domain;
    }

    public void setSip_domain(String sip_domain) {
        this.sip_domain = sip_domain;
    }

    public String getSip_number() {
        return sip_number;
    }

    public void setSip_number(String sip_number) {
        this.sip_number = sip_number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    public String getDtml_mode() {
        return dtml_mode;
    }

    public void setDtml_mode(String dtml_mode) {
        this.dtml_mode = dtml_mode;
    }

    public String getPacketization_time() {
        return packetization_time;
    }

    public void setPacketization_time(String packetization_time) {
        this.packetization_time = packetization_time;
    }

    public String getSilence_suppression() {
        return silence_suppression;
    }

    public void setSilence_suppression(String silence_suppression) {
        this.silence_suppression = silence_suppression;
    }

    public String getIngress_gain() {
        return ingress_gain;
    }

    public void setIngress_gain(String ingress_gain) {
        this.ingress_gain = ingress_gain;
    }

    public String getEgress_gain() {
        return egress_gain;
    }

    public void setEgress_gain(String egress_gain) {
        this.egress_gain = egress_gain;
    }

    public String getEcho_cancellation() {
        return echo_cancellation;
    }

    public void setEcho_cancellation(String echo_cancellation) {
        this.echo_cancellation = echo_cancellation;
    }

    public String getFax_codec() {
        return fax_codec;
    }

    public void setFax_codec(String fax_codec) {
        this.fax_codec = fax_codec;
    }

    public String getLocal_port() {
        return local_port;
    }

    public void setLocal_port(String local_port) {
        this.local_port = local_port;
    }

    public static H300sVoipSettings createFromJson(String jsonString) throws IllegalArgumentException, JSONException {
        if(jsonString == null || jsonString.trim().equals("")){
            throw new IllegalArgumentException("JsonString Should not be empty");
        }

        JSONArray settingsJson = new JSONArray(jsonString);
        H300sVoipSettings settings = new H300sVoipSettings();

        for (int i = 0; i < settingsJson.length(); i++) {
            JSONObject item = settingsJson.getJSONObject(i);
            if(item.getString("type").equals("provider")){
                settings.setPrimary_registar(item.getString("primary_registrar"));
                settings.setPrimary_registar_port(item.getString("primary_registrar_port"));
                settings.setPrimary_proxy(item.getString("primary_proxy"));
                settings.setPrimary_proxy_port(item.getString("primary_proxy_port"));
                settings.setSip_domain(item.getString("sip_domain"));
                String secondary_proxy = item.getString("secondary_proxy");
                if(secondary_proxy != null && !secondary_proxy.trim().equals("")){
                    settings.setSecondary_proxy(secondary_proxy.trim());
                }
                settings.setSecondary_proxy_port(item.getString("secondary_proxy_port"));
                settings.setSecondary_registar(item.getString("secondary_registrar"));
                settings.setSecondary_registar_port(item.getString("secondary_registrar_port"));
            } else if(item.getString("type").equals("number")){
                settings.setSip_number(item.getString("sip_number"));
                settings.setUsername(item.getString("username"));
                settings.setPassword(item.getString("password"));
                settings.setCodec(item.getString("codec"));
                settings.setDtml_mode(item.getString("dtml_mode"));
                settings.setFax_codec(item.getString("fax_codec"));
                settings.setPacketization_time(item.getString("packetization_time"));
                settings.setSilence_suppression(item.getString("silence_suppression"));
                settings.setIngress_gain(item.getString("ingress_gain"));
                settings.setEgress_gain(item.getString("egress_gain"));
                settings.setEcho_cancellation(item.getString("echo_cancellation"));
                settings.setLocal_port(item.getString("local_port"));
            }
        }

        return settings;
    }

    /**
     * Retrieve settings using method described in:
     * https://www.insomnia.gr/forums/topic/750707-h300s-%CE%BD%CE%AD%CE%BF-%CF%80%CF%81%CF%8C%CE%B3%CF%81%CE%B1%CE%BC%CE%BC%CE%B1-backup-voip-%CF%81%CF%85%CE%B8%CE%BC%CE%AF%CF%83%CE%B5%CF%89%CE%BD/?do=findComment&comment=58877078
     * @param targzFile
     * @return
     * @throws IOException
     */
    public static H300sVoipSettings createFromTarGZ(File targzFile) throws IOException, ArchiveException {

        H300sVoipSettings settings= new H300sVoipSettings();
        if(!targzFile.exists() || !targzFile.isFile()){
            throw new IllegalArgumentException("File "+targzFile.getName()+" is either not a file or does not exist");
        }

        String path = targzFile.getAbsoluteFile().getParent();
        File tmpFolder = new File(path);

        TarArchiveInputStream tarIn = (TarArchiveInputStream) new ArchiveStreamFactory().createArchiveInputStream("tar", new FileInputStream(targzFile));

        try {
            TarArchiveEntry entry;
            while ((entry = (TarArchiveEntry) tarIn.getNextEntry()) != null) {
                if(entry.isDirectory()){
                    continue;
                }

                String name = entry.getName();
                if (name.equals("tmp/var/voip_log/system_sh.conf")) {

                    StringBuffer data = new StringBuffer();

                    BufferedReader contents = new BufferedReader(new InputStreamReader(tarIn));
                    String line = "";
                    while((line = contents.readLine())!=null) {
                        if(line.matches("===CONF END===")){
                            continue;
                        }
                        data.append(line);
                        data.append(System.getProperty("line.separator"));
                    };

                    File tmpFile = File.createTempFile("sytem_sh","conf",tmpFolder);
                    BufferedWriter writeData = new BufferedWriter(new FileWriter(tmpFile));
                    writeData.write(data.toString());
                    writeData.flush();
                    writeData.close();

                    settings = readFromIni(tmpFile);
                    settings.setRawSettings(data.toString());

                    tmpFile.delete();
                    return settings;
                }
            }
        } catch(IOException e){
            throw e;
        } catch (Exception e) {
            throw e;
        }

        throw new RuntimeException("Tar gz does not contain the settings");
    }

    public static H300sVoipSettings readFromIni(File iniFile) throws IOException {
        H300sVoipSettings settings = new H300sVoipSettings();
        Ini ini = new Ini(iniFile);

        Ini.Section proxySettings = ini.get("sipreg-1");

        String proxy = proxySettings.get("proxy");
        settings.setPrimary_proxy(proxy.trim());

        String port = proxySettings.get("proxy_port");
        settings.setPrimary_proxy_port(port.trim());

        proxy = proxySettings.get("sec_proxy");
        settings.setSecondary_proxy(proxy);

        port = proxySettings.get("sec_proxy_port");
        settings.setSecondary_proxy_port(port);

        String registar = proxySettings.get("reg_proxy");
        settings.setPrimary_registar(registar);

        String registar_port = proxySettings.get("reg_proxy_port");
        settings.setPrimary_registar_port(registar_port);

        String sipDomain = proxySettings.get("reg_domain");
        settings.setSip_domain(sipDomain);

        Ini.Section phone_settings = ini.get("sip-line1");

        String password = phone_settings.get("pw");
        settings.setPassword(password);

        String username = phone_settings.get("user");
        settings.setUsername(username);

        String phoneNumber = phone_settings.get("telNo");
        settings.setSip_number(phoneNumber);

        String localPort = phone_settings.get("local_port");
        settings.setLocal_port(localPort);

        return settings;
    }

    public boolean equals(H300sVoipSettings other){

        boolean truth =  other.getPassword().equals(this.getPassword()) &&
                other.getUsername().equals(this.getUsername()) &&
                other.getSip_number().equals(this.getSip_number()) &&
                other.getSip_domain().equals(this.getSip_domain()) &&
                other.getPrimary_proxy().equals(this.getPrimary_proxy()) &&
                other.getPrimary_proxy_port().equals(this.getPrimary_proxy_port()) &&
                other.getPrimary_registar().equals(this.getPrimary_registar()) &&
                other.getPrimary_registar_port().equals(this.getPrimary_registar_port()) &&
                other.getSecondary_proxy_port().equals(this.getSecondary_proxy_port()) &&
                other.getSecondary_registar_port().equals(this.getSecondary_registar_port())&&
                other.getCodec().equals(this.getCodec()) &&
                other.getDtml_mode().equals(this.getDtml_mode()) &&
                other.getEcho_cancellation().equals(this.getEcho_cancellation()) &&
                other.getEgress_gain().equals(this.getEgress_gain()) &&
                other.getFax_codec().equals(this.getFax_codec()) &&
                other.getIngress_gain().equals(this.getIngress_gain()) &&
                other.getPacketization_time().equals(this.getPacketization_time()) &&
                other.getLocal_port().equals(this.getLocal_port());


        truth = truth && ((other.getSecondary_proxy() == null && this.getSecondary_proxy() == null) || (other.getSecondary_proxy().equals(this.getSecondary_proxy())));
        truth = truth &&
                (
                        (other.getSecondary_registar() == null && this.getSecondary_registar() == null) ||
                                (
                                        other.getSecondary_registar().equals(this.getSecondary_registar())
                                )
                );
        return truth;
    }

    public String toString()
    {
        StringBuilder txt = new StringBuilder();

            txt.append("Phone Number: ");
            txt.append(this.getSip_number());
            txt.append("\n");

            txt.append("Username: ");
            txt.append(this.getUsername());
            txt.append("\n");

            txt.append("Password: ");
            txt.append(this.getPassword());
            txt.append("\n");

            txt.append("Sip Domain: ");
            txt.append(this.getSip_domain());
            txt.append("\n");

            txt.append("Local Port: ");
            txt.append(this.getLocal_port());
            txt.append("\n");

            txt.append("Codec: ");
            txt.append(this.getCodec());
            txt.append("\n");

            txt.append("Fax Codec: ");
            txt.append(this.getFax_codec());
            txt.append("\n");

            txt.append("DTML Mode: ");
            txt.append(this.getDtml_mode());
            txt.append("\n");

            txt.append("Ingress Gain: ");
            txt.append(this.getIngress_gain());
            txt.append("\n");

            txt.append("Egress Gain: ");
            txt.append(this.getEgress_gain());
            txt.append("\n");

            txt.append("Silence Suppression: ");
            txt.append(this.getSilence_suppression());
            txt.append("\n");

            txt.append("Echo Cancellation: ");
            txt.append(this.getEcho_cancellation());
            txt.append("\n");

            txt.append("Primary Proxy: ");
            txt.append(this.getPrimary_proxy());
            txt.append(" Port: ");
            txt.append(this.getPrimary_proxy_port());
            txt.append("\n");

            txt.append("Secondary Proxy: ");
            String secondary_proxy = this.getSecondary_proxy();
            secondary_proxy = (secondary_proxy == null || !secondary_proxy.trim().equals(""))?"-":secondary_proxy;
            txt.append(secondary_proxy);
            txt.append(" Port: ");
            String secondaryProxyPort = this.getSecondary_proxy_port();
            secondaryProxyPort=(secondaryProxyPort == null || secondaryProxyPort.trim().equals(""))?"-":secondaryProxyPort;
            txt.append(secondaryProxyPort);
            txt.append("\n");

            txt.append("Primary Registar: ");
            String primaryRegistar = this.getPrimary_registar();
            txt.append(primaryRegistar);
            txt.append(" Port: ");
            String primaryRegistarPort = this.getPrimary_registar_port();
            txt.append(primaryRegistarPort);
            txt.append("\n");

            txt.append("Secondary Registar: ");
            String secondary_registar = this.getSecondary_registar();
            secondary_registar = (secondary_registar == null || secondary_registar.trim().equals(""))?"-":secondary_registar;
            txt.append(secondary_registar);
            txt.append(" Port: ");
            String secondaryRegistarPort = this.getSecondary_registar_port();
            secondaryRegistarPort=(secondaryRegistarPort == null || secondaryRegistarPort.trim().equals(""))?"-":secondaryRegistarPort;
            txt.append(secondaryRegistarPort);
            txt.append("\n");

        return txt.toString();
    }

    public void save(File file) throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(file));
        out.println("********");
        out.print("Exported Date: ");
        out.println(new Date().toString());
        out.println("********");
        out.print(this.toString());
        out.close();
    }
}

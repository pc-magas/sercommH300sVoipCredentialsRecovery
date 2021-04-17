package com.example.vodafone_fu_h300s.logic;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;

public class H300sVoipSettingsTest
{
    @Test
    public void testSerialize() throws JSONException, FileNotFoundException {
        File file = (new File("src/test/resources/voipSettings.json")).getAbsoluteFile();
        String path = file.getPath();
        System.out.println(path);
        Scanner fileReader = new Scanner(file);
        String contents = fileReader.useDelimiter("\\Z").next();

        H300sVoipSettings expectedSettings = H300sVoipSettings.createFromJson(contents);
        Map<String,String> values = expectedSettings.serialize();

        Assert.assertEquals("ngn.hol.net",values.get("primary_registar"));
        Assert.assertEquals("5060",values.get("primary_registar_port"));
        Assert.assertEquals("ngn.hol.net",values.get("primary_proxy"));
        Assert.assertEquals("5060",values.get("primary_proxy_port"));

        Assert.assertEquals("ngn.hol.net",values.get("sip_domain"));
        Assert.assertEquals("2016528654",values.get("sip_number"));
        Assert.assertEquals("2016528654@ngn.hol.net",values.get("username"));
        Assert.assertEquals("omaewamoushindeiru",values.get("password"));

        Assert.assertEquals("N/A",values.get("secondary_registar"));
        Assert.assertEquals("5060",values.get("secondary_registar_port"));

        Assert.assertEquals("N/A",values.get("secondary_proxy"));
        Assert.assertEquals("5060",values.get("secondary_proxy_port"));
    }
}

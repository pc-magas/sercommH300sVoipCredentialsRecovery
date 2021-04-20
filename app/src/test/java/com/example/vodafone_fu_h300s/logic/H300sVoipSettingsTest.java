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
    public void testcreateFromJson() throws JSONException, FileNotFoundException {
        File file = (new File("src/test/resources/voipSettings.json")).getAbsoluteFile();
        String path = file.getPath();
        System.out.println(path);
        Scanner fileReader = new Scanner(file);
        String contents = fileReader.useDelimiter("\\Z").next();

        H300sVoipSettings expectedSettings = H300sVoipSettings.createFromJson(contents);

        Assert.assertEquals("ngn.hol.net",expectedSettings.getPrimary_registar());
        Assert.assertEquals("5060",expectedSettings.getPrimary_registar_port());
        Assert.assertEquals("ngn.hol.net",expectedSettings.getPrimary_proxy());
        Assert.assertEquals("5060",expectedSettings.getPrimary_proxy_port());

        Assert.assertEquals("ngn.hol.net",expectedSettings.getSip_domain());
        Assert.assertEquals("2016528654",expectedSettings.getSip_number());
        Assert.assertEquals("2016528654@ngn.hol.net",expectedSettings.getUsername());
        Assert.assertEquals("omaewamoushindeiru",expectedSettings.getPassword());

        Assert.assertEquals(null,expectedSettings.getSecondary_registar());
        Assert.assertEquals("5060",expectedSettings.getSecondary_registar_port());

        Assert.assertEquals(null,expectedSettings.getSecondary_proxy());
        Assert.assertEquals("5060",expectedSettings.getSecondary_registar_port());
    }
}

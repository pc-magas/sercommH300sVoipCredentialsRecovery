package pc_magas.vodafone_fu_h300s.logic;

import org.apache.commons.compress.archivers.ArchiveException;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

public class H300sVoipSettingsTest
{
    @Test
    public void testcreateFromTarGz() throws JSONException, IOException, ArchiveException {

        File file = (new File("src/test/resources/voip_diagnose_info.tar.gz")).getAbsoluteFile();

        H300sVoipSettings expectedSettings = H300sVoipSettings.createFromTarGZ(file);

        Assert.assertEquals("ngn.hol.net",expectedSettings.getPrimary_registar());
        Assert.assertEquals("5060",expectedSettings.getPrimary_registar_port());
        Assert.assertEquals("ngn.hol.net",expectedSettings.getPrimary_proxy());
        Assert.assertEquals("5060",expectedSettings.getPrimary_proxy_port());

        Assert.assertEquals("ngn.hol.net",expectedSettings.getSip_domain());
        Assert.assertEquals("210000000",expectedSettings.getSip_number());
        Assert.assertEquals("210000000@ngn.hol.net",expectedSettings.getUsername());
        Assert.assertEquals("lipsum",expectedSettings.getPassword());

        Assert.assertEquals(null,expectedSettings.getSecondary_registar());
        Assert.assertEquals("",expectedSettings.getSecondary_registar_port());

        Assert.assertEquals("",expectedSettings.getSecondary_proxy());
        Assert.assertEquals("5060",expectedSettings.getSecondary_proxy_port());
    }
}

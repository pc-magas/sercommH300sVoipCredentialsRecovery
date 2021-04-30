package pc_magas.vodafone_fu_h300s.logic;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    @Test
    public void testToString() throws JSONException,FileNotFoundException {
        File file = (new File("src/test/resources/voipSettings.json")).getAbsoluteFile();
        Scanner fileReader = new Scanner(file);
        String contents = fileReader.useDelimiter("\\Z").next();

        File expectedOutputFile = (new File("src/test/resources/settingsTostring.txt")).getAbsoluteFile();
        Scanner expectedOutputFileReader = new Scanner(expectedOutputFile);
        String expectedOutput = expectedOutputFileReader.useDelimiter("\\Z").next()+"\n";

        H300sVoipSettings settings = H300sVoipSettings.createFromJson(contents);
        String output = settings.toString();
        Assert.assertTrue(expectedOutput.equals(output));
    }

//    @Test
//    public void testSave() throws IOException, JSONException {
//        TemporaryFolder folder= new TemporaryFolder();
//        folder.create();
//        File fileToSave = folder.newFile("test.txt");
//
//        File file = (new File("src/test/resources/voipSettings.json")).getAbsoluteFile();
//        Scanner fileReader = new Scanner(file);
//        String contents = fileReader.useDelimiter("\\Z").next();
//
//        File expectedOutputFile = (new File("src/test/resources/settingsTostring.txt")).getAbsoluteFile();
//        Scanner expectedOutputFileReader = new Scanner(expectedOutputFile);
//        String expectedOutput = expectedOutputFileReader.useDelimiter("\\Z").next()+"\n";
//
//        H300sVoipSettings settings = H300sVoipSettings.createFromJson(contents);
//        settings.save(file);
//        String settingsAsString = settings.toString();
//
//        Scanner savedContentsFile = new Scanner(fileToSave);
//        String savedContents = savedContentsFile.useDelimiter("\\Z").next();
//
//        Assert.assertTrue(savedContents.contains(settingsAsString));
//        Assert.assertTrue(savedContents.contains(expectedOutput));
//        Assert.assertTrue(savedContents.contains("Exported Date"));
//    }
}

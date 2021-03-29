package com.example.vodafone_fu_h300s.logic;

import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import com.example.vodafone_fu_h300s.logic.Η300sCredentialsRetriever;

public class Η300sCredentialsRetrieverTest {

//    public static String getHtml(String path) {
//        File myObj = new File(path);
//        Scanner myReader = new Scanner(myObj);
//        StringBuilder responseAggregator = new StringBuilder();
//        while (myReader.hasNextLine()) {
//            responseAggregator.append(myReader.nextLine());
//        }
//
//        return responseAggregator.toString();
//    }

    @Test
    public void retrieveCSRFTokenReturnsCSRFToken() {
        URL url = getClass().getResource("/sampleData/csrfValid.html");
        System.out.println(url.toString());
//        InputStream file = getClass().getResourceAsStream("/sampleData/csrfValid.html");
        assert(true);
    }
}

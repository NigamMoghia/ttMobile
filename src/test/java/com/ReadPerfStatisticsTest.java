package com;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import util.UpdateCredentialManager;

import java.util.HashMap;

public class ReadPerfStatisticsTest {

    ReadExportPerfStatisticsXml obj = new ReadExportPerfStatisticsXml();

    @BeforeClass
    public void startTest() {
        HashMap<String, String> credentialManagerParams = new HashMap<String, String>();
        credentialManagerParams.put("UserName", "sautomation");
        credentialManagerParams.put("Password", "Editorial$123");
        //credentialManagerParams.put("StagingPassword","Spring20!");
        credentialManagerParams.put("StagingPassword", "SA#utd16");
        UpdateCredentialManager.addOrEditCredential(credentialManagerParams);

        // method called in order to set the test output to a log file
        obj.setOutputToLogFile();
    }

    @AfterClass
    public void sendTestEmail(){
        obj.assignAllDynamicVariables();
        obj.sendTestOutputEmail();
    }

    @Test(enabled = true, priority = 0)
    public void readStatisticsXmlTest() {
        System.out.println("\n***** Reading Statistic.xml data *****\n");
        obj.readStatisticsXml("Production", "PR"); //QA,Dev,Production
    }

    @Test(enabled = true, priority = 1)
    public void getConBuildLogTest() {
        System.out.println("\n**** Reading build logs from conbld01p ****\n");
        obj.getBuildLog("Production_01p", "PR"); //QA,Dev,Production_01p

    }

    @Test(enabled = true, priority = 2)
    public void getExportTimeTest() {
        System.out.println("\n**** Reading Window Export time from DB ****\n");
        obj.getExportTime("Production", "PR");
    }

}

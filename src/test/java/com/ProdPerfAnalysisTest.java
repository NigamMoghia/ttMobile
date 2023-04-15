package com;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


public class ProdPerfAnalysisTest {

    ReadPerfAnalysisDB readPerfAnalysisDB = new ReadPerfAnalysisDB();
    EditorialPagesPerfAnalysis editorialPagesPerfAnalysis  = new EditorialPagesPerfAnalysis();


    private static final String emailRecipients = "Dhananjay.Tambe@wolterskluwer.com,Rithika.Gupta@wolterskluwer.com,Evgenia.Boitsev@wolterskluwer.com,Dhilip.Balasuriyan@wolterskluwer.com,Vikas.Anand@wolterskluwer.com,Jose.Ramirez@wolterskluwer.com,MDSaidul.Haque@wolterskluwer.com,Enrique.Callejon@wolterskluwer.com";

    private static final String environment = "Staging", topicID = "8400";

    @BeforeClass

    public void startTest() {
        readPerfAnalysisDB.setGetEnv("Staging");
        readPerfAnalysisDB.cleanTempTable();
         readPerfAnalysisDB.setGetEnv("Production");
            readPerfAnalysisDB.cleanTempTable();



        editorialPagesPerfAnalysis.setOutputToLogFile();
        editorialPagesPerfAnalysis.setUserNameAndPassword(environment);
    }


    @Test(enabled = true, priority = 0)
    public void readDataAfterDeploymentfromDB() throws Exception {

        // example :- readPerfAnalysisDB.getPerAnalysisDataFromDB("Production","82779");
        readPerfAnalysisDB.getPerAnalysisDataAfterDeploymentFromDB(environment,topicID);

        // example :- obj.getPerAnalysisDataFromDB("Production","82779");
            readPerfAnalysisDB.getPerAnalysisDataAfterDeploymentFromDB("Production","82779");


    }

    @Test(enabled = true, priority = 1)
    public void readDataBeforeDeploymentfromDB() throws Exception {

        // example :- readPerfAnalysisDB.getPerAnalysisDataBeforeDeploymentFromDB("Production","82779");
        readPerfAnalysisDB.getPerAnalysisDataBeforeDeploymentFromDB(environment,topicID);

        // example :- obj.getPerAnalysisDataBeforeDeploymentFromDB("Production","82779");
            readPerfAnalysisDB.getPerAnalysisDataBeforeDeploymentFromDB("Production","82779");

    }

    @Test(enabled = true, priority = 2)

    public void gatherWebpagesLoadTime() {
        // example :- editorialPagesPerfAnalysis.gatherWebPagesLoadTimeDataAndCompareWithPrevious("Production","82779");
        editorialPagesPerfAnalysis.gatherWebPagesLoadTimeDataAndCompareWithPrevious(environment, topicID);
    }
    @AfterClass
    public void emailPerfData() throws Exception {
                readPerfAnalysisDB.sendReportEmail("Editorial_Developer@uptodate.com");
        //obj.sendReportEmail("vikas.anand@wolterskluwer.com");

    }

}

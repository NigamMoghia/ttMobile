package com;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import util.ConnectToLinuxBox;
import util.ConnectToMySQLTestDB;
import util.GlobalVariables;
import util.ObjectRepository;

import javax.mail.MessagingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/*
 * Author: Vikas Anand
 *
 *
 * Purpose of the tool: Store the daily export process timings on DB.
 *
 *
 * Last update: Enrique Callejón /// 12-12-2022
 *
 *
 * NOTE: Before execution you must set the class variables to your corresponding values
 *
 * */

public class ReadExportPerfStatisticsXml {

    private static String env = null;
    private static String type = null;
    private String exportRegion = null;
    private String exportFolder = null;
    private String exportBeginDate_pr = null;
    private String xmlPath = null;
    private String manifestID = null;
    private String buildStart = null;
    private String buildEnd = null;
    private String total_export_time = null;
    private String content_build_start = null;
    private String content_build_end = null;
    private String content_build_total = null;
    private String exportLog_manifestId = null;
    private String zip_start_time = null;
    private String zip_end_time = null;
    private String total_time_zip = null;
    private String r1sync_manifestId = null;
    private String r1sync_start_time = null;
    private String r1sync_end_time = null;
    private String total_r1sync_time = null;
    private String test1_Start_Time = null;
    private String test1_End_Time = null;
    private String total_Test1_Time = null;
    private String test2_Start_Time = null;
    private String test2_End_Time = null;
    private String total_Test2_Time = null;
    private String test3_Start_Time = null;
    private String test3_End_Time = null;
    private String total_Test3_Time = null;

    private static final String outputLogFilePath = "C:\\uptodate\\Batch\\logs\\exportStatisticsReport\\exportStatisticsReportProcessLog.txt";


    // Email variables
    private final String emailRecipients = "Dhananjay.Tambe@wolterskluwer.com,Rithika.Gupta@wolterskluwer.com,Vikas.Anand@wolterskluwer.com,Enrique.Callejon@wolterskluwer.com";
    private String emailSubject = "${status} | Export performance statistics | ${date}", emailBody = "<b>Process</b>: Export performance statistics ${date} <br/> <b>Status:</b> ${status}";
    private String reportProcessStatus = "SUCCESS";



    public final PollForExportFolder p = new PollForExportFolder();


    // method called on the BeforeClass method in order to set the test output to a log file
    public void setOutputToLogFile() {

        /* NOTE, IMPORTANT!!
            This method will always be invoked before anything else right after class object instantiation
            otherwise the test will not store its output on the log file
        * */

        try {
            System.setOut(new PrintStream(new FileOutputStream(outputLogFilePath,true)));
        } catch (FileNotFoundException e) {
            System.out.println(e.getLocalizedMessage());
        }

    }

    public void setGetEnv(String env, String type) {
        try {

            System.out.println("Function - setGetEnv");

            p.setEnvironment(env);
            exportRegion = GlobalVariables.exportRegion;
            System.out.println("exportRegion := " + exportRegion);
            System.out.println(System.lineSeparator());
            
            p.findLatestManifest(type);
            
            exportFolder = GlobalVariables.pr_exportFolder;
            exportBeginDate_pr = GlobalVariables.pr_exportDate;
            manifestID = GlobalVariables.pr_manifestID;
            buildStart = GlobalVariables.pr_exportBenginDate;
            buildEnd = GlobalVariables.pr_exportEndDate;

        } catch (Exception e) {
            reportProcessStatus = "FAILED";
            System.out.println("Exception occurred while setting environment at " + getCurrentDateTimeForLog());
        }
    }

    public String getStatisticsXmlPath(String env, String type) {
        try {

            System.out.println("Function - getStatisticsXmlPath");

            setGetEnv(env, type);
            exportRegion = "\\\\" + exportRegion;
            xmlPath = exportRegion + exportFolder + "\\" + "statistics.xml";
            System.out.println("xmlPath ========== " + xmlPath);

            System.out.println(System.lineSeparator());

        } catch (Exception e) {
            reportProcessStatus = "FAILED";
            System.out.println("Exception occurred while getting statistics.xml path '" + xmlPath + "' at " + getCurrentDateTimeForLog());
        }
        return xmlPath;
    }

    public void readStatisticsXml(String env, String type) {

        System.out.println("Function - readStatisticsXml");

        try {

            File file = new File(getStatisticsXmlPath(env, type));

            // parse XML document and normalize it
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(file);
            doc.getDocumentElement().normalize();
            System.out.println("ManifestId: " + manifestID);

            // Get the root element
            Node elemNode;
            System.out.println("Root element: " + doc.getDocumentElement().getNodeName());

            // get all document 'Statistic' nodes
            NodeList nodeList = doc.getElementsByTagName("Statistic");
            System.out.println("'Statistics' nodes list by TagName length: " + nodeList.getLength());

            // declare and initialize array lists
            List<String> builderName = new ArrayList<>();
            List<String> totalTimeForPrep = new ArrayList<>();
            List<String> totalTimeForValidating = new ArrayList<>();
            List<String> totalTimeForBuilding = new ArrayList<>();
            List<String> finalTotalTimeTaken = new ArrayList<>();
            List<String> totalNumOfItem = new ArrayList<>();

            int buildItem = 0;

            // iterate over Statistics nodes
            for (int count = 0; count < nodeList.getLength(); count++) {
                elemNode = nodeList.item(count);


            // if the element has attributes, add the value to the corresponding array
                if (elemNode.hasAttributes()) {

                    String taskName = nodeList.item(count).getAttributes().getNamedItem("Task").getNodeValue();

                    if (taskName.equals("Prepare Manifest")) {
                        builderName.add(nodeList.item(count).getAttributes().getNamedItem("Builder").getNodeValue());
                        totalTimeForPrep.add(nodeList.item(count).getAttributes().getNamedItem("TotalSeconds").getNodeValue());
                    }

                    if (taskName.equals("Validating")) {
                        totalTimeForValidating.add(nodeList.item(count).getAttributes().getNamedItem("TotalSeconds").getNodeValue());
                    }

                    if (taskName.equals("Building Items")) {
                        totalTimeForBuilding.add(nodeList.item(count).getAttributes().getNamedItem("TotalSeconds").getNodeValue());

                        try {
                            totalNumOfItem.add(nodeList.item(count).getAttributes().getNamedItem("NumItems").getNodeValue());
                            buildItem++;
                        } catch (Exception e) {
                            System.out.println("NumItems not present hence setting value as zero  ====== " + builderName.get(buildItem));
                            totalNumOfItem.add("0");
                            buildItem++;

                        }
                    }
                }
            }

            System.out.println("BuilderName ====== " + builderName);
            System.out.println("TotalTimeForPrep ====== " + totalTimeForPrep);
            System.out.println("TotalTimeForValidating ====== " + totalTimeForValidating);
            System.out.println("TotalTimeForBuilding ====== " + totalTimeForBuilding);
            System.out.println("TotalNumOfItem ====== " + totalNumOfItem);


            // calculate builder total time and add it to the corresponding array
            for (int i = 0; i < builderName.size(); i++) {

                double totalTime = Double.parseDouble(totalTimeForPrep.get(i)) + Double.parseDouble(totalTimeForValidating.get(i)) +
                        Double.parseDouble(totalTimeForBuilding.get(i));

                DecimalFormat df = new DecimalFormat("#.##");
                totalTime = Double.parseDouble(df.format(totalTime));
                finalTotalTimeTaken.add(String.valueOf(totalTime));

            }

            System.out.println("FinalTotalTimeTaken ====== " + finalTotalTimeTaken);
            System.out.println("ExportDate_pr ====== " + exportBeginDate_pr);

            System.out.println("\n***** Saving Statistics data to Export_statistics_table TestDB *****\n");
            insertStatisticsDataInTestDB(manifestID, builderName, totalTimeForPrep, totalTimeForValidating, totalTimeForBuilding, finalTotalTimeTaken, exportBeginDate_pr, env, totalNumOfItem);

        } catch (Exception e) {
            reportProcessStatus = "FAILED";
            System.out.println(e.getLocalizedMessage());
            System.out.println("Exception occurred while reading statistics.xml at " + getCurrentDateTimeForLog());
        }

    }

    public void insertStatisticsDataInTestDB(String manifestID, List<String> builderName, List<String> totalTimeForPrep, List<String> totalTimeForValidating,
                                             List<String> totalTimeForBuilding, List<String> finalTotalTimeTaken, String exportBenginDate_pr, String exportRegion, List<String> totalNumOfItem) {

        System.out.println("Function - insertStatisticsDataInTestDB");

        try {

            // Create Connection to  DB
            Connection con = ConnectToMySQLTestDB.getInsstance().getMySqlConnection();

            // iterate over builders list and save all builders data one by one
            for (int i = 0; i < builderName.size(); i++) {

                PreparedStatement stmt = con.prepareStatement("INSERT INTO " + ObjectRepository.Export_statistics_table + " (Manifest_Id, Builder_Name, Prepare_Time, Validating_Time, Building_Time, Total_Time, Export_Date, Region, TotalNumOf_Items) VALUES (?,?,?,?,?,?,?,?,?)");
                stmt.setString(1, manifestID);
                stmt.setString(2, builderName.get(i));
                stmt.setString(3, totalTimeForPrep.get(i));
                stmt.setString(4, totalTimeForValidating.get(i));
                stmt.setString(5, totalTimeForBuilding.get(i));
                stmt.setString(6, finalTotalTimeTaken.get(i));
                stmt.setString(7, exportBenginDate_pr);
                stmt.setString(8, exportRegion);
                stmt.setString(9, totalNumOfItem.get(i));

                System.out.println("PreparedStatement = " + stmt.toString());
                // Execute the SQL Query
                stmt.executeUpdate();

            }

            System.out.println("***** Statistics data saved successfully in TestDB at " + getCurrentDateTimeForLog() + " *****");
            con.close();

            System.out.println(System.lineSeparator());

        } catch (Exception e) {
            reportProcessStatus = "FAILED";
            System.out.println(e.getLocalizedMessage());
            System.out.println("Exception occurred while inserting data in TestDB at " + getCurrentDateTimeForLog());
        }
    }

    public void getExportTime(String env, String type) {

        System.out.println("Function - getExportTime");


        try {

            setGetEnv(env, type);
            System.out.println("ManifestID is : = " + manifestID);
            System.out.println("Build Start Time : = " + buildStart + "\n" + "Build End Time : = " + buildEnd);

            long diffSeconds = getSecondsDurationBetweenTwoStringDates(buildStart, buildEnd, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

            total_export_time = Long.toString(diffSeconds);
            System.out.println("Total time taken for Build : = " + diffSeconds + "sec");


            System.out.println("\n***** Reading R1Sync logs *****\n");
            readR1SyncLogFile(env);


            System.out.println("\n***** Reading ExportJob logs *****\n");
            readExportJobLogFile(env);


            System.out.println("\n***** Saving Export data to Export_Window_statistics_table TestDB *****\n");

            if (exportLog_manifestId.equals(manifestID) && r1sync_manifestId.equals(manifestID)) {
                try {

                    Connection con = ConnectToMySQLTestDB.getInsstance().getMySqlConnection();

                    PreparedStatement stmt = con.prepareStatement("INSERT INTO " + ObjectRepository.Export_Window_statistics_table +" (Server_Name, Manifest_Id, Export_Start_Time, Export_End_Time, Export_Total_Time, Zip_Start_Time, Zip_End_Time, Zip_Total_Time, " +
                            "R1Sync_Publishing_Start_Time, R1Sync_Publishing_End_Time, R1Sync_Publishing_Total_Time, Region) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");

                    stmt.setString(1, exportRegion.replace("\\", ""));
                    stmt.setString(2, manifestID);
                    stmt.setString(3, buildStart);
                    stmt.setString(4, buildEnd);
                    stmt.setString(5, total_export_time);
                    stmt.setString(6, zip_start_time);
                    stmt.setString(7, zip_end_time);
                    stmt.setString(8, total_time_zip);
                    stmt.setString(9, r1sync_start_time);
                    stmt.setString(10, r1sync_end_time);
                    stmt.setString(11, total_r1sync_time);
                    stmt.setString(12, env);

                    System.out.println("PreparedStatement = " + stmt.toString());

                    // Execute the SQL Query
                    stmt.executeUpdate();
                    System.out.println("***** Export data saved successfully in TestDB at " + getCurrentDateTimeForLog() + " *****");

                    con.close();

                    System.out.println(System.lineSeparator());

                } catch (Exception e) {
                    reportProcessStatus = "FAILED";
                    System.out.println(e.getLocalizedMessage());
                    System.out.println("Exception occurred while inserting data in TestDB at " + getCurrentDateTimeForLog());
                }
            } else {
                reportProcessStatus = "FAILED";
                System.out.println("***** Export data save Failed at " + getCurrentDateTimeForLog() +" *****");
                System.out.println("Manifest Id didn't match with Export.log or r1synclog files \n");
            }
        } catch (Exception e) {
            reportProcessStatus = "FAILED";
            System.out.println(e.getLocalizedMessage());
            System.out.println("Exception occurred while getting build time at " + getCurrentDateTimeForLog());
        }

    }

    public void getBuildLog(String environment, String exportType) {

        System.out.println("Function - getBuildLog");

        try {

            // check input environment and apply corresponding value
            if (environment.contains("01p")) {
                env = environment.split("_")[0];
            } else {
                env = environment;
            }

            String path = null;
            setGetEnv(env, exportType);

            switch (env.toLowerCase()) {
                case "production":
                    if (exportType.toUpperCase().equals("PR"))
                        path = ObjectRepository.colbld_publish_path_prod_pr;//"/data2/production_nightly_pr/publish" ;
                    else if (exportType.toUpperCase().equals("NIGHTLY"))
                        path = ObjectRepository.colbld_publish_path_prod_nightly;//"/data2/production_nightly/publish" ;
                    break;

                case "staging":
                    if (exportType.toUpperCase().equals("PR"))
                        path = ObjectRepository.colbld_publish_path_staging_pr;//"/data2/staging_nightly_pr/publish";
                    else if (exportType.toUpperCase().equals("NIGHTLY"))
                        path = ObjectRepository.colbld_publish_path_staging_nightly;//"/data2/staging_nightly/publish";
                    break;

                case "qa":
                    if (exportType.toUpperCase().equals("PR"))
                        path = ObjectRepository.colbld_publish_path_qa_pr;//"/data2/qa_nightly_pr/publish" ;
                    else if (exportType.toUpperCase().equals("NIGHTLY"))
                        path = ObjectRepository.colbld_publish_path_qa_nightly;//"/data2/qa_nightly/publish" ;
                    break;

                case "dev":
                    if (exportType.toUpperCase().equals("PR"))
                        path = ObjectRepository.colbld_publish_path_dev_pr;//"/data2/dev_nightly_pr/publish";
                    else if (exportType.toUpperCase().equals("NIGHTLY"))
                        path = ObjectRepository.colbld_publish_path_dev_nightly;//"/data2/dev_nightly/publish";
                    break;

            }

            System.out.println("Manifest id " + path + "/" + manifestID);

            String exportFolderPathOnLinuxBox = ConnectToLinuxBox.connect(environment).getPathLikeAutoTabComplete(path, manifestID);
            System.out.println("Received export Folder Path name  :  " + exportFolderPathOnLinuxBox);

            String buildLogPath = exportFolderPathOnLinuxBox + "/log/build.log";
            ConnectToLinuxBox connectToLinuxBox = ConnectToLinuxBox.connect(environment);
            String currentDirectoryPath = connectToLinuxBox.changeDirectory(exportFolderPathOnLinuxBox);

            System.out.println("exportFolderPathOnLinuxBox :  " + currentDirectoryPath);
            System.out.println("buildLogPath :  " + buildLogPath);


            try {

                String start = connectToLinuxBox.runCommand("grep 'build date:' " + buildLogPath);
                String end = connectToLinuxBox.runCommand("grep 'Content Build finished' " + buildLogPath);

                GlobalVariables.session.disconnect();


                // check if we could not retrieve the start ot end dates on the log file and end test execution
                if(start.isEmpty() || end.isEmpty()){
                    reportProcessStatus = "FAILED";
                    throw new RuntimeException("Start or End build times could not be found on log file '" + buildLogPath + "' build may not be done.");
                }


                System.out.println("Start Time Build Log Line : = " + start + "\n" + "End Time Build Log Line : = " + end);

                // obtain values
                String startTime = start.split("build date:")[1].trim();
                String endTime = end.split("Content Build finished")[1].trim();
                startTime = startTime.replace("  ", " ");

                System.out.println("Start Time : = " + startTime + "\n" + "End Time : = " + endTime);

                String[] strStart = startTime.split("\\s");
                String[] strEnd = endTime.split("\\s");

                String startDateTime = strStart[5] + " " + strStart[1] + " " + strStart[2] + " " + strStart[3];

                // NOTE: taking start time year value for end time year value as end year value is not stored on logs, this could cause some issues!
                String endDateTime = strStart[5] + " " + strEnd[0] + " " + strEnd[1] + " " + strEnd[2];
                System.out.println("Start Time : = " + startDateTime + "\n" + "End Time : = " + endDateTime);


                // convert date time strings formats
                content_build_start = convertStringDateFormat(startDateTime, new SimpleDateFormat("yyyy MMM d HH:mm:ss"), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
                content_build_end = convertStringDateFormat(endDateTime, new SimpleDateFormat("yyyy MMM d HH:mm:ss"), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

                content_build_total = Long.toString(getSecondsDurationBetweenTwoStringDates(content_build_start, content_build_end, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));

                System.out.println("Total time taken for Build : = " + content_build_total + "sec");


                readBuildTestLogs(environment, exportType, path);


                System.out.println("\n***** Saving Build data to Export_Conbld_statistics_table TestDB *****\n");
                try {

                    Connection con = ConnectToMySQLTestDB.getInsstance().getMySqlConnection();

                    PreparedStatement stmt = con.prepareStatement("INSERT INTO "  + ObjectRepository.Export_Conbld_statistics_table +" (Server_Name, Manifest_Id, Build_Start_Time, Build_End_Time, Build_Total_Time, Test1_Start_Time, Test1_End_Time, Test1_Total_Time, " +
                            "Test2_Start_Time, Test2_End_Time, Test2_Total_Time, Test3_Start_Time, Test3_End_Time, Test3_Total_Time, Region) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

                    stmt.setString(1, GlobalVariables.conbld_serverName);
                    stmt.setString(2, manifestID);
                    stmt.setString(3, content_build_start);
                    stmt.setString(4, content_build_end);
                    stmt.setString(5, content_build_total);
                    stmt.setString(6, test1_Start_Time);
                    stmt.setString(7, test1_End_Time);
                    stmt.setString(8, total_Test1_Time);
                    stmt.setString(9, test2_Start_Time);
                    stmt.setString(10, test2_End_Time);
                    stmt.setString(11, total_Test2_Time);
                    stmt.setString(12, test3_Start_Time);
                    stmt.setString(13, test3_End_Time);
                    stmt.setString(14, total_Test3_Time);
                    stmt.setString(15, env);

                    System.out.println("PreparedStatement = " + stmt.toString());

                    // Execute the SQL Query
                    stmt.executeUpdate();

                    System.out.println("\n***** " + ObjectRepository.Export_Conbld_statistics_table + " data saved successfully in TestDB at " + getCurrentDateTimeForLog() + "*****\n");
                    con.close();

                    System.out.println(System.lineSeparator());

                } catch (Exception e) {
                    reportProcessStatus = "FAILED";
                    System.out.println(e.getLocalizedMessage());
                    System.out.println("Exception occurred while inserting data in TestDB at " + getCurrentDateTimeForLog());
                }

            } catch (Exception e) {
                reportProcessStatus = "FAILED";
                System.out.println(e.getLocalizedMessage());
                System.out.println("Exception occurred while reading logs at " + getCurrentDateTimeForLog());

            }

        } catch (Exception e) {
            reportProcessStatus = "FAILED";
            System.out.println(e.getLocalizedMessage());
            System.out.println("Exception occurred while reading build.log file at " + getCurrentDateTimeForLog());
        }
    }

    public void readExportJobLogFile(String env) {

        System.out.println("Function - readExportJobLogFile");


        String returnValue = null;

        if (env.equalsIgnoreCase("DEV")) {
            returnValue = "edweb01d";
        } else if (env.equalsIgnoreCase("QA")) {
            returnValue = "edweb01q";
        } else if (env.equalsIgnoreCase("STAGING")) {
            returnValue = "edweb0503s";
        } else if (env.equalsIgnoreCase("PRODUCTION")) {
            returnValue = "edapp05p";
        }

        LocalDate date = LocalDate.now();

        String logName = "ExportJob.log";
        System.out.println("Log file name == " + logName);

        String start = null;
        String end = null;
        String manifestId;
        String manifestLine = null;

        String folder = "//" + returnValue + "//Editorial_Log//" + logName;
        System.out.println("Log file folder location == " + folder);

        File file = new File(folder);

        /* obtain exportJob manifest, start and end times */
        if (file.exists()) {
            System.out.println("Log date is == " + date);
            System.out.println("ExportJob Log file is available for == " + logName);

            try (FileInputStream fstream = new FileInputStream(file);
                 BufferedReader br = new BufferedReader(new InputStreamReader(fstream))){

                String strLine;
                /* read log line by line */
                while ((strLine = br.readLine()) != null) {
                    /* parse strLine to obtain what you want */
                    if (strLine.contains("Start active build")) {
                        manifestLine = strLine;
                    }
                    if (strLine.contains("Build finished")) {
                        start = strLine;
                    }
                    if (strLine.contains("done with zip")) {
                        end = strLine;
                        break;
                    }
                }

            } catch (Exception e) {
                reportProcessStatus = "FAILED";
                System.out.println("Error: " + e.getMessage());
            }

            if (manifestLine != null && start != null & end != null) {

                System.out.println("Start Time Log Line : = " + start + "\n" + "End Time Log Line : = " + end);
                String startTime = start.split(",")[0].trim();
                String endTime = end.split(",")[0].trim();

                manifestId = manifestLine.split("build")[1].trim();

                exportLog_manifestId = manifestId;
                System.out.println("ManifestID is : = " + manifestId);

                zip_start_time = startTime;
                zip_end_time = endTime;

                System.out.println("Start Time : = " + startTime + "\n" + "End Time : = " + endTime);

                long diffSeconds = getSecondsDurationBetweenTwoStringDates(startTime, endTime, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

                total_time_zip = Long.toString(diffSeconds);
                System.out.println("Total time taken for ZIP : = " + diffSeconds + "sec");

            } else {
                reportProcessStatus = "FAILED";
                System.out.println("Required log was not found in ExportJob.log file at " + getCurrentDateTimeForLog());
            }

        } else {
            reportProcessStatus = "FAILED";
            System.out.println(logName + " log file not available at " + getCurrentDateTimeForLog());
        }

        System.out.println(System.lineSeparator());

    }

    public void readR1SyncLogFile(String env) {

        System.out.println("Function - readR1SyncLogFile");


        String serverName = null;

        if (env.equalsIgnoreCase("DEV")) {
            serverName = "edweb01d";
        } else if (env.equalsIgnoreCase("QA")) {
            serverName = "edweb01q";
        } else if (env.equalsIgnoreCase("STAGING")) {
            serverName = "edweb0503s";
        } else if (env.equalsIgnoreCase("PRODUCTION")) {
            serverName = "edapp05p";
        }

        LocalDate date = LocalDate.now();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String strDate = dateFormat.format(new Date(cal.getTimeInMillis()));

        String logName = "r1sync.log";
        String logName1 = "r1sync.log" + (strDate);
        System.out.println("Log file name == " + logName);

        String start = null;
        String end = null;
        String manifestId;
        String manifestLine = null;

        String folder = "//" + serverName + "//Editorial_Log//" + logName;
        String folder1 = "//" + serverName + "//Editorial_Log//" + logName1;


        String startLineOnLog = "Starting r1sync with params '-i /mnt/conbld01p_publish_pr/";

        File file = new File(folder);
        File file1 = new File(folder1);

        // if log file does not exist without current date, change log file that we will point to and corresponding variables
        if(!file.exists()){
            file = file1;
        }

        System.out.println("Log file folder location == " + file.getAbsolutePath());

        /* obtain log info */
        if (file.exists()) {

            System.out.println("Log date is == " + date);
            System.out.println("UTD Log file is available for == " + logName);

            try {

                List<String> allLines = Files.readAllLines(Paths.get(folder));

                /* read log line by line */
                for (String strLine : allLines) {
                    /* parse strLine to obtain what you want */
                    if (strLine.contains(startLineOnLog)) {
                        start = strLine;
                    }
                    if (strLine.contains("buildManifestId is")) {
                        manifestLine = strLine;
                    }
                  //  if (strLine.contains("Goodbye. R1sync is now exiting")) {
                    if (strLine.contains("Processing for UpToDate.com content")) {
                        end = strLine;
                    }
                }

                if (start != null & end != null) {
                    System.out.println("Start Time Log Line : = " + start + "\n" + "End Time Log Line : = " + end);

                    String startTime = start.split(",")[0].trim();
                    String endTime = end.split(",")[0].trim();

                    manifestId = manifestLine.split("is")[1].trim();
                    System.out.println("ManifestID is : = " + manifestId);
                    r1sync_manifestId = manifestId;

                    r1sync_start_time = startTime;
                    r1sync_end_time = endTime;
                    System.out.println("Start Time : = " + startTime + "\n " + "End Time : = " + endTime);


                    long diffSeconds = getSecondsDurationBetweenTwoStringDates(startTime, endTime, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

                    total_r1sync_time = Long.toString(diffSeconds);
                    System.out.println("Total time taken for r1sync : = " + diffSeconds + "sec");

                } else {
                    reportProcessStatus = "FAILED";
                    System.out.println("Required log was not found in r1sync.log file at " + getCurrentDateTimeForLog());
                }

            } catch (Exception e) {
                reportProcessStatus = "FAILED";
                System.out.println("Error: " + e.getMessage());
            }

        } else {
            reportProcessStatus = "FAILED";
            System.out.println(logName + " log file not available at " + getCurrentDateTimeForLog());
        }

        System.out.println(System.lineSeparator());

    }

    public void readBuildTestLogs(String environment, String exportType, String path) {

        System.out.println("Function - readBuildTestLogs");


        try {

            System.out.println("\n****** Reading conbld Test log files *******\n");
            System.out.println("Manifest id path = " + path + "/" + manifestID);

            String exportFolderPathOnLinuxBox = ConnectToLinuxBox.connect(environment).getPathLikeAutoTabComplete(path, manifestID);
            System.out.println("Received export Folder Path name  :  " + exportFolderPathOnLinuxBox);

            String buildTest1LogPath = exportFolderPathOnLinuxBox + "/log/test1.log";
            String buildTest2LogPath = exportFolderPathOnLinuxBox + "/log/test2.log";
            String buildTest3LogPath = exportFolderPathOnLinuxBox + "/log/test3.log";

            ConnectToLinuxBox connectToLinuxBox = ConnectToLinuxBox.connect(environment);
            String currentDirectoryPath = connectToLinuxBox.changeDirectory(exportFolderPathOnLinuxBox);

            System.out.println("exportFolderPathOnLinuxBox :  " + currentDirectoryPath);
            System.out.println("testLogPaths :  " + buildTest1LogPath + "\n" + buildTest2LogPath + "\n" + buildTest3LogPath);

            try {

                // Test 1
                String start = connectToLinuxBox.runCommand("grep 'BEGIN|current_group' " + buildTest1LogPath);
                String end = connectToLinuxBox.runCommand("grep 'END|current_group' " + buildTest1LogPath);

                System.out.println("Start Time Test1 Log Line : = " + start + "\n" + "End Time Test1 Log Line : = " + end);
                String[] str1 = start.split("\\|");
                String[] str2 = end.split("\\|");
                String[] strStart = str1[2].split("\\[");
                String[] strEnd = str2[2].split("\\[");
                String d1 = strStart[1].replace("]", "").trim();
                String d2 = strEnd[1].replace("]", "").trim();

                test1_Start_Time = convertStringDateFormat(d1, new SimpleDateFormat("E MMM dd HH:mm:ss yyyy"), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
                test1_End_Time = convertStringDateFormat(d2, new SimpleDateFormat("E MMM dd HH:mm:ss yyyy"), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

                total_Test1_Time = Long.toString(getSecondsDurationBetweenTwoStringDates(test1_Start_Time, test1_End_Time, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));

                System.out.println("Start Time : = " + d1 + "\n" + "End Time : = " + d2);
                System.out.println("Total Test1 time : = " + total_Test1_Time);


                // Test 2
                start = connectToLinuxBox.runCommand("grep 'BEGIN|current_group' " + buildTest2LogPath);
                end = connectToLinuxBox.runCommand("grep 'END|current_group' " + buildTest2LogPath);

                System.out.println("Start Time Test2 Log Line : = " + start + "\n" + "End Time Test2 Log Line : = " + end);

                String[] str3 = start.split("\\|");
                String[] str4 = end.split("\\|");

                String[] strStart1 = str3[2].split("\\[");
                String[] strEnd1 = str4[2].split("\\[");

                String d3 = strStart1[1].replace("]", "").trim();
                String d4 = strEnd1[1].replace("]", "").trim();

                test2_Start_Time = convertStringDateFormat(d3, new SimpleDateFormat("E MMM dd HH:mm:ss yyyy"), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
                test2_End_Time = convertStringDateFormat(d4, new SimpleDateFormat("E MMM dd HH:mm:ss yyyy"), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

                total_Test2_Time = Long.toString(getSecondsDurationBetweenTwoStringDates(test2_Start_Time, test2_End_Time, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));

                System.out.println("Start Time : = " + d3 + "\n" + "End Time : = " + d4);
                System.out.println("Total Test2 time : = " + total_Test2_Time);



                // Test 3
                start = connectToLinuxBox.runCommand("grep 'BEGIN|current_group' " + buildTest3LogPath);
                end = connectToLinuxBox.runCommand("grep 'END|current_group' " + buildTest3LogPath);

                GlobalVariables.session.disconnect();

                System.out.println("Start Time Test3 Log Line : = " + start + "\n" + "End Time Test3 Log Line : = " + end);


                String[] str5 = start.split("\\|");
                String[] str6 = end.split("\\|");

                String[] strStart2 = str5[2].split("\\[");
                String[] strEnd2 = str6[2].split("\\[");

                String d5 = strStart2[1].replace("]", "").trim();
                String d6 = strEnd2[1].replace("]", "").trim();

                test3_Start_Time = convertStringDateFormat(d5, new SimpleDateFormat("E MMM dd HH:mm:ss yyyy"), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
                test3_End_Time = convertStringDateFormat(d6, new SimpleDateFormat("E MMM dd HH:mm:ss yyyy"), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

                total_Test3_Time = Long.toString(getSecondsDurationBetweenTwoStringDates(test3_Start_Time, test3_End_Time, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));

                System.out.println("Start Time : = " + d5 + "\n" + "End Time : = " + d6);
                System.out.println("Total Test3 time : = " + total_Test3_Time);


                System.out.println(System.lineSeparator());

            } catch (Exception e) {
                reportProcessStatus = "FAILED";
                System.out.println(e.getLocalizedMessage());
                System.out.println("Exception occurred while reading logs ");

            }
        } catch (Exception e) {
            reportProcessStatus = "FAILED";
            System.out.println(e.getLocalizedMessage());
            System.out.println("Exception occurred while reading build.log file ");
        }
    }


    // Email functions
    public void sendTestOutputEmail() {

        System.out.println("Function - sendTestOutputEmail");

        try {
            GenericReportEmail.sendReportEmailTo(emailRecipients, emailSubject, emailBody);
        } catch (MessagingException e) {
            System.out.println(e.getLocalizedMessage());
        }

        System.out.println(System.lineSeparator());

    }


    public void assignAllDynamicVariables() {

        System.out.println("Function - assignAllDynamicVariables");


        if(!reportProcessStatus.equals("SUCCESS")){
            emailBody += "<br/>";
            emailBody += "Please check the log file for more details '" + outputLogFilePath + "' on 'edex02q' machine";
        }else{
            emailBody += "<br/> <b>Grafana dashboard:</b> http://edex02q:3000/d/r3iCBen4z/content-export-timings-general?orgId=1&refresh=1d";
        }


        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());

        emailSubject = emailSubject.replaceAll("\\$\\{status}", reportProcessStatus);

        emailSubject = emailSubject.replaceAll("\\$\\{date}", formatter.format(date));


        emailBody = emailBody.replaceAll("\\$\\{status}", reportProcessStatus);

        emailBody = emailBody.replaceAll("\\$\\{date}", formatter.format(date));


        System.out.println(System.lineSeparator());

    }


    // Dates helper methods

    public static String convertStringDateFormat(String originalDateString, SimpleDateFormat originalDateFormat, SimpleDateFormat desiredDateFormat) {

        System.out.println("Function - convertStringDateFormat");


        String formatted4DBString = "";
        Date date1;

        try {
            date1 = originalDateFormat.parse(originalDateString);
            formatted4DBString = desiredDateFormat.format(date1);
        } catch (ParseException e) {
            System.out.println(e.getLocalizedMessage());
        }

        return formatted4DBString;
    }

    private static long getSecondsDurationBetweenTwoStringDates(String date1Str, String date2Str, SimpleDateFormat dateFormat) {

        System.out.println("Function - getSecondsDurationBetweenTwoStringDates");


        Date date1 = null, date2 = null;

        long diff = 0;

        try {
            date1 = dateFormat.parse(date1Str);
            date2 = dateFormat.parse(date2Str);
        } catch (ParseException e) {
            System.out.println(e.getLocalizedMessage());
        }

        try {
            // Get msec from each, and subtract.
            assert date2 != null;
            assert date1 != null;
            diff = date2.getTime() - date1.getTime();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

        return diff / 1000;

    }


    // general helper methods

    private static String getCurrentDateTimeForLog(){
        return new Date().toString() + " ";
    }

}





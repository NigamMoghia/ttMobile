package com;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.UsernameAndPassword;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import util.ConnectToMySQLTestDB;
import util.ObjectRepository;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class EditorialPagesPerfAnalysis {

    /*
     * Author: Enrique Callejón
     *
     *
     * Purpose of the tool: Get a notification email with Topics' performance analysis data,
     *  this will help us to know that there is issue and so it can resolved proactively.
     *
     *
     * Last update: Enrique Callejón /// 12-5-2022
     *
     * Flow:
     *      1-
     *
     *
     * NOTE: Before execution you must set the class variables to your corresponding values
     *
     * */

    private final String outputLogFilePath = "C:\\uptodate\\Batch\\logs\\performance_analysis\\editorialPages_performance.txt";


    private List<String> urlsToAccess;

    private String userName, password;

    private Map<String, Double> currentExecutionUrlTimings;


    // 'webPagesTimingsComparisonMap' map that will be used to store the comparison of the web pages timings
    // The key will be the accessed URL and the value will consist of a map ->
    //      this map key will be the timing stored (PreviousDate, Current, Previous, Difference),
    //      and the value will be the actual timing in seconds or 'Not found'
    //      if the value is not found on DB (for Previous) and for Difference as well
    //      as it cannot be calculated

    private Map<String, Map<String, String>> webPagesTimingsComparisonMap = new HashMap<>();

    private final String NOT_FOUND = "Not found";

    private final String emailSubject = "Performance analysis data";

    private String emailBody;

    public void setUserNameAndPassword(String environment){

        switch (environment){
            case "Production":
            case "Staging":
                this.userName = "sautomation";
                this.password = "SA#utd16";
                break;
            case "Integration":
                this.userName = "sautomation";
                this.password = "Editorial$123";
                break;

        }


    }

    public void gatherWebPagesLoadTimeDataAndCompareWithPrevious(String environment, String topicID) {

        String baseURL = defineBaseURL(environment);

        gatherWebPagesToAccessFromDB(baseURL, topicID);

        gatherWebPagesTimings();

        compareWebPagesTimingsWithPreviousIfAvailable();

        storeCurrentTimingsOnDB();

    }


    private void gatherWebPagesToAccessFromDB(String baseURL, String topicID) {

        System.out.println("**** gatherWebPagesToAccess started ****\n");

        urlsToAccess = new ArrayList<>();

        try {

            Connection con = ConnectToMySQLTestDB.getInsstance().getMySqlConnection();

            PreparedStatement stmt = con.prepareStatement("SELECT * FROM " + ObjectRepository.Editorial_Web_Pages_table);

            System.out.println("PreparedStatement = " + stmt.toString());

            // Execute the SQL Query and store response on resultSet
            ResultSet rs = stmt.executeQuery();


            // iterate over resultSet and store URL on arrayList
            while(rs.next()){

                String currentURL = rs.getString("URL");

                // only add URLs that are suitable for our next tasks
                if(!currentURL.contains("http")){
                    urlsToAccess.add(baseURL + currentURL);
                }

            }

            rs.close();
            stmt.close();
            con.close();

        } catch (Exception E) {
            E.printStackTrace();
            System.out.println("Exception occurred while gathering 'editorialwebpages' table data at " + getCurrentDateTime());
        }


        // modify topicID in URLs that already contain one
        urlsToAccess  = urlsToAccess.stream().map(url -> url.replaceAll("TopicID=[0-9]+","TopicID="+topicID)).collect(Collectors.toList());


        urlsToAccess.forEach(System.out::println);

        System.out.println("**** gatherWebPagesToAccess completed ****\n");

    }


    private String defineBaseURL(String environment) {

        String baseURL = "http://";

        switch (environment){
            case "Production":
                baseURL += "editorial00p";
                break;
            case "Staging":
                baseURL += "editorial00s";
                break;
            case "QA":
                baseURL += "edweb01q";
                break;
            case "Development":
                baseURL += "edweb01d";
                break;
        }

        return baseURL + "/";
    }


    private void gatherWebPagesTimings() {

        // initialize map that will be used to store current timings
        currentExecutionUrlTimings = new HashMap<>();

        WebDriverManager.chromedriver().setup();

        ChromeDriver webDriver = new ChromeDriver();

        // You can either register something for all sites
        webDriver.register(() -> new UsernameAndPassword(userName, password));


        // Access to webpages and store current load time
        try{

            // iterate through urls and gather their load times
            urlsToAccess.forEach(url -> {

                webDriver.get(url);


                waitForPageLoad(webDriver);

                Double pageLoadTime = getPageLoadTime(webDriver);

                currentExecutionUrlTimings.put(url, pageLoadTime);

            });


        }catch (Exception e){
            System.out.println(e.getLocalizedMessage());
            System.out.println("Exception occurred while gathering web pages data at " + getCurrentDateTime());
        }finally {
            // close all web driver tabs
            webDriver.close();
        }

    }

    private double getPageLoadTime(WebDriver webDriver) {

        final JavascriptExecutor js = (JavascriptExecutor) webDriver;

        // return time of the process of navigation and page load
        return (Double) js.executeScript("return (window.performance.timing.loadEventEnd - window.performance.timing.navigationStart) / 1000");

    }


    private void compareWebPagesTimingsWithPreviousIfAvailable() {

        System.out.println("**** compareWebPagesTimingsWithPreviousIfAvailable started ****\n");

        try {

            Connection con = ConnectToMySQLTestDB.getInsstance().getMySqlConnection();

            currentExecutionUrlTimings.forEach((url, currentLoadTime) -> {

                Map<String, String> currentVsPreviousTimings = new HashMap<>();

                currentVsPreviousTimings.put("Current", String.valueOf(currentLoadTime));


                try {

                    PreparedStatement stmt = con.prepareStatement("SELECT * FROM " + ObjectRepository.Editorial_Web_Pages_Timings_table + " WHERE page_link=? ORDER BY id DESC limit 1");

                    stmt.setString(1, url);

                    System.out.println("PreparedStatement = " + stmt.toString());


                    // Execute the SQL Query and store response on resultSet
                    ResultSet rs = stmt.executeQuery();


                    // if previous timings are available
                    if(rs.next()){

                        Double previousLoadTime = Double.parseDouble(rs.getString("page_load_time"));

                        currentVsPreviousTimings.put("PreviousDate", rs.getString("data_gather_date_time"));

                        currentVsPreviousTimings.put("Previous", String.valueOf(previousLoadTime));

                        currentVsPreviousTimings.put("Difference", String.valueOf(previousLoadTime-currentLoadTime));

                    }else{

                        currentVsPreviousTimings.put("PreviousDate", NOT_FOUND);

                        currentVsPreviousTimings.put("Previous", NOT_FOUND);

                        currentVsPreviousTimings.put("Difference", NOT_FOUND);

                    }

                    webPagesTimingsComparisonMap.put(url, currentVsPreviousTimings);


                    rs.close();
                    stmt.close();

                }
                catch (Exception e){
                    System.out.println(e.getLocalizedMessage());
                    System.out.println("Exception occurred while gathering previous editorial_web_pages_timings table data - 1 at " + getCurrentDateTime());
                }

            });

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception occurred while gathering previous editorial_web_pages_timings table data - 2 at " + getCurrentDateTime());
        }


    }


    private void storeCurrentTimingsOnDB() {

        currentExecutionUrlTimings.forEach((url, loadTime) -> {

            try {

                Connection con = ConnectToMySQLTestDB.getInsstance().getMySqlConnection();

                PreparedStatement stmt = con.prepareStatement("INSERT INTO " + ObjectRepository.Editorial_Web_Pages_Timings_table + " (page_link, data_gather_date_time, page_load_time) VALUES (?,?,?)");

                stmt.setString(1, url);
                stmt.setString(2, getCurrentDateTime());
                stmt.setString(3, String.valueOf(loadTime));

                System.out.println("PreparedStatement = " + stmt.toString());

                // Execute the SQL Query
                stmt.executeUpdate();


                stmt.close();
                con.close();
            }
            catch (Exception e){
                System.out.println(e.getLocalizedMessage());
                System.out.println("Exception occurred while storing current editorial_web_pages_timings table data at " + getCurrentDateTime());
            }


        });

    }


    public void sendReportEmail(String emailRecipients) {

        System.out.println("**** Sending Email Report Started ****\n");

        try {

            String cssStyling = "    <style>\n" +
                    "        table,\n" +
                    "        th,\n" +
                    "        td {\n" +
                    "            border: 1px solid black;\n" +
                    "            border-collapse: collapse;\n" +
                    "        }\n" +
                    "\n" +
                    "        td,\n" +
                    "        th{\n" +
                    "            padding: 10px;\n" +
                    "        }\n" +
                    "\n" +
                    "        .positiveTime{\n" +
                    "            background-color: rgb(3, 184, 3);\n" +
                    "        }\n" +
                    "        \n" +
                    "        .negativeTime{\n" +
                    "            background-color: rgb(255, 5, 5);\n" +
                    "        }\n" +
                    "\n" +
                    "    </style>";

            emailBody = "<!doctype html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    cssStyling +
                    "</head>\n" +
                    "<body>\n";


            //generateTopicOperationsComparisonTable();
            generateEditorialPagesTimingsComparisonTable();



            emailBody += "\n</body>\n" + "</html>";


            System.out.println(emailBody);



            GenericReportEmail.sendReportEmailTo(emailRecipients, emailSubject, emailBody);


            System.out.println("**** Sending Email Report Completed ****\n");
        }
        catch (Exception e){
            System.out.println(e.getLocalizedMessage());
            System.out.println("Error while sending Email Report at " + getCurrentDateTime());
        }

    }

    private void generateTopicOperationsComparisonTable() {

        String topicOperationsH3 = "\n<h3>Topic operations timings</h3>\n";
        String topicOperationsTable = "<table>[tableHeaders][tableBody]</table>";

        //Compare result, Date of Action, Content Id, Operation, Content Version, Start End

        String tableHeaders = "\n<thead><th></th>" +
                "\n<th>Compare result</th>" +
                "\n<th>Date of action</th>" +
                "\n<th>Content Id</th>" +
                "\n<th>Operation</th>" +
                "\n<th>Content Version</th>" +
                "\n<th>Start</th>" +
                "\n<th>End</th>" +
                "\n</thead>\n";

        topicOperationsTable = topicOperationsTable.replace("[tableHeaders]", tableHeaders);



        // generate table body
        String tableBody = "<tbody>[tbody]</tbody>";


        AtomicReference<String> tbody = new AtomicReference<>("");


        Map<String, Map<String, String>> topicsOperationsData = retrieveTopicsOperationsData();



        AtomicReference<String> lastOperationDuration = new AtomicReference<>("");

        AtomicBoolean isFirstStr = new AtomicBoolean(true);

        AtomicReference<Double> differenceWithLast = new AtomicReference<>((double) 0);


        String tableRowSeparator =
                "            <tr>\n" +
                        "                <td></td>\n" +
                        "                <td></td>\n" +
                        "                <td></td>\n" +
                        "                <td></td>\n" +
                        "                <td></td>\n" +
                        "                <td></td>\n" +
                        "                <td></td>\n" +
                        "            </tr>\n";


// JSON example representation: ["Topic_Open_2022-01-11_17:20:00" : ["TopicOpen" : 20, "TopicClose" : 12], "Topic_Open_2022-02-11_17:20:00" : ["TopicOpen" : 20, "TopicClose" : 12]]
        topicsOperationsData.forEach((operation, timingsMap) -> {

            String currentTr;

            if(!lastOperationDuration.get().equals("Data not Available")){
                differenceWithLast.set(Double.parseDouble(lastOperationDuration.get()) - Double.parseDouble(timingsMap.get("Total_Time_Sec")));
                lastOperationDuration.set(String.valueOf(timingsMap.get("Total_Time_Sec")));
            }

            String timingClass = defineTimingClass(String.valueOf(differenceWithLast.get()));

            // define if it's the first tr of the pair
            if(isFirstStr.get()){

                currentTr = "\n<tr><td>Current</td>" +
                        "\n<td rowspan=\"2\" class=\"" + timingClass + "\">"+
                        differenceWithLast +
                        "</td>"+
                        "\n<td>"+
                        timingsMap.get("Created_Time") +
                        "</td>" +
                        "\n<td>"+
                        timingsMap.get("Content_Id") +
                        "</td>" +
                        "\n<td>"+
                        timingsMap.get("Content_Version") +
                        "</td>" +
                        "\n<td>" +
                        roundDouble(timingsMap.get("Total_Time_Sec")) + " seconds" +
                        "</td>" +
                        "\n<td>"+
                        timingsMap.get("Start_Time") +
                        "</td>" +
                        "\n<td>"+
                        timingsMap.get("End_Time") +
                        "</td>";


                currentTr += "</tr>\n";


                isFirstStr.set(false);

            }else{

                currentTr = "\n<tr><td>Previous</td>" +
                        "\n<td>"+
                        timingsMap.get("Created_Time") +
                        "</td>" +
                        "\n<td>"+
                        timingsMap.get("Content_Id") +
                        "</td>" +
                        "\n<td>"+
                        timingsMap.get("Content_Version") +
                        "</td>" +
                        "\n<td>" +
                        roundDouble(timingsMap.get("Total_Time_Sec")) + " seconds" +
                        "</td>" +
                        "\n<td>"+
                        timingsMap.get("Start_Time") +
                        "</td>" +
                        "\n<td>"+
                        timingsMap.get("End_Time") +
                        "</td>";

                currentTr += "</tr>\n" + tableRowSeparator;


                isFirstStr.set(true);

            }


            tbody.set(tbody.get() + currentTr);

        });


        tableBody = tableBody.replace("[tbody]", tbody.get());


        topicOperationsTable = topicOperationsTable.replace("[tableBody]", tableBody);


        emailBody += topicOperationsH3 + topicOperationsTable;

    }


    private Map<String, Map<String, String>> retrieveTopicsOperationsData() {

        // create and initialize a map that will allocate the Topics operations timings data
        // the data will be saved as follows:
        //  "parent" map key will be the Topic operation and the Action date time
        // the "child" map will have the Topic data stored on its keys and the value on the value field
        // JSON example representation: ["Topic_Open_2022-01-11_17:20:00" : ["TopicOpen" : 20, "TopicClose" : 12]]
        Map<String, Map<String, String>> topicsOperationsData = new HashMap<>();


        try {

            Connection con = ConnectToMySQLTestDB.getInsstance().getMySqlConnection();

            try {

                PreparedStatement stmt = con.prepareStatement("SELECT * FROM " + ObjectRepository.Perf_analysis_table + "_temp" + " ORDER BY Operation");


                System.out.println("PreparedStatement = " + stmt.toString());


                // Execute the SQL Query and store response on resultSet
                ResultSet rs = stmt.executeQuery();


                while(rs.next()){

                    Map<String, String> currentOperationTimings = new HashMap<>();

                    currentOperationTimings.put("Operation", rs.getString("Operation"));
                    currentOperationTimings.put("Content_Id", rs.getString("Content_Id"));
                    currentOperationTimings.put("Content_Version", rs.getString("Content_Version"));
                    currentOperationTimings.put("Total_Time_Sec", rs.getString("Total_Time_Sec"));
                    currentOperationTimings.put("Start_Time", rs.getString("Start_Time"));
                    currentOperationTimings.put("End_Time", rs.getString("End_Time"));
                    currentOperationTimings.put("Region", rs.getString("Region"));
                    currentOperationTimings.put("Comment", rs.getString("Comment"));
                    currentOperationTimings.put("Created_Time", rs.getString("Created_Time"));



                    topicsOperationsData.put(rs.getString("Operation").replaceAll(" ", "_") + "_" + rs.getString("Start_Time").replaceAll(" ", "_"), currentOperationTimings);

                }


                rs.close();
                stmt.close();

            }
            catch (Exception e){
                System.out.println(e.getLocalizedMessage());
                System.out.println("Exception occurred while gathering gathering 'Topics Operations' table data - 1 at " + getCurrentDateTime());
            }


            con.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception occurred while gathering 'Topics Operations' table data - 2 at " + getCurrentDateTime());
        }


        return topicsOperationsData;

    }


    private void generateEditorialPagesTimingsComparisonTable(){

        String editorialPagesH3 = "\n<h3>Editorial pages timings</h3>\n";
        String editorialPagesTable = "<table>[tableHeaders][tableBody]</table>";

        String tableHeaders = "\n<thead><th></th>" +
                "\n<th>Compare result</th>" +
                "\n<th>Date of action</th>" +
                "\n<th>Total load time taken</th>" +
                "\n<th>Page link</th>" +
                "\n</thead>\n";

        editorialPagesTable = editorialPagesTable.replace("[tableHeaders]", tableHeaders);


        // generate table body
        String tableBody = "<tbody>[tbody]</tbody>";


        AtomicReference<String> tbody = new AtomicReference<>("");

        webPagesTimingsComparisonMap.forEach((url, timingsMap) -> {

            String timingClass = defineTimingClass(timingsMap.get("Difference"));

            String currentTr = "\n<tr><td>Current</td>" +
                    "\n<td rowspan=\"2\" class=\"" + timingClass + "\">"+
                    roundDouble(timingsMap.get("Difference")) +
                    "</td>"+
                    "\n<td>"+
                    getCurrentDateTime() +
                    "</td>" +
                    "\n<td>" +
                    roundDouble(timingsMap.get("Current")) + " seconds" +
                    "</td>" +
                    "\n<td>"+
                    url +
                    "</td>";

            currentTr += "</tr>\n";


            String previousTr = "\n<tr><td>Previous</td>" +
                    "\n<td>"+
                    timingsMap.get("PreviousDate") +
                    "</td>" +
                    "\n<td>" +
                    (timingsMap.get("Previous").equals(NOT_FOUND) ? timingsMap.get("Previous") : roundDouble(timingsMap.get("Previous")) + " seconds") +
                    "</td>" +
                    "\n<td>"+
                    url +
                    "</td>";

            previousTr += "</tr>\n";


            String tableRowSeparator =
                    "            <tr>\n" +
                            "                <td></td>\n" +
                            "                <td></td>\n" +
                            "                <td></td>\n" +
                            "                <td></td>\n" +
                            "            </tr>\n";



            tbody.set(tbody.get() + currentTr + previousTr + tableRowSeparator);

        });

        tableBody = tableBody.replace("[tbody]", tbody.get());


        editorialPagesTable = editorialPagesTable.replace("[tableBody]", tableBody);


        emailBody += editorialPagesH3 + editorialPagesTable;

    }

    private String roundDouble(String difference) {

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);

        return df.format(Double.parseDouble(difference));
    }

    private String defineTimingClass(String difference) {

        String timingClass = "negativeTime";

        if(!difference.equals(NOT_FOUND) && !difference.equals("Data not Available")){

            if(Double.parseDouble(difference) <= 0) {
                timingClass = "positiveTime";
            }

        }

        return timingClass;

    }


    // helper function for webdriver
    public void waitForPageLoad(WebDriver webDriver) {

        Wait<WebDriver> wait = new WebDriverWait(webDriver, Duration.of(30, ChronoUnit.SECONDS));
        wait.until(driver -> {
            System.out.println("Current Window State       : "
                    + ((JavascriptExecutor) driver).executeScript("return document.readyState"));
            return String
                    .valueOf(((JavascriptExecutor) driver).executeScript("return document.readyState"))
                    .equals("complete");
        });

    }


    // helper current date time function
    private String getCurrentDateTime(){

        LocalDateTime dateTime = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return dateTime.format(formatter);

    }


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

}
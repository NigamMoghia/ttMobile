package com;

import util.*;

import javax.mail.MessagingException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ReadPerfAnalysisDB {
    public String env =null;
    public String contentId ="";
    public String action ="";
    public String totalTimeInSec ="";
    public String startTime ="";
    public String endTime ="";
    public String contentVersion ="";
    public String region ="";
    public String comment ="";
    public String dataType="";
    public String[] operation={"TopicOpen","TopicCheckout","TopicSave","TopicSaveAndRelease","TopicSaveAs","TopicInspection"};
    public String[] status ={"Started","Completed"};

    public PollForExportFolder p = new PollForExportFolder();
    public void setGetEnv(String env) {
        System.out.println("**** Setting Environment Started ****\n");
        try {
            p.setEnvironment(env);
        } catch (Exception e) {
            System.out.println("Exception occured while setting environment");
        }
        System.out.println("**** Setting Environment Completed ****\n");
    }

    //Cleaning Temp table for latest data
    public void cleanTempTable() {
        System.out.println("**** Temp Table cleaning started ****\n");
        try {
            String sqlQuery = "Delete From " + ObjectRepository.Perf_analysis_table + "_temp" +
                    " Where Content_Id is not NULL";
            Connection sqlCon = ConnectToMySQLTestDB.getInsstance().getMySqlConnection();
            Statement sqlStmt = sqlCon.createStatement();

            // Execute the SQL Query. to save data in Perf_analysis_table
            sqlStmt.executeUpdate(sqlQuery);
            sqlCon.close();
        } catch (Exception E) {
            E.printStackTrace();
            System.out.println("Exception occurred while Deleting data from Temp table ");
        }
        System.out.println("**** Temp Table cleaning completed ****\n");
    }

    public void getPerAnalysisDataAfterDeploymentFromDB(String env, String topicID) throws Exception
    {
        System.out.println("**** Reading perf DataAfterDeploymentFromDB Started ****\n");
        //Setting environment
        GlobalVariables.exportRegion= env;
        region= env;
       // setGetEnv(env);
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());

        // Create Connection to  DB
        Connection con = ConnectToEditorialDB.getInstance(env).getConnection();
        Connection sqlCon = ConnectToMySQLTestDB.getInsstance().getMySqlConnection();
        Statement stmt = con.createStatement();
        Statement sqlStmt = sqlCon.createStatement();
        String query;
        String query1;
        for(String ops : operation) {
            startTime="";endTime="";totalTimeInSec="";contentVersion="";comment="";dataType ="Current";
            //Query to retrieve operation perf data for a contentID
            query = "select content_id,operation,\n" +
                    "max(case when status='" + status[0] + "' then created_date end) as StartedTime,\n" +
                    "max(case when status='" + status[1] + "' then created_date end)  as CompletedTime,\n" +
                    "datediff(ss,max(case when status='" + status[0] + "' then created_date end),max(case when status='" + status[1] + "' then created_date end)) as TimeDiff_in_Sec \n" +
                    "from ED_ADMIN.PERF_ANALYSIS \n" +
                    "where content_id=" + topicID + " and Operation ='" + ops + "'\n" +
                    "group by content_id,operation order by content_id,operation,StartedTime desc\n";
            //System.out.print("QUERY === " + query);
            ResultSet resultSet = stmt.executeQuery(query);
            try {
                if(!resultSet.isBeforeFirst())
                {
                    System.out.println("Data was not found for operation -> " + ops +    "\n");
                    contentId = topicID;
                    action = ops;
                    startTime ="0000-00-00 00:00:00";
                    endTime ="0000-00-00 00:00:00";
                    totalTimeInSec="0";
                    comment ="Data Not Available";
                }
                else {
                    while (resultSet.next()) {
                        contentId = resultSet.getString("content_ID");
                        action = resultSet.getString("operation");
                        startTime = resultSet.getString("StartedTime");
                        endTime = resultSet.getString("CompletedTime");
                        totalTimeInSec = resultSet.getString("TimeDiff_in_Sec");
                    }
                }
            } catch (ArrayIndexOutOfBoundsException |NullPointerException e) {
                System.out.println("Exception thrown - please check the field might be empty .... " + e);
            }
            //Query to retrieve content version
            query1 = "select content_id,operation,cast(major_version as varchar)+'.'+cast(minor_version as varchar) as Content_Version\n" +
                    "from ED_ADMIN.PERF_ANALYSIS \n"+
                    "where content_id=" + topicID +" and Operation ='" + ops + "' and status='"+ status[1] +"'\n"+
                    "and created_date=(select max(created_date) from ED_ADMIN.PERF_ANALYSIS\n"+
                    "where content_id=" + topicID +" and Operation ='"+ ops +"'\n"+ "and status='"+ status[1] +"')\n";

            //System.out.print("QUERY1 === " + query1);
            ResultSet resultSet1 = stmt.executeQuery(query1);
            try {
                if(!resultSet1.isBeforeFirst())
                {
                    System.out.println("Data was not found for operation -> " + ops + "\n");
                    contentVersion="";
                }
                else {
                    while (resultSet1.next()) {
                        contentVersion = resultSet1.getString("Content_Version");
                    }
                }
            } catch (ArrayIndexOutOfBoundsException |NullPointerException e) {
                System.out.println("Exception thrown - please check the field might be empty .... " + e);
            }

            //Saving Data in MySql DB
            try {
                String sqlQuery = "INSERT INTO " + ObjectRepository.Perf_analysis_table + " (" + "Content_Id" + "," +
                        "Content_Version" + "," + "Operation" + "," + "Total_Time_Sec" + "," + "Start_Time" + "," +
                        "End_Time" + "," + "Region" + "," + "Comment" + "," + "Created_Time" + ")"
                        + " Values " + "(" + "'" + contentId + "'" + "," + "'" + contentVersion + "'" + ","
                        + "'" + action + "'" + "," + "'" + totalTimeInSec + "'" + "," + "'"
                        + startTime + "'" + "," + "'" + endTime + "'" + "," + "'" + region + "'" + "," + "'" + comment + "'" + "," + "'" + timestamp + "'"
                        + ")";
                String sqlQueryTemp = "INSERT INTO " + ObjectRepository.Perf_analysis_table +"_temp" + " (" + "Content_Id" + "," +
                        "Content_Version" + "," + "Operation" + "," + "Total_Time_Sec" + "," + "Start_Time" + "," +
                        "End_Time" + "," + "Region" + "," + "Comment" + "," + "Created_Time" + "," + "data_type" +")"
                        + " Values " + "(" + "'" + contentId + "'" + "," + "'" + contentVersion + "'" + ","
                        + "'" + action + "'" + "," + "'" + totalTimeInSec + "'" + "," + "'"
                        + startTime + "'" + "," + "'" + endTime + "'" + "," + "'" + region + "'" + "," + "'" + comment + "'" + "," + "'" + timestamp + "'"
                        +  "," + "'" + dataType + "'" + ")";
                //System.out.println("SQLQuery = " + sqlQuery);
                //System.out.println("SQLQuery = " + sqlQueryTemp);
                // Execute the SQL Query. to save data in Perf_analysis_data and Perf_analysis_data_temp
                sqlStmt.executeUpdate(sqlQuery);
                sqlStmt.executeUpdate(sqlQueryTemp);

            } catch (Exception E) {
                E.printStackTrace();
                System.out.println("Exception occurred while inserting data in TestDB ");
            }
            System.out.println("**** Reading perf DataAfterDeploymentFromDB Completed ****\n");

        }
        con.close();
        sqlCon.close();
    }

    public void getPerAnalysisDataBeforeDeploymentFromDB(String env, String topicID) throws Exception
    {
        System.out.println("**** Reading perf DataBeforeDeploymentFromDB Started ****\n");
        //Setting environment
        GlobalVariables.exportRegion= env;
        region= env;
        //setGetEnv(env);

        Date date = new Date();
        Timestamp timestamp= new Timestamp(date.getTime());
        //System.out.print("timestamp === " + timestamp);

        // Create Connection to  DB
        Connection con = ConnectToEditorialDB.getInstance(env).getConnection();
        Connection sqlCon = ConnectToMySQLTestDB.getInsstance().getMySqlConnection();
        Statement stmt = con.createStatement();
        Statement sqlStmt = sqlCon.createStatement();
        String query;
        String query1;
        for(String ops : operation) {

            startTime="";endTime="";totalTimeInSec="";contentVersion="";comment="";dataType="Previous";

            //Query to retrieve operation perf data for a contentID
            query = "select content_id,operation,\n" +
                    "max(case when status='" + status[0] + "' then created_date end) as StartedTime,\n" +
                    "max(case when status='" + status[1] + "' then created_date end)  as CompletedTime,\n" +
                    "datediff(ss,max(case when status='" + status[0] + "' then created_date end),max(case when status='" + status[1] + "' then created_date end)) as TimeDiff_in_Sec \n" +
                    "from ED_ADMIN.PERF_ANALYSIS \n" +
                    "where content_id=" + topicID + " and Operation ='" + ops + "'\n" +
                    "and CREATED_DATE<(select convert(date,max(CREATED_DATE)) from ED_ADMIN.PERF_ANALYSIS where content_id='"+ topicID +"' and Operation ='" + ops + "')\n"+
                    "group by content_id,operation order by content_id,operation,StartedTime desc\n";
            //System.out.print("QUERY === " + query);
            ResultSet resultSet = stmt.executeQuery(query);
            try {
                if(!resultSet.isBeforeFirst())
                {
                    System.out.println("Data was not found for operation -> " + ops + "\n");
                    contentId = topicID;
                    action = ops;
                    startTime ="0000-00-00 00:00:00";
                    endTime ="0000-00-00 00:00:00";
                    totalTimeInSec="0";
                    comment="Data not Available";
                }
                else {
                    while (resultSet.next()) {
                        contentId = resultSet.getString("content_ID");
                        action = resultSet.getString("operation");
                        startTime = resultSet.getString("StartedTime");
                        endTime = resultSet.getString("CompletedTime");
                        totalTimeInSec = resultSet.getString("TimeDiff_in_Sec");
                    }
                }
            } catch (ArrayIndexOutOfBoundsException |NullPointerException e) {
                System.out.println("Exception thrown - please check the field might be empty .... " + e);
            }
            //Query to retrieve content version
            query1 = "select content_id,operation,cast(major_version as varchar)+'.'+cast(minor_version as varchar) as Content_Version\n" +
                    "from ED_ADMIN.PERF_ANALYSIS \n"+
                    "where content_id=" + topicID +" and Operation ='" + ops + "' and status='"+ status[1] +"'\n"+
                    "and created_date=(select max(created_date) from ED_ADMIN.PERF_ANALYSIS\n"+
                    "where content_id=" + topicID +" and Operation ='"+ ops +"'\n"+ "and status='"+ status[1] +"'\n"+
                    "and CREATED_DATE<(select convert(date,max(CREATED_DATE)) from ED_ADMIN.PERF_ANALYSIS where content_id='"+ topicID +"' and Operation ='" + ops + "' and status='"+status[1]+"'))\n";

            //System.out.print("QUERY1 === " + query1);
            ResultSet resultSet1 = stmt.executeQuery(query1);
            try {
                if(!resultSet1.isBeforeFirst())
                {
                    System.out.println("Data was not found for operation -> " + ops);
                    contentVersion="";
                }
                else {
                    while (resultSet1.next()) {
                        contentVersion = resultSet1.getString("Content_Version");
                    }
                }
            } catch (ArrayIndexOutOfBoundsException |NullPointerException e) {
                System.out.println("Exception thrown - please check the field might be empty .... " + e);
            }

            //Saving Data in MySql DB
            try {

                String sqlQuery = "INSERT INTO " + ObjectRepository.Perf_analysis_table + " (" + "Content_Id" + "," +
                        "Content_Version" + "," + "Operation" + "," + "Total_Time_Sec" + "," + "Start_Time" + "," +
                        "End_Time" + "," + "Region" + "," + "Comment" + "," + "Created_Time" +")"
                        + " Values " + "(" + "'" + contentId + "'" + "," + "'" + contentVersion + "'" + ","
                        + "'" + action + "'" + "," + "'" + totalTimeInSec + "'" + "," + "'"
                        + startTime + "'" + "," + "'" + endTime + "'" + "," + "'" + region + "'" + "," + "'" + comment + "'" + "," + "'" + timestamp + "'"
                        + ")";

                String sqlQueryTemp = "INSERT INTO " + ObjectRepository.Perf_analysis_table +"_temp" + " (" + "Content_Id" + "," +
                        "Content_Version" + "," + "Operation" + "," + "Total_Time_Sec" + "," + "Start_Time" + "," +
                        "End_Time" + "," + "Region" + "," + "Comment" + "," + "Created_Time" + "," + "data_type" +")"
                        + " Values " + "(" + "'" + contentId + "'" + "," + "'" + contentVersion + "'" + ","
                        + "'" + action + "'" + "," + "'" + totalTimeInSec + "'" + "," + "'"
                        + startTime + "'" + "," + "'" + endTime + "'" + "," + "'" + region + "'" + "," + "'" + comment + "'" + "," + "'" + timestamp + "'"
                        +  "," + "'" + dataType + "'" + ")";

               // System.out.println("SQLQuery = " + sqlQuery);
                //System.out.println("SQLQuery = " + sqlQueryTemp);
                // Execute the SQL Query. to save data in Perf_analysis_data and Perf_analysis_data_temp
                sqlStmt.executeUpdate(sqlQuery);
                sqlStmt.executeUpdate(sqlQueryTemp);

            } catch (Exception E) {
                E.printStackTrace();
                System.out.println("Exception occurred while inserting data in TestDB ");
            }

        }
        System.out.println("**** Reading perf DataBeforeDeploymentFromDB Completed ****\n");
        con.close();
        sqlCon.close();
    }

    //Reading data from temp table
    public List<String> getDatafromTempTable() {

        System.out.println("**** Reading Temp table data Started ****\n");
        try {
            String sqlQuery = "Select Total_time_sec From " + ObjectRepository.Perf_analysis_table + "_temp" +
                    " order by operation";
           // System.out.println("SQLQuery = " + sqlQuery);
            Connection sqlCon = ConnectToMySQLTestDB.getInsstance().getMySqlConnection();
            Statement sqlStmt = sqlCon.createStatement();
            // Execute the SQL Query. to save data in Perf_analysis_data_temp
            ResultSet resultSet = sqlStmt.executeQuery(sqlQuery);
            List<Integer> ls = new ArrayList<Integer>();
            while(resultSet.next())
            {
                ls.add(resultSet.getInt("Total_Time_Sec"));
            }
            System.out.println("Total Time for prev and current = "+ls);
            List<String> timeDiff = new ArrayList<String>();
            for(int i=0 ;i <ls.size() -1 ; i++ )
            {
                if(ls.get(i) !=0 && ls.get(i+1) !=0 ) {
                    int diff = ls.get(i) - ls.get(i + 1);
                    timeDiff.add(Integer.toString(diff));
                }else
                    timeDiff.add("Data Missing");
                i++;
            }
            //System.out.println("TimeDiff = "+ timeDiff);
            sqlCon.close();
            System.out.println("**** Reading Temp table data Completd ****\n");
            return timeDiff;
        } catch (Exception E) {
            E.printStackTrace();
            System.out.println("Exception occurred while Calculating time diff between prev and current ");
            return null;
        }
    }

    public void sendReportEmail(String emailRecipient ) throws SQLException, MessagingException {
        System.out.println("**** Sending Email Report Started ****\n");
        BuildReportFromDB.buildReportFromPerfData( ObjectRepository.Perf_analysis_table + "_temp","Operation ");
        Email.buildSimpleMessageForPerfAnalysisTest(emailRecipient);
        System.out.println("**** Sending Email Report Completed ****\n");


    }

}

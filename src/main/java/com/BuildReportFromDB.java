package com;

import util.ConnectToMySQLTestDB;
import util.GlobalVariables;

import javax.swing.text.html.HTML;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rgupta on 2/26/2019.
 */
public class BuildReportFromDB {
    public  static int passCount = 0;
    public  static int failCount = 0;
    public static String reportHtml = "";

   public static String css = "<!DOCTYPE html>\n" +
           "<html>\n" +
           "<table>\n" +
           "<thead> </thead>\n" +
           "\t\t\t<style>\n" +
           "\t\t\ttable, th, td {\n" +
           "\t\t\t\tborder: 1px solid #BABABA;\n" +
           "\t\t\t\tborder-collapse: collapse;\n" +
           "\t\t\t}\n" +
           "            table {\n" +
           "\t\t\ttable-layout:auto; \n" +
           "\t\t\tmax-width:767px;\n" +
           "\t\t\tfont-family: arial, sans-serif;\n" +
           "\t\t\t}\n" +
           "            th {\n" +
           "\t\t\tstyle =\"color:blue\";\n" +
           "\t\t\ttext-align: left;\n" +
           "\t\t\tpadding: 8px;\n" +
           "\t\t\tword-wrap:break-word;\n" +
           "\t\t\t}\n" +
           "            td {\n" +
           "\t\t\tmax-width:767px;\n" +
           "\t\t\ttext-align: left;\n" +
           "\t\t\tpadding: 8px;\n" +
           "\t\t\t}\n" +
           "\t\t\ttr:nth-child(even){background-color: #f2f2f2;}\n" +
           "           \n" +
           "            .pass, .pass a {\n" +
           "                color: #04853A;\n" +
           "            }\n" +
           "   \n" +
           "            .fail, .fail a {\n" +
           "             color: #b60808;\n" +
           "            }\n" +
           "            \n" +
           "           </style>";


    public static void  buildReport(String tableName, ArrayList<String> tableHeadersToBeIgnored, String orderby) throws Exception {
        makeHtml(tableName,tableHeadersToBeIgnored, orderby);
        //System.out.println("The HTML : "+ reportHtml);
    }

    public static String getReportHtml(){
        return reportHtml;
    }

    private static void  makeHtml(String tableName, ArrayList<String> tableHeadersToBeIgnored,String orderby) throws Exception {
        ArrayList<String> tableHeaderNames = new ArrayList<String>();


        //ignoreTableHeaderNames.add("Status");
        Connection con = ConnectToMySQLTestDB.getInsstance().getMySqlConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = null;
        String name = null;

        String query = "Select *  from "+tableName +" order by "+orderby;
        System.out.println(query);
        rs = stmt.executeQuery(query);

        /* NOW CREATE TABLE HEADING AS HTML HEADERS IN THE REPORT*/
        ResultSetMetaData rsmd = rs.getMetaData();

        String startTableRowHtml = "<tr>\n";
        String TableHeadinghtml = "";
        String endTableRowHtml = "</tr>\n"; //</thead>


        //this is loop to check if user asked any table headers not to be included
        if (tableHeadersToBeIgnored!= null){
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                 name = rsmd.getColumnName(i);
                boolean found = false;
                for(int j=0; j<tableHeadersToBeIgnored.size();j++){
                    String s = tableHeadersToBeIgnored.get(j);
                    if(name.equals(s)){
                        System.out.println("I found what to take out.");
                        found = true;
                    }
                }
                if(found)
                    continue;
                tableHeaderNames.add(name);
            }
        }
        else{
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                 name = rsmd.getColumnName(i);

                tableHeaderNames.add(name);
            }
        }



        /* NOW CREATE TABLE HEADING VALUES AS HTML IN THE REPORT*/
        String tableDataHTML = "";
        while (rs.next()){
            tableDataHTML =tableDataHTML + "<tr>";
            for ( int i =0 ; i<tableHeaderNames.size(); i++)
                tableDataHTML = tableDataHTML + "\n<td class=\"" + rs.getString("status").toLowerCase() + "\">"
                        + rs.getString(tableHeaderNames.get(i)) + "</td>\n";

            if (rs.getString("status").toLowerCase().equalsIgnoreCase("pass"))
                passCount++;
            else if (rs.getString("status").toLowerCase().equalsIgnoreCase("fail"))
                failCount++;

            tableDataHTML = tableDataHTML + "</tr>\n";
        }//DONE CREEATING CELL VALUES



        /* Now replace the header name with underscore to space then form the header "TH" HTML*/
        for (int i = 0; i < tableHeaderNames.size(); i++) {
            name = tableHeaderNames.get(i);
            name = name.replace("_", " ");
            TableHeadinghtml = TableHeadinghtml + "<th align=\"center\"; bgcolor=\"#C2DDF6\">" + name + "</th>\n";
        }
        String tableHeaderHTML = startTableRowHtml + TableHeadinghtml + endTableRowHtml;
        //DONE CREEATING HEADERS



        con.close();
        reportHtml = reportHtml + css  + tableHeaderHTML + tableDataHTML + "</table>\n</table>\n</html>";
        System.out.println("*************************HTML Report *********************");
        System.out.println(reportHtml);
    }

    public static void addMessage(String message){
        reportHtml = reportHtml + "\n"+message;
    }
    public static void buildReportFromPerfData(String tableName, String orderby) throws SQLException {
        try{
        String cssLocal = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<table>\n" +
                "<thead> </thead>\n" +
                "\t\t\t<style>\n" +
                "\t\t\ttable, th, td {\n" +
                "\t\t\t\tborder: 1px solid #BABABA;\n" +
                "\t\t\t\tborder-collapse: collapse;\n" +
                "\t\t\t}\n" +
                "            table {\n" +
                "\t\t\ttable-layout:auto; \n" +
                "\t\t\tmax-width:94%;\n" +
                "\t\t\twidth:100%;\n" +
                "\t\t\tfont-family: arial, sans-serif;\n" +
                "\t\t\t}\n" +
                "            th {\n" +
                "\t\t\tstyle =\"color:blue\";\n" +
                "\t\t\ttext-align: left;\n" +
                "\t\t\tpadding: 8px;\n" +
                "\t\t\tword-wrap:break-word;\n" +
                "\t\t\t}\n" +
                "            td {\n" +
                "\t\t\tmax-width:767px;\n" +
                "\t\t\ttext-align: left;\n" +
                "\t\t\tpadding: 8px;\n" +
                "\t\t\t}\n" +
                "             td {\n" +
                "\t\t\tmax-width:767px;\n" +
                    "\t\t\ttext-align: left;\n" +
                    "\t\t\tpadding: 8px;\n" +
                    "\t\t\t}\n" +
                "            \n" +
                "           </style>";

        ArrayList<String> tableHeaderNames = new ArrayList<String>();
        ResultSet rs = null;
        String name = null;
        ResultSetMetaData rsmd = null;
        Connection con =null;
        Statement stmt =null;
        String query;
        ReadPerfAnalysisDB obj = new ReadPerfAnalysisDB();
        List<String> timeDiff = obj.getDatafromTempTable();
        //ignoreTableHeaderNames.add("Status");
        try {
            con = ConnectToMySQLTestDB.getInsstance().getMySqlConnection();
            stmt= con.createStatement();
            query = "select Data_Type,Content_Id, Operation, Content_Version, Total_Time_Sec, DATE_FORMAT(Start_Time , '%d-%m-%Y %T') as Start_Time" +
                    ", DATE_FORMAT(End_Time , '%d-%m-%Y %T') as End_Time, Region, Comment from "
                    + tableName + " order by " + orderby;
            System.out.println("QUERY = " + query);
            rs = stmt.executeQuery(query);
            rsmd = rs.getMetaData();
            for (int i =1; i<= rsmd.getColumnCount(); i++) {
                name = rsmd.getColumnName(i);
                tableHeaderNames.add(name);
            }
        }catch(Exception e)
        {
            System.out.println("Exception occured while reading data from "+tableName+" table");
        }

        String startTableRowHtml = "<tr>\n";
        String TableHeadinghtml = "";
        String endTableRowHtml = "</tr>\n"; //</thead>

        /* NOW CREATE TABLE HEADING VALUES AS HTML IN THE REPORT*/
        String tableDataHTML = "";
        int j=0;
        int count =0;
        String color ="";
        while (rs.next()){
            tableDataHTML =tableDataHTML + "<tr>";
            for ( int i =0 ; i<tableHeaderNames.size(); i++) {
                if(i==1) {
                    if(j<=timeDiff.size()) {
                        if(count%2==0) {
                            if(!timeDiff.get(j).equals("Data Missing")){
                            if (Integer.valueOf(timeDiff.get(j)) <= 0) {
                                color = "bgcolor=\"green\"";
                            } else if (Integer.valueOf(timeDiff.get(j)) > 0)
                                color = "bgcolor=\"red\"";
                        }else
                                color = "bgcolor=\"yellow\"";
                            tableDataHTML = tableDataHTML + "\n<td class=\"" + "WK-PerfTest\"" +" ; " + color + "\">"
                                    + timeDiff.get(j) + "</td>\n";
                            j++;
                        }else
                            tableDataHTML = tableDataHTML + "\n<td class=\"" + "WK-PerfTest" + "\">"
                                    + "" + "</td>\n";

                    }
                }
                tableDataHTML = tableDataHTML + "\n<td class=\"" + "WK-PerfTest" + "\">"
                        + rs.getString(tableHeaderNames.get(i)) + "</td>\n";
            }
            tableDataHTML = tableDataHTML + "</tr>\n";
            count++;
        }
        //DONE CREEATING CELL VALUES

        /* Now replace the header name with underscore to space then form the header "TH" HTML*/
        for (int i = 0; i < tableHeaderNames.size(); i++) {
            name = tableHeaderNames.get(i);
            name = name.replace("_", " ");
            if(i==1) {
                TableHeadinghtml = TableHeadinghtml + "<th align=\"center\"; bgcolor=\"#C2DDF6\">" + "Compare Result" + "</th>\n";
            }
                TableHeadinghtml = TableHeadinghtml + "<th align=\"center\"; bgcolor=\"#C2DDF6\">" + name + "</th>\n";
        }
        String tableHeaderHTML = startTableRowHtml + TableHeadinghtml + endTableRowHtml;
        //DONE CREEATING HEADERS
        con.close();
        reportHtml = reportHtml+ cssLocal + tableHeaderHTML + tableDataHTML + "</table>\n</table>\n</html>";
        System.out.println("*************************HTML Report *********************");
        System.out.println(reportHtml);
        }catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("Exception occured while building html report for email \n");
        }

    }
    public static boolean buildReportFromCertificate(String tableName, long days) throws SQLException {
        boolean flag = false;
        try{
            String cssLocal = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<table>\n" +
                    "<thead> </thead>\n" +
                    "\t\t\t<style>\n" +
                    "\t\t\ttable, th, td {\n" +
                    "\t\t\t\tborder: 1px solid #BABABA;\n" +
                    "\t\t\t\tborder-collapse: collapse;\n" +
                    "\t\t\t}\n" +
                    "            table {\n" +
                    "\t\t\ttable-layout:auto; \n" +
                    "\t\t\tmax-width:94%;\n" +
                    "\t\t\twidth:100%;\n" +
                    "\t\t\tfont-family: arial, sans-serif;\n" +
                    "\t\t\t}\n" +
                    "            th {\n" +
                    "\t\t\tstyle =\"color:blue\";\n" +
                    "\t\t\ttext-align: left;\n" +
                    "\t\t\tpadding: 8px;\n" +
                    "\t\t\tword-wrap:break-word;\n" +
                    "\t\t\t}\n" +
                    "            td {\n" +
                    "\t\t\tmax-width:767px;\n" +
                    "\t\t\ttext-align: left;\n" +
                    "\t\t\tpadding: 8px;\n" +
                    "\t\t\t}\n" +
                    "             td {\n" +
                    "\t\t\tmax-width:767px;\n" +
                    "\t\t\ttext-align: left;\n" +
                    "\t\t\tpadding: 8px;\n" +
                    "\t\t\t}\n" +
                    "            \n" +
                    "           </style>";

            ArrayList<String> tableHeaderNames = new ArrayList<String>();
            ResultSet rs = null;
            String name = null;
            ResultSetMetaData rsmd = null;
            Connection con =null;
            Statement stmt =null;
            String query ="";
            //ignoreTableHeaderNames.add("Status");
            try {
                con = ConnectToMySQLTestDB.getInsstance().getMySqlConnection();
                stmt= con.createStatement();
                if(GlobalVariables.certFlagMonthly) {
                    query = "select app_name,app_url,Environment,expiry_date,expiry_in_days from " + tableName ;
                }
                if(!GlobalVariables.certFlagMonthly) {
                    query = "select app_name,app_url,Environment,expiry_date,expiry_in_days,certificate_status from " + tableName + " where expiry_in_days = " + days;
                }
                System.out.println("QUERY = " + query);
                rs = stmt.executeQuery(query);
                rsmd = rs.getMetaData();
                System.out.println("colum count =" + rsmd.getColumnCount());
                for (int i =1; i<= rsmd.getColumnCount(); i++) {
                    name = rsmd.getColumnName(i);
                    tableHeaderNames.add(name);
                }
            }catch(Exception e)
            {
                System.out.println("Exception occured while reading data from "+tableName+" table");
            }

            String startTableRowHtml = "<tr>\n";
            String TableHeadinghtml = "";
            String endTableRowHtml = "</tr>\n"; //</thead>

            /* NOW CREATE TABLE HEADING VALUES AS HTML IN THE REPORT*/
            String tableDataHTML = "";
            while (rs.next()){
                flag = true;
                tableDataHTML =tableDataHTML + "<tr>";
                for ( int i =0 ; i<tableHeaderNames.size(); i++) {
                    if(tableHeaderNames.get(i).equalsIgnoreCase("certificate_status"))
                    {
                        tableDataHTML = tableDataHTML + "\n<td class=\"" + "WK-CertificateClass\" ; bgcolor=\"yellow\">"
                                + rs.getString(tableHeaderNames.get(i)) + "</td>\n";

                    }else
                    tableDataHTML = tableDataHTML + "\n<td class=\"" + "WK-CertificateClass" + "\">"
                            + rs.getString(tableHeaderNames.get(i)) + "</td>\n";
                }
                tableDataHTML = tableDataHTML + "</tr>\n";
            }
            //DONE CREEATING CELL VALUES

            /* Now replace the header name with underscore to space then form the header "TH" HTML*/
            for (int i = 0; i < tableHeaderNames.size(); i++) {
                name = tableHeaderNames.get(i);
                name = name.replace("_", " ").toUpperCase();
                TableHeadinghtml = TableHeadinghtml + "<th align=\"center\"; bgcolor=\"#C2DDF6\">" + name + "</th>\n";
            }
            String tableHeaderHTML = startTableRowHtml + TableHeadinghtml + endTableRowHtml;
            //DONE CREEATING HEADERS
            con.close();
            reportHtml = reportHtml+ cssLocal + tableHeaderHTML + tableDataHTML + "</table>\n</table>\n</html>";
            System.out.println("*************************HTML Report *********************");
            System.out.println(reportHtml);
        }catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("Exception occured while building html report for email \n");
        }
     return flag;
    }
}

package com;

import util.ConnectToLinuxBox;
import util.ConnectToMySQLTestDB;
import util.GlobalVariables;
import util.ObjectRepository;

import javax.mail.MessagingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReadLicenseExpiry {
    static ReadExportPerfStatisticsXml ob = new ReadExportPerfStatisticsXml();
    public static List<String> serverUrl = new ArrayList<String>();
    public static List<String> serverUrl1 = new ArrayList<String>();
    public static Connection sqlCon = null;
    public static Statement sqlStmt = null;
    public static Statement sqlStmt1 = null;
    public static String formattedDate = null;
    public static long diff;
    public static long noDays;
    public static String licenseStatus;
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    public static String currentDate = simpleDateFormat.format(new Date());
    public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    public static Date date1 = null;
    public static Date date2 = null;
    public static long numberDays;

    public static void readServerURLFromTestDB() {

        System.out.println("**** Reading URLs Data from TestDB Started ****\n");
        try {
            String sqlQuery = "Select app_url From " + ObjectRepository.Certificate_data_table + " where app_name not like '%PDFC%' and app_name not like '%CONLOAD%'";
            String sqlQuery1 = "Select app_url From " + ObjectRepository.Certificate_data_table + " where app_name like '%PDFC%' or app_name like '%CONLOAD%'";

            // System.out.println("SQLQuery = " + sqlQuery);
            sqlCon = ConnectToMySQLTestDB.getInsstance().getMySqlConnection();
            sqlStmt = sqlCon.createStatement();
            sqlStmt1 = sqlCon.createStatement();
            // Execute the SQL Query. to save data in Perf_analysis_data_temp
            ResultSet resultSet = sqlStmt.executeQuery(sqlQuery);
            ResultSet resultSet1 = sqlStmt1.executeQuery(sqlQuery1);
            while (resultSet.next()) {
                serverUrl.add(resultSet.getString("app_url"));
            }
            while (resultSet1.next()) {
                serverUrl1.add(resultSet1.getString("app_url"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeExpiryDateToTestDB(String url, String formattedDate, String status, long noDays) {

        String updateQuery;
        System.out.println("**** Updating Expiry dates in TestDB ****\n");
        try {
            if (formattedDate != null) {
                updateQuery = "UPDATE " + ObjectRepository.Certificate_data_table + " SET expiry_date = '" + formattedDate +
                        "' , certificate_status = '" + status + "' , expiry_in_days = '" + noDays +
                        "' WHERE app_url='" + url + "'";
                sqlStmt.executeUpdate(updateQuery);
            }
            if (formattedDate == null) {
                updateQuery = "UPDATE " + ObjectRepository.Certificate_data_table + " SET expiry_date = null " +
                        ", certificate_status = '" + status +
                        "' WHERE app_url='" + url + "'";
                sqlStmt.executeUpdate(updateQuery);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeExpiryDateToTestDB(String url, String status, long noDays) {

        String updateQuery;
        System.out.println("**** Updating Expiry dates in TestDB ****\n");
        try {
            updateQuery = "UPDATE " + ObjectRepository.Certificate_data_table +
                    " SET certificate_status = '" + status + "' , expiry_in_days = '" + noDays +
                    "' WHERE app_url='" + url + "'";
            sqlStmt.executeUpdate(updateQuery);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getCertificateExpiryDate(int days) {

        System.out.println("**** Fetching Expiry dates from server for each APP ****\n");
        numberDays = days;
        GlobalVariables.certFlagMonthly = Boolean.valueOf(System.getProperty("certFlagMonthly"));
        readServerURLFromTestDB();
        try {
            ConnectToLinuxBox connectToLinuxBox = ConnectToLinuxBox.connect("QA");
            String expiryDate;
            String extractedDate;
            for (String url : serverUrl) {
                licenseStatus = "";
                expiryDate = connectToLinuxBox.runCommand("curl " + url + " -vI --stderr - | grep \"expire date\"");
                if (expiryDate != null) {
                    extractedDate = expiryDate.split("date:")[1].trim();
                    formattedDate = ob.convertStringDateFormat(extractedDate, new SimpleDateFormat("MMM dd hh:mm:ss yyyy z"), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
                    date1 = format.parse(formattedDate);
                    date2 = format.parse(currentDate);
                    diff = date1.getTime() - date2.getTime();
                    noDays = diff / (24 * 60 * 60 * 1000);
                    if (noDays == numberDays) {
                        licenseStatus = "Renew certificate as early as possible";
                    }
                } else {
                    formattedDate = null;
                    licenseStatus = "Expiry date not found";
                }
                writeExpiryDateToTestDB(url, formattedDate, licenseStatus, noDays);
            }
            expiryDateForPDFC();
            sqlCon.close();
            GlobalVariables.session.disconnect();
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public static void expiryDateForPDFC() throws SQLException, ParseException {

        ResultSet resultSet = null;
        for (String url : serverUrl1) {
            licenseStatus = "";
            String sqlQuery = "Select expiry_date From " + ObjectRepository.Certificate_data_table + " where app_url ='" + url + "'";
            // Execute the SQL Query. to save data in Perf_analysis_data_temp
            resultSet = sqlStmt.executeQuery(sqlQuery);
            while (resultSet.next()) {
                date1 = resultSet.getDate("expiry_date");
            }
            if (date1 != null) {
                date2 = format.parse(currentDate);
                diff = date1.getTime() - date2.getTime();
                noDays = diff / (24 * 60 * 60 * 1000);
                if (noDays == numberDays) {
                    licenseStatus = "Renew certificate as early as possible";
                }
                writeExpiryDateToTestDB(url, licenseStatus, noDays);
            }

        }
    }

    public static void sendReportEmail(String emailRecipient) throws SQLException, MessagingException {

        System.out.println("**** Sending Email Report Started ****\n");
        boolean flag = BuildReportFromDB.buildReportFromCertificate(ObjectRepository.Certificate_data_table, numberDays);
        if (flag) {
            Email.buildSimpleMessageForCertificateDataTest(emailRecipient);
        }
        System.out.println("**** Sending Email Report Completed ****\n");
    }
}

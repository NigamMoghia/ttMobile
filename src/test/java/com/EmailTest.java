package com;

import org.testng.annotations.Test;
import util.GetSelectQueryResultSet;
import util.ObjectRepository;

import java.util.ArrayList;

/**
 * Created by rgupta on 8/31/2017.
 */
public class EmailTest {


    @Test
    public void sendEMail() throws Exception {
        String query = "Select Status from " + ObjectRepository.Export_test_table +
                " where Export_Region = 'Production' and Export_Type like 'PR'";
        String status = GetSelectQueryResultSet.getSingleColumnValue(query, "MySQL");

        String query1 = "Select Message from " + ObjectRepository.Export_test_table +
                " where Export_Region = 'Production' and Export_Type like 'PR'";
        String mess = "";
        if (status.equalsIgnoreCase("pass"))
            mess = "<p style=\"color:blue\"><strong>" + GetSelectQueryResultSet.getSingleColumnValue(query1, "MySQL")
                    + "</strong><p>\n";
        else
            mess = "<p style=\"color:red\"><strong>" + GetSelectQueryResultSet.getSingleColumnValue(query1, "MySQL")
                    + "</strong><p>\n";
        ArrayList<String> ignoreTableHeaderNames = new ArrayList<String>();
        ignoreTableHeaderNames.add("Status");
        ignoreTableHeaderNames.add("SequenceNo");

        BuildReportFromDB.addMessage(mess);
        BuildReportFromDB.buildReport("export_test_tbl_copy", ignoreTableHeaderNames, "SequenceNo ");
        //Email.sendEmailTo("vikas.anand@wolterskluwer.com");
        Email.sendEmailTo("CE-TO-DL-EE-Content-Delivery@wolterskluwer.com");
    }

}
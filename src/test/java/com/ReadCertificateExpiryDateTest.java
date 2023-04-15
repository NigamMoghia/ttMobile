package com;

import org.testng.annotations.Test;
import util.GlobalVariables;

public class ReadCertificateExpiryDateTest {
    ReadLicenseExpiry obj = new ReadLicenseExpiry();

    @Test(enabled = true, priority = 0)
    public void readCertificateExpiryDateTestFromTestDB() throws Exception {
        obj.getCertificateExpiryDate(15);
    }

    @Test(enabled = true, priority = 1)
    public void emailCertificateData() throws Exception {
        if (GlobalVariables.certFlagMonthly) {
            obj.sendReportEmail("Dhananjay.Tambe@wolterskluwer.com,Rithika.Gupta@wolterskluwer.com,Evgenia.Boitsev@wolterskluwer.com,Dhilip.Balasuriyan@wolterskluwer.com,Vikas.Anand@wolterskluwer.com,Jose.Ramirez@wolterskluwer.com,MDSaidul.Haque@wolterskluwer.com,Enrique.Callejon@wolterskluwer.com");
        }
        if (!GlobalVariables.certFlagMonthly) {
            //obj.sendReportEmail("vikas.anand@wolterskluwer.com,Rithika.Gupta@wolterskluwer.com");
            obj.sendReportEmail("Editorial_Developer@uptodate.com");
        }
    }
}
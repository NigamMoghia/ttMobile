package util;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by RGupta on 3/15/2018.
 */
public class UpdateCredentialManager {


    public  static void addOrEditCredential(HashMap<String, String> params){

        //FIRST DELETE ALL CREDENTIALS
        deleteCredential (params);

        // GET ALL PARAMETERS USER PROVIDED
        String userName = "ad01q\\"+ params.get("UserName");
        String password = params.get("Password");
        String stagingPassword = params.get("StagingPassword");
        String StatingUserName = "utd\\"+params.get("UserName");


        //SET THE VARIABLES WITH VALUES BASED ON THE TEST ENVIRONMENT
        String editorialQAAddress = "edweb01q";

        String editorialDevAddress = "edweb01d";

        String editorialStagingAddress = "editorial00s";
        String sharepointStagingAddress = "edsp0203s";

        String editorialProductionAddress = "editorial00p";
        String sharepointProductionAddress = "edsp02p";
        String editorialProductionAddress2 = "edapp05p";



        boolean status = false;
        try {
            Process p = Runtime.getRuntime().exec("cmdkey /add:" + editorialQAAddress + " /user:" + userName + " /pass:" + password);

            p = Runtime.getRuntime().exec("cmdkey /add:" + editorialDevAddress + " /user:" + userName + " /pass:" + password);

            p = Runtime.getRuntime().exec("cmdkey /add:" + editorialStagingAddress + " /user:" + StatingUserName + " /pass:" + stagingPassword);
            p = Runtime.getRuntime().exec("cmdkey /add:" + sharepointStagingAddress + " /user:" + StatingUserName + " /pass:" + stagingPassword);

            p = Runtime.getRuntime().exec("cmdkey /add:" + editorialProductionAddress + " /user:" + StatingUserName + " /pass:" + stagingPassword);
            p = Runtime.getRuntime().exec("cmdkey /add:" + sharepointProductionAddress + " /user:" + StatingUserName + " /pass:" + stagingPassword);
            p = Runtime.getRuntime().exec("cmdkey /add:" + editorialProductionAddress2 + " /user:" + StatingUserName + " /pass:" + stagingPassword);

            status = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(status)
            System.out.println("Successfully modified the credetial manager for user "+ userName);

    }






    public static boolean deleteCredential (HashMap<String, String> params){


        //SET THE VARIABLES WITH VALUES BASED ON THE RE
        String editorialQAAddress = "edweb01q";
        String editorialStagingAddress = "editorial00s";
        String editorialDevAddress = "edweb01d";
        String sharepointStagingAddress = "edsp0203s";
        String editorialProductionAddress = "editorial00p";
        String sharepointProductionAddress = "edsp02p";

        boolean status = false;
        try {
            Process p = Runtime.getRuntime().exec("cmdkey /delete:" + editorialQAAddress);
            p = Runtime.getRuntime().exec("cmdkey /delete:" + editorialStagingAddress);
            p = Runtime.getRuntime().exec("cmdkey /delete:" + editorialDevAddress);
            p = Runtime.getRuntime().exec("cmdkey /delete:" + sharepointStagingAddress);
            p = Runtime.getRuntime().exec("cmdkey /delete:" + editorialProductionAddress);
            p = Runtime.getRuntime().exec("cmdkey /delete:" + sharepointProductionAddress);
            status = true;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return status;
    }
}

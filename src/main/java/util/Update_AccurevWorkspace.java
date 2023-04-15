package util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by RGupta on 1/2/2018.
 */
public class Update_AccurevWorkspace {
    private  String localpath = null;
    private  String environment = null;

    public  Update_AccurevWorkspace(String env) throws Exception{
        this.environment = env;
        String userId =null, pwd=null, serverName=null;

        Connection mySql = ConnectToMySQLTestDB.getInsstance().getMySqlConnection();
        String query = "Select * from User_tbl where Region = 'accurev'";
        Statement stmt = mySql.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        if(!rs.isBeforeFirst())
            System.out.println("There is no user mentioned in User_Tbl for accurev");
        while (rs.next()) {
            userId = rs.getString("UserName");
            pwd = rs.getString("Password");
            serverName = rs.getString("ServerName");
        }

        update(env, userId, pwd, serverName);

    }

    public void update(String env, String userID, String pwd, String server){
         try {
            String[] command =
                    {
                            "C:\\Windows\\System32\\cmd.exe",

                    };

            Process p = Runtime.getRuntime().exec(command);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            new Thread(new SyncPipe_CMDLine(p.getErrorStream(), System.err)).start();
            Thread inputThread = new Thread(new SyncPipe_CMDLine(p.getInputStream(), baos));
            inputThread.start();
            PrintWriter stdin = new PrintWriter(p.getOutputStream());

            switch (env.toUpperCase()) {
                case "QA":
                    stdin.println("cd c:\\Users\\RGupta\\accurev\\QA_6.0");
                    localpath = "c:\\Users\\RGupta\\accurev\\QA_6.0";
                    break;
                case "DEV":
                    stdin.println("cd c:\\Users\\RGupta\\accurev\\Integration_6.0");
                    localpath = "c:\\Users\\RGupta\\accurev\\Integration_6.0";
                    break;
                case "STAGING":
                    stdin.println("cd c:\\Users\\RGupta\\accurev\\ReleasePending_6.0");
                    localpath = "C:\\MyFiles\\Resources";//"c:\\Users\\RGupta\\accurev\\ReleasePending_6.0";
                    break;
            }
            stdin.println("Path");
            stdin.println("accurev login -H "+server+" "+userID+" "+pwd);
            stdin.println("accurev update");

            stdin.println("cd " + localpath + "\\Source\\UpToDate.Editorial.Export\\Resources");
            localpath = localpath + "\\Source\\UpToDate.Editorial.Export\\Resources";

            stdin.close();

            int returnCode = p.waitFor();
            System.out.println("Return code = " + returnCode);
            inputThread.join();
            System.out.println(baos.toString());
        }
        catch(IOException | InterruptedException e){
            System.out.println("EXCEPTION Thrown \n"+e);
        }

    }

    public  String getAccurevLocalWrokspacePath(){
        return localpath;
    }
}

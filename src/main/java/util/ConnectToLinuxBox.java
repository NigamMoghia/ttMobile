package util;

import com.jcraft.jsch.*;


import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by RGupta on 3/19/2018.
 */
public class ConnectToLinuxBox {

    // Referenced : http://www.jcraft.com/jsch/examples/Shell.java.html to originally write this class toi connect

    private static String HOST = null;
    private static String USERNAME = null;
    private static String PWD = null;
    public static  Session session = null;

    private static  void setHostUserNameAndPWD(String evironment) {
        GlobalVariables.conbld_serverName=null;
       try {
           Connection mySQL = ConnectToMySQLTestDB.getInsstance().getMySqlConnection();
           Statement stmt = mySQL.createStatement();
           String query = "Select * from  User_tbl where type ='linux box' and Region = '"  + evironment.toLowerCase()+"'";
            ResultSet rs = stmt.executeQuery(query);
           System.out.println(query+ " this query resulted ");
           if(!rs.isBeforeFirst()){
               System.out.println(query+ " this query resulted in no records");
           }
           rs.next();
               HOST = rs.getString("ServerName");
               USERNAME = rs.getString("UserName");
               PWD = rs.getString("Password");

       }catch (Exception e){
           System.out.println(e);
       }
        GlobalVariables.conbld_serverName=HOST;
    }

    public static ConnectToLinuxBox connect(String env) {

        setHostUserNameAndPWD(env);
        try {
            JSch jsch = new JSch();
            java.util.Properties config = new java.util.Properties();
            config.setProperty("StrictHostKeyChecking", "no");
            //config.put("PreferredAuthentications", PWD);

            // enter username and ipaddress for machine you need to connect
            session = jsch.getSession(USERNAME, HOST, 22);
            // Set password.
            session.setPassword(PWD);
            session.setConfig(config);

            session.connect();
            GlobalVariables.session = session;
        }
        catch(Exception e){
            System.out.println(e);
        }
        return new ConnectToLinuxBox();
    }

    public  String runCommand(String command) {

        String result=null;
        // enter any command you need to execute
        System.out.println("Linux command:  "+command);
        try {
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            channel.setInputStream(null);

            ((ChannelExec) channel).setErrStream(System.err);

            InputStream in = channel.getInputStream();

            channel.connect();

            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    result = new String(tmp, 0, i);
                    System.out.print(result);
                }
                if (channel.isClosed()) {
                    System.out.println("exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }
            channel.disconnect();
        } catch (Exception e) {
            System.out.println(e);
        }
        return result;
    }
    public  String changeDirectory(String command) {

        // enter any command you need to execute
        //Referenced: https://stackoverflow.com/questions/27480126/how-to-execute-putty-commands-in-java-to-move-files-between-foldersmv-command
        // And Referenced : http://www.jcraft.com/jsch/examples/Sftp.java.html
        String currentDirectoryPath = null;
        try {
            Channel channel = session.openChannel("sftp");
            ChannelSftp sftp = (ChannelSftp) channel;
            channel.connect();

            sftp.cd(command);
            System.out.println("You are now in Dircetory: "+sftp.pwd());
            currentDirectoryPath = sftp.pwd();
            channel.disconnect();

        } catch (Exception e) {
            System.out.println(e);
        }
        return currentDirectoryPath;
    }


    public String getPathLikeAutoTabComplete(String directoryName, String partialFileOrFolderName){
        String currentDirectoryPath = null;
        String folderName =null;
        try {
            Channel channel = session.openChannel("sftp");
            ChannelSftp sftp = (ChannelSftp) channel;
            channel.connect();
            System.out.println("******You are now connected: "+sftp.pwd());
            sftp.cd(directoryName);
            Vector<ChannelSftp.LsEntry> fileList = sftp.ls(directoryName);
            System.out.println("You are now in Dircetory: "+sftp.pwd());
            for(int i=0; i<fileList.size();i++) {
                String currentFolderOrFileName = fileList.get(i).getFilename().toString().toLowerCase();
                if(currentFolderOrFileName.contains(partialFileOrFolderName.toLowerCase())) {
                    folderName = currentFolderOrFileName;
                    System.out.println("Folder Name Found = "+folderName);
                    break;

                }
            }
            currentDirectoryPath = directoryName + "/"+ folderName;

              channel.disconnect();
              //session.disconnect();
        } catch (Exception e) {
            System.out.println(e);
        }
            return  currentDirectoryPath;
    }



    public ArrayList<String> getAllGraphicIDs(String command){
        ArrayList<String> listOfGraphicIDs = new ArrayList<String>();
            try {
            Channel channel = session.openChannel("sftp");
            ChannelSftp sftp = (ChannelSftp) channel;
            channel.connect();
            sftp.cd(command);
            Vector<ChannelSftp.LsEntry> filelist = sftp.ls(command);
            System.out.println("You are now in Dircetory: "+sftp.pwd());
            for(int i=0; i<filelist.size();i++) {

                String directoryName = filelist.get(i).getFilename().toString();
                //System.out.println(filelist.get(i).getFilename().toString());
               // System.out.println(command + "/" + filelist.get(i).getFilename().toString());

                if (!directoryName.equals(".") && !directoryName.equals("..") && !directoryName.equals("graphic_ids_old_new.xml")) {
                    sftp.cd(command + "/" + directoryName);

                    Vector<ChannelSftp.LsEntry> list = sftp.ls(command + "/" + directoryName);
                     list.remove(0);
                    for (ChannelSftp.LsEntry entry : list) {
                       // System.out.println(entry.getFilename());
                        listOfGraphicIDs.add(entry.getFilename());
                    }
                }
            }
            channel.disconnect();
        } catch (Exception e) {
            System.out.println(e);
        }
        return listOfGraphicIDs;
    }


    public String getXMls(String filePath) throws Exception{
             StringBuilder xmlFile = new StringBuilder();
            Channel channel = session.openChannel("sftp");
            ChannelSftp sftp = (ChannelSftp) channel;
            channel.connect();
            if (!filePath.equals(".") && !filePath.equals("..") ) {

                          //  System.out.println("Reading FIle: " + filePath);
                            InputStream inStream = sftp.get(filePath);
                            BufferedReader br = new BufferedReader((new InputStreamReader(inStream)));

                            String line;
                            while ((line=br.readLine() )!= null) {
                                xmlFile.append(line);
                                xmlFile.append("\n");
                            }
                           // System.out.println(xmlFile.toString());
                              br.close();
                        }
            channel.disconnect();
        return xmlFile.toString();
    }


    public ArrayList<String> getListOfAllSpecialitiesPResentInTopic(String directoryPath) throws Exception{
        ArrayList<String> listOfSubFolders = new ArrayList<String>();
        Channel channel = session.openChannel("sftp");
        ChannelSftp sftpChannel = (ChannelSftp) channel;
        channel.connect();
        sftpChannel.cd(directoryPath);
        System.out.println("You are now in Dircetory: "+sftpChannel.pwd());

        Vector<String> files = sftpChannel.ls("*");

        for (int i = 0; i < files.size(); i++)
        {
            Object obj = files.elementAt(i);
            if (obj instanceof ChannelSftp.LsEntry)
            {
                ChannelSftp.LsEntry entry = ( ChannelSftp.LsEntry) obj;
                if (true && entry.getAttrs().isDir())
                {
                    if (!entry.getFilename().equals(".") && !entry.getFilename().equals(".."))
                    {
                        listOfSubFolders.add(entry.getFilename());
                     //   System.out.println(entry.getFilename());
                    }
                }

            }
        }
        sftpChannel.disconnect();
        return listOfSubFolders;

    }


    public ArrayList<String> getListOfAllFIlesPresentInTopic(String directoryPath) throws Exception{
        ArrayList<String> listOfSubFolders = new ArrayList<String>();
        Channel channel = session.openChannel("sftp");
        ChannelSftp sftpChannel = (ChannelSftp) channel;
        channel.connect();
        sftpChannel.cd(directoryPath);
        System.out.println("You are now in Dircetory: "+sftpChannel.pwd());

        Vector<String> files = sftpChannel.ls("*");

        for (int i = 0; i < files.size(); i++)
        {
            Object obj = files.elementAt(i);
            if (obj instanceof ChannelSftp.LsEntry)
            {
                ChannelSftp.LsEntry entry = ( ChannelSftp.LsEntry) obj;
                if (true && !entry.getAttrs().isDir())
                {
                    if (!entry.getFilename().equals(".") && !entry.getFilename().equals(".."))
                    {
                        listOfSubFolders.add(entry.getFilename());
                        //System.out.println(entry.getFilename());
                    }
                }

            }
        }
        sftpChannel.disconnect();
        return listOfSubFolders;

    }


    public static  String getConnectHostName(){
        return  HOST;
    }
    public static  Session getSession(){
        return  session;
    }

    public static void disconnect(){
        session.disconnect();
    }
}

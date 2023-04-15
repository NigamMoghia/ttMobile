package getterClasses;


import util.ConnectToLinuxBox;
import util.ConnectToMySQLTestDB;
import util.ObjectRepository;

import java.io.File;
import java.io.FileFilter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by RGupta on 12/14/2017.
 */
public class GetterClass {


    public static String ManifestID = null;
    public static  String windowsExportPath = null;
    public static  String linuxExportPath = null;

    public static String getLinuxExportFolderPath(String environment,String exportType) throws Exception{

            getWindowsExportFolderPath(environment,exportType);

        String path = null;

        switch (environment.toLowerCase()){
            case "production":
                if(exportType.toUpperCase().equals("PR"))
                    path = ObjectRepository.colbld_publish_path_prod_pr;//"/data2/production_nightly_pr/publish" ;
                else if (exportType.toUpperCase().equals("NIGHTLY"))
                    path = ObjectRepository.colbld_publish_path_prod_nightly ;//"/data2/production_nightly/publish" ;
                break;

            case "staging":
                if(exportType.toUpperCase().equals("PR"))
                    path = ObjectRepository.colbld_publish_path_staging_pr ;//"/data2/staging_nightly_pr/publish";
                else if (exportType.toUpperCase().equals("NIGHTLY"))
                    path = ObjectRepository.colbld_publish_path_staging_nightly ;//"/data2/staging_nightly/publish";
                break;

            case "qa":
                if(exportType.toUpperCase().equals("PR"))
                    path = ObjectRepository.colbld_publish_path_qa_pr ;//"/data2/qa_nightly_pr/publish" ;
                else if (exportType.toUpperCase().equals("NIGHTLY"))
                    path = ObjectRepository.colbld_publish_path_qa_nightly ;//"/data2/qa_nightly/publish" ;
                break;

            case "dev":
                if(exportType.toUpperCase().equals("PR"))
                    path =  ObjectRepository.colbld_publish_path_dev_pr;//"/data2/dev_nightly_pr/publish";
                else if (exportType.toUpperCase().equals("NIGHTLY"))
                    path =  ObjectRepository.colbld_publish_path_dev_nightly;//"/data2/dev_nightly/publish";
                break;

        }
        System.out.println("Manifest id "+path +"/"+ManifestID);
        linuxExportPath = ConnectToLinuxBox.connect(environment).getPathLikeAutoTabComplete(path,ManifestID);

            return (linuxExportPath);
    }

    public static File getWindowsExportFolderPath(String environment, String exportType) {

        try {
            Connection mysqlCon = ConnectToMySQLTestDB.getInsstance().getMySqlConnection();
            Statement stmt = mysqlCon.createStatement();

            String query = "Select * from "+ ObjectRepository.Export_test_table+" where Export_Region = '"
                    + environment + "' and Export_Type = '"
                    + exportType+ "';";
            System.out.println(query);
            ResultSet rs = stmt.executeQuery(query);

            if (!rs.isBeforeFirst())
                System.out.println("Export Test Table is empty");
            System.out.println(query);
            rs.next();
            windowsExportPath = rs.getString("Export_Folder_(Windows)");
            ManifestID = rs.getString("Manifest_Id");
            mysqlCon.close();

        } catch (Exception e) {
            System.out.println(e);
        }
        return new File(windowsExportPath);
    }


    public static File[] getListOfSubDirectories(File path){
        File [] allSubdirectories = path.listFiles(new FileFilter() {
            @Override
            public boolean accept(File path) {
                return true;
            }
        });
        return allSubdirectories;
    }


    public static String getColumnValue(String columnName, String tableName, String whereCondition, Connection dbConnection) throws Exception{

        Statement stm = dbConnection.createStatement();
        ResultSet rs = stm.executeQuery("Select "+columnName+" from "+tableName+
                " where "+whereCondition);
        rs.next();
        return rs.getString(columnName);
    }



}

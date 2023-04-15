package util;

import java.sql.*;

/**
 * Created by rgupta on 8/21/2017.
 */
public class ConnectToEditorialDB  {

    private static String servername;
    private static String dbName;
    private static String dbUserName;
    private static String dbPassword;
    private static String dbPort;
    private static String env = null;
    private static Connection SQLCon = null;
    EnvironmentPropertiesParser envParser ;
    static ConnectToEditorialDB myObj = null;

    private ConnectToEditorialDB() throws  Exception{
        setConnectionString();
    }


    public static  ConnectToEditorialDB getInstance(String environment) throws Exception
    {
            env = environment.toUpperCase();
            myObj = new ConnectToEditorialDB();
        return myObj;

    }

    private void setConnectionString() throws Exception{

        envParser = new EnvironmentPropertiesParser();

            servername = envParser.getValue(env+"_"+"DBDSNName");
            dbName = envParser.getValue(env+"_"+ "DBName");
            dbUserName = envParser.getValue(env+"_"+"DBUserName");
            dbPassword = envParser.getValue(env+"_"+"DBPassword");
            dbPort = envParser.getValue(env+"_"+"DBPort");

      }

    public  Connection getConnection() throws SQLException, ClassNotFoundException {
        String url = null;
        try {

            Class.forName ("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            url ="jdbc:sqlserver://"+servername+":"+dbPort+";databaseName=" + dbName ;

            SQLCon = DriverManager.getConnection(url, dbUserName,dbPassword);

            return SQLCon;

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        }

    }

}

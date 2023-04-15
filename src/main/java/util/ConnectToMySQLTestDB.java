package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by rgupta on 9/1/2017.
 */
public class ConnectToMySQLTestDB {

    static String dsnName;
    static String dbName;
    static String dbUserName;
    static String dbPassword;
    static String dbPort;
    private static Connection MySQLCon = null;
    EnvironmentPropertiesParser envParser ;
    static ConnectToMySQLTestDB myObj = null;

    private ConnectToMySQLTestDB() throws Exception {
        setConnectionString();
    }
    private void setConnectionString() throws Exception{

        envParser = new EnvironmentPropertiesParser();

        dsnName = envParser.getValue("MySqlDBDSNName");
        dbName = envParser.getValue("MySqlDBName");
        dbUserName = envParser.getValue("MySqlDBUserName");
        dbPassword = envParser.getValue("MySqlDBPassword");
        dbPort = envParser.getValue("MySqlDBPort");

    }

    public static ConnectToMySQLTestDB getInsstance() throws Exception {
        myObj = new ConnectToMySQLTestDB();
        return myObj;
    }

    public Connection getMySqlConnection() throws ClassNotFoundException, SQLException {
        Connection con = null;
        String strURL = "";
       Class.forName ("com.mysql.jdbc.Driver");
        strURL = "jdbc:mysql://" + dsnName + ":" + dbPort + "/" + dbName;
        //System.out.println(strURL);
        con = DriverManager.getConnection(strURL, dbUserName,dbPassword);
        return con;
    }

}

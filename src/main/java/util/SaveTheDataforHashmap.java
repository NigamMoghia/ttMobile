package util;

/**
 * Created by rgupta on 6/17/2019.
 */


import java.util.*;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;


public class SaveTheDataforHashmap {

    public static void run(String sTableName, String key, String value) throws Exception
    {
        try{

            // Create Connection to  DB
            Connection con = ConnectToMySQLTestDB.getInsstance().getMySqlConnection();

            String query = "INSERT INTO " + sTableName +
                    key + ")"
                    + " Values "+ value + ")";

            System.out.println(query);

            // Create Statement Object
            Statement stmt = con.createStatement();

            // Execute the SQL Query. Store results in ResultSet
            stmt.executeUpdate(query);

            con.close();
        }
        catch(Exception E)
        {
            E.printStackTrace();
        }

    }


    public static void run(String sTableName, HashMap<String, String> params, HashMap<String, Double> paramsDouble) throws Exception
    {
        String key = "(";
        String value = "(";

        for(Map.Entry<String, String> entry : params.entrySet())
        {
            String sColumnName = entry.getKey();
            String sColumnValue = entry.getValue().toString();

            if(key.length()<2)
            {
                key = key + "`" + sColumnName + "`";
                value = value + "'" + sColumnValue + "'";
            }
            else
            {
                key = key + "," + "`" + sColumnName + "`" ;
                value = value + "," + "'" + sColumnValue + "'";
            }
        }

        if(paramsDouble!=null){
            for(Map.Entry<String, Double> entry : paramsDouble.entrySet())
            {
                String sColumnName = entry.getKey();
                Double sColumnValue = entry.getValue();

                if(key.length()<2)
                {
                    key = key + "`" + sColumnName + "`";
                    value = value + sColumnValue;
                }
                else
                {
                    key = key + "," + "`" + sColumnName + "`" ;
                    value = value + "," + sColumnValue;
                }
            }}

        run(sTableName, key,value);
    }

    public static void run(String sTableName, HashMap<String, String> params) throws Exception
    {
        run(sTableName,params,null);
    }


    public static void runQuery(String query, boolean updateTable){
        try{

            Connection con = ConnectToMySQLTestDB.getInsstance().getMySqlConnection();

            System.out.println(query);

            // Create Statement Object
            Statement stmt = con.createStatement();

            // Execute the SQL Query. Store results in ResultSet
            if(updateTable)
                stmt.executeUpdate(query);
            else
                stmt.executeQuery(query);

            con.close();
        }
        catch(Exception e){
            System.out.println("ERROR in executing the query : "+query +"\n"+e);
        }

    }
}

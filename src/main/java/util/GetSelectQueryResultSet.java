package util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GetSelectQueryResultSet {
    public static ArrayList<String> columnValue= new ArrayList<String>() ;

    public static   String getSingleColumnValue(String Query, String dbConnectionName) throws Exception{
        Connection con =null;
        String value=null;

        switch (dbConnectionName)
        {
            case "MySQL":
                con =  ConnectToMySQLTestDB.getInsstance().getMySqlConnection();
                break;
       }

        System.out.println("Query formed= "+Query);
        Statement stm = con.createStatement();

        try{
            ResultSet rs = stm.executeQuery(Query);
            rs.next();
            value =  rs.getString(1);
            System.out.println("Col value = "+value);
            con.close();

        }

        catch(SQLException e){
            System.out.println("Query did not return any value because it is ...................... null");
            e.printStackTrace();

        }

        return value;
    }



    /* The below method is to get the list of one col value and store it in Array Lits*/
    public static List<String> getColumnValueInAList(String Query, String dbConnectionName) throws Exception{
        Connection con =null;
        //String columnName=params.get("ColumnName");

        columnValue.clear();

        switch (dbConnectionName)
        {
            case "MySQL":
                con =  ConnectToMySQLTestDB.getInsstance().getMySqlConnection();
                break;
        }

        System.out.println("Query formed= "+Query);
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(Query);
        ResultSetMetaData rsmd = rs.getMetaData();
        try{
            while(rs.next()) {
                // System.out.println(rs.getString(1));
                columnValue.add( rs.getString(1));
            }
        }catch(Exception e){
            System.out.println("Query did not return any value because it is ...................... null");
            e.printStackTrace();
        }
        return columnValue;
    }

}

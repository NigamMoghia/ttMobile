package util;

import java.sql.Connection;
import java.sql.Statement;

public class UpdateQuery {

    public static String SQLUpDateQuery(String Query) throws Exception{
        Connection con = ConnectToMySQLTestDB.getInsstance().getMySqlConnection();
        String value=null;
        System.out.println("Query formed= "+Query ) ;
        Statement stm = con.createStatement();
        int count = stm.executeUpdate(Query);
        con.close();
        value=Integer.toString(count);
        System.out.println("Number of Row (s) Affected from the updated query .......................................... "+value);
        return value;
    }

}

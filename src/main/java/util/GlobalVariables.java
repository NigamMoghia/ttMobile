package util;

import com.jcraft.jsch.Session;

/**
 * Created by rgupta on 8/28/2017.
 */
public class GlobalVariables {

    public static String environment;
    public static String exportRegion;
    public static String nightly_exportFolder = null;
    public static String pr_exportFolder = null;
    public static String conbld_serverName = null;
    public static String pr_exportDate = null;
    public static String pr_manifestID = null;
    public static String pr_exportBenginDate = null;
    public static String pr_exportEndDate = null;
    public static Session session = null;
    public static boolean certFlagMonthly;

}
